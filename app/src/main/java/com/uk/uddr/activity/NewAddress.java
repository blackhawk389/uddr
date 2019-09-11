package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class NewAddress extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText edt_name, edt_streetname, edt_postalcode,edt_town,edt_county,edt_label;
    Button btn_addaddress,btn_newAddress_home,btn_newAddress_work,btn_newAddress_other;
    ProgressBar progressbar;
    ImageView img_back;
    Double lat=0.0,longi=0.0;
    //RelativeLayout relative_work,relative_home,relative_other;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();

    }

    private void init() {
        progressbar = findViewById(R.id.progressbar);
        edt_label= findViewById(R.id.edt_label);
        edt_name = findViewById(R.id.edt_name);
        edt_streetname = findViewById(R.id.edt_streetname);
        edt_town = findViewById(R.id.edt_town);
        edt_county = findViewById(R.id.edt_county);
        edt_postalcode = findViewById(R.id.edt_postcode);
        btn_addaddress = findViewById(R.id.btn_addaddress);
        img_back = findViewById(R.id.img_back);


        btn_addaddress.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);



    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_addaddress:
                    setValidates();
                    break;
                case R.id.img_back:
                    finish();
                    break;


            }
        }
    };

    private void setValidates() {
        if (edt_label.getText().toString().equals("")) {
            getlatlong(edt_postalcode.getText().toString());
            Toast.makeText(NewAddress.this, "Label name can't be empty", Toast.LENGTH_SHORT).show();
        } else if (edt_name.getText().toString().equals("")) {
            getlatlong(edt_postalcode.getText().toString());
            Toast.makeText(NewAddress.this, "Property name can't be empty", Toast.LENGTH_SHORT).show();
        } else if (edt_streetname.getText().toString().equals("")) {
            Toast.makeText(NewAddress.this, "Street name can't be empty", Toast.LENGTH_SHORT).show();
        }  else if (edt_county.getText().toString().equals("")) {
            Toast.makeText(NewAddress.this, "County can't be empty", Toast.LENGTH_SHORT).show();
        }  else if (edt_town.getText().toString().equals("")) {
            Toast.makeText(NewAddress.this, "Town can't be empty", Toast.LENGTH_SHORT).show();
        } else if (edt_postalcode.getText().toString().equals("")) {
            Toast.makeText(NewAddress.this, "PostalCode can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            getlatlong(edt_postalcode.getText().toString());
        }
    }

    void getAddress(String zipcode){
        final Geocoder geocoder = new Geocoder(this);
        //final String zip = "90210";
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                String message = String.format("Latitude: %f, Longitude: %f",
                        address.getLatitude(), address.getLongitude());
                lat=address.getLatitude();
                longi=address.getLongitude();
                Log.e("latlong",message);
                //edt_address.setText(address.getAddressLine(0));
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode of postcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
    }


    void getlatlong(String zipcode){
        final Geocoder geocoder = new Geocoder(this);
        //final String zip = "90210";
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                String message = String.format("Latitude: %f, Longitude: %f",
                        address.getLatitude(), address.getLongitude());
                lat=address.getLatitude();
                longi=address.getLongitude();

                Log.e("latlong",message);
                progressbar.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(100);
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode of postcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendData.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendData.put("lable", edt_label.getText().toString().trim());
                    sendData.put("house_name", edt_name.getText().toString().trim());
                    sendData.put("street", edt_streetname.getText().toString().trim());
                    sendData.put("town", edt_town.getText().toString().trim());
                    sendData.put("county", edt_county.getText().toString().trim());
                    sendData.put("latitude", lat);
                    sendData.put("longitude", longi);


                    sendData.put("postcode", edt_postalcode.getText().toString().trim());

                    Log.e("senddata", sendData.toString());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "saveUserAddress", sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressbar.setVisibility(View.GONE);
                            Log.e("newaddress_response", response.toString());
                            try {
                                String status = response.getString("status");
                                String messaege = response.getString("message");
                                if (status.equals("1")) {
//                                    JSONObject object = response.getJSONObject("data");

                                    progressbar.setVisibility(View.GONE);
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NewAddress.this);
                                    builder.setTitle("Alert");
                                    builder.setMessage(messaege);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent in = new Intent(NewAddress.this, SelectAddress.class);
                                            startActivity(in);
                                            finish();
                                        }
                                    });
                                    builder.show();

                                } else {
                                    progressbar.setVisibility(View.GONE);
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NewAddress.this);
                                    builder.setTitle("Alert");
                                    builder.setMessage(messaege);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
//                                             finish();
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
                        public void onErrorResponse(VolleyError volleyError) {
                            progressbar.setVisibility(View.GONE);
                            Log.e("error", volleyError.toString());
                            Log.e("errorMessage", volleyError.getMessage());
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
                    jsonObjectRequest.setShouldCache(false);
                    Volley.newRequestQueue(NewAddress.this).add(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
