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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.adapter.SearchAdapter;
import com.uk.uddr.adapter.SearchPartnerAdapter;
import com.uk.uddr.adapter.StoreAdapter;
import com.uk.uddr.model.SearchModelCategory;
import com.uk.uddr.model.SearchPartnersModel;
import com.uk.uddr.model.StoreModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchPartners extends Fragment {

    View view;
    Context context;
    SharedPreferences sharedPreferences;
    RecyclerView partnersListView;
    ProgressBar progressBar;
    EditText edt_search;
    String keyword="";
    ArrayList<StoreModel> partnersList;
    ImageView img_cross;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.searchpartners,container,false);
        context=view.getContext();
        sharedPreferences=context.getSharedPreferences(Utils.sharedfilename,Context.MODE_PRIVATE);
        init();
        return view;
    }

    void init(){
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        partnersListView=(RecyclerView)view.findViewById(R.id.recycler_partners);
        edt_search=(EditText)view.findViewById(R.id.edt_search);
        img_cross=(ImageView)view.findViewById(R.id.img_cross);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword=s.toString();
                if(!keyword.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessage(100);
                }
                Log.e("keyword",keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword=s.toString();
                if(!keyword.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessage(100);
                }
            }
        });
        edt_search.requestFocus();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_search, 0);
        img_cross.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.img_cross:
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();
                    break;
            }
        }
    };

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {

                JSONObject jsonObject=new JSONObject();
                try {

                    jsonObject.put("user_id",sharedPreferences.getString(Utils.user_id, ""));
                    jsonObject.put("keyword",keyword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("senddata",jsonObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getSearchStores", jsonObject, new Response.Listener<JSONObject>() {
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
                                partnersList=new ArrayList<>();
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String store_id = object.getString("store_id");
                                    String store_type_id = object.getString("store_type_id");
                                    String store_name = object.getString("store_name");
                                    String store_type = object.getString("store_type");
                                    String distance = object.getString("distance");
                                    String store_address = object.getString("store_address");
                                    //String store_phone = object.getString("store_phone");
                                    String store_image = object.getString("store_image");
                                    String store_logo = object.getString("store_logo");
                                    String postcode = object.getString("postcode");
                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String total_cat = object.getString("total_cat");
                                    String store_rating = object.getString("store_rating");
                                    String review_count = object.getString("review_count");
                                    partnersList.add(new StoreModel(store_id, store_type_id, store_name, "", store_address, store_type, store_image, store_logo, postcode, latitude, longitude, total_cat, store_rating, review_count));
                                    //partnersList.add(new StoreModel(store_id,store_type_id,store_type,store_name,store_address,delivery_text,store_image,store_logo,postcode,latitude,longitude,tag_line,is_commerce,store_rating,review_count));
                                }

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                partnersListView.setLayoutManager(mLayoutManager);
                                partnersListView.setItemAnimator(new DefaultItemAnimator());
                                //SearchPartnerAdapter searchPartnerAdapter=new SearchPartnerAdapter(context,partnersList);
                                StoreAdapter adapeter = new StoreAdapter(context, partnersList);
                                partnersListView.setAdapter(adapeter);
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
