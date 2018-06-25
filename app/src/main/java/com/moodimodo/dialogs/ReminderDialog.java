package com.moodimodo.dialogs;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.receivers.RemindersReceiver;
import com.moodimodo.receivers.RemindersService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ReminderDialog {
    private static final String TAG = ReminderDialog.class.getSimpleName();

	private View mOverlayView;

	@InjectView(R.id.notification_title_layout)
	LinearLayout mTitleLayout;

    @InjectView(R.id.notification_dialog_title)
    TextView mTitleTextView;

	private boolean mMoodDialogShowing;
	private boolean mTapToClose;
	private static ReminderDialog mInstance;
    private Context mContext;
    private RemindersReceiver.RemindersType mReminderType;

	public static ReminderDialog getInstance(){
		if (mInstance == null){
			mInstance = new ReminderDialog();
		}
		return mInstance;
	}

	private WindowManager.LayoutParams createParams(int align){
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
						WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSPARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | align;
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

		return params;
	}

	public void show(final Context context, final int typeIndex) {
        mReminderType = RemindersReceiver.RemindersType.values()[typeIndex];
        this.mContext = context;
		if (mMoodDialogShowing) {
			Log.i(TAG, "currently showing, so not showing new dialog");
			return;
		}
		mMoodDialogShowing = true;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mTapToClose = prefs.getBoolean(Global.PREF_DOUBLE_TAP_CLOSE, true);

		final WindowManager.LayoutParams params = createParams(Gravity.TOP);
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		if (mOverlayView != null) {
			try {
				windowManager.removeView(mOverlayView);
			}
			catch (Exception ignored) {
			}
		}

		mOverlayView = LayoutInflater.from(context).inflate(R.layout.dialog_reminders, null);
		mOverlayView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					if (mTapToClose) {
						dismiss(context);
					}
			}
		});

		final View container = mOverlayView.findViewById(R.id.notification_main_layout);
		container.getBackground().setAlpha(0);
		ButterKnife.inject(this, mOverlayView);
        setUpTitle();
		final Handler handler = new Handler();
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				Utils.changeAlpha(handler,container);
			}
		};
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				new Thread(run).start();
			}
		}, 600);

		Animation anim = AnimationUtils.loadAnimation(context, R.anim.mood_slide_up);
		anim.setStartOffset(300);
		mTitleLayout.startAnimation(anim);
		windowManager.addView(mOverlayView, params);
	}

	public boolean isShowing(){
		return mMoodDialogShowing;
	}

    private void setUpTitle(){
        switch (mReminderType){
            case MOOD:
                mTitleTextView.setText(R.string.reminders_notif_title_mood);
                break;
            case DIET:
                mTitleTextView.setText(R.string.reminders_notif_title_diet);
                break;
            case TREATMENT:
                mTitleTextView.setText(R.string.reminders_notif_title_treatments);
                break;
			case SYMPTOM:
				mTitleTextView.setText(R.string.reminders_notif_title_symptoms);
				break;
			case PHYSICAL:
				mTitleTextView.setText(R.string.reminders_notif_title_physical);
				break;
        }
    }

	public void dismiss(final Context context) {
		if (mOverlayView != null) {
			Log.i(TAG, "Overlay not null");
			final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			final Handler handler = new Handler();
			final Runnable run = new Runnable() {
				@Override
				public void run() {
					Utils.changeAlpha(handler,mOverlayView);
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							long longestDelay = 0;

							Animation anim = AnimationUtils.loadAnimation(context, R.anim.mood_slide_down);
							anim.setFillAfter(true);
							anim.setFillEnabled(true);
                            handler.postDelayed(new Runnable()
                            {
                                @Override public void run()
                                {
                                    try {
                                        windowManager.removeView(mOverlayView);
                                        ButterKnife.reset(this);
                                        mInstance = null;
                                        Log.i(TAG, "Removed view");
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        Log.i(TAG, "Error removing view");
                                    }

                                    mMoodDialogShowing = false;
                                }
                            }, longestDelay + 250);
                        }
					});
				}
			};
			new Thread(run).start();
		}
	}

    @OnClick(R.id.notification_dialog_ok)
    void onTrackClick(){
        Log.i(TAG, "ok clicked");
        Intent trackIntent = new Intent(mContext, MainActivity.class);
        trackIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (mReminderType){
            case MOOD:
                trackIntent.putExtra(MainActivity.EXTRA_TRACK_MOOD, true);
                break;
            case DIET:
                trackIntent.putExtra(MainActivity.EXTRA_TRACK_DIET, true);
                break;
            case TREATMENT:
                trackIntent.putExtra(MainActivity.EXTRA_TRACK_TREATMENT, true);
                break;
            case SYMPTOM:
                trackIntent.putExtra(MainActivity.EXTRA_TRACK_SYMPTOM, true);
                break;
            case PHYSICAL:
                trackIntent.putExtra(MainActivity.EXTRA_TRACK_PHYSICAL, true);
                break;

        }
        mContext.startActivity(trackIntent);
        dismiss(mContext);
        cancelNotification();
    }

    @OnClick(R.id.notification_dialog_snooze)
    void onSnoozeClick(){
        Log.i(TAG, "snooze clicked");
        RemindersReceiver receiver = new RemindersReceiver();
        receiver.setAlarm(mContext, mReminderType, RemindersReceiver.FrecuencyType.SNOOZE);
		dismiss(mContext);
        cancelNotification();
    }

	@OnClick(R.id.notification_dialog_skip)
	void onSkipClick(){
		dismiss(mContext);
        cancelNotification();
	}

	private void cancelNotification(){
		NotificationManager notifManager = (NotificationManager) mContext.getSystemService(
				Context.NOTIFICATION_SERVICE);
		switch (mReminderType){
            case MOOD:
                notifManager.cancel(RemindersService.NOTIFICATION_ID_MOOD);
                break;
            case DIET:
                notifManager.cancel(RemindersService.NOTIFICATION_ID_DIET);
                break;
            case TREATMENT:
                notifManager.cancel(RemindersService.NOTIFICATION_ID_TREATMENT);
                break;
            case SYMPTOM:
                notifManager.cancel(RemindersService.NOTIFICATION_ID_SYMPTOM);
                break;
            case PHYSICAL:
                notifManager.cancel(RemindersService.NOTIFICATION_ID_PHYSICAL);
                break;
        }
	}
}
