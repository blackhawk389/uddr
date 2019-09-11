package com.uk.uddr.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.uk.uddr.adapter.RatingAdapter;
import com.uk.uddr.model.RatingModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.uk.uddr.utils.Utils.user_image;


public class RatingListFragment extends Fragment {
    public RatingListFragment() {
    }

    SharedPreferences sharedPreferences;
    View view;
    Context mContext;
    ProgressBar progressBar;
    RecyclerView recycler_view;
    ArrayList<RatingModel> ratingModels;
    RatingAdapter ratingAdapter;
    RelativeLayout rel_back;
    String store_type_id, store_type ,store_id ;
    TextView txt_review_count;
    RatingBar ratingBar;
    String review_count, store_rating, rating, review, created_at, updated_at, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rating_list, container, false);
        mContext = view.getContext();
        sharedPreferences = getContext().getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            store_id = bundle.getString("store_id");
            store_type_id = bundle.getString("store_type_id");
            store_type = bundle.getString("store_type");

        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("back_fargment", "working");
                    android.support.v4.app.Fragment home = new HomeFragment();
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
        init();

        return view;
    }

    private void init() {
        progressBar = view.findViewById(R.id.progressBar);
        recycler_view = view.findViewById(R.id.recycler_view);
        rel_back = view.findViewById(R.id.rel_back);
        txt_review_count = view.findViewById(R.id.txt_review_count);
        ratingBar = view.findViewById(R.id.ratingBar);
        rel_back.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
//        recycleview.setLayoutManager(mLayoutManager);
//        recycleview.setItemAnimator(new DefaultItemAnimator());
//        ratingModels = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            ratingModels.add(new RatingModel("", "", ""));
//        }
//        ratingAdapter = new RatingAdapter(getActivity(), ratingModels);
//        recycleview.setAdapter(ratingAdapter);


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment;
            Bundle bundle;
            FragmentManager fm;
            FragmentTransaction fragmentTransaction;
            switch (v.getId()) {
                case R.id.rel_back:
//                    fragmentManager.popBackStack();
                    fragment = new PartnerprofileFragment();
                    bundle = new Bundle();
                    bundle.putString("store_type_id", store_type_id);
                    bundle.putString("store_id", store_id);
                    bundle.putString("store_type", store_type);

                    fragment.setArguments(bundle);
                    fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes
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
                ratingModels = new ArrayList<>();
                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString("user_id", ""));
                    sendata.put("loginToken", sharedPreferences.getString("loginToken", ""));
                    sendata.put("store_id", store_id);
                    Log.e("sendData", sendata.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreReview", sendata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("getstore_response", response.toString());

                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            review_count = response.getString("review_count");
                            store_rating = response.getString("store_rating");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                if(data.length()>0){
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject object = data.getJSONObject(i);
                                        String id = object.getString("id");
                                        String store_id = object.getString("store_id");
                                        String user_id = object.getString("user_id");
                                        rating = object.getString("rating");
                                        Log.e("hello", rating);
                                        review = object.getString("review");
                                        created_at = object.getString("created_at");
                                        updated_at = object.getString("updated_at");
                                        JSONObject userinfo = object.getJSONObject("userinfo");
                                        name = userinfo.getString("name");
                                        user_image = userinfo.getString("user_image");
                                        ratingModels.add(new RatingModel(id, store_id, user_id, rating, review, created_at, updated_at, name, user_image));
                                    }
                                    RatingAdapter adapter = new RatingAdapter(mContext, ratingModels);
                                    recycler_view.setLayoutManager(new LinearLayoutManager(mContext));
                                    recycler_view.setAdapter(adapter);

                                    ratingBar.setRating(Float.valueOf(rating));
                                    if(review_count.equals("0"))
                                        review_count = "No";
                                    txt_review_count.setText(review_count + " Reviews");
                                    ratingBar.setVisibility(View.VISIBLE);
                                }else{
                                    ratingBar.setVisibility(View.GONE);
                                }



                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }

        }

    };


}
