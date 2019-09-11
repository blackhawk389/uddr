package com.uk.uddr.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CTextregular extends AppCompatTextView {
    public CTextregular(Context context) {
        super(context);
        init();
    }

    public CTextregular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CTextregular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        try {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/avenirlight.ttf");
            setTypeface(typeface);
        } catch (Exception e) {
        }
    }
}
