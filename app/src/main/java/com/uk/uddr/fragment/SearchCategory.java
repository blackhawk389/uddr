package com.uk.uddr.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.uk.uddr.adapter.HomeAdapter;
import com.uk.uddr.adapter.SearchAdapter;
import com.uk.uddr.model.HomeModel;
import com.uk.uddr.model.SearchModelCategory;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchCategory extends Fragment {


    View view;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    Context context;
    RecyclerView topcategoriesListView,morecategoriesListView;
    ArrayList<SearchModelCategory> topcategoryList,morecategoryList;
    RelativeLayout rel_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.searchcategory,container,false);
        context=view.getContext();
        sharedPreferences=context.getSharedPreferences(Utils.sharedfilename,Context.MODE_PRIVATE);
        init();
        return view;
    }

    void init(){
        topcategoriesListView=(RecyclerView)view.findViewById(R.id.recycler_topcat);
        morecategoriesListView=(RecyclerView)view.findViewById(R.id.recycler_morecat);
        rel_search=(RelativeLayout)view.findViewById(R.id.rel_search);
        rel_search.setOnClickListener(clickListener);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        HomeActivity.changeicon(2);
    }

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.rel_search:
                    Fragment fragment = new SearchPartners();
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit(); // save the changes
                    break;
            }
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Utils.base_url + "getSearchStoreCategories", null, new Response.Listener<JSONObject>() {
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
                                topcategoryList=new ArrayList<>();
                                morecategoryList=new ArrayList<>();
                                JSONArray data = response.getJSONArray("top_cat_array");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String type_image = object.getString("type_image");
                                    String tag_line = object.getString("tag_line");
                                    String description = object.getString("description");
                                    String type_order = object.getString("type_order");
                                    String stat = object.getString("status");
                                    topcategoryList.add(new SearchModelCategory(id, title, type_image, description, tag_line, type_order, status));
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                RecyclerView.LayoutManager rvLayoutManager = new GridLayoutManager(context,2);
                                topcategoriesListView.setLayoutManager(rvLayoutManager);
                                topcategoriesListView.setItemAnimator(new DefaultItemAnimator());
                                SearchAdapter searchAdapter = new SearchAdapter(context, topcategoryList);
                                topcategoriesListView.setAdapter(searchAdapter);


                                JSONArray all_cat_array=response.getJSONArray("all_cat_array");
                                for (int i = 0; i < all_cat_array.length(); i++) {
                                    JSONObject object = all_cat_array.getJSONObject(i);
                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String type_image = object.getString("type_image");
                                    String tag_line = object.getString("tag_line");
                                    String description = object.getString("description");
                                    String type_order = object.getString("type_order");
                                    String stat = object.getString("status");
                                    morecategoryList.add(new SearchModelCategory(id, title, type_image, description, tag_line, type_order, status));
                                }
                                RecyclerView.LayoutManager rvLayoutManager1 = new GridLayoutManager(context,2);
                                morecategoriesListView.setLayoutManager(rvLayoutManager1);
                                morecategoriesListView.setItemAnimator(new DefaultItemAnimator());
                                SearchAdapter searchAdapter1 = new SearchAdapter(context, morecategoryList);
                                morecategoriesListView.setAdapter(searchAdapter1);



                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                    } }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(context).add(jsonObjectRequest);
            }
        }
    };
}
