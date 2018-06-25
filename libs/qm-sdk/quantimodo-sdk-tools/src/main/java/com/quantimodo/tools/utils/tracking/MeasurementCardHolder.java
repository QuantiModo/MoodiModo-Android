package com.quantimodo.tools.utils.tracking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.activities.CustomRemindersCreateActivity;
import com.quantimodo.tools.adapters.UnitSpinnerAdapter;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.utils.ConvertUtils;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.quantimodo.tools.utils.ViewUtils;
import com.quantimodo.tools.views.NDSpinner;

import java.text.DateFormat;
import java.util.*;

public class MeasurementCardHolder {
    public Calendar selectedDate;
    public double selectedValue;
    public Unit selectedUnit;

    public final Context context;

    // Views
    public final View measurementCard;
    public final TextView tvMeasurementTimeTitle;
    public final NDSpinner spMeasurementTime;
    public final NDSpinner spMeasurementDate;
    public final Spinner spMeasurementUnit;
    public final EditText etValue;
    public final EditText etNote;
    public final NDSpinner spReminderTime;
    private final TextView reminderTitle;

    public UnitSpinnerAdapter unitAdapter;

    private ArrayList<Unit> allUnits;
    private int defaultUnitIndex;

    public OnMeasurementCardRemovedListener removedListener;

    public interface OnMeasurementCardRemovedListener {
        void onMeasurementCardRemoved(MeasurementCardHolder measurementCardHolder);
    }

    public MeasurementCardHolder(Context context) {
        measurementCard = LayoutInflater.from(context).inflate(R.layout.qmt_f_tracking_measurementcard, null);
        tvMeasurementTimeTitle = (TextView) measurementCard.findViewById(R.id.tvMeasurementsTimeTitle);
        spMeasurementDate = (NDSpinner) measurementCard.findViewById(R.id.spMeasurementDate);
        spMeasurementUnit = (Spinner) measurementCard.findViewById(R.id.spMeasurementUnit);
        etValue = (EditText) measurementCard.findViewById(R.id.etMeasurementValue);
        etNote = (EditText) measurementCard.findViewById(R.id.etNote);
        spMeasurementTime = (NDSpinner) measurementCard.findViewById(R.id.spMeasurementTime);
        spReminderTime = (NDSpinner) measurementCard.findViewById(R.id.spReminderTime);
        reminderTitle = (TextView) measurementCard.findViewById(R.id.reminder_title);

        this.context = context;
    }

    public void init(boolean removable, ArrayList<Unit> allUnits,
                     int defaultUnitIndex,TrackingFragment.CategoryDef categoryDef,
                     Double defaultValue, Variable variable) {
        this.allUnits = allUnits;
        this.defaultUnitIndex = defaultUnitIndex;

        selectedUnit = allUnits.get(defaultUnitIndex);
        if (defaultValue == null) {
            selectedValue = categoryDef.getDefaultValue();
        } else {
            selectedValue = defaultValue;
        }
        selectedDate = Calendar.getInstance();

        initOverflowButton(removable, variable);
        initDatePicker();
        initTimePicker();
        initValueEntry();
        initUnitPicker();
        initReminderTime();

        initCategory(categoryDef);
        fillSavedData(variable);
    }

    public void init(boolean removable, ArrayList<Unit> allUnits,
                     int defaultUnitIndex,TrackingFragment.CategoryDef categoryDef) {
        init(removable, allUnits, defaultUnitIndex, categoryDef, null, null);
    }

    private void fillSavedData(Variable variable){
        if(variable == null) return;
//        CustomRemindersHelper.Reminder reminder = CustomRemindersHelper.getReminder(
//                context, Long.toString(variable.getId()));
//        //load frequency
//        spReminderTime.setSelection(reminder.frequencyIndex);
    }

    private void initCategory(TrackingFragment.CategoryDef categoryDef) {
        tvMeasurementTimeTitle.setVisibility(View.GONE);
        spMeasurementDate.setVisibility(View.GONE);
        spMeasurementTime.setVisibility(View.GONE);
//        spReminderTime.setVisibility(View.GONE);
//        reminderTitle.setVisibility(View.GONE);
    }

