package com.quantimodo.tools.dialogs;

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
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.quantimodo.android.sdk.model.Measurement;
import com.quantimodo.android.sdk.model.MeasurementSet;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.DefaultSdkResponseListener;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.sdk.request.SendMeasurementsRequest;
import com.quantimodo.tools.utils.CustomRemindersHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

public class CustomReminderDialog {

    @Inject
    ToolsPrefs mPrefs;

	@Inject
	AuthHelper mAuthHelper;

    private static final String TAG = CustomReminderDialog.class.getSimpleName();

	private View mOverlayView;
    private TextView mTitleTextView;

	private boolean mMoodDialogShowing;
	private boolean mTapToClose;
	private static CustomReminderDialog mInstance;
    private Context mContext;
    private CustomRemindersHelper.Reminder mReminder;

	public static CustomReminderDialog getInstance(){
		if (mInstance == null){
			mInstance = new CustomReminderDialog();
            QTools.getInstance().inject(mInstance);
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

	public void show(final Context context, final String reminderId) {
        this.mContext = context;
        mReminder = CustomRemindersHelper.getReminder(mContext, reminderId);
		if (mMoodDialogShowing) {
			Log.i(TAG, "currently showing, so not showing new dialog");
			return;
		}
		mMoodDialogShowing = true;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mTapToClose = true;

		final WindowManager.LayoutParams params = createParams(Gravity.TOP);
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		if (mOverlayView != null) {
			try {
				windowManager.removeView(mOverlayView);
			}
			catch (Exception ignored) {
			}
		}

		mOverlayView = LayoutInflater.from(context).inflate(R.layout.custom_reminder_dialog, null);
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


        LinearLayout mTitleLayout = (LinearLayout) container.findViewById(R.id.custom_reminder_title_layout);
        mTitleTextView = (TextView) container.findViewById(R.id.custom_reminder_dialog_title);

        setUpTitle();
		final Handler handler = new Handler();
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 255; i += 5) {
					final int alpha = i;

					handler.post(new Runnable() {
						public void run() {
							container.getBackground().setAlpha(alpha);
						}
					});
					try {
						Thread.sleep(10);
					}
					catch (Exception e) {
						break;
					}
				}
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						container.getBackground().setAlpha(255);
					}
				});
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

        setListeners(container);
	}

    private void setUpTitle() {
		StringBuilder builder = new StringBuilder();
		builder
				.append("Track ")
				.append(CustomRemindersHelper.removeTrailingZeros(mReminder.value)).append(" ")
				.append(mReminder.unitName).append(" of ")
				.append(mReminder.name).append("?");

        mTitleTextView.setText(builder.toString());
    }

	public void dismiss(final Context context) {
		if (mOverlayView != null) {
			Log.i(TAG, "Overlay not null");
			final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			final Handler handler = new Handler();
			final Runnable run = new Runnable() {
				@Override
				public void run() {
					for (int i = 255; i > 0; i -= 8) {
						final int alpha = i;

						handler.post(new Runnable()
						{
							public void run()
							{
								mOverlayView.getBackground().setAlpha(alpha);
							}
						});
						try {
							Thread.sleep(10);
						}
						catch (Exception e) {
							break;
						}
					}
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

    public boolean isShowing(){
        return mMoodDialogShowing;
    }

    private void setListeners(View container){
        container.findViewById(R.id.notification_dialog_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Thread thread = new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        final HashMap<String, MeasurementSet> measurementSets = new HashMap<>();
//                        long timestampSeconds = new Date().getTime() / 1000;
//
//                        Measurement measurement = new Measurement(timestampSeconds, Float.parseFloat(mReminder.value));
//                        MeasurementSet newSet = new MeasurementSet(
//                                mReminder.name, null, mReminder.variableCategory, mReminder.unitName,
//                                mReminder.combinationOperation, mPrefs.getApplicationSource());
//                        newSet.getMeasurements().add(measurement);
//                        measurementSets.put(mReminder.unitName, newSet);
//
//                        SpiceManager mSpiceManager = new SpiceManager(QTools.getInstance().getServiceClass());
//                        mSpiceManager.start(mContext.getApplicationContext());
//                        mSpiceManager.execute(new SendMeasurementsRequest(null, new ArrayList<>(measurementSets.values())),
//                                new DefaultSdkResponseListener<Boolean>() {
//                                    @Override
//                                    public void onRequestSuccess(Boolean aBoolean) {
//                                        Toast.makeText(mContext, "Tracked!", Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                    }
//                });
//                thread.start();
                try {
                    CustomRemindersHelper.postRemoteTrack(mReminder.remoteId,
                            mAuthHelper.getAuthTokenWithRefresh());
                } catch (NoNetworkConnection noNetworkConnection) {
                    noNetworkConnection.printStackTrace();
                }
                dismiss(mContext);
                cancelNotification(mContext);
            }
        });
        container.findViewById(R.id.notification_dialog_snooze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "snooze clicked");
                CustomRemindersHelper.setAlarm(mContext, mReminder.id, CustomRemindersHelper.FrequencyType.SNOOZE);
                dismiss(mContext);
                cancelNotification(mContext);
				try {
					CustomRemindersHelper.postRemoteSnooze(mReminder.remoteId,
                            mAuthHelper.getAuthTokenWithRefresh());
				} catch (NoNetworkConnection noNetworkConnection) {
					noNetworkConnection.printStackTrace();
				}
            }
        });
        container.findViewById(R.id.notification_dialog_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "edit clicked");
                Intent trackIntent = new Intent(mContext,
                        CustomRemindersHelper.getInstance().getRegisteredActivity());
                trackIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                trackIntent.putExtra(CustomRemindersHelper.EXTRA_VARIABLE_NAME, mReminder.name);
                mContext.startActivity(trackIntent);
                dismiss(mContext);
                cancelNotification(mContext);
            }
        });
    }

	private void cancelNotification(Context context){
		NotificationManager notifManager = (NotificationManager) context.getSystemService(
				Context.NOTIFICATION_SERVICE);
		notifManager.cancel(Integer.parseInt(mReminder.id));
	}
}
