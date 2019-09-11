package com.uk.uddr.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class CEditTextSemiBold extends AppCompatEditText {


    public CEditTextSemiBold(Context context) {
        super(context);
        init();
    }

    public CEditTextSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CEditTextSemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
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
