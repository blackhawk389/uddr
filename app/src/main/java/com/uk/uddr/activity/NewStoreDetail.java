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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.adapter.CommentAdapter;
import com.uk.uddr.adapter.NewPhotoAdapter;
import com.uk.uddr.model.CommentModel;
import com.uk.uddr.model.PhotoModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewStoreDetail extends AppCompatActivity {

    RecyclerView comment_recycler_view,phot0_recycler_view;
    ProgressBar progressBar,progressBar_5,progressBar_4,progressBar_3,progressBar_2,progressBar_1;
    TextView txt_rating_5,txt_rating_4,txt_rating_3,txt_rating_2,
            txt_rating_1,txt_partner,txt_rating,txt_rating_count;
    RatingBar ratingBar;
    Button btn_write_review,btn_choose_service;
    ImageView img_back;
    SharedPreferences sharedPreferences;
    ArrayList<PhotoModel> photoModelArrayList;
    ArrayList<CommentModel> commentModelArrayList;
    String store_id = "",store_name = "",store_type = "",store_type_id="";
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_store_detail);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        store_id = getIntent().getStringExtra("store_id");
        store_name = getIntent().getStringExtra("store_name");
        store_type = getIntent().getStringExtra("store_type");
        store_type_id = getIntent().getStringExtra("store_type_id");
        init();
        txt_partner.setText(store_name);
    }

    private void init() {
        comment_recycler_view = findViewById(R.id.comment_recycler_view);
        phot0_recycler_view = findViewById(R.id.phot0_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        progressBar_5 = findViewById(R.id.progressBar_5);
        progressBar_4 = findViewById(R.id.progressBar_4);
        progressBar_3 = findViewById(R.id.progressBar_3);
        progressBar_2 = findViewById(R.id.progressBar_2);
        progressBar_1 = findViewById(R.id.progressBar_1);
        txt_rating_5 = findViewById(R.id.txt_rating_5);
        txt_rating_4 = findViewById(R.id.txt_rating_4);
        txt_rating_3 = findViewById(R.id.txt_rating_3);
        txt_rating_2 = findViewById(R.id.txt_rating_2);
        txt_rating_1 = findViewById(R.id.txt_rating_1);
        txt_partner = findViewById(R.id.txt_partner);
        txt_rating = findViewById(R.id.txt_rating);
        txt_rating_count = findViewById(R.id.txt_rating_count);
        ratingBar = findViewById(R.id.ratingBar);
        btn_write_review = findViewById(R.id.btn_write_review);
        btn_choose_service = findViewById(R.id.btn_choose_service);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(clickListener);
        btn_write_review.setOnClickListener(clickListener);
        btn_choose_service.setOnClickListener(clickListener);
        //img_upload = findViewById(R.id.img_upload);
        setphotoRecycler();
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
    }
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.btn_write_review:
                    intent = new Intent(NewStoreDetail.this,WriteReviewFragment.class);
                    intent.putExtra("store_id", store_id);
                 /*   intent.putExtra("store_type_id", store_type_id);
                    intent.putExtra("store_id", store_id);
                    intent.putExtra("store_type", store_type);*/
                    startActivity(intent);
                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_choose_service:
                    Intent product=new Intent(NewStoreDetail.this,ProductActivity.class);
                    product.putExtra("store_id",store_id);
                    product.putExtra("store_type_id",store_type_id);
                    product.putExtra("store_type",store_type);
                    startActivity(product);
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
                    sendata.put("store_id",store_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                commentModelArrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreReview", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("store_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                                    Log.e("Status", status);
                            if (status.equals("1")) {

                                String rating_count1 = response.getString("rating_count1");
                                String rating_count2 = response.getString("rating_count2");
                                String rating_count3 = response.getString("rating_count3");
                                String rating_count4 = response.getString("rating_count4");
                                String rating_count5 = response.getString("rating_count5");
                                String review_count = response.getString("review_count");
                                String store_rating = response.getString("store_rating");
                                setprogressRating(rating_count1,rating_count2,rating_count3,rating_count4,rating_count5,review_count,store_rating);
                                progressBar.setVisibility(View.GONE);
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String id = object.getString("id");
                                    String store_id = object.getString("store_id");
                                    String user_id = object.getString("user_id");
                                    String rating = object.getString("rating");
                                    String review = object.getString("review");
                                    String status1 = object.getString("status");
                                    String created_at = object.getString("created_at");
                                    String updated_at = object.getString("updated_at");
                                    JSONObject object1 = object.getJSONObject("userinfo");
                                    String name = object1.getString("name");
                                    String user_image = object1.getString("user_image");
                                    commentModelArrayList.add(new CommentModel(id,store_id,user_id,rating,review,status,created_at,updated_at,name,user_image));
                                }
                                CommentAdapter adapter = new CommentAdapter(NewStoreDetail.this,commentModelArrayList,store_id,progressBar);
                                comment_recycler_view.setLayoutManager(new LinearLayoutManager(NewStoreDetail.this,LinearLayoutManager.VERTICAL,false));
                                comment_recycler_view.setAdapter(adapter);
                                scrollView.setVisibility(View.VISIBLE);
                                btn_choose_service.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

//                                StoreAdapter adapeter = new StoreAdapter(StoreActivity.this, arrayList);
//                                recycleview.setAdapter(adapeter);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(NewStoreDetail.this);
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
                        Toast.makeText(NewStoreDetail.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(NewStoreDetail.this).add(jsonObjectRequest);
            }
        }
    };


    private void setphotoRecycler() {
        photoModelArrayList = new ArrayList<>();
        photoModelArrayList = PartnerProfileActivity.arrayListphoto;
        NewPhotoAdapter adapter = new NewPhotoAdapter(NewStoreDetail.this,photoModelArrayList, store_name);
        phot0_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        phot0_recycler_view.setAdapter(adapter);
    }

    private void setprogressRating(String rating_count1, String rating_count2, String rating_count3, String rating_count4, String rating_count5, String review_count, String rating) {
        int max = Integer.valueOf(review_count);
        ratingBar.setRating(Float.valueOf(rating));
        progressBar_1.setMax(max);
        progressBar_2.setMax(max);
        progressBar_3.setMax(max);
        progressBar_4.setMax(max);
        progressBar_5.setMax(max);
        progressBar_1.setProgress(Integer.valueOf(rating_count1));
        progressBar_2.setProgress(Integer.valueOf(rating_count2));
        progressBar_3.setProgress(Integer.valueOf(rating_count3));
        progressBar_4.setProgress(Integer.valueOf(rating_count4));
        progressBar_5.setProgress(Integer.valueOf(rating_count5));
        txt_rating.setText(rating);
        txt_rating_1.setText(rating_count1);
        txt_rating_2.setText(rating_count2);
        txt_rating_3.setText(rating_count3);
        txt_rating_4.setText(rating_count4);
        txt_rating_5.setText(rating_count5);
        if(review_count.equals("0"))
            txt_rating_count.setText("(No Reviews)");
        else
            txt_rating_count.setText("("+review_count+" Reviews)");
    }
}
