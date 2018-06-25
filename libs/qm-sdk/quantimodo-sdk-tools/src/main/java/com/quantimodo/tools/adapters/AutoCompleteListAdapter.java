package com.quantimodo.tools.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.R;

import java.util.ArrayList;

/**
 * Adapter for TrackingFragment
 * Used to show suggestions for variables
 */
public class AutoCompleteListAdapter extends ArrayAdapter<Variable> {

    static class AutoCompleteViewHolder {
        TextView tvVariableName;
        TextView tvVariableLastUpdated;
        TextView tvCategory;
    }

    private LayoutInflater mInflater;

    public AutoCompleteListAdapter(Context context, ArrayList<Variable> data) {
        super(context, R.layout.qmt_f_tracking_autocompleterow,data);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutoCompleteViewHolder holder;
        if (convertView == null) {
            holder = new AutoCompleteViewHolder();

            convertView = mInflater.inflate(R.layout.qmt_f_tracking_autocompleterow, null);
            holder.tvVariableName = (TextView) convertView.findViewById(R.id.tvSuggestedVariableName);
            holder.tvVariableLastUpdated = (TextView) convertView.findViewById(R.id.tvSuggestedVariableLastUpdated);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tvSuggestedVariableCategory);

            convertView.setTag(holder);
        } else {
            holder = (AutoCompleteViewHolder) convertView.getTag();
        }

        Variable variable = getItem(position);

        holder.tvVariableName.setText(variable.name);
        holder.tvCategory.setText(variable.category);

        if (variable.latestMeasurementTime != null && variable.latestMeasurementTime.getTime() != 0) {
            holder.tvVariableLastUpdated.setText(DateUtils.getRelativeTimeSpanString(variable.latestMeasurementTime.getTime()));
            holder.tvVariableLastUpdated.setVisibility(View.VISIBLE);
        } else {
            holder.tvVariableLastUpdated.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
