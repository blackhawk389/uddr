package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.activity.PartnerProfileActivity;
import com.uk.uddr.model.SearchPartnersModel;
import com.uk.uddr.utils.Utils;

import java.util.ArrayList;

public class SearchPartnerAdapter extends RecyclerView.Adapter<SearchPartnerAdapter.MyViewHolder>{


    Context context;
    ArrayList<SearchPartnersModel> partnersList;
    SharedPreferences sharedPreferences;

    public SearchPartnerAdapter(Context context,ArrayList<SearchPartnersModel> partnersList){
        this.context=context;
        this.partnersList=partnersList;
        sharedPreferences=context.getSharedPreferences(Utils.sharedfilename,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(viewGroup.getContext());
        return new MyViewHolder(layoutInflater.inflate(R.layout.searchpartnerlook,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
           final SearchPartnersModel spm=partnersList.get(i);
           myViewHolder.txt_storename.setText(spm.getStore_name());
           myViewHolder.txt_address.setText(spm.getStore_address());
           if (!spm.getStore_image().equals("")) {
                Picasso.with(context).load(spm.getStore_image()).into(myViewHolder.img_store);
           }
           myViewHolder.txt_reviews.setText("("+spm.getReview_count()+ "Reviews)");
           myViewHolder.txt_stype.setText(spm.getStore_type());
           myViewHolder.review_ratingBar.setRating(Float.valueOf(spm.getStore_rating()));
           myViewHolder.rel_main.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, PartnerProfileActivity.class);
                   intent.putExtra("store_id", spm.getStore_id());
                   intent.putExtra("store_name", spm.getStore_name());
                   intent.putExtra("store_type_id",spm.getStore_type_id());
                   intent.putExtra("store_type",spm.getStore_type());
                   context.startActivity(intent);
               }
           });
    }

    @Override
    public int getItemCount() {
        return partnersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_store;
        TextView txt_storename,txt_address,txt_reviews,txt_stype;
        RatingBar review_ratingBar;
        RelativeLayout rel_main;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_store=(ImageView)itemView.findViewById(R.id.img_store);
            txt_storename=(TextView)itemView.findViewById(R.id.txt_stitle);
            txt_reviews=(TextView)itemView.findViewById(R.id.txt_reviews);
            txt_stype=(TextView)itemView.findViewById(R.id.txt_stype);
            txt_address=(TextView)itemView.findViewById(R.id.txt_address);
            rel_main=(RelativeLayout)itemView.findViewById(R.id.rel_main);
            review_ratingBar=(RatingBar)itemView.findViewById(R.id.review_ratingBar);
        }
    }
}
