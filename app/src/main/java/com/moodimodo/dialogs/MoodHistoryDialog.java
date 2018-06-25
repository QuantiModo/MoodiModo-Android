package com.moodimodo.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.things.Ratings;
import com.quantimodo.tools.models.Measurement;

public class MoodHistoryDialog extends DialogFragment {
    private static final String TAG = MoodHistoryDialog.class.getSimpleName();
    private static final String MOOD_THING_KEY = "moodThing";

    private int mCurrentRating;

    @InjectViews({R.id.btDepressed,R.id.btSad,R.id.btOk,R.id.btHappy,R.id.btEcstatic})
    ImageButton[] mButtons;
    @InjectView(R.id.etNote)
    EditText etNote;

    private Measurement mMoodThing;
    private DialogListener mListener;

    public interface DialogListener {
        void onSubmit(Measurement thing);
        void onCancel(Measurement thing);
        void onDelete(Measurement thing);
    }

    public static MoodHistoryDialog newInstance(Measurement moodThing) {
        MoodHistoryDialog dialog = new MoodHistoryDialog();
        Bundle args = new Bundle();
        args.putSerializable(MOOD_THING_KEY, moodThing);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogListener){
            mListener = (DialogListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMoodThing = (Measurement) getArguments().getSerializable(MOOD_THING_KEY);
        Log.d(TAG, "Mood measurement id: " + mMoodThing.getId());
        View view = View.inflate(getActivity(), R.layout.dialog_mood_edit, null);
        ButterKnife.inject(this, view);
        etNote.setText(mMoodThing.getNote());
        int state = (int) Math.round(mMoodThing.getValue());
        mCurrentRating = state;
        int id = 0;
        switch (state){
            case 1:
                id = R.id.btDepressed;
                break;
            case 2:
                id = R.id.btSad;
                break;
            case 3:
                id = R.id.btOk;
                break;
            case 4:
                id = R.id.btHappy;
                break;
            case 5:
                id = R.id.btEcstatic;
                break;
        }
        setState(id);

        String title = mMoodThing.getVariableName() + " " + Global.DEFAULT_MOOD_DATE_FORMATTER.format(mMoodThing.getTimestamp());

        if (!mMoodThing.getVariableName().equals(BuildConfig.OUTCOME_VARIABLE)){
            mButtons[0].setImageResource(R.drawable.ic_mood_pos_1);
            mButtons[1].setImageResource(R.drawable.ic_mood_pos_2);
            mButtons[2].setImageResource(R.drawable.ic_mood_3);
            mButtons[3].setImageResource(R.drawable.ic_mood_pos_4);
            mButtons[4].setImageResource(R.drawable.ic_mood_pos_5);
        }

        return new AlertDialog.Builder(getActivity(), R.style.QAppCompatAlertDialogStyle)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.fragment_mood_edit_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        onSubmit();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.fragment_mood_edit_dialog_delete_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //as we are displaying another dialog, we loose the reference of mListener
                        final DialogListener localListener = mListener;
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.fragment_mood_edit_dialog_delete_question)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        localListener.onDelete(mMoodThing);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .create().show();
                    }
                })
                .setNegativeButton(R.string.fragment_mood_edit_dialog_negativ_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        onCancel();
                        dismiss();
                    }
                })
                .create();
    }

    @OnClick({R.id.btDepressed,R.id.btSad,R.id.btOk,R.id.btHappy,R.id.btEcstatic})
    void onMoodClick(View v){
        setState(v.getId());

        switch (v.getId()){
            case R.id.btDepressed:
                mCurrentRating = Ratings.RATING_1.intValue;
                break;

            case R.id.btSad:
                mCurrentRating = Ratings.RATING_2.intValue;
                break;

            case R.id.btOk:
                mCurrentRating = Ratings.RATING_3.intValue;
                break;

            case R.id.btHappy:
                mCurrentRating = Ratings.RATING_4.intValue;
                break;

            case R.id.btEcstatic:
                mCurrentRating = Ratings.RATING_5.intValue;
                break;
        }
    }

    private void setState(int activeId){
        for (ImageButton button : mButtons){
            if (button.getId() == activeId){
                button.setAlpha(1f);
            } else {
                button.setAlpha(0.2f);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void onSubmit() {
        mMoodThing.setValue(mCurrentRating);
        mMoodThing.setNote(etNote.getText().toString());

        if (mListener != null) {
            mListener.onSubmit(mMoodThing);
        }
    }

    private void onCancel() {
        if (mListener != null) {
            mListener.onCancel(mMoodThing);
        }
    }
}
