package com.uk.uddr.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.uk.uddr.model.RatingModel;
import com.uk.uddr.utils.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {

    Context context;
    ArrayList<RatingModel> arrayList;
    SharedPreferences sharedPreferences;
    String store_id = "";

    public RatingAdapter(Context context, ArrayList<RatingModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RatingAdapter.MyViewHolder holder, int position) {
        final RatingModel model = arrayList.get(position);
        holder.txt_name.setText(model.getName());
        holder.txt_des.setText(model.getReview());
        holder.txt_time.setText(model.getCreated_at());
        Picasso.with(context).load(model.getUser_image()).error(R.drawable.dummy_round).into(holder.profile_image);
        holder.review_ratingBar.setRating(Float.valueOf(model.getRating()));
        holder.txt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store_id = model.getStore_id();
                handler.sendEmptyMessage(100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView txt_name, txt_time, txt_des, txt_report;
        RatingBar review_ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_des = (TextView) itemView.findViewById(R.id.txt_des);
            txt_report = (TextView) itemView.findViewById(R.id.txt_report);
            review_ratingBar = (RatingBar) itemView.findViewById(R.id.review_ratingBar);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString("user_id",""));
                    sendata.put("loginToken",sharedPreferences.getString("loginToken",""));
                    sendata.put("store_id",store_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Utils.base_url + "reportReview", sendata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Home Response",response.toString());
                            String status=response.getString("status");
                            if(status.equals("1")){
                                String messaege="Review reported sucessfully";
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.show();
                            }else{
                                String messaege=response.getString("message");
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
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
                        Toast.makeText(context,"Unknown Error",Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(context).add(jsonObjectRequest);
            }
        }
    };
}




