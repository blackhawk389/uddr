package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.uk.uddr.R;
import com.uk.uddr.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView txt_createaccount, txt_forgot;
    EditText edt_email, edt_pass;
    Button btn_login;
    ImageView img_back;
    ProgressBar progressbar;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private JSONObject userData;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;
    private static final String TAG = "AndroidClarified";
    private Button logingoogle;
    private Button loginfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        callbackManager = CallbackManager.Factory.create();
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();

    }

    private void init() {

        progressbar = findViewById(R.id.progressbar);
        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        txt_createaccount = findViewById(R.id.txt_createaccount);
        btn_login = findViewById(R.id.btn_login);
        txt_forgot = findViewById(R.id.txt_forgot);
        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(clickListener);
        btn_login.setOnClickListener(clickListener);
        txt_createaccount.setOnClickListener(clickListener);
        txt_forgot.setOnClickListener(clickListener);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        googleSignInButton = findViewById(R.id.sign_in_button);

        googleSignInButton.setBackgroundResource(R.drawable.google);

        loginfb = (Button) findViewById(R.id.button);
        logingoogle = (Button) findViewById(R.id.button2);

        logingoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInButton.performClick();
            }
        });

        loginfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithPublishPermissions(LoginActivity.this, Arrays.asList("email"));
                loginButton.performClick();
            }
        });

       // loginButton.setReadPermissions("email", "public_profile");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });


        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        useLoginInformation(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.i("log", "can");
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("log", "erro");
                        // App code
                    }
                });

    }

    private void useLoginInformation(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    userData = object;
                    handler2.sendEmptyMessage(100);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }




    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_login:
                    setLoginValidates();
                    break;
                case R.id.txt_createaccount:
                    Intent in1 = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(in1);
                    break;
                case R.id.txt_forgot:
                    Intent in2 = new Intent(LoginActivity.this, ForgotActivity.class);
                    startActivity(in2);
                    break;
                case R.id.img_back:
                    finish();
                    break;

            }
        }
    };

    private void setLoginValidates() {
        if (edt_email.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
        } else if (edt_pass.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
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
                TimeZone timeZone=TimeZone.getDefault();
                try {
                    sendData.put("email", edt_email.getText().toString().trim());
                    sendData.put("password", edt_pass.getText().toString().trim());
                    sendData.put("deviceToken", "hrethiruthriuth");
                    sendData.put("deviceType", "1");
                    sendData.put("appversion", "1.1");
                    sendData.put("timeZone", timeZone.getID().toString());
                    Log.e("object", sendData.toString() +" - "+Utils.base_url);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "login", sendData, new Response.Listener<JSONObject>() {
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
//                                    Intent in=new Intent(LoginActivity.this,HomeActivity.class);
//                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
//                                            Intent.FLAG_ACTIVITY_CLEAR_TASK|
//                                            Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(in);
                                    finish();
                                } else {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
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
                            Log.e("errorMessage", volleyError.getMessage());
                        }
                    });
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
                    jsonObjectRequest.setShouldCache(false);
                    Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("name", userData.getString("name").trim());
                    sendData.put("email", userData.getString("email"));
                    sendData.put("password", "123456");
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
                                    editor.putString(Utils.user_image, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    editor.putString(Utils.loginToken, response.getString("loginToken"));
                                    //editor.putString(Utils.is_member, object.getString("is_member"));
                                    editor.commit();
                                    Intent in = new Intent(LoginActivity.this, HomeActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(in);
                                    finish();
                                } else if(messaege.contains("already")){
                                    new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            JSONObject sendData = new JSONObject();
                                            TimeZone timeZone = TimeZone.getDefault();
                                            try {
                                                sendData.put("email", userData.getString("email"));
                                                sendData.put("password", "123456");
                                                sendData.put("deviceToken", "hrethiruthriuth");
                                                sendData.put("deviceType", "1");
                                                sendData.put("appversion", "1.1");
                                                sendData.put("timeZone", timeZone.getID().toString());
                                                Log.e("object", sendData.toString() + " - " + Utils.base_url);

                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "login", sendData, new Response.Listener<JSONObject>() {
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
                                Intent in=new Intent(LoginActivity.this,HomeActivity.class);
                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                                                finish();
                                                            } else {
                                                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
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
                                                        Log.e("errorMessage", volleyError.getMessage());
                                                    }
                                                });
                                                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
                                                jsonObjectRequest.setShouldCache(false);
                                                Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                    }
                                }.sendEmptyMessage(100);}

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
                Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        //onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }

            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);

        }
    }



}
