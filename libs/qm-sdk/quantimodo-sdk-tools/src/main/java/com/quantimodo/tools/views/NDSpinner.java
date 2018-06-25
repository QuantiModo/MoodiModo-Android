package com.quantimodo.tools.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;


/**
 * Spinner extension that calls onChildSelected even when the selection is the same as its previous value
 */
public class NDSpinner extends Spinner {
    OnItemSelectedListener listener;

    public NDSpinner(Context context) {
        super(context);
    }

    public NDSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NDSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null) {
            listener.onItemSelected(null, null, position, 0);
        }
    }

    public void setOnItemSelectedEvenIfUnchangedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

}