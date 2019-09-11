package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.activity.ZipCode;
import com.uk.uddr.fragment.PartnerFragment;
import com.uk.uddr.model.SearchModelCategory;
import com.uk.uddr.utils.Utils;

import java.util.ArrayList;

import io.apptik.widget.Util;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{


    ArrayList<SearchModelCategory> categoriesList;
    Context context;
    SharedPreferences sharedPreferences;

    public SearchAdapter(Context context,ArrayList<SearchModelCategory> searchModelCategories){
        this.context=context;
        this.categoriesList=searchModelCategories;
        sharedPreferences=context.getSharedPreferences(Utils.sharedfilename,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(viewGroup.getContext());
        return new MyViewHolder(layoutInflater.inflate(R.layout.searchcategorylook,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final SearchModelCategory smc=categoriesList.get(i);
        if (!smc.getType_image().equals("")) {
            Picasso.with(context).load(smc.getType_image()).into(myViewHolder.img_category);
        }
        myViewHolder.txt_catname.setText(smc.getTitle());
        myViewHolder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zip = sharedPreferences.getString(Utils.zipcode, "");
                if (zip.equals("")) {
                    Intent loca = new Intent(context, ZipCode.class);
                    context.startActivity(loca);
                } else {
                    Fragment fragment = new PartnerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("store_type_id", smc.getId());
                    bundle.putString("store_image", smc.getType_image());
                    bundle.putString("store_type", smc.getTitle());
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit(); // save the changes
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_category;
        RelativeLayout rel_main;
        TextView txt_catname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category=(ImageView)itemView.findViewById(R.id.img_category);
            rel_main=(RelativeLayout)itemView.findViewById(R.id.rel_main);
            txt_catname=(TextView)itemView.findViewById(R.id.txt_category);
        }
    }
}
