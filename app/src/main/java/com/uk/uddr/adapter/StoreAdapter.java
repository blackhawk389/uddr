package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uk.uddr.R;
import com.uk.uddr.activity.PartnerProfileActivity;
import com.uk.uddr.fragment.PartnerprofileFragment;
import com.uk.uddr.fragment.StoreDetailFragment;
import com.uk.uddr.model.StoreModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {


    Context context;
    ArrayList<StoreModel> arrayList;

    public StoreAdapter(Context context, ArrayList<StoreModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new StoreAdapter.ViewHolder(inflater.inflate(R.layout.item_store, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder viewHolder, int i) {

        final StoreModel model = arrayList.get(i);
        viewHolder.store_name.setText(model.getStore_name());
        viewHolder.store_distance.setText(model.getStore_distance() + " miles away");
        if (!model.getStore_image().equals("")) {
            Picasso.with(context).load(model.getStore_image()).into(viewHolder.store_image);
        }
        viewHolder.car_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(model.getTotal_cat()) > 0) {
//                    Intent productlist = new Intent(context, ProductActivity.class);
//                    productlist.putExtra("store_id",model.getStore_id());
//                    productlist.putExtra("store_name",model.getStore_name());
//                    productlist.putExtra("store_address",model.getStore_address());
//                    productlist.putExtra("store_image",model.getStore_image());
//                    productlist.putExtra("store_logo",model.getStore_logo());
//                    context.startActivity(productlist);
                  /*  Fragment fragment = new PartnerprofileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("store_id", model.getStore_id());
                    bundle.putString("store_type_id", model.getStore_type_id());
                    bundle.putString("store_type", model.getstore_type());
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes*/

                    Intent intent = new Intent(context, PartnerProfileActivity.class);
                    intent.putExtra("store_id", model.getStore_id());
                    intent.putExtra("store_name", model.getStore_name());
                    intent.putExtra("store_type_id",model.getStore_type_id());
                    intent.putExtra("store_type",model.getStore_type());
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "No Products available in this store", Toast.LENGTH_SHORT).show();
                }


            }
        });
        viewHolder.store_address.setText(model.getStore_address());
        viewHolder.store_type.setText(model.getstore_type());
        viewHolder.review_ratingBar.setRating(Float.valueOf(model.getStore_rating()));
        viewHolder.txt_review.setText(model.getReview_count()+ " Reviews");


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //RelativeLayout btn_click;

        TextView store_name, store_distance, store_type, store_address,txt_review;
        ImageView store_image;
        CardView car_main;
        RatingBar review_ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            store_name = itemView.findViewById(R.id.store_name);
            store_image = itemView.findViewById(R.id.store_image);
            car_main = itemView.findViewById(R.id.cardView);
            store_distance = itemView.findViewById(R.id.store_distance);
            store_type = itemView.findViewById(R.id.store_type);
            store_address = itemView.findViewById(R.id.store_address);
            txt_review = itemView.findViewById(R.id.txt_review);
            review_ratingBar = itemView.findViewById(R.id.review_ratingBar);

        }
    }
}

