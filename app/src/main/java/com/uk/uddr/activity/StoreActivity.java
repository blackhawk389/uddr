package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import com.uk.uddr.adapter.StoreAdapter;
import com.uk.uddr.model.StoreModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {



    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<StoreModel> arrayList;
    RecyclerView recycleview;
    ImageView img_back;
    String store_type_id;
    TextView txt_zipcode;
    RelativeLayout rel_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        store_type_id=getIntent().getStringExtra("store_type_id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        rel_location= findViewById(R.id.relative_location);
        txt_zipcode= findViewById(R.id.txt_zipcode);
        recycleview = findViewById(R.id.recycleview);
        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        String zip=sharedPreferences.getString(Utils.zipcode,"");
        if(zip.equals("")){
            txt_zipcode.setText("Enter ZipCode");
        }else{
            txt_zipcode.setText(zip);
        }
        rel_location.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
    }

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.relative_location:
                    Intent loca=new Intent(StoreActivity.this, ZipCode.class);
                    startActivity(loca);
                    break;
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
                    sendata.put("store_type_id",store_type_id);
                    sendata.put("latitude",sharedPreferences.getString(Utils.latitude,"0.0"));
                    sendata.put("longitude",sharedPreferences.getString(Utils.longitude,"0.0"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

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
                                    String store_address = object.getString("store_address");
                                    String store_phone = object.getString("store_phone");
                                    String store_image = object.getString("store_image");
                                    String store_logo = object.getString("store_logo");
                                    String postcode = object.getString("postcode");
                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String total_cat = object.getString("total_cat");

//                                    arrayList.add(new StoreModel(store_id,store_type_id,store_name,"",store_address,store_phone,store_image,store_logo,postcode,latitude,longitude,total_cat));


                                }
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(StoreActivity.this, 2);
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.addItemDecoration(new StoreActivity.GridSpacingItemDecoration(2, dpToPx(0), true));
                                recycleview.setItemAnimator(new DefaultItemAnimator());
//                                StoreAdapter adapeter = new StoreAdapter(StoreActivity.this, arrayList);
//                                recycleview.setAdapter(adapeter);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);
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
                        Toast.makeText(StoreActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(StoreActivity.this).add(jsonObjectRequest);
            }
        }
    };


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
