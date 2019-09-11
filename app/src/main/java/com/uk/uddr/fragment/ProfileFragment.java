package com.uk.uddr.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.uk.uddr.activity.EditProfileActivity;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.LoginActivity;
import com.uk.uddr.activity.SelectAddress;
import com.uk.uddr.activity.UpdatePasswordActivity;
import com.uk.uddr.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends android.support.v4.app.Fragment {
    public ProfileFragment() {
    }

    ProgressBar progressBar;
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView btn_logout;
    TextView btn_editprofile;
    RelativeLayout relative_resetpassword;
    ImageView img_profile;
    TextView txt_name, txt_email;
    RelativeLayout relativelayout,rel_shipping,rel_customer,rel_rate,rel_resetpassword,rel_heart;
    Context mContext;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext=view.getContext();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Log.e("back_fargment","working");
                    Fragment home=new HomeFragment();
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = mContext.getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
        HomeActivity.changeicon(4);

    }

    private void init() {

        progressBar = view.findViewById(R.id.progressBar);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_editprofile = view.findViewById(R.id.btn_editprofile);
        relative_resetpassword = view.findViewById(R.id.relative_resetpassword);
        img_profile = view.findViewById(R.id.img_profile);
        txt_name = view.findViewById(R.id.txt_name);
        txt_email = view.findViewById(R.id.txt_email);
        relativelayout = view.findViewById(R.id.relativelayout);
        rel_shipping = view.findViewById(R.id.rel_shipping);
        rel_customer = view.findViewById(R.id.rel_customer);
        rel_rate = view.findViewById(R.id.rel_rate);
        rel_resetpassword = view.findViewById(R.id.rel_resetpassword);
        rel_heart = view.findViewById(R.id.rel_heart);



        btn_logout.setOnClickListener(clickListener);
        btn_editprofile.setOnClickListener(clickListener);
        relativelayout.setOnClickListener(clickListener);
        rel_shipping.setOnClickListener(clickListener);
        rel_customer.setOnClickListener(clickListener);
        rel_rate.setOnClickListener(clickListener);
        rel_heart.setOnClickListener(clickListener);
        rel_resetpassword.setOnClickListener(clickListener);
    }



    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_logout:
                    editor=sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    ((Activity)mContext).finish();
                    break;

                case R.id.btn_editprofile:
                    Intent intent1 = new Intent(mContext, EditProfileActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.rel_resetpassword:
                    Intent intent2 = new Intent(mContext, UpdatePasswordActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.rel_shipping:
                    Intent address=new Intent(mContext, SelectAddress.class);
                    startActivity(address);
                    break;
                case R.id.rel_customer:
                    contactwhatsappsupport();
                    break;
                case R.id.rel_rate:
                    Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                    }
                    break;

                case R.id.rel_heart:

                    Fragment fragment = new FavouriteStoresFragment();
                    Bundle bundle = new Bundle();
                    fragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes
                    fragmentTransaction.addToBackStack(null);
                    break;
            }

        }
    };

    void contactwhatsappsupport(){
        //String contact = "+44 7565892455"; // use country code with your phone number
        String url = "https://chat.whatsapp.com/DQC9O15GSFt9o12mnZ0Huf";
        try {

            PackageManager pm = mContext.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mContext, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

       /* String url = "https://chat.whatsapp.com/DQC9O15GSFt9o12mnZ0Huf";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/

    }


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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getProfile", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("profileresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                relativelayout.setVisibility(View.VISIBLE);
                                JSONObject object = response.getJSONObject("data");
                                String id = object.getString("id");
                                String user_id = object.getString("user_id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String phone = object.getString("phone");
                                String user_image = object.getString("user_image");

                                if (!user_image.equals("")) {
                                    Picasso.with(mContext).load(user_image).into(img_profile);
                                }
                                txt_name.setText(name);
                                txt_email.setText(email);
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