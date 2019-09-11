package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.adapter.OrderDetailAdapter;
import com.uk.uddr.model.CartDetailModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {

    RecyclerView recycleview;
    ArrayList<CartDetailModel> orderList;
    OrderDetailAdapter orderDetailAdapter;
    ProgressBar progressBar;
    TextView txt_subtotal, txt_total, txt_address, txt_aname, txt_date_time,txt_ordernumber,txtview_address;
    String transactionId="";
    SharedPreferences sharedPreferences;
    Button btn_orderstatus;
    ImageView img_back;
    RelativeLayout rel_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        transactionId=getIntent().getStringExtra("t_id");
        init();
    }



    private void init() {
        progressBar = findViewById(R.id.progressBar);
        img_back= findViewById(R.id.img_back);
        rel_main=findViewById(R.id.rel_main);
        recycleview = findViewById(R.id.recycleview);
        txt_ordernumber = findViewById(R.id.txt_ordernumber);
        txt_subtotal = findViewById(R.id.txt_subtotal);
        txt_total = findViewById(R.id.txt_total);
        txt_address = findViewById(R.id.txt_address);
        txt_aname = findViewById(R.id.txt_addName);
        txt_date_time = findViewById(R.id.txt_date_time);
        btn_orderstatus=findViewById(R.id.btn_orderstatus);
        txtview_address=findViewById(R.id.txtview_address);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        img_back.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("TransactionID",transactionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                orderList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "orderDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("store_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                rel_main.setVisibility(View.VISIBLE);
                                //offset = response.getString("offset");
                                String prodImageURL = response.getString("prodImageURL");
                                JSONObject data = response.getJSONObject("data");
                                String TransactionID=data.getString("TransactionID");
                                String Total=data.getString("Total");
                                String address_id=data.getString("address_id");
                                String dilivery_time=data.getString("dilivery_time");
                                String timesloct = data.getString("timesloct");
                                String availability = data.getString("availability");
                                String is_delivery = data.getString("is_delivery");
                                String store_address = data.getString("store_address");
                                String PaymentStatus=data.getString("PaymentStatus");
                                JSONObject address=data.getJSONObject("address");
                                String house_name=address.getString("house_name");
                                String street=address.getString("street");
                                //String state=address.getString("county");
                                //String city=address.getString("town");
                                JSONArray items=data.getJSONArray("itemsArr");
                                for(int i=0;i<items.length();i++){
                                    JSONObject prod=items.getJSONObject(i);
                                    String product_name=prod.getString("product_name");
                                    String Price=prod.getString("Price");
                                    String Quantity=prod.getString("Quantity");
                                    String image=prod.getString("product_image");
                                    sum=sum+(Float.parseFloat(Price)*Float.parseFloat(Quantity));
                                    orderList.add(new CartDetailModel(prodImageURL+image, product_name, Price,Quantity));
                                }
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(OrderDetailActivity.this, 1);
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.setItemAnimator(new DefaultItemAnimator());
                                orderDetailAdapter = new OrderDetailAdapter(OrderDetailActivity.this, orderList);
                                recycleview.setAdapter(orderDetailAdapter);

                                txt_ordernumber.setText("Order : #"+TransactionID);
                                txt_date_time.setText(dilivery_time+" "+timesloct);
                                txt_subtotal.setText("£"+String.format("%.2f", sum));
                                txt_total.setText("£"+String.format("%.2f", sum+0.49));
                                btn_orderstatus.setText(PaymentStatus);
                                if(is_delivery.equals("1")){
                                    txtview_address.setText("Address");
                                    txt_aname.setText(house_name+" "+street);
                                }else{
                                    txtview_address.setText("Store Address");
                                    txt_aname.setText(store_address);
                                }
                                txt_address.setVisibility(View.GONE);
                                Log.e("storeaddress",store_address);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderDetailActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(OrderDetailActivity.this).add(jsonObjectRequest);
            }
        }
    };
}
