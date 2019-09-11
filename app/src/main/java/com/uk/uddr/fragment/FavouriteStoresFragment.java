package com.uk.uddr.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.uk.uddr.adapter.FavouriteAdapter;
import com.uk.uddr.adapter.ImageSlideAdapter;
import com.uk.uddr.adapter.RatingAdapter;
import com.uk.uddr.model.FavouriteModel;
import com.uk.uddr.model.RatingModel;
import com.uk.uddr.model.StoreModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

import static android.content.Context.MODE_PRIVATE;
import static com.uk.uddr.utils.Utils.user_image;


public class FavouriteStoresFragment extends Fragment {
    public FavouriteStoresFragment() {
    }

    Context mContext;
    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<FavouriteModel> arrayList;
    RecyclerView recycleview;
    RelativeLayout  relativeLayout;
    ImageView img_back;
    TextView txt_noitem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite_stores, container, false);
        mContext = view.getContext();
        sharedPreferences = getContext().getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        return view;
    }
    private void init() {
        progressBar = view.findViewById(R.id.progressBar);
        img_back = view.findViewById(R.id.img_back);
        recycleview = view.findViewById(R.id.recycleview);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        txt_noitem=view.findViewById(R.id.txt_noitem);
        img_back.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_back:
                    Fragment fragment = new ProfileFragment();
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the chang
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
                arrayList = new ArrayList<>();
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString("user_id", ""));
                    sendata.put("loginToken", sharedPreferences.getString("loginToken", ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("senddata",sendata.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreBookmark", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("product_detailrespobse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            String total_store = response.getString("total_store");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                relativeLayout.setVisibility(View.VISIBLE);
                                JSONArray data = response.getJSONArray("data");
                                if(data.length()>0){

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject object = data.getJSONObject(i);
                                        String store_id = object.getString("store_id");
                                        String store_name = object.getString("store_name");
                                        String store_address = object.getString("store_address");
                                        String distance = object.getString("distance");
                                        String store_image = object.getString("store_image");
                                        String store_logo = object.getString("store_logo");
                                        String postcode = object.getString("postcode");
                                        String latitude = object.getString("latitude");
                                        String longitude = object.getString("longitude");
                                        String total_cat = object.getString("total_cat");
                                        String store_rating = object.getString("store_rating");
                                        String review_count = object.getString("review_count");

                                        arrayList.add(new FavouriteModel(store_id, store_name, store_address, distance, store_image, store_logo, postcode, latitude, longitude, total_cat, store_rating, review_count));

                                    }
                                    FavouriteAdapter adapter = new FavouriteAdapter(mContext, arrayList);
                                    recycleview.setLayoutManager(new LinearLayoutManager(mContext));
                                    recycleview.setAdapter(adapter);
                                    txt_noitem.setVisibility(View.GONE);

                                }else{
                                    txt_noitem.setVisibility(View.VISIBLE);
                                }
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