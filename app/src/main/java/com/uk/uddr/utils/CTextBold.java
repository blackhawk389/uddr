package com.uk.uddr.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CTextBold extends AppCompatTextView {
    public CTextBold(Context context) {
        super(context);
        init();
    }


    public CTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CTextBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        try {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/avenir_bold1.ttf");
            setTypeface(typeface);
        } catch (Exception e) {
        }
    }
}
