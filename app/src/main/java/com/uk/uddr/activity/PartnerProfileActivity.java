package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.adapter.NewPhotoAdapter;
import com.uk.uddr.adapter.PhotoAdapter;
import com.uk.uddr.model.PhotoModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.uk.uddr.utils.Utils.user_image;

public class PartnerProfileActivity extends AppCompatActivity {

    ImageView img_heart,img_back,img_call,img_website,img_map,imageView;
    TextView txt_des,txt_call,txt_website,txt_map,txt_tag_line,txt_store_name,
            txt_address,txt_rating,txt_rating_count,txt_review;
    LinearLayout len_call,len_website,len_map;
    RecyclerView photo_recycler_view;
    static ArrayList<PhotoModel> arrayListphoto;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String store_id = "",store_rating = "",store_name = "",store_type_id="",store_type="";
    RelativeLayout rel_certification;
    RatingBar ratingBar;
    CardView card_view;
    String phone_no = "",website = "",lat = "",lng = "",name = "",what="";
    Button btn_service;
    LinearLayout lin_main;
    AppBarLayout mAppBarLayout;
    final String TAG = "AppBarTest";

    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        store_id = getIntent().getStringExtra("store_id");
        store_name = getIntent().getStringExtra("store_name");
        store_type_id= getIntent().getStringExtra("store_type_id");
        store_type= getIntent().getStringExtra("store_type");

