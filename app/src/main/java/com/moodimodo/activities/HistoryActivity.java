package com.moodimodo.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.moodimodo.R;
import com.moodimodo.adapters.HistoryAdapter;
import com.moodimodo.dialogs.MoodHistoryDialog;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.moodimodo.things.Question;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.MeasurementDao;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.sync.SyncHelper;
import com.quantimodo.tools.utils.QtoolsUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import io.swagger.client.ApiException;
import io.swagger.client.api.MeasurementsApi;
import io.swagger.client.model.MeasurementDelete;

public class HistoryActivity extends MActivity implements AdapterView.OnItemClickListener, MoodHistoryDialog.DialogListener {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    private Question[] moodQuestions;
    private HistoryAdapter mAdapter;

    @Inject
    DaoSession mSession;

    @Inject
    AuthHelper mAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodlist);
        QTools.getInstance().inject(this);

        if(getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        if (moodQuestions == null) {
            moodQuestions = Question.getAllQuestions(this);
        }

        final ListView listView = (ListView) findViewById(android.R.id.list);
        //TODO read data
        mAdapter = new HistoryAdapter(this, readMeasurements());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        registerForContextMenu(listView);

        final SharedPreferences sharedPref = getSharedPreferences(
                HistoryActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("entered_history_first_time", true)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.help_content_03)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setCancelable(false)
                    .create().show();
            sharedPref.edit().putBoolean("entered_history_first_time", false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.action_history_sync:
                showSyncDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onEventBackgroundThread(MeasurementsUpdatedEvent e){
        final ArrayList<Measurement> measurements = readMeasurements();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
                mAdapter.addAll(measurements);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<Measurement> readMeasurements(){
        return new ArrayList<>(mSession.getMeasurementDao().queryBuilder().orderDesc(MeasurementDao.Properties.Timestamp).list());
    }


    private void showSyncDialog() {
        new AlertDialog.Builder(this, R.style.QAppCompatAlertDialogStyle)
                .setTitle(R.string.activity_history_sync_dialog_title)
                .setMessage(R.string.activity_history_sync_dialog_message)
                .setPositiveButton(R.string.activity_history_sync_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SyncHelper.invokeFullSync(HistoryActivity.this, false);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.activity_history_sync_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        showChangeDialog(mAdapter.getItem(position));
    }

    private void updateMoodThing(Measurement thing) {
        thing.setNeedUpdate(true);
        mSession.getMeasurementDao().update(thing);
        SyncHelper.invokeSync(this);
        mAdapter.notifyDataSetChanged();
    }


    private void showChangeDialog(final Measurement moodThing) {
        MoodHistoryDialog dialog = MoodHistoryDialog.newInstance(moodThing);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void onSubmit(Measurement thing) {
        updateMoodThing(thing);
    }

    @Override
    public void onCancel(Measurement thing) {

    }

    public void onDelete(final Measurement thing) {
        if (!QtoolsUtils.hasInternetConnection(this)) {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.error_no_connection,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        Log.d(TAG, "Deleting mood measurement, id: " + thing.getId());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MeasurementsApi measurementApi = new MeasurementsApi();
                try {
                    MeasurementDelete body = new MeasurementDelete();
                    body.setStartTime((int)(thing.getTimestamp().getTime() / 1000));
                    body.setVariableName(thing.getVariableName());
                    boolean succeed = measurementApi.v1MeasurementsDeletePost(body,
                            mAuthHelper.getAuthTokenWithRefresh()).getSuccess();
                    if (succeed) {
                        Log.d(TAG, "Succeed deleting!, now deleting locally");
                        deleteLocally(thing);
                    }
                    else{
                        showErrorMessage();
                    }
                } catch (NoNetworkConnection e) {
                    showErrorMessage();
                    e.printStackTrace();
                } catch (ApiException apiException) {
                    apiException.printStackTrace();
                    Log.d(TAG, "Not found on server, now deleting locally");
                    if (apiException.getMessage().toLowerCase().contains("not found") ||
                            apiException.getMessage().toLowerCase().contains("unauthorized")) {
                        deleteLocally(thing);
                    }
                }
            }
        });
        thread.start();
    }

    private void showErrorMessage(){
        HistoryActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(
                        getApplicationContext(),
                        R.string.error_delete_measurement,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void deleteLocally(final Measurement thing) {
        mSession.getMeasurementDao().deleteByKey(thing.getId());

        HistoryActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.remove(thing);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(
                        getApplicationContext(),
                        R.string.measurement_deleted,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
