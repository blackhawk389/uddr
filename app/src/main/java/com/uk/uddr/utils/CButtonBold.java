package com.uk.uddr.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class CButtonBold extends AppCompatButton {

    public CButtonBold(Context context) {
        super(context);
        init();
    }

    public CButtonBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CButtonBold(Context context, AttributeSet attrs, int defStyleAttr) {
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
