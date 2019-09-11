package com.uk.uddr.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.ZipCode;
import com.uk.uddr.adapter.FeatureStoreAdapter;
import com.uk.uddr.adapter.HomeAdapter;
import com.uk.uddr.model.FeatureStoreModel;
import com.uk.uddr.model.HomeModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<HomeModel> storetypeList;
    ArrayList<FeatureStoreModel> featurestores;
    RecyclerView recyclerView,recycleview1;
    public HomeFragment() {}
    RelativeLayout rel_location;
    TextView txt_zipcode,productfeature,txt_catviewall;
    Context mContext;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext=view.getContext();
        sharedPreferences = getContext().getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        return view;
    }

    private void init() {
        recyclerView = view.findViewById(R.id.recycleview);
        recycleview1 = view.findViewById(R.id.recycleview1);
        progressBar = view.findViewById(R.id.progressBar);
        rel_location= view.findViewById(R.id.relative_location);
        txt_zipcode = view.findViewById(R.id.txt_zipcode);
        txt_catviewall= view.findViewById(R.id.txt_catviewall);
        productfeature= view.findViewById(R.id.productfeature);

        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        handler.sendEmptyMessage(200);
        String zip=sharedPreferences.getString(Utils.zipcode,"");
        if(zip.equals("")){
            txt_zipcode.setText("Enter Postcode");
            productfeature.setVisibility(View.VISIBLE);
        }else{
            txt_zipcode.setText(zip);
            productfeature.setVisibility(View.GONE);
        }
        Log.e("checklat",sharedPreferences.getString(Utils.longitude,"")+ " - "+sharedPreferences.getString(Utils.latitude,""));
        rel_location.setOnClickListener(clickListener);
        txt_catviewall.setOnClickListener(clickListener);
        HomeActivity.changeicon(1);
    }

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.relative_location:
                    Intent loca=new Intent(mContext, ZipCode.class);
                    startActivity(loca);
                    break;
                case R.id.txt_catviewall:
                    Fragment fragment = new SearchCategory();
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit(); // save the changes
                    HomeActivity.changeicon(2);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("latitude", sharedPreferences.getString(Utils.latitude, "0.0"));
                    sendata.put("longitude", sharedPreferences.getString(Utils.longitude, "0.0"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                storetypeList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreTypes", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("disresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                             //   productfeature.setVisibility(View.VISIBLE);
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String type_image = object.getString("type_image");
                                    String tag_line = object.getString("tag_line");
                                    String description = object.getString("description");
                                    storetypeList.add(new HomeModel(id,title,tag_line,type_image,description));
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                HomeAdapter adapeter = new HomeAdapter(mContext, storetypeList);
                                recyclerView.setAdapter(adapeter);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else if(msg.what==200){
                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString("user_id", ""));
                    sendata.put("loginToken", sharedPreferences.getString("loginToken", ""));
                    sendata.put("latitude", sharedPreferences.getString(Utils.latitude, "0.0"));
                    sendata.put("longitude", sharedPreferences.getString(Utils.longitude, "0.0"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("senddata",sendata.toString());
                featurestores = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getFeaturedStores", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("disresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                               // productfeature.setVisibility(View.VISIBLE);
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String store_id = object.getString("store_id");
                                    String store_name = object.getString("store_name");
                                    String type_image = object.getString("store_address");
                                    String distance = object.getString("distance");
                                    String store_image = object.getString("store_image");
                                    String store_logo = object.getString("store_logo");
                                    String postcode = object.getString("postcode");
                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String tag_line = object.getString("tag_line");
                                    String total_cat = object.getString("total_cat");
                                    String store_rating = object.getString("store_rating");
                                    String review_count = object.getString("review_count");
                                    String is_bookmark = object.getString("is_bookmark");
                                    featurestores.add(new FeatureStoreModel(store_id,store_name,type_image,distance,store_image,store_logo,postcode,latitude,longitude,total_cat,store_rating,review_count,tag_line,"",is_bookmark));
                                }

                                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false);
                                recycleview1.setLayoutManager(mLayoutManager1);
                                recycleview1.setItemAnimator(new DefaultItemAnimator());
                                FeatureStoreAdapter featureStoreAdapter=new FeatureStoreAdapter(mContext,featurestores);
                                recycleview1.setAdapter(featureStoreAdapter);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void loadFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
