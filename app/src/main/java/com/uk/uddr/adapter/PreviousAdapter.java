package com.uk.uddr.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.activity.Basket;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.OrderDetailActivity;
import com.uk.uddr.fragment.CartFragment;
import com.uk.uddr.model.PreviousModel;
import com.squareup.picasso.Picasso;
import com.uk.uddr.utils.DBHelper;

import java.util.ArrayList;

public class PreviousAdapter  extends RecyclerView.Adapter<PreviousAdapter.ViewHolder> {

    Context context;
    ArrayList<PreviousModel> orderList;
    String selectedId="";
    DBHelper dbHelper;

    public PreviousAdapter(Context context, ArrayList<PreviousModel> orderList) {
        this.context = context;
        this.orderList = orderList;
        dbHelper=new DBHelper(context);
    }

    @NonNull
    @Override
    public PreviousAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new PreviousAdapter.ViewHolder(inflater.inflate(R.layout.item_previous, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull PreviousAdapter.ViewHolder viewHolder, int i) {
        final PreviousModel pm=orderList.get(i);
        viewHolder.txt_orderId.setText("Order : #"+pm.getTransactionID());
        //viewHolder.txt_orderName.setText(pm.getTitle());
        viewHolder.txt_orderCost.setText("Total: £"+pm.getTotal());
        //viewHolder.btn_orderStatus.setText(pm.getPaymentStatus());
        viewHolder.txt_orderstatus.setText(pm.getPaymentStatus());
        viewHolder.txt_orderdate.setText(pm.getOrder_date()+" "+pm.getOrder_time());
        if(!pm.getType_image().equals("")){
            Picasso.with(context).load(pm.getType_image()).into(viewHolder.img_logo);
        }
        viewHolder.btn_reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearDB();
                Intent basket = new Intent(context, Basket.class);
                basket.putExtra("t_id",pm.getTransactionID());
                context.startActivity(basket);
               /* CartFragment cartFragment=new CartFragment();
                Bundle bundle = new Bundle();
                bundle.putString("t_id", pm.getTransactionID());
                cartFragment.setArguments(bundle);
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, cartFragment);
                fragmentTransaction.commit(); // save the changes
                HomeActivity.changeicon(3);*/
            }
        });
        viewHolder.rel_orderdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetail=new Intent(context, OrderDetailActivity.class);
                orderDetail.putExtra("t_id",pm.getTransactionID());
                context.startActivity(orderDetail);
            }
        });
        viewHolder.img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetail=new Intent(context, OrderDetailActivity.class);
                orderDetail.putExtra("t_id",pm.getTransactionID());
                context.startActivity(orderDetail);
            }
        });

        viewHolder.rel_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+pm.getStore_phone()));
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_logo;
        TextView txt_orderId,txt_orderCost,txt_orderstatus,txt_orderdate;
        //Button btn_orderStatus;
        Button btn_reorder;
        RelativeLayout rel_orderdetail,rel_help;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_logo=(ImageView)itemView.findViewById(R.id.img_logo);
            txt_orderId=(TextView)itemView.findViewById(R.id.txt_orderID);
            txt_orderCost=(TextView)itemView.findViewById(R.id.txt_total);
            txt_orderdate=(TextView)itemView.findViewById(R.id.txt_orderdate);
            txt_orderstatus=(TextView)itemView.findViewById(R.id.txt_orderstatus);
            btn_reorder=(Button) itemView.findViewById(R.id.btn_reorder);
            rel_orderdetail=(RelativeLayout) itemView.findViewById(R.id.rel_orderdetail);
            rel_help=(RelativeLayout) itemView.findViewById(R.id.rel_help);

        }
    }
}
