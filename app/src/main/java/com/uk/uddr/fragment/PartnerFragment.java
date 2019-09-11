package com.uk.uddr.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.uk.uddr.adapter.StoreAdapter;
import com.uk.uddr.model.StoreModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PartnerFragment extends android.support.v4.app.Fragment {

    Context mContext;
    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<StoreModel> arrayList;
    RecyclerView recycleview;
    ImageView img_back;
    String store_type_id = "0", store_image = "", store_type = "";
    TextView txt_nodata;
//    TextView txt_zipcode;
//    RelativeLayout rel_location;
    LinearLayout lnSortPart;
    LinearLayout lnSort;

    LinearLayout lnMostReviews, lnMostReviewScore, lnDistance;
    RelativeLayout RnSecondView;
    TextView txt_sort;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_partners, container, false);
        mContext = view.getContext();
        RnSecondView = (RelativeLayout)view.findViewById(R.id.RnSecondView);
        RnSecondView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RnSecondView.setVisibility(View.GONE);
            }
        });
        RnSecondView.setVisibility(View.GONE);


        txt_sort = (TextView)view.findViewById(R.id.txt_sort);
        txt_sort.setText("Distance");
        lnDistance = view.findViewById(R.id.lnDistance);
        lnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_sort.setText("Distance");
                RnSecondView.setVisibility(View.GONE);
                Collections.sort(arrayList, new Comparator<StoreModel>() {
                    @Override
                    public int compare(StoreModel product1, StoreModel product2) {
                        double distance1 = Double.parseDouble(product1.getStore_distance());
                        double distance2 = Double.parseDouble(product2.getStore_distance());
                        if(distance1 < distance2)
                            return -1;
                        else if(distance1 == distance2)
                            return 0;
                        else
                            return 1;
                    }
                });
                StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
                recycleview.setAdapter(adapeter);
            }
        });

        lnMostReviews = view.findViewById(R.id.lnMostReviews);
        lnMostReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_sort.setText("Most Reviews");
                RnSecondView.setVisibility(View.GONE);
                Collections.sort(arrayList, new Comparator<StoreModel>() {
                    @Override
                    public int compare(StoreModel product1, StoreModel product2) {
                        double distance1 = Double.parseDouble(product1.getReview_count());
                        double distance2 = Double.parseDouble(product2.getReview_count());
                        if(distance1 > distance2)
                            return -1;
                        else if(distance1 == distance2)
                            return 0;
                        else
                            return 1;
                    }
                });
                StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
                recycleview.setAdapter(adapeter);
            }
        });

        lnMostReviewScore = view.findViewById(R.id.lnMostReviewScore);
        lnMostReviewScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_sort.setText("Best Reviews");
                RnSecondView.setVisibility(View.GONE);
                Collections.sort(arrayList, new Comparator<StoreModel>() {
                    @Override
                    public int compare(StoreModel product1, StoreModel product2) {
                        double distance1 = Double.parseDouble(product1.getStore_rating());
                        double distance2 = Double.parseDouble(product2.getStore_rating());
                        if(distance1 > distance2)
                            return -1;
                        else if(distance1 == distance2)
                            return 0;
                        else
                            return 1;
                    }
                });
                StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
                recycleview.setAdapter(adapeter);
            }
        });

        lnSort = view.findViewById(R.id.lnSort);
        lnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RnSecondView.setVisibility(View.VISIBLE);
            }
        });

        lnSortPart = view.findViewById(R.id.lnSortPart);
        lnSortPart.setVisibility(View.VISIBLE);
        sharedPreferences = mContext.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            store_type_id = bundle.getString("store_type_id");
            store_image = bundle.getString("store_image");
            store_type = bundle.getString("store_type");
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("back_fargment", "working");
//                    android.support.v4.app.Fragment home = new HomeFragment();
//                    // create a FragmentManager
//                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
//                    // create a FragmentTransaction to begin the transaction and replace the Fragment
//                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                    // replace the FrameLayout with new Fragment
//                    fragmentTransaction.replace(R.id.frameLayout, home);
//                    fragmentTransaction.commit(); // save the changes

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();
                    HomeActivity.changeicon(1);
                    return true;
                }
                return false;
            }
        });
        init();
//        store_type_id=getIntent().getStringExtra("store_type_id");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void init() {
        txt_nodata = (TextView) view.findViewById(R.id.txt_nodata);
        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        img_back = (ImageView) view.findViewById(R.id.img_back);
        String zip = sharedPreferences.getString(Utils.zipcode, "");
        img_back.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.relative_location:
                    Intent loca = new Intent(mContext, ZipCode.class);
                    startActivity(loca);
                    break;
                case R.id.img_back:
//                    Fragment fragment = new HomeFragment();
//                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                    fragmentTransaction.replace(R.id.frameLayout, fragment);
//                    fragmentTransaction.commit(); // save the changes

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();
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
                    sendata.put("store_type_id", store_type_id);
                    sendata.put("latitude", sharedPreferences.getString(Utils.latitude, "0.0"));
                    sendata.put("longitude", sharedPreferences.getString(Utils.longitude, "0.0"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata", sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getAreasStores", sendata, new Response.Listener<JSONObject>() {
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
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String store_id = object.getString("store_id");
                                    String store_type_id = object.getString("store_type_id");
                                    String store_name = object.getString("store_name");
                                    String distance = object.getString("distance");
                                    String store_address = object.getString("store_address");
                                    //String store_phone = object.getString("store_phone");
                                    String store_image = object.getString("store_image");
                                    String store_logo = object.getString("store_logo");
                                    String postcode = object.getString("postcode");
                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String total_cat = object.getString("total_cat");
                                    String store_rating = object.getString("store_rating");
                                    String review_count = object.getString("review_count");

                                    arrayList.add(new StoreModel(store_id, store_type_id, store_name, distance, store_address, store_type, store_image, store_logo, postcode, latitude, longitude, total_cat, store_rating, review_count));
                                }

                                if (data.length() == 0) {
                                    txt_nodata.setVisibility(View.VISIBLE);
                                } else {
                                    txt_nodata.setVisibility(View.GONE);
                                }
                                Collections.sort(arrayList, new Comparator<StoreModel>() {
                                    @Override
                                    public int compare(StoreModel product1, StoreModel product2) {
                                        double distance1 = Double.parseDouble(product1.getStore_distance());
                                        double distance2 = Double.parseDouble(product2.getStore_distance());
                                        if(distance1 < distance2)
                                            return -1;
                                        else if(distance1 == distance2)
                                            return 0;
                                        else
                                            return 1;
                                    }
                                });
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.setItemAnimator(new DefaultItemAnimator());
                                StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
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
