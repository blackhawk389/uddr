package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.ProductDetail;
import com.uk.uddr.fragment.CartFragment;
import com.uk.uddr.model.CartModel;
import com.uk.uddr.utils.DBHelper;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {


    Context context;
    ArrayList<CartModel> arrayList;
    DBHelper dbHelper;
    String store_id;

    public CartAdapter(Context context, ArrayList<CartModel> arrayList,String store_id) {
        this.context = context;
        this.arrayList = arrayList;
        this.store_id=store_id;
        dbHelper=new DBHelper(context);
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new CartAdapter.ViewHolder(inflater.inflate(R.layout.order_detail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.ViewHolder viewHolder, final int i) {
        final CartModel model = arrayList.get(i);
        viewHolder.txt_title.setText(model.getTitle());
        viewHolder.txt_price.setText("£"+model.getPrice());
        viewHolder.txt_qty.setText(model.getCount());
        viewHolder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetail=new Intent(context, ProductDetail.class);
                productDetail.putExtra("product_id",model.getId());
                productDetail.putExtra("store_id",store_id);
                context.startActivity(productDetail);
            }
        });
  /*      viewHolder.txt_pname.setText(model.getTitle());
        //viewHolder.txt_pdesc.setText(model.getText());
        viewHolder.txt_count.setText(model.getCount());
        if(!model.getImage().equals("")) {
            Picasso.with(context).load(model.getImage()).into(viewHolder.img_pimage);
        }
        viewHolder.txt_pprice.setText("£"+model.getPrice());
        viewHolder.img_min.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int count = Integer.parseInt(model.getCount());
            if(count>1){
                count--;
                model.setCount(count+"");
                viewHolder.txt_count.setText(model.getCount());
                Double dp = Double.parseDouble(model.getPrice());
                int c= Integer.parseInt(model.getCount());
                int p=dp.intValue();
                if(dbHelper.checkproduct(model.getId())>0){
                    dbHelper.updateproduct(model.getId(),model.getPrice(),model.getCount());
                }else{
                    dbHelper.addproduct(model.getId(),model.getTitle(),model.getImage(),model.getPrice(),model.getCount(),store_id,"storename",model.getText());
                }
            }else if(count==1){
                count--;
                model.setCount(count+"");
                viewHolder.txt_count.setText(model.getCount());
                if(dbHelper.checkproduct(model.getId())>0){
                    dbHelper.updateproduct(model.getId(),model.getPrice(),model.getCount());
                    dbHelper.deleteproduct(model.getId());
                }else{
                    dbHelper.deleteproduct(model.getId());
                }
                arrayList.remove(i);
                notifyDataSetChanged();
            }
            CartFragment.updatevalue();
            HomeActivity.updatecart();

        }
    });

        viewHolder.img_max.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int count = Integer.parseInt(model.getCount());
            count++;
            model.setCount(count+"");
            viewHolder.txt_count.setText(model.getCount());
            Double dp = Double.parseDouble(model.getPrice());
            int c= Integer.parseInt(model.getCount());
            int p=dp.intValue();

            if(dbHelper.checkproduct(model.getId())>0){
                dbHelper.updateproduct(model.getId(),model.getPrice(),model.getCount());
            }else{
                dbHelper.addproduct(model.getId(),model.getTitle(),model.getImage(),model.getPrice(),model.getCount(),store_id,"storename",model.getText());
            }
            CartFragment.updatevalue();

        }
    });*/


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*ImageView img_pimage;
        TextView txt_count,txt_pname,txt_pprice;
        RelativeLayout img_min,img_max;*/
        TextView txt_qty,txt_title,txt_price;
        RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_qty = itemView.findViewById(R.id.txt_qty);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_price = itemView.findViewById(R.id.txt_price);
            rel_main=itemView.findViewById(R.id.rel_main);
            /*img_min=(RelativeLayout) itemView.findViewById(R.id.relative_minus);
            img_max=(RelativeLayout) itemView.findViewById(R.id.relative_plus);
            img_pimage=(ImageView)itemView.findViewById(R.id.img_pimage);
            txt_pprice=(TextView)itemView.findViewById(R.id.txt_pprice);
            txt_count=(TextView)itemView.findViewById(R.id.txt_count);
            txt_pname=(TextView)itemView.findViewById(R.id.txt_pname);
           // txt_pdesc=(TextView)itemView.findViewById(R.id.txt_pdesc);
*/

        }
    }
}

