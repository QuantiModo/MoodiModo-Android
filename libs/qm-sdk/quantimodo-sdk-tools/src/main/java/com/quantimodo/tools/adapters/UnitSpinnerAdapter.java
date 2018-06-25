package com.quantimodo.tools.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.tools.R;
import com.quantimodo.tools.utils.ConvertUtils;

import java.util.ArrayList;

/**
 * Adapter that helps to display the units items on a Spinner
 */
public class UnitSpinnerAdapter extends ArrayAdapter<Unit> {
    LayoutInflater inflater;
    int preferredHeight;

    // The view showing the current selection. Public so that it can be modified to update the spinner with a new value
    public TextView selectedView;

    public UnitSpinnerAdapter(Context context, ArrayList<Unit> units) {
        //Creating new list, so unitAdapter wouldn't bound to unitsInCategory field
        super(context, 0, new ArrayList<>(units));
        this.inflater = LayoutInflater.from(context);
        this.preferredHeight = ConvertUtils.convertDpToPixel(48, context.getResources());

        selectedView = (TextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        selectedView.setTextColor(Color.BLACK);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        selectedView.setText(getItem(position).name);
        return selectedView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.qmt_f_tracking_measurementsunit, null);

        Unit unit = getItem(position);

        TextView tvUnitReadable = (TextView) view.findViewById(R.id.tvUnitReadable);
        tvUnitReadable.setText(unit.name);

        TextView tvUnit = (TextView) view.findViewById(R.id.tvUnit);
        tvUnit.setText(unit.abbreviatedName);

        return view;
    }
}
