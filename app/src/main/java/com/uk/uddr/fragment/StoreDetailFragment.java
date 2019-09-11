package com.uk.uddr.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.adapter.ProductListAdapter;
import com.uk.uddr.model.CategoryModel;
import com.uk.uddr.model.ProductListModel;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreDetailFragment extends android.support.v4.app.Fragment {


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
    ImageView img_back, img_store, img_slogo;
    TextView txt_saddress, txt_sname, txt_sdistance, txt_sdesc;
    DBHelper dbHelper;
    View view;
    Context mContext;
    RelativeLayout rel_back;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.store_detail, container, false);
        mContext = view.getContext();
        sharedPreferences = mContext.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
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

    @Override
    public void onResume() {
        super.onResume();

    }

    void init() {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        product_RecyclerView = (RecyclerView) view.findViewById(R.id.product_RecyclerView);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        rel_back = (RelativeLayout) view.findViewById(R.id.rel_back);
        img_back = (ImageView) view.findViewById(R.id.img_back);
        img_store = (ImageView) view.findViewById(R.id.img_store_background);
        img_slogo = (ImageView) view.findViewById(R.id.store_logo);
        txt_sname = (TextView) view.findViewById(R.id.store_name);
        txt_saddress = (TextView) view.findViewById(R.id.store_address);
        txt_sdesc = (TextView) view.findViewById(R.id.store_desc);
        txt_sdistance = (TextView) view.findViewById(R.id.store_distance);
        //cat_RecyclerView=(RecyclerView)view.findViewById(R.id.category_recyclerview);
        img_back.setOnClickListener(clickListener);
        rel_back.setOnClickListener(clickListener);
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
    }

    private void setupViewPager(ArrayList<CategoryModel> cList) {
        //ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),cList);
        for (int i = 0; i < cList.size(); i++) {
            CategoryModel cm = cList.get(i);
            tabLayout.addTab(tabLayout.newTab().setText(cm.getCat_title()));
            //adapter.addFrag(new ProductListFragment(),cm.getCat_title());
        }
        // viewPager.setAdapter(adapter);
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
                case R.id.rel_back:
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
                                txt_sname.setText(store_name);
                                txt_saddress.setText(store_address);
                                txt_sdistance.setText(distance + " mi");
                                txt_sdistance.setVisibility(View.GONE);
                                if (!store_image.equals("")) {
                                    Picasso.with(mContext).load(store_image).into(img_slogo);
                                }
                                txt_sdesc.setVisibility(View.GONE);
                                if (!store_logo.equals("")) {
                                    Picasso.with(mContext).load(store_logo).into(img_store);
                                }
//                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//                                cat_RecyclerView.setLayoutManager(mLayoutManager);
//                                cat_RecyclerView.setItemAnimator(new DefaultItemAnimator());
//                                CategoryAdapter categoryAdapter=new CategoryAdapter(mContext,categoryList,store_id,store_type_id);
//                                //StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
//                                cat_RecyclerView.setAdapter(categoryAdapter);
//
//
//
//
//
//                                JSONArray data = response.getJSONArray("data");
//                                for (int i = 0; i < data.length(); i++) {
//                                    JSONObject object = data.getJSONObject(i);
//                                    String cat_id = object.getString("cat_id");
//                                    String sta = object.getString("status");
//                                    String title = object.getString("cat_title");
//                                    String type_image = object.getString("cat_image");
//                                    categoryList.add(new CategoryModel(cat_id,title,type_image,sta));
//                                }
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
                                ProductListAdapter adapeter = new ProductListAdapter(mContext, productList,null);
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
            }
        }
    };

   /* @SuppressLint("HandlerLeak")
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

                categoryList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreCategories", sendata, new Response.Listener<JSONObject>() {
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
                                JSONObject store=response.getJSONObject("store_info");
                                String s_id = store.getString("store_id");
                                String store_name = store.getString("store_name");
                                String distance = store.getString("distance");
                                String store_address = store.getString("store_address");
                                String store_image = store.getString("store_image");
                                String store_logo = store.getString("store_logo");
                                String postcode = store.getString("postcode");
                                String latitude = store.getString("latitude");
                                String longitude = store.getString("longitude");
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String cat_id = object.getString("cat_id");
                                    //String sta = object.getString("status");
                                    String title = object.getString("cat_title");
                                    String type_image = object.getString("cat_image");
                                    categoryList.add(new CategoryModel(cat_id,title,type_image,""));
                                    //arrayList.add(new StoreModel(store_id,store_type_id,store_name,distance,store_address,"",store_image,store_logo,postcode,latitude,longitude,total_cat));
                                }
                                txt_sname.setText(store_name);
                                txt_saddress.setText(store_address);
                                txt_sdistance.setText(distance+" mi");
                                if(!store_image.equals("")) {
                                    Picasso.with(mContext).load(store_image).into(img_slogo);
                                }
                                txt_sdesc.setVisibility(View.GONE);
                                if(!store_logo.equals("")) {
                                    Picasso.with(mContext).load(store_logo).into(img_store);
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                cat_RecyclerView.setLayoutManager(mLayoutManager);
                                cat_RecyclerView.setItemAnimator(new DefaultItemAnimator());
                                CategoryAdapter categoryAdapter=new CategoryAdapter(mContext,categoryList,store_id,store_type_id);
                                //StoreAdapter adapeter = new StoreAdapter(mContext, arrayList);
                                cat_RecyclerView.setAdapter(categoryAdapter);
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
    };*/

}
