package com.uk.uddr.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uk.uddr.R;
import com.uk.uddr.adapter.BigSliderAdapter;
import com.uk.uddr.fragment.ProductdetailFragment;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class FullImage extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    ImageView roomthumb;
    ImageView img_cross;
    ArrayList<String> arrayList;
    ViewPager viewPager;
    RelativeLayout rel_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        rel_back =  findViewById(R.id.rel_back);
        arrayList = new ArrayList<>();

        arrayList =  ProductdetailFragment.imagesList;
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new BigSliderAdapter(FullImage.this, arrayList));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        rel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
