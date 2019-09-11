package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.activity.GalleryActivity;
import com.uk.uddr.model.PhotoModel;

import java.util.ArrayList;

public class NewPhotoAdapter extends RecyclerView.Adapter<NewPhotoAdapter.MyViewHolder> {
    Context context;
    ArrayList<PhotoModel> arrayList;
    String mTitle;
    public NewPhotoAdapter(Context context, ArrayList<PhotoModel> arrayList, String title) {
        this.context = context;
        this.arrayList = arrayList;
        mTitle = title;
    }

    @NonNull
    @Override
    public NewPhotoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_photo_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPhotoAdapter.MyViewHolder holder, int position) {
        final PhotoModel model = arrayList.get(position);

        if (!model.getFilepath().equals("")) {
            Picasso.with(context).load(model.getFilepath()).into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("imagePath", model.getFilepath());
                    intent.putExtra("title", mTitle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
