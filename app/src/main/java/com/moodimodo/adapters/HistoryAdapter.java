package com.moodimodo.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.quantimodo.tools.models.Measurement;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<Measurement>
{

	private Context mCtx;
	private Drawable[] mMoodIcons;
    private Drawable[] mNumbers;
	private String[] mMoodTitles;

	class ViewHolder
    {
		@InjectView(R.id.tvTimestamp)
        TextView tvMood;
		@InjectView(R.id.tvMood)
        TextView tvTimestamp;
		@InjectView(R.id.imMoodIcon)
        ImageView imMoodIcon;
    }

    public HistoryAdapter(Context mCtx, ArrayList<Measurement> moodThings)
    {
        super(mCtx, R.layout.activity_moodlist_row, new ArrayList<>(moodThings));
        this.mCtx = mCtx;

		Resources res = mCtx.getResources();
		if(mMoodIcons == null) {
			mMoodIcons = new Drawable[5];
			mMoodIcons[0] = res.getDrawable(R.drawable.ic_mood_depressed);
			mMoodIcons[1] = res.getDrawable(R.drawable.ic_mood_sad);
			mMoodIcons[2] = res.getDrawable(R.drawable.ic_mood_ok);
			mMoodIcons[3] = res.getDrawable(R.drawable.ic_mood_happy);
			mMoodIcons[4] = res.getDrawable(R.drawable.ic_mood_ecstatic);
		}

        if (mNumbers == null){
            mNumbers = new Drawable[5];
            mNumbers[0] = res.getDrawable(R.drawable.ic_mood_pos_1);
            mNumbers[1] = res.getDrawable(R.drawable.ic_mood_pos_2);
            mNumbers[2] = res.getDrawable(R.drawable.ic_mood_3);
            mNumbers[3] = res.getDrawable(R.drawable.ic_mood_pos_4);
            mNumbers[4] = res.getDrawable(R.drawable.ic_mood_pos_5);
        }

		if(mMoodTitles == null) {
			mMoodTitles = res.getStringArray(R.array.moods);
		}
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mCtx,R.layout.activity_moodlist_row, null);
			ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Measurement entry = getItem(position);

        if (entry.getVariableName().equals(BuildConfig.OUTCOME_VARIABLE)) {
            holder.imMoodIcon.setImageDrawable(mMoodIcons[((int) (entry.getValue() - 1))]);
        } else {
            holder.imMoodIcon.setImageDrawable(mNumbers[((int) (entry.getValue() - 1))]);
        }

        String note = entry.getNote();
        note = note == null || note.isEmpty() ? "" : ",  " + note;
        holder.tvMood.setText(String.format("%s/5 %s", (int)Math.round(entry.getValue()),entry.getVariableName()));
        holder.tvTimestamp.setText(Global.DEFAULT_MOOD_DATE_FORMATTER.format(entry.getTimestamp()) + note);

        return convertView;
    }

}
