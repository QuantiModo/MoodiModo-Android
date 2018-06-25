package com.moodimodo.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.moodimodo.dialogs.MoodDialog;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.Unit;
import com.quantimodo.tools.models.VariableDao;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodResultReceiver extends BroadcastReceiver
{
	public static final String INTENT_ACTION_SUBMIT = "com.moodimodo.SUBMIT";
	public static final String INTENT_ACTION_DIALOG = "com.moodimodo.MOOD_SHOW_DIALOG";

	public static final String EXTRA_FROM_DIALOG = "fromDialog";
	public static final String EXTRA_RESULTS = "results";
	public static final String EXTRA_NOTE = "note";
	public static final String EXTRA_ID = "id";
	public static final String EXTRA_MEASUREMENTS = "measurements";

	@Inject
	DaoSession mDaoSession;

	@Inject @Named("outcome")
	Unit defaultUnit;

	@Override public void onReceive(Context context, Intent intent)
	{
		QTools.getInstance().inject(this);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(MoodDialog.NOTIFICATION_ID);
		MoodTimeReceiver.cancelReminderAlarm(context);

		switch (intent.getAction()){
			case INTENT_ACTION_SUBMIT:
				actionSubmit(context, intent);
				break;

			case INTENT_ACTION_DIALOG:
				actionOpenDialog(context, intent);
				break;
		}
	}

	private void actionOpenDialog(Context context,Intent intent){
		MoodDialog.getInstance().show(context.getApplicationContext());
	}

	private void actionSubmit(Context ctx, Intent intent){
		String note = intent.getStringExtra(EXTRA_NOTE);
		Bundle measurements = intent.getBundleExtra(EXTRA_MEASUREMENTS);
		int id = intent.getIntExtra(EXTRA_ID, 0);

		List<Measurement> measurementList = new ArrayList<>();
		for (String k : measurements.keySet()){
			Measurement measurement = new Measurement();
			measurement.setUnit(defaultUnit);
			measurement.setNote(note);
			measurement.setTimestamp(new Date());
			measurement.setValue(measurements.getDouble(k));
			measurement.setVariable(mDaoSession.getVariableDao().queryBuilder().where(VariableDao.Properties.Name.eq(k)).unique());
			measurement.setVariableName(k);
			measurement.setUnitName(defaultUnit.getAbbreviatedName());
			measurementList.add(measurement);
		}

		mDaoSession.getMeasurementDao().insertInTx(measurementList);

		QTools.getInstance().postEvent(new MeasurementsUpdatedEvent());
	}
}
