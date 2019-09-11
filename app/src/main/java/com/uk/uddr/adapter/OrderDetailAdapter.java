package com.uk.uddr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.model.CartDetailModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {


    Context context;
    ArrayList<CartDetailModel> productList;

    public OrderDetailAdapter(Context context, ArrayList<CartDetailModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new OrderDetailAdapter.ViewHolder(inflater.inflate(R.layout.order_detail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder viewHolder, int i) {
        CartDetailModel model = productList.get(i);
        viewHolder.txt_title.setText(model.getTitle());
        viewHolder.txt_price.setText("£"+model.getPrice());
        viewHolder.txt_qty.setText(model.getQuantity());
        /*viewHolder.txt_pname.setText(model.getTitle());
        viewHolder.txt_pprice.setText("£"+model.getPrice());
        viewHolder.txt_pqty.setText(model.getQuantity());
        if(!model.getImg().equals("")){
            Picasso.with(context).load(model.getImg()).into(viewHolder.product_image);
        }*/
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         /*TextView txt_pname,txt_pprice,txt_pqty;
         ImageView product_image;*/
         TextView txt_qty,txt_title,txt_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_qty = itemView.findViewById(R.id.txt_qty);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_price = itemView.findViewById(R.id.txt_price);
           /* txt_pname = itemView.findViewById(R.id.txt_pname);
            txt_pprice = itemView.findViewById(R.id.txt_pprice);
            txt_pqty = itemView.findViewById(R.id.txt_pqty);
            product_image=(ImageView)itemView.findViewById(R.id.product_image);
       */
        }
    }
}


