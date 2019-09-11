package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText edit_username, edit_email, edit_password;
    Button btn_sign;
    TextView text_termcondition;
    ProgressBar progressbar;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
    }

    private void init() {
        edit_username = findViewById(R.id.edt_name);
        edit_email = findViewById(R.id.edt_email);
        edit_password = findViewById(R.id.edt_password);
        btn_sign = findViewById(R.id.btn_sign);
        img_back=findViewById(R.id.img_back);
        text_termcondition = findViewById(R.id.text_termcondition);
        progressbar = findViewById(R.id.progressbar);

        img_back.setOnClickListener(clickListener);
        btn_sign.setOnClickListener(clickListener);
        text_termcondition.setOnClickListener(clickListener);


    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_sign:
                    setValidates();
                    break;
                case R.id.img_back:
                    finish();
                    break;
                case R.id.text_termcondition:
                    String url = "http://uddr.co.uk/?page_id=1573";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    break;

            }

        }
    };

    private void setValidates() {

        if (!Patterns.EMAIL_ADDRESS.matcher(edit_email.getText().toString().trim()).matches()) {
            Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
        } else if (edit_username.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Full name can't be empty", Toast.LENGTH_SHORT).show();
        } else if (edit_email.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
        } else if (edit_password.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
        } else if (edit_username.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Enter full name", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(100);
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
                    sendData.put("name", edit_username.getText().toString().trim());
                    sendData.put("email", edit_email.getText().toString().trim());
                    sendData.put("password", edit_password.getText().toString().trim());
                    sendData.put("phone", "9857487874");
                    sendData.put("device_token", "123456");
                    sendData.put("deviceType", "1");
                    sendData.put("appversion", "1.1");
                    sendData.put("timeZone", "Asia/kolkata");
                    Log.e("object", sendData.toString());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "register", sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressbar.setVisibility(View.GONE);
                            Log.e("login_response", response.toString());
                            try {
                                String status = response.getString("status");
                                String messaege = response.getString("message");
                                if (status.equals("1")) {
                                    String loginToken = response.getString("loginToken");
                                    JSONObject object = response.getJSONObject("data");
                                    String id = object.getString("id");
                                    editor = sharedPreferences.edit();
                                    editor.putString(Utils.user_id, object.getString("user_id"));
                                    editor.putString(Utils.id, id);
                                    editor.putString(Utils.name, object.getString("name"));
                                    editor.putString(Utils.email, object.getString("email"));
                                    editor.putString(Utils.phone, object.getString("phone"));
                                    editor.putString(Utils.user_image, object.getString("user_image"));
                                    editor.putString(Utils.loginToken, response.getString("loginToken"));
                                    //editor.putString(Utils.is_member, object.getString("is_member"));
                                    editor.commit();
                                    Intent in = new Intent(SignUpActivity.this, HomeActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(in);
                                    finish();
                                } else {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this);
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
                                progressbar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressbar.setVisibility(View.GONE);
                            Log.e("error", volleyError.toString());
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
                    jsonObjectRequest.setShouldCache(false);
                    Volley.newRequestQueue(SignUpActivity.this).add(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
