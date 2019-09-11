package com.uk.uddr.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.adapter.RatingAdapter;
import com.uk.uddr.fragment.HomeFragment;
import com.uk.uddr.fragment.PartnerprofileFragment;
import com.uk.uddr.model.RatingModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class WriteReviewFragment extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    RecyclerView recycleview;
    ImageView img_back;
    Button btn_submit;
    EditText edt_review;
    RatingBar ratingBar;
    String rating = "";
    String store_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_write_review);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        store_id = getIntent().getStringExtra("store_id");
             init();
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        recycleview = findViewById(R.id.recycler_view);
        img_back = findViewById(R.id.img_back);
        btn_submit = findViewById(R.id.btn_submit);
        edt_review = findViewById(R.id.edt_review);
        ratingBar = findViewById(R.id.ratingBar);

        img_back.setOnClickListener(clickListener);
        btn_submit.setOnClickListener(clickListener);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float touchPositionX = motionEvent.getX();
                float width = ratingBar.getWidth();
                float starsf = (touchPositionX / width) * 5.0f;
                int stars = (int) starsf + 1;
                ratingBar.setRating(stars);
                rating = String.valueOf(ratingBar.getRating());
                Log.e("rating check", rating + "--" + starsf + "--" + ratingBar.getRating());
                // finalstars[0] =stars;
                return true;
            }
        });


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_back:
                    finish();
                    break;

                case R.id.btn_submit:
                    if (edt_review.getText().toString().equals("")) {
                        edt_review.setError("Please give us review");
                        edt_review.requestFocus();
                    } else if (rating.equals("")) {

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(100);
                    }
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
                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString("user_id", ""));
                    sendata.put("loginToken", sharedPreferences.getString("loginToken", ""));
                    sendata.put("store_id", store_id);
                    sendata.put("rating", rating);
                    sendata.put("review", edt_review.getText().toString().trim());
                    Log.e("writereview", sendata.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "saveStoreReview", sendata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("writereview_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            if(status.equals("success")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(WriteReviewFragment.this);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface diaAddRoomterface, int i) {
                                        diaAddRoomterface.dismiss();
                                        finish();

                                    }
                                });
                                builder.show();
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(WriteReviewFragment.this);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface diaAddRoomterface, int i) {
                                        diaAddRoomterface.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WriteReviewFragment.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(WriteReviewFragment.this).add(jsonObjectRequest);
            }

        }

    };
}




