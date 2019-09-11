package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

public class UpdatePasswordActivity extends AppCompatActivity {

    EditText etxt_oldpassword, etxtnew_password, etxt_cpassword;
    ProgressBar progressBar;
    Button btn_update;
    ImageView img_back;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
    }

    private void init() {

        progressBar = findViewById(R.id.progressBar);
        etxt_oldpassword = findViewById(R.id.etxt_oldpassword);
        etxtnew_password = findViewById(R.id.etxtnew_password);
        etxt_cpassword = findViewById(R.id.etxt_cpassword);
        btn_update = findViewById(R.id.btn_update);
        img_back = findViewById(R.id.img_back);
        btn_update.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_update:
                    setvalidates();
                    break;
                case R.id.img_back:
                    finish();
                    break;
            }

        }
    };

    private void setvalidates() {
        if (etxt_oldpassword.getText().toString().trim().equals("")) {
            Toast.makeText(UpdatePasswordActivity.this, "Please enter old password", Toast.LENGTH_SHORT).show();
        } else if (etxtnew_password.getText().toString().trim().equals("")) {
            Toast.makeText(UpdatePasswordActivity.this, "Please enter new password", Toast.LENGTH_SHORT).show();
        } else if (etxt_cpassword.getText().toString().trim().equals("")) {
            Toast.makeText(UpdatePasswordActivity.this, "Please enter Confirm password", Toast.LENGTH_SHORT).show();
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
                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("new_password", etxtnew_password.getText().toString());
                    sendata.put("old_password", etxt_oldpassword.getText().toString());
                    sendata.put("new_password", etxt_cpassword.getText().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "changePassword", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("Passwordresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage(messaege);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();

                                    }
                                });
                                builder.show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordActivity.this);
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
                        Toast.makeText(UpdatePasswordActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(UpdatePasswordActivity.this).add(jsonObjectRequest);
            }
        }
    };

    private void cleardata() {
//               etxt_oldpassword.setText("");
//        etxtnew_password.setText("");
//        etxt_cpassword.setText("");

    }
}
