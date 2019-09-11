package com.uk.uddr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.activity.ZipCode;
import com.uk.uddr.fragment.PartnerFragment;
import com.uk.uddr.model.HomeModel;
import com.squareup.picasso.Picasso;
import com.uk.uddr.utils.Utils;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {


    Context context;
    ArrayList<HomeModel> arrayList;
    SharedPreferences sharedPreferences;

    public HomeAdapter(Context context, ArrayList<HomeModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ViewHolder(inflater.inflate(R.layout.home_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final HomeModel model = arrayList.get(i);

        viewHolder.title.setText(model.getTitle());
        viewHolder.txt_tag_line.setText(model.getTag_line());
        viewHolder.txt_des.setText(model.getDes());
        if (!model.getType_image().equals("")) {
            Picasso.with(context).load(model.getType_image()).into(viewHolder.type_image);
        }
        viewHolder.card_storetype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String zip = sharedPreferences.getString(Utils.zipcode, "");
                if (zip.equals("")) {
                    Intent loca = new Intent(context, ZipCode.class);
                    context.startActivity(loca);
                } else {
                    Fragment fragment = new PartnerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("store_type_id", model.getId());
                    bundle.putString("store_image", model.getType_image());
                    bundle.putString("store_type", model.getTitle());
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit(); // save the changes
                }


            }
        });

//        Screensize();
//        viewHolder.rel_main.getLayoutParams().width= ((int)(Screensize()));
    }

    float Screensize(){
//        DisplayMetrics dm = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
//        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
//        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
//        double screenInches = Math.sqrt(x + y) * dm.scaledDensity;
        Display display =  ((Activity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = context.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
        Log.d("debug", "Screen inches : " + dpHeight+" - "+dpWidth);
        return dpWidth;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,txt_rating,txt_des,txt_tag_line;
        ImageView type_image;
        RatingBar ratingBar;
        CardView card_storetype;
        RelativeLayout rel_main;

        // Button btn_shopnow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_rating = itemView.findViewById(R.id.txt_rating);
            title = itemView.findViewById(R.id.title);
            txt_des = itemView.findViewById(R.id.txt_des);
            txt_tag_line = itemView.findViewById(R.id.txt_tag_line);
            card_storetype = itemView.findViewById(R.id.card_storetype);
            rel_main=itemView.findViewById(R.id.rel_main);
            //btn_shopnow=itemView.findViewById(R.id.btn_shopnow);
            type_image = itemView.findViewById(R.id.type_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }
    }
}

