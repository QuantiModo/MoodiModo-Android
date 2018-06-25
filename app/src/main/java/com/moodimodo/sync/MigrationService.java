package com.moodimodo.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.moodimodo.BuildConfig;
import com.moodimodo.databases.DatabaseWrapper;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.moodimodo.things.MoodThing;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.sync.SyncHelper;
import de.greenrobot.dao.query.Query;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;

public class MigrationService extends IntentService {

    public static final String PREFS_NAME = "migration";
    public static final String PREF_NEED_UPDATE = "update";
    public static final String PREF_NEED_NOTE_MIGRATIONS = "note";
    public static final String PREF_NEED_UPDATE_FROM = "from";

    public static final int LIMIT = 200;


    @Inject @Named("outcome")
    Variable variable;

    @Inject @Named("outcome")
    Unit unit;

    @Inject
    DaoSession mSession;

    public MigrationService() {
        super("Migration service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        QTools.getInstance().inject(this);
        DatabaseWrapper wrapper = new DatabaseWrapper(this);
        Date start = wrapper.getFirstMoodReportDate();
        Date end = wrapper.getLastMoodReportDate();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        boolean fullSync = prefs.getBoolean(PREF_NEED_UPDATE,true);
        int count = wrapper.count(start,end);
        for (int i = 0 ; i<count; i += LIMIT) {
            ArrayList<MoodThing> things = wrapper.readAll(false, start, end, LIMIT, i);
            if (fullSync) {
                saveMoods(things);
            } else {
                updateNotes(things);
            }
        }

        wrapper.close();


        prefs.edit().putBoolean(PREF_NEED_UPDATE,false).putBoolean(PREF_NEED_NOTE_MIGRATIONS,false).commit();
        SyncHelper.invokeFullSync(this,false);
        QTools.getInstance().postEvent(new MeasurementsUpdatedEvent(true));
    }

    protected void saveMoods(ArrayList<MoodThing> things){
        ArrayList<Measurement> measurements = new ArrayList<>();

        for (MoodThing t : things){
            Measurement m = new Measurement();
            m.setVariable(variable);
            m.setUnit(unit);
            m.setNote(t.getNote());
            m.setTimestamp(new Date(t.timestamp * 1000));
            m.setSource(BuildConfig.APPLICATION_SOURCE);
            m.setValue(t.getOneToFiveMood());
            m.setVariableName(variable.getName());
            m.setUnitName(unit.getAbbreviatedName());
            measurements.add(m);
        }

        mSession.getMeasurementDao().insertOrReplaceInTx(measurements);
    }

    protected void updateNotes(ArrayList<MoodThing> things){
        MeasurementDao dao = mSession.getMeasurementDao();
        ArrayList<Measurement> measurements = new ArrayList<>();

        Query<Measurement> q = dao.queryBuilder()
                .where(
                        MeasurementDao.Properties.Timestamp.eq(new Date()),
                        MeasurementDao.Properties.VariableName.eq(variable.getName())).build();

        for (MoodThing t : things){
            if (t.getNote() != null && !t.getNote().isEmpty()){
                q.setParameter(0,new Date(t.timestamp * 1000));
                Measurement m = q.unique();
                if ( m != null && (m.getNote() == null || m.getNote().isEmpty())){
                    m.setNote(t.getNote());
                    m.setNeedUpdate(true);
                    measurements.add(m);
                }
            }
        }

        dao.insertOrReplaceInTx(measurements);
    }

    /**
     * Checks if we need to migrate to other database, and create intent for it.
     * Return null if we don't need update
     * @param context Context
     * @return Intent or null
     */
    public static Intent needUpdate(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        if(!prefs.getBoolean(PREF_NEED_UPDATE,true) && !prefs.getBoolean(PREF_NEED_NOTE_MIGRATIONS,true)){
            return null;
        }

        Intent intent = new Intent(context,MigrationService.class);
        return intent;
    }
}
