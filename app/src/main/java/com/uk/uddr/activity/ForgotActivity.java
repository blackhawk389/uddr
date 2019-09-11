package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class ForgotActivity extends AppCompatActivity {

    EditText edit_forgotemail;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    Button btn_forgot;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();

    }

    private void init() {

        progressBar = findViewById(R.id.progressBar);
        edit_forgotemail = findViewById(R.id.edt_email);
        btn_forgot = findViewById(R.id.btn_forgot);
        img_back = findViewById(R.id.img_back);

        btn_forgot.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_forgot:
                    setValidates();
                    break;

                case R.id.img_back:
                    finish();
                break;

            }

        }
    };

    private void setValidates() {


        if (edit_forgotemail.getText().toString().trim().equals("")) {
            edit_forgotemail.setError("Input field can't be empty");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(100);
        }


    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {

                JSONObject sendata = new JSONObject();
                try {
                    sendata.put("email", edit_forgotemail.getText().toString().trim());
                    Log.e("forgot_param", sendata.toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "forgotPassword", sendata, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("forgot_response", response.toString());
                            try {
                                String status = response.getString("status");
                                String messaege = response.getString("message");
                                if (status.equals("1")) {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ForgotActivity.this);
                                    builder.setTitle("Alert");
                                    builder.setMessage(messaege);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            finish();
                                            cleardata();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ForgotActivity.this);
                                    builder.setTitle("Alert");
                                    builder.setMessage(messaege);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            // finish();
                                        }
                                    });
                                    builder.show();
                                }
                                progressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("error", volleyError.toString());
                            Log.e("errorMessage", volleyError.getMessage());
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
                    Volley.newRequestQueue(ForgotActivity.this).add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
    };

    private void cleardata() {
        edit_forgotemail.setText("");

    }
}