    private void showDateTime(){
        tvMeasurementTimeTitle.setVisibility(View.VISIBLE);
        spMeasurementDate.setVisibility(View.VISIBLE);
        spMeasurementTime.setVisibility(View.VISIBLE);
//        spReminderTime.setVisibility(View.VISIBLE);
//        reminderTitle.setVisibility(View.VISIBLE);
    }

    public void hideRemindersButton(){
        measurementCard.findViewById(R.id.btReminder).setVisibility(View.GONE);
    }


    private void initOverflowButton(final boolean removable, final Variable variable) {
        // Remove button
        final ImageButton btOverflow = (ImageButton) measurementCard.findViewById(R.id.btTime);
        final ImageButton btReminder = (ImageButton) measurementCard.findViewById(R.id.btReminder);

        btOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTime();
            }
        });
        btReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(variable == null) return;
                CustomRemindersHelper.Reminder reminder = CustomRemindersHelper.getReminder(context,
                        Long.toString(variable.getId()));
                Intent intent = new Intent(context, CustomRemindersCreateActivity.class);
                if(reminder != null)
                    intent.putExtra(CustomRemindersCreateActivity.EXTRA_REMINDER_ID, reminder.id);
                else {
                    intent.putExtra(CustomRemindersCreateActivity.EXTRA_FLAG_CREATING, true);
                    intent.putExtra(CustomRemindersCreateActivity.EXTRA_VARIABLE, variable);
                }
                context.startActivity(intent);
            }
        });
        btReminder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, R.string.custom_reminders_button_info, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initDatePicker() {
        final List<String> dateOptions = Arrays.asList(context.getResources().getStringArray(R.array.measurement_dates));
        final ArrayAdapter<String> dateSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.qmt_v_simple_spinner_item, dateOptions);

        spMeasurementDate.setAdapter(dateSpinnerAdapter);
        spMeasurementDate.setSelection(1);
        spMeasurementDate.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // The bottom item is "Set date...", so we show a datepicker
                if (position == spMeasurementDate.getAdapter().getCount() - 1) {
                    // Default datepicker to today's date
                    Calendar nowCal = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            // If the date is equal to today, set the spinner to "today"
                            Calendar nowCal = Calendar.getInstance();
                            if (nowCal.get(Calendar.YEAR) == year && nowCal.get(Calendar.MONTH) == month && nowCal.get(Calendar.DAY_OF_MONTH) == day) {
                                spMeasurementDate.setSelection(1);
                                dateOptions.set(dateOptions.size() - 1, context.getString(R.string.tracking_setdate));
                                dateSpinnerAdapter.notifyDataSetChanged();
                            } else {
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, month);
                                selectedDate.set(Calendar.DAY_OF_MONTH, day);

                                DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(context);

                                dateOptions.set(dateOptions.size() - 1, dateFormat.format(selectedDate.getTime()));
                                dateSpinnerAdapter.notifyDataSetChanged();
                            }
                        }
                    }, nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DAY_OF_MONTH));

                    // Limit the date picker to three days in advance
                    nowCal.add(Calendar.DAY_OF_YEAR, 3);
                    dialog.getDatePicker().setMaxDate(nowCal.getTimeInMillis());

                    dialog.show();
                } else if (position == 0)  // "Yesterday"
                {
                    Calendar nowCal = Calendar.getInstance();
                    nowCal.add(Calendar.DAY_OF_YEAR, -1);   // Subtract one day
                    selectedDate.set(Calendar.YEAR, nowCal.get(Calendar.YEAR));
                    selectedDate.set(Calendar.MONTH, nowCal.get(Calendar.MONTH));
                    selectedDate.set(Calendar.DAY_OF_MONTH, nowCal.get(Calendar.DAY_OF_MONTH));
                } else if (position == 1)  // "Today"
                {
                    Calendar nowCal = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, nowCal.get(Calendar.YEAR));
                    selectedDate.set(Calendar.MONTH, nowCal.get(Calendar.MONTH));
                    selectedDate.set(Calendar.DAY_OF_MONTH, nowCal.get(Calendar.DAY_OF_MONTH));
                } else if (position == 2)  // "Tomorrow"
                {
                    Calendar nowCal = Calendar.getInstance();
                    nowCal.add(Calendar.DAY_OF_YEAR, 1);   // Add one day
                    selectedDate.set(Calendar.YEAR, nowCal.get(Calendar.YEAR));
                    selectedDate.set(Calendar.MONTH, nowCal.get(Calendar.MONTH));
                    selectedDate.set(Calendar.DAY_OF_MONTH, nowCal.get(Calendar.DAY_OF_MONTH));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initTimePicker() {
        final List<String> timeOptions = Arrays.asList(context.getResources().getStringArray(R.array.measurement_times));
        final ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.qmt_v_simple_spinner_item, timeOptions);

        spMeasurementTime.setAdapter(timeSpinnerAdapter);
        spMeasurementTime.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // The bottom item is "Set time...", so we show a timepicker
                if (i == spMeasurementTime.getAdapter().getCount() - 1) {
                    final Calendar calendar = Calendar.getInstance();
                    final int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int nowMinute = calendar.get(Calendar.MINUTE);
                    TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            // If we picked "now" set the spinner to the position of the "Just now" entry
                            if (nowHour == hour && nowMinute == minute) {
                                spMeasurementDate.setSelection(0);
                                timeOptions.set(timeOptions.size() - 1, context.getString(R.string.tracking_settime));
                                timeSpinnerAdapter.notifyDataSetChanged();
                            } else {
                                selectedDate.set(Calendar.HOUR_OF_DAY, hour);
                                selectedDate.set(Calendar.MINUTE, minute);

                                DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);

                                timeOptions.set(timeOptions.size() - 1, dateFormat.format(selectedDate.getTime()));
                                timeSpinnerAdapter.notifyDataSetChanged();
                            }
                        }
                    }, nowHour, nowMinute, android.text.format.DateFormat.is24HourFormat(context));
                    dialog.show();
                } else    // "Just now" selected"
                {
                    Calendar nowCal = Calendar.getInstance();

                    selectedDate.set(Calendar.HOUR_OF_DAY, nowCal.get(Calendar.HOUR_OF_DAY));
                    selectedDate.set(Calendar.MINUTE, nowCal.get(Calendar.MINUTE));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initValueEntry() {
        if (!Double.isNaN(selectedValue)){
            etValue.setText(String.format("%.1f",selectedValue));
        }

        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    // Make sure this is a double
                    selectedValue = Double.valueOf(editable.toString());
                    etValue.setTextColor(Color.BLACK);
                    etValue.setHintTextColor(Color.BLACK);
                } catch (NumberFormatException e) {
                    // Otherwise set NaN and alert the user
                    selectedValue = Double.NaN;
                    etValue.setTextColor(Color.RED);
                    etValue.setHintTextColor(Color.RED);
                }
            }
        });

        etValue.clearFocus();
        etValue.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//            if(!inputMethodManager.showisActive())
//                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputMethodManager.showSoftInput(etValue, InputMethodManager.SHOW_IMPLICIT);
    }

    private void initUnitPicker() {
        unitAdapter = new UnitSpinnerAdapter(context, allUnits);
        spMeasurementUnit.setAdapter(unitAdapter);
        spMeasurementUnit.setSelection(defaultUnitIndex);
        spMeasurementUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUnit = allUnits.get(i);
                Log.i(ToolsPrefs.DEBUG_TAG, "Selected unit: " + selectedUnit.abbreviatedName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initReminderTime(){
        final List<String> timeOptions = Arrays.asList(context.getResources().getStringArray(R.array.mood_interval_entries));
        final ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.qmt_v_simple_spinner_item, timeOptions);

        spReminderTime.setAdapter(timeSpinnerAdapter);
    }

    public void setOnRemovedListener(OnMeasurementCardRemovedListener listener) {
        removedListener = listener;
    }

    public void remove(final OnMeasurementCardRemovedListener listener) {
        ViewUtils.collapseView(measurementCard, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (listener != null) {
                    listener.onMeasurementCardRemoved(MeasurementCardHolder.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
