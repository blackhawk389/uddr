package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.activity.FullImage;

import java.util.ArrayList;

public class ImageSlideAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;
//    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;

    public ImageSlideAdapter(Context context,ArrayList<String> images){
        this.context=context;
        this.images=images;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mRequestQueue = Volley.newRequestQueue(context);
//        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
//
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//
//            public Bitmap getBitmap(String url) {
//                return mCache.get(url);
//            }
//        });
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View imagelayout=inflater.inflate(R.layout.imageslider,container,false);
        ImageView roomthumb=imagelayout.findViewById(R.id.roomthumb);
        Picasso.with(context).load(images.get(position)).into(roomthumb);
//        roomthumb.setImageUrl(images.get(position),mImageLoader);
        container.addView(imagelayout);
        roomthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roomimage=new Intent(context, FullImage.class);
                roomimage.putExtra("imgurl",images.get(position));
                context.startActivity(roomimage);
            }
        });
        return imagelayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}

