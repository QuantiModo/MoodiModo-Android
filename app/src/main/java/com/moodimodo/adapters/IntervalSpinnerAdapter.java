package com.moodimodo.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.fragments.WelcomeFragment;

public class IntervalSpinnerAdapter extends ArrayAdapter<String>
{
    private WelcomeFragment welcomeFragment;
    LayoutInflater inflater;
    int height;

    public IntervalSpinnerAdapter(WelcomeFragment welcomeFragment, Context context)
    {
        super(context, 0, welcomeFragment.getResources().getStringArray(R.array.mood_interval_entries));
        this.welcomeFragment = welcomeFragment;

        this.inflater = LayoutInflater.from(context);
        this.height = Utils.convertDpToPixel(48, welcomeFragment.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView tvLabel = new TextView(getContext());

        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvLabel.setTextColor(welcomeFragment.getResources().getColor(android.R.color.black));
        tvLabel.setText(getItem(position));

        return tvLabel;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        TextView tvLabel = new TextView(getContext());

        tvLabel.setMinimumHeight(height);

        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvLabel.setTextColor(welcomeFragment.getResources().getColor(android.R.color.black));
        tvLabel.setText(getItem(position));

        return tvLabel;
    }
}
