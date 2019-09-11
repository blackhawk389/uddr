package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.uk.uddr.R;
import com.uk.uddr.adapter.ProductListAdapter;
import com.uk.uddr.fragment.PartnerprofileFragment;
import com.uk.uddr.model.CategoryModel;
import com.uk.uddr.model.ProductListModel;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    //private ViewPager viewPager;
    ArrayList<CategoryModel> categoryList;
    ArrayList<ProductListModel> productList;
    RecyclerView product_RecyclerView;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    String categoryId = "";
    String store_id, store_type_id, store_type;
    ImageView img_back, img_store,img_heart;
    TextView txt_tag_line,txt_store_name,
            txt_address,txt_rating,txt_rating_count,txt_review;
    DBHelper dbHelper;
    View view;
    Context mContext;
    CardView card_view;
    RelativeLayout rel_main;
    RatingBar ratingBar;
    AppBarLayout mAppBarLayout;
    final String TAG = "AppBarTest";
    Button btn_viewbasket;

    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        dbHelper=new DBHelper(this);
        store_id =getIntent().getStringExtra("store_id");
        store_type_id =getIntent().getStringExtra("store_type_id");
        store_type = getIntent().getStringExtra("store_type");
        mContext=this;
        init();
    }

    void init() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        product_RecyclerView = (RecyclerView)findViewById(R.id.product_RecyclerView);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_store = (ImageView) findViewById(R.id.backdrop);
        txt_store_name = findViewById(R.id.txt_store_name);
        txt_tag_line = findViewById(R.id.txt_tag_line);
        txt_review = findViewById(R.id.txt_review);
        txt_rating = findViewById(R.id.txt_rating);
        card_view = findViewById(R.id.card_view);
        txt_rating_count = findViewById(R.id.txt_rating_count);
        txt_address = findViewById(R.id.txt_address);
        ratingBar = findViewById(R.id.ratingBar);
        img_heart = findViewById(R.id.img_heart);
        btn_viewbasket=findViewById(R.id.btn_viewbasket);
        //img_slogo = (ImageView) findViewById(R.id.store_logo);
        //cat_RecyclerView=(RecyclerView)view.findViewById(R.id.category_recyclerview);
        img_back.setOnClickListener(clickListener);
        btn_viewbasket.setOnClickListener(clickListener);
        card_view.setOnClickListener(clickListener);
        img_heart.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        dbHelper = new DBHelper(mContext);
        handler.sendEmptyMessage(100);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int po = tab.getPosition();
                categoryId = categoryList.get(po).getCat_id();
                progressBar.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(200);
                //Toast.makeText(mContext,po+" - "+categoryList.get(po).getCat_title()+" - "+categoryList.get(po).getCat_id(),Toast.LENGTH_SHORT).show();
