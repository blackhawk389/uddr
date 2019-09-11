package com.uk.uddr.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.uk.uddr.model.AddressModel;
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {


    Context context;
    ArrayList<AddressModel> arrayList;
    ArrayList<ViewHolder> holdersList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String address_id;

    public AddressAdapter(Context context, ArrayList<AddressModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        holdersList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Utils.sharedfilename, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new AddressAdapter.ViewHolder(inflater.inflate(R.layout.item_address, viewGroup, false));
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final AddressAdapter.ViewHolder viewHolder, final int i) {
        final AddressModel model = arrayList.get(i);
        holdersList.add(viewHolder);
        viewHolder.txt_flat.setText(model.getName() + " " + model.getAddress());
        //viewHolder.txt_address.setVisibility(View.GONE);
        viewHolder.txt_address.setText(model.getCity() + "," + model.getState());
        viewHolder.txt_number.setText(model.getPostcode());
        viewHolder.txt_title.setText(model.getLable());

        if (model.getAddress_id().equals(sharedPreferences.getString(Utils.address_id, ""))) {
            viewHolder.img_tick.setVisibility(View.VISIBLE);
            //viewHolder.image_cardview.setBackgroundColor(context.getColor(R.color.light_green));
        } else {
            viewHolder.img_tick.setVisibility(View.GONE);
           //viewHolder.image_cardview.setBackgroundColor(context.getColor(R.color.light_grey));
        }

        viewHolder.image_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.img_tick.setVisibility(View.VISIBLE);
//                viewHolder.image_cardview.setBackgroundColor(context.getColor(R.color.light_green));
                holdersList.remove(i);
                for (int j = 0; j < holdersList.size(); j++) {
                    if (j != i) {
                        ViewHolder vh = holdersList.get(j);
                        vh.img_tick.setVisibility(View.GONE);
                        vh.image_cardview.setBackgroundColor(context.getColor(R.color.light_grey));
                    }
                }
                editor = sharedPreferences.edit();
                editor.putString(Utils.address_id, model.getAddress_id());
                editor.putString(Utils.address_name, model.getName() + " " + model.getAddress());
                editor.putString(Utils.address_line, model.getCity() + "," + model.getState());
                editor.commit();
                viewHolder.relative_click.setVisibility(View.VISIBLE);
                Log.e("addressselect",model.getAddress_id()+" - "+model.getCity() + "," + model.getState());
               // viewHolder.image_cardview.setBackgroundColor(context.getColor(R.color.light_green));
                ((Activity) context).finish();
            }
        });
        viewHolder.img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup(viewHolder.img_menu,i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txt_flat, txt_address, txt_number,txt_title;
        CardView image_cardview;
        RelativeLayout relative_click;
        ImageView img_tick,img_menu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txt_address = itemView.findViewById(R.id.txt_address);
            img_tick = itemView.findViewById(R.id.icon_tick);
            img_menu = itemView.findViewById(R.id.img_menu);
            txt_flat = itemView.findViewById(R.id.txt_flat);
            txt_number = itemView.findViewById(R.id.txt_number);
            txt_title = itemView.findViewById(R.id.txt_title);
            image_cardview = itemView.findViewById(R.id.image_cardview);
            relative_click = itemView.findViewById(R.id.relative_click);

        }
    }


    void popup(View dots,final int pos){

        final AddressModel model = arrayList.get(pos);
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, dots);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.delete:
                        Toast.makeText(context,"You Clicked : " + model.getAddress_id(), Toast.LENGTH_SHORT).show();
                        address_id=model.getAddress_id();
                        handler.sendEmptyMessage(200);
                        arrayList.remove(pos);
                        notifyDataSetChanged();
                        break;
                }

                return true;
            }
        });

        popup.show();//showing popup menu
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
                    sendata.put("address_id", address_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "deleteUserAddress", sendata, new Response.Listener<JSONObject>() {
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

