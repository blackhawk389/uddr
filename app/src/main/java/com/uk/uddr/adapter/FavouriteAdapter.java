package com.uk.uddr.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.uk.uddr.activity.PartnerProfileActivity;
import com.uk.uddr.model.FavouriteModel;
import com.uk.uddr.model.PhotoModel;
import com.uk.uddr.model.StoreModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {


    Context context;
    ArrayList<FavouriteModel> arrayList;
    SharedPreferences sharedPreferences;
    String store_id="";

    public FavouriteAdapter(Context context, ArrayList<FavouriteModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new FavouriteAdapter.ViewHolder(inflater.inflate(R.layout.item_favourite, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder viewHolder, final int i) {
        final FavouriteModel model = arrayList.get(i);
        viewHolder.txt_Fav_title.setText(model.getStore_name());
        viewHolder.txt_address.setText(model.getStore_address());
        viewHolder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store_id=model.getStore_id();
                arrayList.remove(i);
                notifyDataSetChanged();
                handler.sendEmptyMessage(200);
            }
        });
        viewHolder.review_ratingBar.setRating(Float.valueOf(model.getStore_rating()));
        Picasso.with(context).load(model.getStore_image()).error(R.drawable.dummy_round).into(viewHolder.productfavimage);
        viewHolder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PartnerProfileActivity.class);
                intent.putExtra("store_id", model.getStore_id());
                intent.putExtra("store_name", model.getStore_name());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txt_Fav_title, txt_storetype,txt_address;
        ImageView productfavimage,img_fav;
        RatingBar review_ratingBar;
        RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productfavimage = itemView.findViewById(R.id.productfavimage);
            txt_Fav_title = itemView.findViewById(R.id.txt_Fav_title);
            img_fav = itemView.findViewById(R.id.img_fav);
            txt_address = itemView.findViewById(R.id.txt_address);
            review_ratingBar = itemView.findViewById(R.id.review_ratingBar);
            rel_main=itemView.findViewById(R.id.rel_main);

        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(context).add(jsonObjectRequest);
            }
        }

    };

}

