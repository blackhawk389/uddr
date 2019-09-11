package com.uk.uddr.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.uk.uddr.activity.LoginActivity;
import com.uk.uddr.activity.PartnerProfileActivity;
import com.uk.uddr.activity.ZipCode;
import com.uk.uddr.fragment.PartnerFragment;
import com.uk.uddr.fragment.PartnerprofileFragment;
import com.uk.uddr.model.FeaturePartnerProfile;
import com.uk.uddr.model.FeatureStoreModel;
import com.uk.uddr.model.HomeModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeatureStoreAdapter extends RecyclerView.Adapter<FeatureStoreAdapter.MyViewHolder> {

    Context context;
    ArrayList<FeatureStoreModel> featureStoreList;
    SharedPreferences sharedPreferences;
    String store_id;

    public FeatureStoreAdapter(Context context, ArrayList<FeatureStoreModel> featureStoreList){
        this.context=context;
        this.featureStoreList=featureStoreList;
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        return new MyViewHolder(inflater.inflate(R.layout.feature_store_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final FeatureStoreModel model = featureStoreList.get(i);

        myViewHolder.title.setText(model.getStore_name());
        if (!model.getStore_image().equals("")) {
            Picasso.with(context).load(model.getStore_image()).into(myViewHolder.type_image);
        }
        myViewHolder.txt_rating.setText(model.getStore_rating());
        myViewHolder.txt_review.setText("("+model.getReview_count()+" Reviews)");
        myViewHolder.ratingBar.setRating(Float.valueOf(model.getStore_rating()));
        myViewHolder.txt_address.setText(model.getStore_address());
        myViewHolder.txt_tag_line.setText(model.getTag_line());
        if(model.getIs_bookmark().equals("1")){
            Drawable myDrawable = context.getResources().getDrawable(R.mipmap.ic_heart_dark);
            myViewHolder.img_heart.setImageDrawable(myDrawable);
            myViewHolder.img_heart.setTag(R.mipmap.ic_heart_dark);
        }else{
            Drawable myDrawable = context.getResources().getDrawable(R.mipmap.ic_favourite);
            myViewHolder.img_heart.setImageDrawable(myDrawable);
            myViewHolder.img_heart.setTag(R.mipmap.ic_favourite);
        }

        myViewHolder.img_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = "";
                user_id = sharedPreferences.getString(Utils.user_id,"");
                if(user_id.equals("")){
                    Intent intent = new Intent(context, LoginActivity.class);
                    //intent.putExtra("CarID",model.getCarID());
                    context.startActivity(intent);

                }else{
                    int resourceid=(int)myViewHolder.img_heart.getTag();
                    if(resourceid == R.mipmap.ic_heart_dark){
                        //  Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
                        Drawable myDrawable = context.getResources().getDrawable(R.mipmap.ic_favourite);
                        myViewHolder.img_heart.setImageDrawable(myDrawable);
                        myViewHolder.img_heart.setTag(R.mipmap.ic_favourite);
                        store_id=model.getStore_id();
                        handler.sendEmptyMessage(200);
                    }else{
                        // Toast.makeText(context,"UnLike",Toast.LENGTH_SHORT).show();
                        Drawable myDrawable = context.getResources().getDrawable(R.mipmap.ic_heart_dark);
                        myViewHolder.img_heart.setImageDrawable(myDrawable);
                        myViewHolder.img_heart.setTag(R.mipmap.ic_heart_dark);
                        store_id=model.getStore_id();
                        handler.sendEmptyMessage(200);
                    }
                }

            }
        });

        myViewHolder.card_storetype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          /*      Fragment fragment = new FeaturePartnerProfile();
                Bundle bundle = new Bundle();
                bundle.putString("store_id", model.getStore_id());
                fragment.setArguments(bundle);
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit(); // save the changes*/
                Intent intent = new Intent(context,PartnerProfileActivity.class);
                intent.putExtra("store_id", model.getStore_id());
                intent.putExtra("store_name", model.getStore_name());
                context.startActivity(intent);




                   /* Fragment fragment = new PartnerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("store_type_id", model.getId());
                    bundle.putString("store_image", model.getType_image());
                    bundle.putString("store_type", model.getTitle());
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes
*/


            }
        });
    }

    @Override
    public int getItemCount() {
        return featureStoreList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title,txt_address,txt_rating,txt_review,txt_tag_line;
        ImageView type_image,img_heart;
        RatingBar ratingBar;
        CardView card_storetype;

        // Button btn_shopnow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_review = itemView.findViewById(R.id.txt_review);
            txt_rating = itemView.findViewById(R.id.txt_rating);
            txt_tag_line = itemView.findViewById(R.id.txt_tag_line);
            title = itemView.findViewById(R.id.title);
            txt_address = itemView.findViewById(R.id.txt_address);
            card_storetype = itemView.findViewById(R.id.card_storetype);
            //btn_shopnow=itemView.findViewById(R.id.btn_shopnow);
            img_heart=itemView.findViewById(R.id.img_heart);
            type_image = itemView.findViewById(R.id.type_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);


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
