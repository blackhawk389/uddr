package com.uk.uddr.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.uk.uddr.model.ProductListModel;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ProductListFragment extends android.support.v4.app.Fragment {


    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ArrayList<ProductListModel> arrayList;
    RecyclerView recycleview;
    String cat_id, cat_name, cat_image, store_id, store_type_id;
    DBHelper dbHelper;
    Context mContext;
    ImageView img_back, img_store;
    TextView txt_catname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_list, container, false);
        mContext = view.getContext();
        Bundle bundle = getArguments();
        if (bundle != null) {
            store_id = bundle.getString("store_id");
            store_type_id = bundle.getString("store_type_id");
            cat_id = bundle.getString("cat_id");
            cat_name = bundle.getString("cat_name");
            cat_image = bundle.getString("cat_image");
        }
        return view;
    }

    private void init() {
        dbHelper = new DBHelper(mContext);
        txt_catname = (TextView) view.findViewById(R.id.txt_category);
        img_back = (ImageView) view.findViewById(R.id.img_back);
        img_store = (ImageView) view.findViewById(R.id.img_store_background);
        recycleview = view.findViewById(R.id.recycleview);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        txt_catname.setText(cat_name);
        if (!cat_image.equals("")) {
            Picasso.with(mContext).load(cat_image).into(img_store);
        }
        img_back.setOnClickListener(clickListener);

//        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = mContext.getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        Log.e("checking", cat_image + "working" + cat_id);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_back:
                    Fragment fragment = new StoreDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("store_id", store_id);
                    bundle.putString("store_type_id", store_type_id);
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
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
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("cat_id", cat_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("disresponse", sendata.toString());
                arrayList = new ArrayList<>();
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
                                        arrayList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, dbHelper.fetchproduct(product_id) + ""));
                                    } else {
                                        Log.e("getting value 1", dbHelper.fetchproduct(product_id) + "");
                                        //arrayList.add(new ProductModel(ProductID, CostPrice, Name, SalePrice, Description,"0",product_image,subProductsList));
                                        arrayList.add(new ProductListModel(product_id, store_id, cat_id, product_name, product_price, "", "", product_description, product_image, sta, "0"));
                                    }

                                }

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                recycleview.setLayoutManager(mLayoutManager);
                                recycleview.setItemAnimator(new DefaultItemAnimator());
                                ProductListAdapter adapeter = new ProductListAdapter(mContext, arrayList,null);
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
