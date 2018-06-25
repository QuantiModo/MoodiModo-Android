package com.quantimodo.tools.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quantimodo.android.sdk.model.VariableCategory;
import com.quantimodo.tools.R;

import java.util.List;

/**
 * Used in TrackingFragment to show category
 */
public class VariableCategorySelectSpinnerAdapter extends ArrayAdapter<VariableCategory> {
    private LayoutInflater inflater;
    private TextView selectedView;
    private List<VariableCategory> categories;

    public VariableCategorySelectSpinnerAdapter(Context context, List<VariableCategory> categories) {
        super(context, android.R.layout.simple_spinner_dropdown_item, categories);
        this.inflater = LayoutInflater.from(context);

        this.categories = categories;
        selectedView = (TextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        selectedView.setTextColor(Color.BLACK);
    }

    public int getPosition(String category) {
        int result = -1;

        int n = categories.size();
        for (int i=0; i<n; i++){
            if (categories.get(i).getName().equals(category)){
                return i;
            }
        }

        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        selectedView.setText(getItem(position).getName());
        return selectedView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.qmt_f_tracking_measurementsunit, null);

        VariableCategory category = getItem(position);

        TextView tvUnitReadable = (TextView) view.findViewById(R.id.tvUnitReadable);
        tvUnitReadable.setText(category.name);

        TextView tvUnit = (TextView) view.findViewById(R.id.tvUnit);
        tvUnit.setText("");

        return view;
    }
}
