package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.adapter.AddressAdapter;
import com.uk.uddr.model.AddressModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectAddress extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    RecyclerView recycleview;
    ArrayList<AddressModel> arrayList;
    ImageView img_back;
    ProgressBar progressBar;
    Button btn_addclick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);

    }

    private void init() {

        progressBar = findViewById(R.id.progressBar);
        recycleview = findViewById(R.id.recycleview);
        img_back = findViewById(R.id.img_back);
        btn_addclick = findViewById(R.id.btn_addclick);
        img_back.setOnClickListener(clickListener);
        btn_addclick.setOnClickListener(clickListener);


//        setRecylwerview();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_addclick:
                    Intent in = new Intent(SelectAddress.this, NewAddress.class);
                    startActivity(in);
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
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getUserAddress", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("address_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String address_id = object.getString("address_id");
                                    String user_id = object.getString("user_id");
                                    String house_name = object.getString("house_name");
                                    String street = object.getString("street");
                                    String county = object.getString("county");
                                    String town = object.getString("town");
                                    String postcode = object.getString("postcode");
                                    String lable = object.getString("lable");
                                    arrayList.add(new AddressModel(address_id,user_id,house_name,street,county,town,postcode,"",lable));
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectAddress.this);
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.setItemAnimator(new DefaultItemAnimator());
                                AddressAdapter adapeter = new AddressAdapter(SelectAddress.this, arrayList);
                                recycleview.setAdapter(adapeter);

                            }else{
                                progressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(SelectAddress.this);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
//                                        finish();
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
                        Toast.makeText(SelectAddress.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(SelectAddress.this).add(jsonObjectRequest);
            }
        }
    };
}