package com.uk.uddr.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CTextSemiBold extends AppCompatTextView {
    public CTextSemiBold(Context context) {
        super(context);
        init();
    }



    public CTextSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CTextSemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        try {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/avenir_medium.ttf");
            setTypeface(typeface);
        } catch (Exception e) {
        }
    }
}
