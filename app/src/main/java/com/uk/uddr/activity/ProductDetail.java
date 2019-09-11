package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.devspark.appmsg.AppMsg;
import com.uk.uddr.R;
import com.uk.uddr.adapter.ImageSlideAdapter;
import com.uk.uddr.fragment.StoreDetailFragment;
import com.uk.uddr.model.DetailModel;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ProductDetail extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    Context mContext;
    RelativeLayout  img_min,img_max;
    String product_id = "",product_price,product_name,product_image,product_description;
    ArrayList<DetailModel> arrayListdetail;
    TextView txt_price, txtproduct_title, txtproduct_description,txt_count, txt_add;
    ViewPager viewPager;
    RelativeLayout relativelayout;
    RelativeLayout rel_btn;
    ImageView img_back;
    DBHelper dbHelper;
    CircleIndicator indicator;
    public static ArrayList<String> imagesList;
    int count=0;
    String store_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        mContext = this;
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        store_id=getIntent().getStringExtra("store_id");
        product_id=getIntent().getStringExtra("product_id");
        init();

    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        rel_btn = (RelativeLayout) findViewById(R.id.rel_button);
        txt_add = (TextView) findViewById(R.id.txt_add1);
        img_min = (RelativeLayout) findViewById(R.id.relative_minus);
        img_max = (RelativeLayout) findViewById(R.id.relative_plus);
        relativelayout = findViewById(R.id.relativelayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        txt_price = findViewById(R.id.detail_price);
        txtproduct_title = findViewById(R.id.product_title);
        txt_count = (TextView) findViewById(R.id.txt_count);
        txtproduct_description = findViewById(R.id.product_description);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.pager);
        dbHelper=new DBHelper(mContext);
        img_back.setOnClickListener(clickListener);
        txt_add.setOnClickListener(clickListener);
        img_max.setOnClickListener(clickListener);
        img_min.setOnClickListener(clickListener);

//        viewPager.setAdapter(new ImageSlideAdapter(mContext, imagesList));
//        indicator.setViewPager(viewPager);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);

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

                case R.id.relative_minus:
                    //int count = Integer.parseInt(model.getCount());
                    Log.e("checking","working"+count);
                    if (count > 1) {
                        Log.e("checking1","working"+count);
                        count--;
                        txt_count.setText(count+"");
                        Double dp = Double.parseDouble(product_price);
//                        int c = Integer.parseInt(model.getCount());
//                        int p = dp.intValue();
                        if (dbHelper.checkproduct(product_id) > 0) {
                            dbHelper.updateproduct(product_id, product_price,count+"");
                        } else {
                            dbHelper.addproduct(product_id, product_name, product_image, product_price, count+"", store_id, "storename", product_description);
                        }
                    } else if (count == 1) {
                        count--;
                        txt_count.setText(count+"");
                        txt_add.setVisibility(View.VISIBLE);
                        rel_btn.setVisibility(View.GONE);
                        if (dbHelper.checkproduct(product_id) > 0) {
                            dbHelper.updateproduct(product_id, product_price,count+"");
                            dbHelper.deleteproduct(product_id);
                        } else {
                            dbHelper.deleteproduct(product_id);
                        }
                    }
                    HomeActivity.updatecart();
                    break;
                case R.id.relative_plus:
                    count++;
                    txt_count.setText(count+"");
                    Double dp = Double.parseDouble(product_price);
//                    int c = Integer.parseInt(model.getCount());
//                    int p = dp.intValue();

                    if (dbHelper.checkproduct(product_id) > 0) {
                        dbHelper.updateproduct(product_id, product_price, count+"");
                    } else {
                        dbHelper.addproduct(product_id, product_name, product_image, product_price, count+"", store_id, "storename", product_description);
                    }

                    HomeActivity.updatecart();
                    break;

                case R.id.txt_add1:
                    if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
                        Intent login = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(login);
                    } else {
                        if (dbHelper.getStoreId().equals(store_id) || dbHelper.getStoreId().equals("-1")) {
                            AppMsg.makeText((Activity) mContext, "Product Added to Basket", AppMsg.STYLE_INFO).show();
                            count++;
                            txt_count.setText(count+"");
                            txt_add.setVisibility(View.GONE);
                            rel_btn.setVisibility(View.VISIBLE);
                            if (dbHelper.checkproduct(product_id) > 0) {
                                dbHelper.updateproduct(product_id, product_price, "1");
                            } else {
                                dbHelper.addproduct(product_id, product_name, product_image, product_price, "1", store_id, "storename", product_description);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Alert");
                            builder.setMessage(mContext.getResources().getString(R.string.store_error));
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    dbHelper.clearDB();
                                    AppMsg.makeText((Activity) mContext, "Product Added to Basket", AppMsg.STYLE_INFO).show();
                                    count=1;
                                    txt_count.setText(count+"");
                                    txt_add.setVisibility(View.GONE);
                                    rel_btn.setVisibility(View.VISIBLE);
                                    if (dbHelper.checkproduct(product_id) > 0) {
                                        dbHelper.updateproduct(product_id, product_price, "1");
                                    } else {
                                        dbHelper.addproduct(product_id, product_name, product_image, product_price, "1", store_id, "storename", product_description);
                                    }
                                    HomeActivity.updatecart();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                        HomeActivity.updatecart();
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
                arrayListdetail = new ArrayList<>();
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("product_id", product_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                imagesList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getProductDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("product_detailrespobse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                relativelayout.setVisibility(View.VISIBLE);
                                JSONObject object = response.getJSONObject("data");
                                product_id = object.getString("product_id");
                                String store_id = object.getString("store_id");
                                String cat_id = object.getString("cat_id");
                                product_name = object.getString("product_name");
                                product_price = object.getString("product_price");
                                product_description = object.getString("product_description");

                                JSONArray images = object.getJSONArray("product_image");
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject imageobj = images.getJSONObject(i);
                                    String imagePath = imageobj.getString("imagePath");
                                    String imageId = imageobj.getString("imageId");
                                    imagesList.add(imagePath);
                                    product_image=imagePath;
                                }
                                viewPager.setAdapter(new ImageSlideAdapter(mContext, imagesList));
                                CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
                                indicator.setViewPager(viewPager);

                                txtproduct_title.setText(product_name);
                                txtproduct_description.setText(product_description);
                                txt_price.setText("£" + product_price);

                                if (dbHelper.checkproduct(product_id) > 0) {
                                    Log.e("getting value", dbHelper.fetchproduct(product_id) + "");
                                    //productList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, dbHelper.fetchproduct(product_id) + ""));
                                    txt_add.setVisibility(View.GONE);
                                    rel_btn.setVisibility(View.VISIBLE);
                                    txt_count.setText(dbHelper.fetchproduct(product_id)+"");
                                    count=dbHelper.fetchproduct(product_id);
                                } else {
                                    Log.e("getting value 1", dbHelper.fetchproduct(product_id) + "");
                                    txt_add.setVisibility(View.VISIBLE);
                                    rel_btn.setVisibility(View.GONE);
                                    //arrayList.add(new ProductModel(ProductID, CostPrice, Name, SalePrice, Description,"0",product_image,subProductsList));
                                    //productList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, "0"));

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
