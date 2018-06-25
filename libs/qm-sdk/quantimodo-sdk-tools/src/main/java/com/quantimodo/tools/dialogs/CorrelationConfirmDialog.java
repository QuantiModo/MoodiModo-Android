package com.quantimodo.tools.dialogs;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.quantimodo.android.sdk.model.Correlation;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.adapters.CorrelationAdapter;

/**
 *  Confirm dialog , that shown in FactorsFragment,
 *  When user first time click thumbs up or thumb down button
 */
public class CorrelationConfirmDialog extends DialogFragment{

    private static final String KEY_CORRELATION = "correlation";
    private static final String KEY_STATE_VALUE = "state";
    private static final String KEY_TYPE_VALUE = "type";

    public interface DialogListener{
        void onConfirm(Correlation correlation,@CorrelationAdapter.CorrelationState int state);
    }

    public static CorrelationConfirmDialog createDialog(Correlation correlation,@CorrelationAdapter.CorrelationType int type, @CorrelationAdapter.CorrelationState int state){
        CorrelationConfirmDialog dialog = new CorrelationConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_CORRELATION, correlation);
        args.putInt(KEY_STATE_VALUE, state);
        args.putInt(KEY_TYPE_VALUE, type);

        dialog.setArguments(args);
        return dialog;
    }

//    private CheckBox checkBox;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int type = args.getInt(KEY_TYPE_VALUE);
        final int state = args.getInt(KEY_STATE_VALUE);

        final Correlation correlation = (Correlation) args.getSerializable(KEY_CORRELATION);
        String factor = correlation.getCause();
        String variable = correlation.getEffect();

        int format,positiveId;
        if (state == CorrelationAdapter.STATE_DOWN){
            format = R.string.correlation_dialog_down;
            positiveId = R.string.correlation_dialog_button_positive_disagree;
        } else {
            format = R.string.correlation_dialog_up;
            positiveId = R.string.correlation_dialog_button_positive_agree;
        }

        String msg = String.format(getString(format), factor, variable);

        View view = View.inflate(getActivity(), R.layout.dialog_correlation_confirm, null);
        TextView message = (TextView) view.findViewById(R.id.dialogMessage);
        message.setText(msg);

//        checkBox = (CheckBox) view.findViewById(R.id.dialogCheckbox);




        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.QAppCompatAlertDialogStyle)
                .setTitle(R.string.correlation_dialog_title)
                .setView(view)
                .setNegativeButton(R.string.correlation_dialog_button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton(positiveId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        onConfirm(correlation,state);
                    }
                });

        return builder.create();
    }

    private void onConfirm(Correlation correlation,int state){
//        SharedPreferences prefs = getActivity().getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
//        prefs.edit().putBoolean(ToolsPrefs.PREF_SHOW_CORRELATION_CONFIRM,checkBox.isChecked()).apply();
//        checkBox = null;

        Fragment target = getTargetFragment();
        if (target != null && target instanceof DialogListener){
            ((DialogListener) target).onConfirm(correlation,state);
        }
    }
}
