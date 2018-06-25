package com.moodimodo.dialogs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.moodimodo.*;
import com.moodimodo.receivers.MoodResultReceiver;
import com.moodimodo.receivers.MoodTimeReceiver;
import com.moodimodo.things.Question;
import com.moodimodo.things.Ratings;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class MoodDialog
{
	public static final int NOTIFICATION_ID = 133710;
	private static final long TIME_BETWEEN_TAP = 1000;

	private View mOverlayView;

	@InjectView(R.id.lnAskMoreQuestions)
	LinearLayout lnAskMoreQuestions;
	@InjectView(R.id.lnCurrentQuestion)
	LinearLayout lnCurrentQuestion;
	@InjectView(R.id.lnButtons)
	LinearLayout lnButtons;
	@InjectView(R.id.lnNoteContainer)
	LinearLayout lnNotes;
	@InjectView(R.id.btSubmit)
	Button btSubmit;
	@InjectView(R.id.etNote)
	TextView etNote;
	@InjectView(R.id.imAskMoreQuestions)
	ImageButton imAskMoreQuestions;
	@InjectViews({R.id.btDepressed,R.id.btSad,R.id.btOk,R.id.btHappy,R.id.btEcstatic})
	ImageButton[] moodButtons;
	@InjectView(R.id.tvQuestion)
	TextView tvQuestion;
	@InjectView(R.id.tvQuestionDescription)
	TextView tvQuestionDescription;

	private boolean mMoodDialogShowing;
	private boolean mAskMoreQuestions;
	private boolean mShowNotes;
	private boolean mTapToClose;

	private long mLastTap;

	private Question[] questions;
	private int currentQuestion;

	private Bundle mReportedMeasurements;

	private static MoodDialog mInstance;
	private String mNote;

	public static MoodDialog getInstance(){
		if (mInstance == null){
			mInstance = new MoodDialog();
		}
		return mInstance;
	}

	private static PendingIntent createPendingIntent(Context context,double value){
		return PendingIntent.getBroadcast(context,100 + (int) value,Utils.createIntent(context, BuildConfig.OUTCOME_VARIABLE,value),0);
	}

	public static void showNotification(Context context)
	{
		MoodTimeReceiver.setReminderAlarm(context);

		Intent intent5 = new Intent(context, MoodResultReceiver.class);
		intent5.setAction(MoodResultReceiver.INTENT_ACTION_DIALOG);
		PendingIntent intentShowPopup = PendingIntent.getBroadcast(context, 0, intent5, 0);

		Resources res = context.getResources();

		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(res.getString(R.string.notif_mood_title))
				.setContentText(res.getString(R.string.notif_mood_subtitle))
				.setSmallIcon(R.drawable.ic_action_appicon)
				.setContentIntent(intentShowPopup).build();

		if (Build.VERSION.SDK_INT >= 16)
		{
			RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_mood);
			contentView.setOnClickPendingIntent(R.id.btDepressed, createPendingIntent(context, Ratings.RATING_1.intValue));
			contentView.setOnClickPendingIntent(R.id.btSad, createPendingIntent(context,Ratings.RATING_2.intValue));
			contentView.setOnClickPendingIntent(R.id.btOk, createPendingIntent(context,Ratings.RATING_3.intValue));
			contentView.setOnClickPendingIntent(R.id.btHappy, createPendingIntent(context,Ratings.RATING_4.intValue));
			contentView.setOnClickPendingIntent(R.id.btEcstatic, createPendingIntent(context,Ratings.RATING_5.intValue));
			noti.bigContentView = contentView;
		}

		noti.tickerText = res.getString(R.string.notif_mood_title);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(NOTIFICATION_ID, noti);
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

	public void show(final Context context)
	{
		if (mMoodDialogShowing)
		{
			Log.i("MoodDialog showing, not showing new dialog");
			return;
		}
		mMoodDialogShowing = true;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mAskMoreQuestions = prefs.getBoolean(Global.PREF_ASK_MORE_QUESTIONS, false);
		mShowNotes = prefs.getBoolean(Global.PREF_NOTES_ENABLED,false);
		mTapToClose = prefs.getBoolean(Global.PREF_DOUBLE_TAP_CLOSE,true);


		currentQuestion = 0;
		mReportedMeasurements = new Bundle();
		questions = Question.getAllQuestions(context);
		Arrays.sort(questions, new Comparator<Question>() {
			@Override
			public int compare(Question q1, Question q2) {
				if (q1.resultType == 1)  // Always have mood first
				{
					return 1;
				} else if (q1.isRequired && q2.isRequired)     // Then sort by required/not required
				{
					return 0;
				} else if (q1.isRequired) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		final WindowManager.LayoutParams params = createParams(Gravity.TOP);
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		if (mOverlayView != null)
		{
			try
			{
				windowManager.removeView(mOverlayView);
			}
			catch (Exception ignored)
			{
			}
		}

		mOverlayView = LayoutInflater.from(context).inflate(R.layout.dialog_mood, null);
		mOverlayView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					if (mTapToClose) {
						dismiss(context, 2);
					}
			}
		});

		final View container = mOverlayView.findViewById(R.id.rlMoodDialog);
		container.getBackground().setAlpha(0);
		ButterKnife.inject(this,mOverlayView);
		final Handler handler = new Handler();
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				Utils.changeAlpha(handler,container);
			}
		};
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				new Thread(run).start();
			}
		}, 600);

		Animation anim = AnimationUtils.loadAnimation(context, R.anim.mood_slide_up);
		anim.setStartOffset(300);
		lnAskMoreQuestions.startAnimation(anim);

		View btAskMoreQuestions = lnAskMoreQuestions.findViewById(R.id.btAskMoreQuestions);
		btAskMoreQuestions.setOnClickListener(onAskMoreQuestionsClicked);
		if (mAskMoreQuestions)
		{
			imAskMoreQuestions.setImageResource(R.drawable.ic_checked);
			lnCurrentQuestion.startAnimation(anim);
			lnCurrentQuestion.setVisibility(View.VISIBLE);

		}
		else
		{
			imAskMoreQuestions.setImageResource(R.drawable.ic_unchecked);
			if(questions[1].isRequired)    // If the question at pos 1 is required we have > 1 required question
			{
				if(lnCurrentQuestion.getVisibility() != View.VISIBLE)
				{
					lnCurrentQuestion.startAnimation(anim);
					lnCurrentQuestion.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				lnCurrentQuestion.setVisibility(View.GONE);
			}
		}

		tvQuestion.setText(styleQuestionTitle(context, questions[0].title, R.style.MoodPopup_QuestionKeyWord));
		tvQuestionDescription.setText(questions[0].description);

		initMoodButton(context,moodButtons[0],Ratings.RATING_1,300);
		initMoodButton(context,moodButtons[1],Ratings.RATING_2,150);
		initMoodButton(context,moodButtons[2],Ratings.RATING_3,50);
		initMoodButton(context,moodButtons[3],Ratings.RATING_4,150);
		initMoodButton(context,moodButtons[4],Ratings.RATING_5,300);

		windowManager.addView(mOverlayView, params);
	}

	private void initMoodButton(Context context,ImageButton imageButton,Object tag, int time){
		imageButton.setOnClickListener(onMoodButtonClicked);
		imageButton.setTag(tag);
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.mood_slide_up);
		anim.setStartOffset(time);
		imageButton.setAnimation(anim);
	}

	private long hideButton(Context ctx, int buttonNum, int idx,long longestDelay){
		Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.mood_slide_down);
		long delay = (buttonNum - idx) * 75;
		if (delay > longestDelay)
		{
			longestDelay = delay;
		}
		anim.setFillAfter(true);
		anim.setFillEnabled(true);
		anim.setStartOffset(delay);
		moodButtons[idx].startAnimation(anim);
		return longestDelay;
	}

	public void dismiss(final Context context, final int buttonNum)
	{
		if (mOverlayView != null)
		{
			Log.i("Overlay not null");
			final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			final Handler handler = new Handler();
			final Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
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

							if (lnNotes.getVisibility() != View.VISIBLE) {
								lnAskMoreQuestions.startAnimation(anim);

								for (int i = buttonNum; i > -1; i--) {
									longestDelay = hideButton(context, buttonNum, i, longestDelay);
								}
								for (int i = buttonNum + 1; i < moodButtons.length; i++) {
									longestDelay = hideButton(context, buttonNum, i, longestDelay);
								}
							}

							if (lnCurrentQuestion.getVisibility() == View.VISIBLE)
							{

								anim = AnimationUtils.loadAnimation(context, R.anim.mood_slide_down);
								anim.setStartOffset(longestDelay + 75);
								anim.setAnimationListener(new Animation.AnimationListener()
								{
									@Override public void onAnimationStart(Animation animation)
									{
									}

									@Override public void onAnimationEnd(Animation animation)
									{
										try
										{
											windowManager.removeView(mOverlayView);
											ButterKnife.reset(this);
											mInstance = null;
											Log.i("Removed view");
										}
										catch (IllegalArgumentException ignored)
										{
										}

										mMoodDialogShowing = false;
									}

									@Override public void onAnimationRepeat(Animation animation)
									{
									}
								});
								lnCurrentQuestion.startAnimation(anim);
							}
							else
							{
								handler.postDelayed(new Runnable()
								{
									@Override public void run()
									{
										try
										{
											windowManager.removeView(mOverlayView);
											ButterKnife.reset(this);
											mInstance = null;
											Log.i("Removed view");
										}
										catch (IllegalArgumentException ignored)
										{
											Log.i("Error removing view");
										}

										mMoodDialogShowing = false;
									}
								}, longestDelay + 250);
							}
						}
					});
				}
			};
			new Thread(run).start();
		}
	}

	private View.OnClickListener onMoodButtonClicked = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			double value = ((Ratings) view.getTag()).intValue;
			String variableName = questions[currentQuestion].variableName;
			mReportedMeasurements.putDouble(variableName,value);

			if (currentQuestion + 1 < questions.length && (mAskMoreQuestions || questions[currentQuestion + 1].isRequired))
			{
				nextQuestion(view.getContext());
			}
			else
			{
				if (mShowNotes) {
					showNote();
				} else {
					dismiss(lnButtons.getContext(), 2);
					reportMood(view.getContext());
				}
			}
		}
	};

	private View.OnClickListener onAskMoreQuestionsClicked = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			if (!questions[currentQuestion].isRequired) // If the current question is not required the "Ask more questions" button is a close button
			{
				dismiss(view.getContext(), 2);
				reportMood(view.getContext());
			}
			else
			{
				mAskMoreQuestions = !mAskMoreQuestions;
				PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putBoolean(Global.PREF_ASK_MORE_QUESTIONS, mAskMoreQuestions).commit();

				if (mAskMoreQuestions)
				{
					imAskMoreQuestions.setImageResource(R.drawable.ic_checked);
					lnCurrentQuestion.setVisibility(View.VISIBLE);
				}
				else
				{
					imAskMoreQuestions.setImageResource(R.drawable.ic_unchecked);
					if(!questions[1].isRequired)    // If the question at position 1 is required we have more than 1 required question
					{
						lnCurrentQuestion.setVisibility(View.GONE);
					}
				}
			}
		}
	};

	public void reportMood(Context context)
	{
		Intent intent = new Intent(context, MoodResultReceiver.class);
		intent.setAction(MoodResultReceiver.INTENT_ACTION_SUBMIT);
		intent.putExtra(MoodResultReceiver.EXTRA_MEASUREMENTS,mReportedMeasurements);
		intent.putExtra(MoodResultReceiver.EXTRA_NOTE,mNote);
		context.sendBroadcast(intent);
	}

	private void nextQuestion(final Context context)
	{
		currentQuestion++;

		Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
		fadeOut.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationStart(Animation animation)
			{
			}

			@Override public void onAnimationEnd(Animation animation)
			{
				tvQuestion.setText(styleQuestionTitle(context, questions[currentQuestion].title, R.style.MoodPopup_QuestionKeyWord));
				tvQuestionDescription.setText(questions[currentQuestion].description);

				Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_fast);
				tvQuestionDescription.setAnimation(fadeIn);
				tvQuestion.startAnimation(fadeIn);
			}

			@Override public void onAnimationRepeat(Animation animation)
			{
			}
		});
		tvQuestionDescription.setAnimation(fadeOut);
		tvQuestion.startAnimation(fadeOut);

		// Animate "Ask more questions" row to a close button if we move to the not required questions
		if (!questions[currentQuestion].isRequired && questions[currentQuestion - 1].isRequired)
		{
			final TextView tvAskMoreQuestions = (TextView) lnAskMoreQuestions.findViewById(R.id.tvAskMoreQuestions);

			fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
			fadeOut.setAnimationListener(new Animation.AnimationListener()
			{
				@Override public void onAnimationStart(Animation animation)
				{
				}

				@Override public void onAnimationEnd(Animation animation)
				{
					tvAskMoreQuestions.setText(R.string.popup_askmorequestions_cancel);
					imAskMoreQuestions.setImageResource(R.drawable.ic_cancel_dark);

					Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_fast);
					tvAskMoreQuestions.setAnimation(fadeIn);
					imAskMoreQuestions.startAnimation(fadeIn);
				}

				@Override public void onAnimationRepeat(Animation animation)
				{
				}
			});
			tvAskMoreQuestions.setAnimation(fadeOut);
			imAskMoreQuestions.startAnimation(fadeOut);
		}

		reorderButtons(context);
	}

	private void showNote(){
		((LinearLayout)mOverlayView).setGravity(Gravity.CENTER);

		lnButtons.setVisibility(View.GONE);
		lnAskMoreQuestions.setVisibility(View.GONE);
		lnCurrentQuestion.setVisibility(View.GONE);
		lnNotes.setVisibility(View.VISIBLE);

		etNote.requestFocus();
	}

	@OnClick(R.id.btSkip)
	void onSkip(){
		mNote = null;
		dismiss(lnButtons.getContext(), 2);
		reportMood(lnButtons.getContext());
	}

	@OnClick(R.id.btSubmit)
	void onNoteSubmit(){
		mNote = etNote.getText().toString();
		dismiss(lnButtons.getContext(), 2);
		reportMood(lnButtons.getContext());
	}

	private void reorderButtons(final Context context)
	{
		final Handler handler = new Handler();
		Runnable run = new Runnable()
		{
			@Override public void run()
			{

				String iconPrefix = questions[currentQuestion].iconPrefix;
				Class res = R.drawable.class;

				for (final ImageButton moodButton : moodButtons)
				{
					// Get the drawable for the new button
					final Drawable drawable;
					switch ((Ratings) moodButton.getTag())
					{
						case RATING_1:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 1, iconPrefix));
							break;
						case RATING_2:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 2, iconPrefix));
							break;
						case RATING_3:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 3, iconPrefix));
							break;
						case RATING_4:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 4, iconPrefix));
							break;
						case RATING_5:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 5, iconPrefix));
							break;
						default:
							drawable = context.getResources().getDrawable(getDrawableIdForButton(res, 2, iconPrefix));
					}

					final Animation animIn = AnimationUtils.loadAnimation(context, R.anim.flip_in);
					final Animation animOut = AnimationUtils.loadAnimation(context, R.anim.flip_out);

					// Start the animation on the main thread
					handler.post(new Runnable()
					{
						@Override public void run()
						{
							moodButton.startAnimation(animIn);
						}
					});

					// Delay starting the reentry animation
					handler.postDelayed(new Runnable()
					{
						@Override public void run()
						{
							moodButton.setImageDrawable(drawable);
							moodButton.startAnimation(animOut);
						}
					}, animIn.getDuration());

					// Sleep 75ms (time between the animations)
					try
					{
						Thread.sleep(75);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(run);
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		thread.start();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private SpannableString styleQuestionTitle(Context context, String value, int style)
	{
		int startLocation = value.indexOf("{");
		int endLocation = value.indexOf("}") - 1;

		value = value.replace("{", "");
		value = value.replace("}", "");

		SpannableString styledText = new SpannableString(value);
		styledText.setSpan(new TextAppearanceSpan(context, style), startLocation, endLocation, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return styledText;
	}

	private HashMap<String, Integer> drawableIds = new HashMap<>();
	private int getDrawableIdForButton(Class<?> resClass, int rating, String iconPrefix)
	{
		try
		{
			if(rating == 3)
			{
				return R.drawable.ic_mood_3;
			}
			else if(drawableIds.containsKey(iconPrefix + rating))
			{
				return drawableIds.get(iconPrefix + rating);
			}
			else
			{
				Field idField = resClass.getDeclaredField("ic_mood_" + iconPrefix + "_" + rating);
				int id = idField.getInt(idField);
				drawableIds.put(iconPrefix + rating, id);
				return id;
			}
		}
		catch(Exception ignored)
		{
		}
		return R.drawable.icon;
	}
}