//                ProductListFragment pro=new ProductListFragment();
//                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.detach(pro);
//                ft.attach(pro);
//                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        rel_main = findViewById(R.id.rel_main);
        product_RecyclerView.setNestedScrollingEnabled(false);
        basketbutton();
    }

    void basketbutton(){
        Cursor cursor=dbHelper.selectallproduct();
        if(cursor.getCount()>0){
            btn_viewbasket.setVisibility(View.VISIBLE);
        }else{
            btn_viewbasket.setVisibility(View.GONE);
        }
    }
    private void setupViewPager(ArrayList<CategoryModel> cList) {
        for (int i = 0; i < cList.size(); i++) {
            CategoryModel cm = cList.get(i);
            tabLayout.addTab(tabLayout.newTab().setText(cm.getCat_title()));
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment;
            Bundle bundle;
            FragmentManager fm;
            FragmentTransaction fragmentTransaction;
            switch (v.getId()) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_viewbasket:
                    Intent basket = new Intent(ProductActivity.this, Basket.class);
                    basket.putExtra("t_id","-1");
                    startActivity(basket);
                    break;
                case R.id.card_view:
                    Intent  review = new Intent(ProductActivity.this,NewStoreDetail.class);
                    review.putExtra("store_id", store_id);
                    review.putExtra("store_name", txt_store_name.getText().toString());
                    startActivity(review);
                    break;
                case R.id.img_heart:
                    String user_id = "";
                    user_id = sharedPreferences.getString(Utils.user_id,"");
                    if(user_id.equals("")){

                        Intent login = new Intent(ProductActivity.this,LoginActivity.class);
                        startActivity(login);
                    }else{
                        int resourceid=(int)img_heart.getTag();
                        if(resourceid == R.mipmap.ic_heartwhite){
                            //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                            img_heart.setImageDrawable(myDrawable);
                            img_heart.setTag(R.mipmap.ic_heart_dark);
                            handler.sendEmptyMessage(300);
                        }else{
                            // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                            img_heart.setImageDrawable(myDrawable);
                            img_heart.setTag(R.mipmap.ic_heartwhite);
                            handler.sendEmptyMessage(300);
                        }
                    }
                    break;
            }
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                categoryList = new ArrayList<>();
                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("store_id", store_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata", sendata.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreCategories", sendata, new Response.Listener<JSONObject>() {
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
                                JSONObject store = response.getJSONObject("store_info");
                                String s_id = store.getString("store_id");
                                String store_name = store.getString("store_name");
                                String distance = store.getString("distance");
                                String store_address = store.getString("store_address");
                                String store_image = store.getString("store_image");
                                String store_logo = store.getString("store_logo");
                                String postcode = store.getString("postcode");
                                String latitude = store.getString("latitude");
                                String longitude = store.getString("longitude");
                                String availability = store.getString("days_availability");
                                String is_delivery = store.getString("is_delivery");
                                String tag_line = store.getString("tag_line");
                                String is_commerce=store.getString("is_commerce");
                                String is_bookmark = store.getString("is_bookmark");
                                Log.e("bookmark",is_bookmark);
                                if (is_bookmark.equals("1")) {
                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                    img_heart.setTag(R.mipmap.ic_heart_dark);
                                    img_heart.setImageDrawable(myDrawable);
                                    Log.e("bookmarkcheck",is_bookmark);
                                } else if (is_bookmark.equals("0")) {
                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                    img_heart.setImageDrawable(myDrawable);
                                    Log.e("bookmarkcheck",is_bookmark);
                                }
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String cat_id = object.getString("cat_id");
                                    String sta = object.getString("status");
                                    String title = object.getString("cat_title");
                                    String type_image = object.getString("cat_image");
                                    categoryList.add(new CategoryModel(cat_id, title, type_image, ""));
                                    //arrayList.add(new StoreModel(store_id,store_type_id,store_name,distance,store_address,"",store_image,store_logo,postcode,latitude,longitude,total_cat));
                                }

                                String store_rating = store.getString("store_rating");
                                int review_count = Integer.parseInt(store.getString("review_count"));


                                txt_store_name.setText(store_name);
                                txt_tag_line.setText(tag_line);
                                txt_address.setText(store_address);
                                //txt_des.setText(description);
                                ratingBar.setRating(Float.valueOf(store_rating));
                                txt_rating.setText(store_rating);
                                if(review_count != 0)
                                    txt_rating_count.setText("("+review_count + " Reviews)");
                                else
                                    txt_rating_count.setText("(No Reviews)");
                                if (!store_logo.equals("")) {
                                    Picasso.with(mContext).load(store_logo).into(img_store);
                                }
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
                                                params.setMargins(0, 260, 0, 0);
                                                rel_main.setLayoutParams(params);
                                                int resourceid=(int)img_heart.getTag();
                                                if(resourceid == R.mipmap.ic_heartwhite){
                                                    //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heartwhite);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                                    //handler.sendEmptyMessage(200);
                                                }else{
                                                    // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heart_dark);

                                                    //handler.sendEmptyMessage(200);
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
                                                rel_main.setLayoutParams(params);
                                                int resourceid=(int)img_heart.getTag();
                                                if(resourceid == R.mipmap.ic_heartwhite){
                                                    //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_favourite);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heartwhite);
                                                    //handler.sendEmptyMessage(200);
                                                }else{
                                                    // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                                                    Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                                                    img_heart.setImageDrawable(myDrawable);
                                                    img_heart.setTag(R.mipmap.ic_heart_dark);

                                                    //handler.sendEmptyMessage(200);
                                                }
                                                Drawable backarrow = getResources().getDrawable(R.mipmap.back_black);
                                                img_back.setImageDrawable(backarrow);
                                            }
                                            state = State.IDLE;

                                        }
                                    }
                                });
                                setupViewPager(categoryList);
                            } else {

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
                            progressBar.setVisibility(View.GONE);
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
            } else if (msg.what == 200) {
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("cat_id", categoryId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("disresponse", sendata.toString());
                productList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getCategoryProducts", sendata, new Response.Listener<JSONObject>() {
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
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String product_id = object.getString("product_id");
                                    String store_id = object.getString("store_id");
                                    String cat_id = object.getString("cat_id");
                                    String product_name = object.getString("product_name");
                                    String product_price = object.getString("product_price");
                                    String product_image = object.getString("product_image");
                                    String product_description = object.getString("product_description");
                                    String sta = object.getString("status");
                                    if (dbHelper.checkproduct(product_id) > 0) {
                                        Log.e("getting value", dbHelper.fetchproduct(product_id) + "");
                                        productList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, dbHelper.fetchproduct(product_id) + ""));
                                    } else {
                                        Log.e("getting value 1", dbHelper.fetchproduct(product_id) + "");
                                        //arrayList.add(new ProductModel(ProductID, CostPrice, Name, SalePrice, Description,"0",product_image,subProductsList));
                                        productList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, "0"));
                                    }
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                product_RecyclerView.setLayoutManager(mLayoutManager);
                                product_RecyclerView.setItemAnimator(new DefaultItemAnimator());
                                ProductListAdapter adapeter = new ProductListAdapter(mContext, productList,btn_viewbasket);
                                product_RecyclerView.setAdapter(adapeter);
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
            } else if (msg.what == 300) {
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
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
                        Toast.makeText(ProductActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(ProductActivity.this).add(jsonObjectRequest);
            }
        }
    };
}