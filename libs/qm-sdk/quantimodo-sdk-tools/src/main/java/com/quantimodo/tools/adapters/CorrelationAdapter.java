package com.quantimodo.tools.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.quantimodo.android.sdk.model.Correlation;
import com.quantimodo.android.sdk.model.CorrelationPost;
import com.quantimodo.tools.R;
import com.quantimodo.tools.sdk.DefaultSdkResponseListener;
import com.quantimodo.tools.sdk.request.VoteCorrelationRequest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Adapter class for positive/negative factor/correlations
 * Can show both types of factors, both types should be passed in constructor.
 * They would be sorted using weight of each correlation ( which basically is correlationCoefficient * userVote )
 * Can be configured with resources:
 * To hide shopping cart, add bool value <b>show_shopping_card</b>=false
 * Target activity/fragment should implement button listener check {@link CorrelationEventsListener CorrelationButtonOnClick} interface
 */
public class CorrelationAdapter extends BaseAdapter {

    @IntDef({TYPE_POSITIVE, TYPE_NEGATIVE, TYPE_ANY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CorrelationType {
    }

    @IntDef({PREDICTOR_COMMON, PREDICTOR_PRIVATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PredictorType{
    }

    @IntDef({BUTTON_SHOP, BUTTON_THUMBS_UP, BUTTON_THUMBS_DOWN, BUTTON_ADD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CorrelationButton {
    }

    @IntDef({STATE_UP, STATE_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CorrelationState {
    }

    public static final int BUTTON_SHOP = 0;
    public static final int BUTTON_THUMBS_UP = 1;
    public static final int BUTTON_THUMBS_DOWN = 2;
    public static final int BUTTON_ADD = 3;

    /**
     * Should be implemented by target activity/fragment
     */
    public interface CorrelationEventsListener {
        /**
         * Would be called when user click on one of buttons (thumbs up,thumbs down, shopping cart)
         * @param view view object ofr button
         * @param buttonType button {@link com.quantimodo.tools.adapters.CorrelationAdapter.CorrelationButton type}
         * @param position position of item in adapter
         * @param item correlation item, on which button clicked
         */
        void onClick(View view, @CorrelationButton int buttonType, int position, Correlation item);

        /**
         * Deletes a previous vote
         * @param item
         */
        void deleteVote(Correlation item);
    }

    public static final int TYPE_POSITIVE = 0;
    public static final int TYPE_NEGATIVE = 1;
    public static final int TYPE_ANY = 2;

    public static final int PREDICTOR_COMMON = 3;
    public static final int PREDICTOR_PRIVATE = 4;

    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_CANCEL = 2;

    private static final int DIFF = Integer.MAX_VALUE / 2;

    private Context mCtx;
    private ArrayList<Correlation> mAllItems = new ArrayList<>();
    private ArrayList<Correlation> mPositiveItems = new ArrayList<>();
    private ArrayList<Correlation> mNegativeItems = new ArrayList<>();
    private ArrayList<Correlation> mCurrentItems;

    private CorrelationEventsListener mButtonListener;
    private int mType = TYPE_POSITIVE;

    private boolean mShowShoppingCart;

    private boolean checkFlag(Double source, int flag) {
        if (source == null) {
            return false;
        }

        if (flag == STATE_UP) {
            return source >= 1.0d;
        } else {
            return source == 0;
        }
    }

    private int applyFlag(int source, int flag) {
        return source | flag;
    }

    /**
     * Update state of correlation
     * @param correlation correlation, which should be updated
     * @param state state
     */
    public void setState(Correlation correlation, @CorrelationState int state) {
        Correlation target = mAllItems.get(mAllItems.indexOf(correlation));
        if (state == STATE_UP) {
            target.setUserVote(1.0);
        } else {
            target.setUserVote(0d);
        }
        notifyDataSetChanged();
    }

    public CorrelationAdapter(Context ctx, ArrayList<Correlation> correlations) {
        this(ctx, correlations, TYPE_POSITIVE);
    }

    public CorrelationAdapter(Context ctx, ArrayList<Correlation> correlations, @CorrelationType int type) {
        mCtx = ctx;
        mShowShoppingCart = ctx.getResources().getBoolean(R.bool.show_shopping_card);
        //Sort correlations by correlation coefficient
//        ArrayList<Correlation> allItems = new ArrayList<>(correlations);
//        Collections.sort(allItems, new Comparator<Correlation>() {
//            @Override
//            public int compare(Correlation lhs, Correlation rhs) {
//                if (lhs.getWeight() > rhs.getWeight()) {
//                    return 1;
//                } else if (lhs.getWeight() < rhs.getWeight()) {
//                    return -1;
//                }
//                return 0;
//            }
//        });

        mAllItems = correlations;
        //Split into positive and negative correlations
//        for (Correlation c : allItems) {
//            mAllItems.add(c);
//            if (c.getCorrelationCoefficient() > 0) {
//                mPositiveItems.add(c);
//            } else if (c.getCorrelationCoefficient() < 0) {
//                mNegativeItems.add(c);
//            }
//        }
//        mAllItems = allItems;
//        Collections.reverse(mPositiveItems);
        switchItems(type);
    }

    private void switchItems(@CorrelationType int type) {
        Log.d("CorrelationAdapter", "switchItems: " + type);
        mCurrentItems = mAllItems;
//        switch (type){
//            case TYPE_POSITIVE:
//                mCurrentItems = mPositiveItems;
//                return;
//            case TYPE_NEGATIVE:
//                mCurrentItems = mNegativeItems;
//                return;
//            case TYPE_ANY:
//                mCurrentItems = mAllItems;
//        }
    }


    public CorrelationEventsListener getButtonListener() {
        return mButtonListener;
    }

    public void setButtonListener(CorrelationEventsListener mButtonListener) {
        this.mButtonListener = mButtonListener;
    }

    private void notifyButtonListener(View view, @CorrelationButton int buttonType, int position, Correlation item) {
        if (mButtonListener != null) {
            mButtonListener.onClick(view, buttonType, position, item);
        }
    }

    public int getType() {
        return mType;
    }

    /**
     * Set type of displayed correlations
     * @param mType Positive or Negative
     */
    public void setType(@CorrelationType int mType) {
        this.mType = mType;
        switchItems(mType);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCurrentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mCurrentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mType == TYPE_POSITIVE ? position : position + DIFF;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CorrelationViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mCtx, R.layout.qmt_list_item_correlation, null);
            vh = new CorrelationViewHolder(convertView);
            vh.imShoppingCart.setOnClickListener(onShoppingCartClick);
            vh.imThumbUp.setOnClickListener(onThumbUpClick);
            vh.imThumbDown.setOnClickListener(onThumbDownClick);
//            vh.imAdd.setOnClickListener(onAddClick);

            convertView.setTag(vh);
        } else {
            vh = (CorrelationViewHolder) convertView.getTag();
        }

        Correlation correlation = mCurrentItems.get(position);
        vh.mItemPosition = position;
        vh.tvCorrelationTitle.setText(correlation.getCause());
        if(TextUtils.isEmpty(correlation.getPredictorExplanation()))
            vh.tvCorrelationDesc.setVisibility(View.GONE);
        else {
            vh.tvCorrelationDesc.setVisibility(View.VISIBLE);
            vh.tvCorrelationDesc.setText(correlation.getPredictorExplanation());
        }
        if(TextUtils.isEmpty(correlation.getValuePredictingHighOutcomeExplanation()))
            vh.tvCorrelationHigh.setVisibility(View.GONE);
        else {
            vh.tvCorrelationHigh.setVisibility(View.VISIBLE);
            vh.tvCorrelationHigh.setText(correlation.getValuePredictingHighOutcomeExplanation());
        }
        if(TextUtils.isEmpty(correlation.getValuePredictingLowOutcomeExplanation()))
            vh.tvCorrelationLow.setVisibility(View.GONE);
        else {
            vh.tvCorrelationLow.setVisibility(View.VISIBLE);
            vh.tvCorrelationLow.setText(correlation.getValuePredictingLowOutcomeExplanation());
        }


        Double value = correlation.getUserVote();

        if (checkFlag(value, STATE_UP)) {
            vh.imThumbUp.setImageResource(R.drawable.ic_thumb_up_filled);
        } else {
            vh.imThumbUp.setImageResource(R.drawable.ic_thumb_up);
        }

        if (checkFlag(value, STATE_DOWN)) {
            vh.imThumbDown.setImageResource(R.drawable.ic_thumb_down_filled);
        } else {
            vh.imThumbDown.setImageResource(R.drawable.ic_thumb_down);
        }

        if (mShowShoppingCart) {
            vh.imShoppingCart.setVisibility(View.VISIBLE);
        } else {
            vh.imShoppingCart.setVisibility(View.GONE);
        }

        return convertView;
    }

    private View.OnClickListener onShoppingCartClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CorrelationViewHolder vh = (CorrelationViewHolder) ((View) v.getParent().getParent()).getTag();
            notifyButtonListener(v, BUTTON_SHOP, vh.mItemPosition, mCurrentItems.get(vh.mItemPosition));
        }
    };

    private View.OnClickListener onThumbUpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CorrelationViewHolder vh = (CorrelationViewHolder) ((View) v.getParent().getParent()).getTag();
            Correlation correlation = mCurrentItems.get(vh.mItemPosition);
            if (!checkFlag(correlation.getUserVote(), STATE_UP)) {
                notifyButtonListener(v, BUTTON_THUMBS_UP, vh.mItemPosition, correlation);
            }
            else{
                correlation.setUserVote(null);
                notifyDataSetChanged();
                mButtonListener.deleteVote(correlation);
            }
        }
    };

    private View.OnClickListener onThumbDownClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CorrelationViewHolder vh = (CorrelationViewHolder) ((View) v.getParent().getParent()).getTag();
            Correlation correlation = mCurrentItems.get(vh.mItemPosition);
            if (!checkFlag(correlation.getUserVote(), STATE_DOWN)) {
                notifyButtonListener(v, BUTTON_THUMBS_DOWN, vh.mItemPosition, correlation);
            }
            else{
                correlation.setUserVote(null);
                notifyDataSetChanged();
                mButtonListener.deleteVote(correlation);
            }
        }
    };

//    private View.OnClickListener onAddClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            CorrelationViewHolder vh = (CorrelationViewHolder) ((View) v.getParent()).getTag();
//            Correlation correlation = mCurrentItems.get(vh.mItemPosition);
//            notifyButtonListener(v, BUTTON_ADD, vh.mItemPosition, correlation);
//
//        }
//    };


    static class CorrelationViewHolder {
        TextView tvCorrelationTitle;
        TextView tvCorrelationDesc;
        TextView tvCorrelationHigh;
        TextView tvCorrelationLow;

        ImageView imThumbUp;
        ImageView imThumbDown;
        ImageView imShoppingCart;
//        ImageView imAdd;

        int mItemPosition;

        CorrelationViewHolder(View view) {
            tvCorrelationTitle = (TextView) view.findViewById(R.id.tvCorrelationTitle);
            tvCorrelationDesc = (TextView) view.findViewById(R.id.tvCorrelationDesc);
            tvCorrelationHigh = (TextView) view.findViewById(R.id.tvCorrelationHigh);
            tvCorrelationLow = (TextView) view.findViewById(R.id.tvCorrelationLow);
            imThumbUp = (ImageView) view.findViewById(R.id.imThumbUp);
            imThumbDown = (ImageView) view.findViewById(R.id.imThumbDown);
            imShoppingCart = (ImageView) view.findViewById(R.id.imShoppingCart);
//            imAdd = (ImageView) view.findViewById(R.id.imAdd);
        }
    }
}
