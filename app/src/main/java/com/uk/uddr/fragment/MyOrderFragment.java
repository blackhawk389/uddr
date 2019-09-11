package com.uk.uddr.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.adapter.PreviousAdapter;
import com.uk.uddr.model.PreviousModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MyOrderFragment extends android.support.v4.app.Fragment {
    public MyOrderFragment() {
    }

    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<PreviousModel> orderList;
    RecyclerView recycleview;
    String offset = "0";
    Context mContext;
    TextView txt_nodata;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_order, container, false);
        mContext = view.getContext();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("back_fargment", "working");
                    Fragment home = new HomeFragment();
                    // create a FragmentManager
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    fragmentTransaction.replace(R.id.frameLayout, home);
                    fragmentTransaction.commit(); // save the changes
                    HomeActivity.changeicon(1);
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = mContext.getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        offset = "0";
        init();
    }

    private void init() {
        recycleview = view.findViewById(R.id.recycleview);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        HomeActivity.changeicon(3);

    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("offset", offset);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata", sendata.toString());

                orderList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getOrderReceipt", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("store_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                offset = response.getString("offset");
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String transactionID = object.getString("TransactionID");
                                    String store_id = object.getString("store_id");
                                    String Total = object.getString("Total");
                                    String PaymentStatus = object.getString("PaymentStatus");
                                    String receipts = object.getString("receipts");
                                    String title = object.getString("title");
                                    String store_phone = object.getString("store_phone");
                                    String type_image = object.getString("type_image");
                                    String order_date = object.getString("order_date");
                                    String order_time = object.getString("order_time");

                                    orderList.add(new PreviousModel(transactionID, store_id, store_phone,Total, PaymentStatus, receipts, order_date, order_time, title, type_image));

                                }
                                if (data.length() > 0) {
                                    txt_nodata.setVisibility(View.GONE);
                                } else {
                                    txt_nodata.setVisibility(View.VISIBLE);
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.setItemAnimator(new DefaultItemAnimator());
                                PreviousAdapter adapeter = new PreviousAdapter(mContext, orderList);
                                recycleview.setAdapter(adapeter);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }
        }
    };


}
