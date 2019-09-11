package com.uk.uddr.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    ImageView  profile_image;
    ImageView img_back;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    EditText edt_name, edt_email,edt_phone;
    Button btn_login;
    RelativeLayout relative_camera;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 2000;
    public static int MY_PERMISSIONS_REQUEST_CAMERA1 = 100;
    public static final int MY_PERMISSIONS_REQUEST_SCAMERA1 = 200;
    private int SELECT_GALLERY = 1;
    private int CAM_REQUEST1 = 2;
    String base64 = "";
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();


    }


    private void init() {

        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        profile_image = findViewById(R.id.profile_image);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        btn_login = findViewById(R.id.btn_login);
        relative_camera = findViewById(R.id.relative_camera);
        edt_phone = findViewById(R.id.edt_phone);
        img_back.setOnClickListener(clickListener);
        btn_login.setOnClickListener(clickListener);
        relative_camera.setOnClickListener(clickListener);
        progressBar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(200);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_login:
                    if(edt_name.getText().toString().trim().equals("")){
                        Toast.makeText(EditProfileActivity.this,"Name should not be empty",Toast.LENGTH_SHORT).show();
                    }else if(edt_email.getText().toString().trim().equals("")){
                        Toast.makeText(EditProfileActivity.this,"Email can't be empty",Toast.LENGTH_SHORT).show();
                    }else{
                        progressBar.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(100);
                    }
                    break;

                case R.id.relative_camera:
                    options(1);

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

                final JSONObject sendata = new JSONObject();
                try {
                    sendata.put("user_id", sharedPreferences.getString(Utils.user_id, ""));
//                    sendata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    sendata.put("user_image",base64);
                    sendata.put("name",edt_name.getText().toString().trim());
                    sendata.put("phone",edt_phone.getText().toString().trim());
                    sendata.put("email",edt_email.getText().toString().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "editProfile", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("editprofileresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
//                                progressBar.setVisibility(View.GONE);
                               /* JSONObject object = response.getJSONObject("data");
                                String id = object.getString("id");
                                String user_id = object.getString("user_id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String phone = object.getString("phone");
                                String user_image = object.getString("user_image");
                                edt_name.setText(name);
                                edt_email.setText(email);
                                edt_phone.setText(phone);
                                if(!user_image.equals("")){
                                    Picasso.with(EditProfileActivity.this).load(user_image).into(profile_image);
                                }*/
                                 progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
                        Toast.makeText(EditProfileActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(EditProfileActivity.this).add(jsonObjectRequest);
            }else if (msg.what == 200) {
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
                            Log.e("editProfileresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject object = response.getJSONObject("data");
                                String id = object.getString("id");
                                String user_id = object.getString("user_id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String phone = object.getString("phone");
                                String user_image = object.getString("user_image");
                                edt_name.setText(name);
                                edt_email.setText(email);
                                edt_phone.setText(phone);
                                if(!user_image.equals("")){
                                    Picasso.with(EditProfileActivity.this).load(user_image).into(profile_image);
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
                        Toast.makeText(EditProfileActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(EditProfileActivity.this).add(jsonObjectRequest);
            }
        }
    };

    private void options(int i) {

        android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(EditProfileActivity.this);

        // add a list
        String[] animals = {"Camera", "Gallery"};

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setItems(animals, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        checkpremission("camera");
                        break;
                    case 1:
                        checkpremission("storage");
                        break;
                }
            }
        });
        builderSingle.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void checkpremission(String which) {
        // Here, thisActivity is the current activity
        switch (which) {
            case "storage":
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE);

                    } else {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, SELECT_GALLERY);
                }
                break;
            case "camera":
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA1);
                    } else {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA1);
                    }
                } else {
                    checkpremission("scamera1");
                }
                break;
            case "scamera1":
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_SCAMERA1);
                    } else {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_SCAMERA1);
                    }
                } else {
                    Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, CAM_REQUEST1);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {

            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, SELECT_GALLERY);
                } else{

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_SCAMERA1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, CAM_REQUEST1);
                } else{

                }
                break;
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                base64 = encodeImage(bitmap);
                Log.e("uri",data.getData()+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            profile_image.setImageURI(uri);
        } else if (requestCode == CAM_REQUEST1 && resultCode == RESULT_OK) {
            Log.e("uri1","1");
            if (data != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.e("uri",data.getData()+"");
                uri = getImageUri(EditProfileActivity.this,thumbnail);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    base64 = encodeImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile_image.setImageURI(uri);

            }

        } else if (requestCode == 1) {
            assert data != null;
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
