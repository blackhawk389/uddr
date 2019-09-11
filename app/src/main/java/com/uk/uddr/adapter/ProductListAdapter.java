package com.uk.uddr.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.LoginActivity;
import com.uk.uddr.activity.ProductDetail;
import com.uk.uddr.fragment.HomeFragment;
import com.uk.uddr.fragment.ProductListFragment;
import com.uk.uddr.fragment.ProductdetailFragment;
import com.uk.uddr.model.ProductListModel;
import com.uk.uddr.utils.DBHelper;
import com.devspark.appmsg.AppMsg;
import com.squareup.picasso.Picasso;
import com.uk.uddr.utils.Utils;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductListModel> arrayList;
    DBHelper dbHelper;
    SharedPreferences sharedPreferences;
    Button btn_viewbasket;


    public ProductListAdapter(Context context, ArrayList<ProductListModel> arrayList,Button btn_viewbasket) {
        this.context = context;
        this.arrayList = arrayList;
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
        dbHelper = new DBHelper(context);
        this.btn_viewbasket=btn_viewbasket;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ProductListAdapter.ViewHolder(inflater.inflate(R.layout.item_productlist, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final ProductListModel model = arrayList.get(i);
        viewHolder.product_name.setText(model.getProduct_name());
        //viewHolder.product_description.setText(model.getProduct_description());
        viewHolder.product_price.setText("£" + model.getProduct_price());
        viewHolder.txt_count.setText(model.getCount());
        if (Integer.parseInt(model.getCount()) > 0) {
            viewHolder.txt_add.setVisibility(View.GONE);
            viewHolder.rel_btn.setVisibility(View.VISIBLE);
        }
        viewHolder.img_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(model.getCount());
                if (count > 1) {
                    count--;
                    model.setCount(count + "");
                    viewHolder.txt_count.setText(model.getCount());
                    Double dp = Double.parseDouble(model.getProduct_price());
                    int c = Integer.parseInt(model.getCount());
                    int p = dp.intValue();
                    if (dbHelper.checkproduct(model.getProduct_id()) > 0) {
                        dbHelper.updateproduct(model.getProduct_id(), model.getProduct_price(), model.getCount());
                    } else {
                        dbHelper.addproduct(model.getProduct_id(), model.getProduct_name(), model.getProduct_image(), model.getProduct_price(), model.getCount(), model.getStore_id(), "storename", model.getProduct_description());
                    }
                } else if (count == 1) {
                    count--;
                    model.setCount(count + "");
                    viewHolder.txt_count.setText(model.getCount());
                    viewHolder.txt_add.setVisibility(View.VISIBLE);
                    viewHolder.rel_btn.setVisibility(View.GONE);
                    if (dbHelper.checkproduct(model.getProduct_id()) > 0) {
                        dbHelper.updateproduct(model.getProduct_id(), model.getProduct_price(), model.getCount());
                        dbHelper.deleteproduct(model.getProduct_id());
                    } else {
                        dbHelper.deleteproduct(model.getProduct_id());
                    }
                }
                HomeActivity.updatecart();
                basketbutton();

            }
        });

        viewHolder.img_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = Integer.parseInt(model.getCount());
                count++;
                model.setCount(count + "");
                viewHolder.txt_count.setText(model.getCount());
                Double dp = Double.parseDouble(model.getProduct_price());
                int c = Integer.parseInt(model.getCount());
                int p = dp.intValue();

                if (dbHelper.checkproduct(model.getProduct_id()) > 0) {
                    dbHelper.updateproduct(model.getProduct_id(), model.getProduct_price(), model.getCount());
                } else {
                    dbHelper.addproduct(model.getProduct_id(), model.getProduct_name(), model.getProduct_image(), model.getProduct_price(), model.getCount(), model.getStore_id(), "storename", model.getProduct_description());
                }

                HomeActivity.updatecart();
                basketbutton();
            }
        });

        viewHolder.txt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("check_storeid", dbHelper.getStoreId() + " - " + model.getStore_id());
                if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
                    Intent login = new Intent(context, LoginActivity.class);
                    context.startActivity(login);
                } else {
                    if (dbHelper.getStoreId().equals(model.getStore_id()) || dbHelper.getStoreId().equals("-1")) {
                        AppMsg.makeText((Activity) context, "Product Added to Basket", AppMsg.STYLE_INFO).show();
                        model.setCount("1");
                        viewHolder.txt_count.setText(model.getCount());
                        viewHolder.txt_add.setVisibility(View.GONE);
                        viewHolder.rel_btn.setVisibility(View.VISIBLE);
                        if (dbHelper.checkproduct(model.getProduct_id()) > 0) {
                            dbHelper.updateproduct(model.getProduct_id(), model.getProduct_price(), "1");
                        } else {
                            dbHelper.addproduct(model.getProduct_id(), model.getProduct_name(), model.getProduct_image(), model.getProduct_price(), "1", model.getStore_id(), "storename", model.getProduct_description());
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Alert");
                        builder.setMessage(context.getResources().getString(R.string.store_error));
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                dbHelper.clearDB();
                                AppMsg.makeText((Activity) context, "Product Added to Basket", AppMsg.STYLE_INFO).show();
                                model.setCount("1");
                                viewHolder.txt_count.setText(model.getCount());
                                viewHolder.txt_add.setVisibility(View.GONE);
                                viewHolder.rel_btn.setVisibility(View.VISIBLE);
                                if (dbHelper.checkproduct(model.getProduct_id()) > 0) {
                                    dbHelper.updateproduct(model.getProduct_id(), model.getProduct_price(), "1");
                                } else {
                                    dbHelper.addproduct(model.getProduct_id(), model.getProduct_name(), model.getProduct_image(), model.getProduct_price(), "1", model.getStore_id(), "storename", model.getProduct_description());
                                }
                                HomeActivity.updatecart();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                    HomeActivity.updatecart();

                    basketbutton();
                }

            }
        });


        viewHolder.product_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetail=new Intent(context, ProductDetail.class);
                productDetail.putExtra("product_id",model.getProduct_id());
                productDetail.putExtra("store_id",model.getStore_id());
                context.startActivity(productDetail);
            }
        });

    }


    void basketbutton(){
        Cursor cursor=dbHelper.selectallproduct();
        if(cursor.getCount()>0){
            btn_viewbasket.setVisibility(View.VISIBLE);
        }else{
            btn_viewbasket.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView product_name, product_price, txt_count, txt_add,product_more;
       // ImageView product_image;
        RelativeLayout rel_btn, img_min, img_max;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_price = (TextView) itemView.findViewById(R.id.selling_price1);
            //product_description = (TextView) itemView.findViewById(R.id.product_description);
            //product_image = (ImageView) itemView.findViewById(R.id.product_image);
            rel_btn = (RelativeLayout) itemView.findViewById(R.id.rel_button);
            txt_add = (TextView) itemView.findViewById(R.id.txt_add1);
            img_min = (RelativeLayout) itemView.findViewById(R.id.relative_minus);
            img_max = (RelativeLayout) itemView.findViewById(R.id.relative_plus);
            txt_count = (TextView) itemView.findViewById(R.id.txt_count);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            product_more = (TextView) itemView.findViewById(R.id.product_more);
        }
    }
}