        init();
       /* final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

    }

    private void init() {
        txt_store_name = findViewById(R.id.txt_store_name);
        txt_tag_line = findViewById(R.id.txt_tag_line);
        txt_review = findViewById(R.id.txt_review);
        txt_rating = findViewById(R.id.txt_rating);
        card_view = findViewById(R.id.card_view);
        txt_rating_count = findViewById(R.id.txt_rating_count);
        txt_address = findViewById(R.id.txt_address);
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.backdrop);
        photo_recycler_view = findViewById(R.id.photo_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        rel_certification = findViewById(R.id.rel_certification);
        txt_des = findViewById(R.id.txt_des);
        txt_call = findViewById(R.id.txt_call);
        txt_website = findViewById(R.id.txt_website);
        txt_map = findViewById(R.id.txt_map);
        img_back = findViewById(R.id.img_back);
        img_call = findViewById(R.id.img_call);
        img_heart = findViewById(R.id.img_heart);
        img_website = findViewById(R.id.img_website);
        img_map = findViewById(R.id.img_map);
        len_call = findViewById(R.id.len_call);
        len_website = findViewById(R.id.len_website);
        btn_service=findViewById(R.id.btn_choose_service);
        len_map = findViewById(R.id.len_map);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);

        setClicks();
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        lin_main = findViewById(R.id.lin_main);

    }

    private void sendToContactUs() {
        String url = "https://api.whatsapp.com/send?phone= " +phone_no +" &text=" +"Hello, I saw you on uddr...";

        try {

            PackageManager pm = getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }



    private void setClicks() {
        card_view.setOnClickListener(clickListener);
        len_call.setOnClickListener(clickListener);
        len_map.setOnClickListener(clickListener);
        len_website.setOnClickListener(clickListener);
        btn_service.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);
        img_heart.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.card_view:
                    intent = new Intent(PartnerProfileActivity.this,NewStoreDetail.class);
                    intent.putExtra("store_id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("store_type_id",store_type_id);
                    intent.putExtra("store_type",store_type);
                    startActivity(intent);
                    break;
                case R.id.len_call:
//                    intent = new Intent(Intent.ACTION_DIAL);
//                    intent.setData(Uri.parse("tel:"+phone_no));
//                    startActivity(intent);
                    sendToContactUs();
                    break;
                case R.id.len_map:
                    String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + store_name + ")";
                    Intent intent3 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent3.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent3);
                    break;
                case R.id.len_website:
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(PartnerProfileActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;

                case R.id.img_back:
                    finish();
                    break;

                case R.id.btn_choose_service:
                    Intent product=new Intent(PartnerProfileActivity.this,ProductActivity.class);
                    product.putExtra("store_id",store_id);
                    product.putExtra("store_type_id",store_type_id);
                    product.putExtra("store_type",store_type);
                    startActivity(product);
                    break;


                case R.id.img_heart:
                    String user_id = "";
                    user_id = sharedPreferences.getString(Utils.user_id,"");
                    if(user_id.equals("")){

                                Intent login = new Intent(PartnerProfileActivity.this,LoginActivity.class);
                                startActivity(login);


                    }else{
                        int resourceid=(int)img_heart.getTag();
                        if(resourceid == R.mipmap.ic_heartwhite){
                            //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                            img_heart.setImageDrawable(myDrawable);
                            img_heart.setTag(R.mipmap.ic_heart_dark);
                            handler.sendEmptyMessage(200);
                        }else{
                            // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                            img_heart.setImageDrawable(myDrawable);
                            img_heart.setTag(R.mipmap.ic_heartwhite);

                            handler.sendEmptyMessage(200);
                        }
                    }
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

  /*  private void setphotoRecycler() {
        photoModelArrayList = new ArrayList<>();
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.drawable.dummy_round.png"));
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.mipmap.ic_home_img.png"));
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.drawable.dummy_round.png"));
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.mipmap.ic_home_img.png"));
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.drawable.dummy_round.png"));
        photoModelArrayList.add(new PhotoModel("R.drawable.dummy_round.png","R.mipmap.ic_home_img.png"));
        NewPhotoAdapter adapter = new NewPhotoAdapter(PartnerProfileActivity.this,photoModelArrayList);
        photo_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photo_recycler_view.setAdapter(adapter);
    }*/

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                arrayListphoto = new ArrayList<>();
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("store_id", store_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata", sendata.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("partnerprofile_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            //relativelayout.setVisibility(View.VISIBLE);
//                           String store_certificates = response.getString("store_certificates");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject object = response.getJSONObject("data");
                                String store_id = object.getString("store_id");
                                String store_name = object.getString("store_name");
                                String store_email = object.getString("store_email");
                                String store_address = object.getString("store_address");
                                phone_no = object.getString("store_phone");
                                String store_logo = object.getString("store_logo");
                                String postcode = object.getString("postcode");
                                String description = object.getString("description");
                                String delivery_text = object.getString("delivery_text");
                                website = object.getString("website");
                                String tag_line = object.getString("tag_line");
                                String is_commerce=object.getString("is_commerce");

                                String is_bookmark = object.getString("is_bookmark");
                                if (is_bookmark.equals("1")) {
                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                    img_heart.setTag(R.mipmap.ic_heart_dark);
                                    img_heart.setImageDrawable(myDrawable);
                                } else if (is_bookmark.equals("0")) {
                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                    img_heart.setImageDrawable(myDrawable);
                                }
                                lat = object.getString("latitude");
                                lng = object.getString("longitude");
                                if(is_commerce.equals("1")){
                                    btn_service.setVisibility(View.VISIBLE);
                                }else{
                                    btn_service.setVisibility(View.GONE);
                                }
                                store_rating = object.getString("store_rating");
                                int review_count = Integer.parseInt(object.getString("review_count"));

                                JSONArray store_r=object.getJSONArray("store_review");
                                final FrameLayout.LayoutParams params = new FrameLayout
                                        .LayoutParams(
                                        FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT
                                );
                                mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                                    private State state;

                                    @Override
                                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                        if (verticalOffset == 0) {
                                            if (state != State.EXPANDED) {
                                                Log.e(TAG,"Expanded");
                                                card_view.setVisibility(View.VISIBLE);
                                                params.setMargins(0, 300, 0, 0);
                                                lin_main.setLayoutParams(params);
                                                int resourceid=(int)img_heart.getTag();
                                                if(resourceid == R.mipmap.ic_heartwhite){
                                                    //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                                }else{
                                                    // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heart_dark);
                                                }
                                                Drawable backarrow = getResources().getDrawable(R.mipmap.back_white);
                                                img_back.setImageDrawable(backarrow);
                                            }
                                            state = State.EXPANDED;
                                        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                                            if (state != State.COLLAPSED) {
                                                Log.e(TAG,"Collapsed");


                                            }
                                            state = State.COLLAPSED;
                                        } else {
                                            if (state != State.IDLE) {
                                                Log.e(TAG,"Idle");
                                                card_view.setVisibility(View.GONE);
                                                params.setMargins(0, 20, 0, 0);
                                                lin_main.setLayoutParams(params);

                                                int resourceid=(int)img_heart.getTag();
                                                if(resourceid == R.mipmap.ic_heartwhite){
                                                    //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_favourite);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                                }else{
                                                    // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heart_dark);

                                                }
                                                Drawable backarrow = getResources().getDrawable(R.mipmap.back_black);
                                                img_back.setImageDrawable(backarrow);

                                            }
                                            state = State.IDLE;

                                        }
                                    }
                                });


                                //txt_phone.setText(store_phone);
                                JSONArray certi_images = object.getJSONArray("store_certificates");
                                if(certi_images.length()>0){
                                    for (int i = 0; i < certi_images.length(); i++) {
                                        JSONObject imageobj = certi_images.getJSONObject(i);
                                        String imagePath = imageobj.getString("imagePath");
                                        String imageId = imageobj.getString("imageId");
                                        arrayListphoto.add(new PhotoModel(imagePath, imageId));
                                    }
                                    rel_certification.setVisibility(View.VISIBLE);
                                }else{
                                    rel_certification.setVisibility(View.GONE);
                                }
                                NewPhotoAdapter adapter = new NewPhotoAdapter(PartnerProfileActivity.this, arrayListphoto, store_name);
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(PartnerProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                photo_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                                photo_recycler_view.setVisibility(View.VISIBLE);
                                photo_recycler_view.setAdapter(adapter);



                                txt_store_name.setText(store_name);
                                txt_tag_line.setText(tag_line);
                                txt_address.setText(store_address);
                                txt_des.setText(description);
                                ratingBar.setRating(Float.valueOf(store_rating));
                                txt_rating.setText(store_rating);
                                if(review_count != 0)
                                    txt_rating_count.setText("("+review_count + " Reviews)");
                                else
                                    txt_rating_count.setText("(No Reviews)");
                                if (!store_logo.equals("")) {
                                    Picasso.with(PartnerProfileActivity.this).load(store_logo).into(imageView);
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(PartnerProfileActivity.this);
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
                        Toast.makeText(PartnerProfileActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(PartnerProfileActivity.this).add(jsonObjectRequest);
            } else if (msg.what == 200) {
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString("user_id", ""));
                    sendata.put("loginToken", sharedPreferences.getString("loginToken", ""));
                    sendata.put("store_id", store_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "saveStoreBookmark", sendata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("fave_Response", response.toString());
                            String status = response.getString("status");
                            if (status.equals("1")) {
                            } else {
                                String messaege = response.getString("messaege");
                                AlertDialog.Builder builder = new AlertDialog.Builder(PartnerProfileActivity.this);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PartnerProfileActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(PartnerProfileActivity.this).add(jsonObjectRequest);
            }
        }

    };



}
