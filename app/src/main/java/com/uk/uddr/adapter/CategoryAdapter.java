package com.uk.uddr.adapter;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.fragment.ProductListFragment;
import com.uk.uddr.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<CategoryModel> catList;
    String store_id, store_type_id;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> catList, String store_id, String store_type_id) {
        this.context = context;
        this.catList = catList;
        this.store_id = store_id;
        this.store_type_id = store_type_id;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //ayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        //return new StoreAdapter.ViewHolder(inflater.inflate(R.layout.item_store, viewGroup, false));
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MyViewHolder(inflater.inflate(R.layout.categories_look, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final CategoryModel categoryModel = catList.get(i);
        myViewHolder.txt_catname.setText(categoryModel.getCat_title());
        myViewHolder.rel_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProductListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("store_id", store_id);
                bundle.putString("store_type_id", store_type_id);
                bundle.putString("cat_id", categoryModel.getCat_id());
                bundle.putString("cat_name", categoryModel.getCat_title());
                bundle.putString("cat_image", categoryModel.getCat_image());
                fragment.setArguments(bundle);
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit(); // save the changes
            }
        });
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rel_cat;
        TextView txt_catname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rel_cat = (RelativeLayout) itemView.findViewById(R.id.rel_cat);
            txt_catname = (TextView) itemView.findViewById(R.id.category_name);
        }
    }
}
