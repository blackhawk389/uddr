package com.uk.uddr.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.adapter.PhotoAdapter;
import com.uk.uddr.fragment.HomeFragment;
import com.uk.uddr.fragment.RatingListFragment;
import com.uk.uddr.activity.WriteReviewFragment;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.uk.uddr.utils.Utils.user_image;

public class FeaturePartnerProfile extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    View view;
    SharedPreferences sharedPreferences;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static int MY_PERMISSIONS_REQUEST_LOCATION = 5000;
    ProgressBar progressBar;
    Context mContext;
    RelativeLayout rel_back;
    ImageView img_fav, partner_image, user_profile_image, img_mark, img_fav_dark;
    TextView txt_address, txt_desciption, txt_profile_name, txt_user_time, txt_userreview,
            txt_read_rev, txt_review, txt_user_name, txt_mail, txt_phone, txt_whtsapp;
    Button btn_profile_menu;
    TextView btn_submit_review;
    String store_id, store_type_id, store_type;
    RecyclerView photo_list;
    String store_rating, review_count, name, user_rating, user_review, user_created_at, u_id, user_store_id, user_id;
    RatingBar ratingBar, user_review_ratingBar;
    ArrayList<PhotoModel> arrayListphoto;
    //    Double currentLatitude = 0.0, currentLongitude = 0.0;
    RelativeLayout relativelayout;
    private double currentLatitude;
    private double currentLongitude;
    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    RelativeLayout rel_review, rel_certification;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_partnerprofile, container, false);
        mContext = view.getContext();
        sharedPreferences = getContext().getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            store_id = bundle.getString("store_id");

        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fragment = new HomeFragment();
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes
                    HomeActivity.changeicon(1);
                    return true;
                }
                return false;
            }
        });

        checkpremission("location");
        init();
        return view;
    }

    void checkpremission(String which){
        // Here, thisActivity is the current activity
        if(which.equals("location")){
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                }else {
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }else {
                //handler.sendEmptyMessage(100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

                if(requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(mContext, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }



    }

    private void init() {

        progressBar = view.findViewById(R.id.progressBar);
        rel_back = view.findViewById(R.id.rel_back);
        img_fav = view.findViewById(R.id.img_fav);
        partner_image = view.findViewById(R.id.partner_image);
        txt_address = view.findViewById(R.id.txt_address);
        txt_desciption = view.findViewById(R.id.txt_desciption);
        txt_profile_name = view.findViewById(R.id.txt_profile_name);
        txt_user_time = view.findViewById(R.id.txt_user_time);
        txt_userreview = view.findViewById(R.id.txt_userreview);
        txt_read_rev = view.findViewById(R.id.txt_read_rev);
        btn_profile_menu = view.findViewById(R.id.btn_profile_menu);
        btn_submit_review = view.findViewById(R.id.btn_submit_review);
        user_profile_image = view.findViewById(R.id.user_profile_image);
        photo_list = view.findViewById(R.id.photo_list);
        txt_review = view.findViewById(R.id.txt_review);
        ratingBar = view.findViewById(R.id.ratingBar);
        txt_user_name = view.findViewById(R.id.txt_user_name);
        user_review_ratingBar = view.findViewById(R.id.user_review_ratingBar);
        img_mark = view.findViewById(R.id.img_mark);
        relativelayout = view.findViewById(R.id.relativelayout);
        txt_mail = view.findViewById(R.id.txt_mail);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_whtsapp = view.findViewById(R.id.txt_whtsapp);
        rel_review = view.findViewById(R.id.rel_review);
        rel_certification = view.findViewById(R.id.rel_certification);
        btn_profile_menu.setVisibility(View.GONE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        img_fav_dark = view.findViewById(R.id.img_fav_dark);


        rel_back.setOnClickListener(clickListener);
        img_fav.setOnClickListener(clickListener);
        btn_profile_menu.setOnClickListener(clickListener);
        btn_submit_review.setOnClickListener(clickListener);
        user_profile_image.setOnClickListener(clickListener);
        txt_read_rev.setOnClickListener(clickListener);
        img_mark.setOnClickListener(clickListener);
//        img_fav_dark.setOnClickListener(clickListener);

        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(100);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Fragment fragment4;
            Bundle bundle4;
            FragmentManager fm4;
            FragmentTransaction fragmentTransaction4;
            switch (v.getId()) {
                case R.id.rel_back:
                    Fragment fragment = new HomeFragment();
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit(); // save the changes


                    break;


                case R.id.btn_submit_review:
/*
                    Fragment fragment1 = new WriteReviewFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("store_type_id", store_type_id);
                    bundle1.putString("store_id", store_id);
                    bundle1.putString("store_type", store_type);
                    fragment1.setArguments(bundle1);
                    FragmentManager fm1 = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();
                    fragmentTransaction1.replace(R.id.frameLayout, fragment1);
                    fragmentTransaction1.commit(); // save the changes
                    fragmentTransaction1.addToBackStack(null);*/
                    break;

                case R.id.img_fav:

                    int resourceid = (int) img_fav.getTag();
                    if (resourceid == R.mipmap.ic_heart_dark) {
                        Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_heart_dark);
                        img_fav.setTag(R.mipmap.ic_heart_dark);
                        img_fav.setImageDrawable(myDrawable);
                        handler.sendEmptyMessage(200);
                    } else if (resourceid == R.mipmap.ic_favourite) {
                        Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_favourite);
                        img_fav.setTag(R.mipmap.ic_favourite);
                        img_fav.setImageDrawable(myDrawable);
                        handler.sendEmptyMessage(200);
                    }

                    break;

                case R.id.img_mark:
                    String strUri = "http://maps.google.com/maps?q=loc:" + currentLatitude + "," + currentLongitude + " (" + name + ")";
                    Intent intent3 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent3.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent3);

                    break;


                case R.id.txt_read_rev:

                    Fragment fragment2 = new RatingListFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("store_type_id", store_type_id);
                    bundle2.putString("store_type", store_type);
                    bundle2.putString("store_id", store_id);
                    fragment2.setArguments(bundle2);
                    FragmentManager fm2 = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fm2.beginTransaction();
                    fragmentTransaction2.replace(R.id.frameLayout, fragment2);
                    fragmentTransaction2.commit();// save the changes
                    fragmentTransaction2.addToBackStack(null);
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
                arrayListphoto = new ArrayList<>();
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("store_id", store_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata", sendata.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("partnerprofile_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            relativelayout.setVisibility(View.VISIBLE);
//                           String store_certificates = response.getString("store_certificates");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject object = response.getJSONObject("data");
                                String store_id = object.getString("store_id");
                                String store_name = object.getString("store_name");
                                String store_email = object.getString("store_email");
                                String store_address = object.getString("store_address");
                                String store_phone = object.getString("store_phone");
                                String store_image = object.getString("store_image");
                                String postcode = object.getString("postcode");
                                String description = object.getString("description");
                                String delivery_text = object.getString("delivery_text");

                                String is_bookmark = object.getString("is_bookmark");
                                if (is_bookmark.equals("1")) {
                                    Drawable myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_heart_dark);
                                    img_fav.setTag(R.mipmap.ic_heart_dark);
                                    img_fav.setImageDrawable(myDrawable);
                                } else if (is_bookmark.equals("0")) {
                                    Drawable myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_favourite);
                                    img_fav.setTag(R.mipmap.ic_favourite);
                                    img_fav.setImageDrawable(myDrawable);
                                }
                                txt_mail.setText(store_email);
                                currentLatitude = Double.valueOf(object.getString("latitude"));
                                currentLongitude = Double.valueOf(object.getString("longitude"));
                                if(mMap!=null){
                                    setMaker(currentLatitude,currentLongitude);
                                }

                                store_rating = object.getString("store_rating");
                                int review_count = Integer.parseInt(object.getString("review_count"));

                                JSONArray store_r=object.getJSONArray("store_review");
                                if(store_r.length()>0){
                                    JSONObject store_review = store_r.getJSONObject(0);
                                    user_store_id = store_review.getString("store_id");
                                    user_id = store_review.getString("user_id");
                                    user_rating = store_review.getString("rating");
                                    user_review = store_review.getString("review");
                                    user_created_at = store_review.getString("created_at");

                                    JSONObject userinfo = store_review.getJSONObject("userinfo");
                                    name = userinfo.getString("name");
                                    user_image = userinfo.getString("user_image");
                                    txt_user_time.setText(user_created_at);
                                    user_review_ratingBar.setRating(Float.valueOf(user_rating));
                                    txt_userreview.setText(user_review);
                                    txt_user_name.setText(name);
                                    if (!user_image.equals("")) {
                                        Picasso.with(mContext).load(user_image).into(user_profile_image);
                                    }
                                    rel_review.setVisibility(View.VISIBLE);
                                    if(review_count>1){
                                        txt_read_rev.setVisibility(View.VISIBLE);
                                    }else{
                                        txt_read_rev.setVisibility(View.GONE);
                                    }

                                }else{
                                    rel_review.setVisibility(View.GONE);
                                    txt_user_name.setVisibility(View.GONE);
                                    user_profile_image.setVisibility(View.GONE);
                                    txt_read_rev.setVisibility(View.GONE);
                                }

                                txt_phone.setText(store_phone);
                                JSONArray certi_images = object.getJSONArray("store_certificates");
                                if(certi_images.length()>0){
                                    for (int i = 0; i < certi_images.length(); i++) {
                                        JSONObject imageobj = certi_images.getJSONObject(i);
                                        String imagePath = imageobj.getString("imagePath");
                                        String imageId = imageobj.getString("imageId");
                                        arrayListphoto.add(new PhotoModel(imagePath, imageId));
                                    }
                                    rel_certification.setVisibility(View.VISIBLE);
                                }else{
                                    rel_certification.setVisibility(View.GONE);
                                }


                                PhotoAdapter adapter = new PhotoAdapter(mContext, arrayListphoto);
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                                photo_list.setLayoutManager(horizontalLayoutManagaer);
                                photo_list.setVisibility(View.VISIBLE);
                                photo_list.setAdapter(adapter);



                                txt_profile_name.setText(store_name);
                                txt_address.setText(store_address);
                                txt_desciption.setText(description);
                                ratingBar.setRating(Float.valueOf(store_rating));
                                if(review_count != 0)
                                    txt_review.setText(review_count + " Reviews");
                                else
                                    txt_review.setText("No Reviews");
                                if (!store_image.equals("")) {
                                    Picasso.with(mContext).load(store_image).into(partner_image);
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
            } else if (msg.what == 200) {
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
                            relativelayout.setVisibility(View.VISIBLE);
                            if (status.equals("1")) {
                            } else {
                                String messaege = response.getString("messaege");
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }

        }

    };



    void setMaker(Double lat,Double longi){
        LatLng loca = new LatLng(lat, longi);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loca);

        //        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loca));
        //        mMap.animateCamera(Cam  eraUpdateFactory.zoomTo(11));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(13)
                .bearing(30)
                .tilt(45)
                .build()));

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {



        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


//        if (location != null) {
//
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
//
//            // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//
//        }

        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng loca = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loca);

//        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.setMyLocationEnabled(true);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loca));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(13)
                .bearing(30)
                .tilt(45)
                .build()));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        if (location != null) {

            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((AppCompatActivity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }


}
