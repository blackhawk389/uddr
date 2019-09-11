package com.uk.uddr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.uk.uddr.R;
import com.uk.uddr.activity.HomeActivity;
import com.uk.uddr.activity.LoginActivity;
import com.uk.uddr.activity.ZipCode;
import com.uk.uddr.utils.Utils;

public class Splash extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        String zip = sharedPreferences.getString(Utils.zipcode, "");
        if (zip.equals("")) {
            handler.sendEmptyMessageDelayed(300, 2500);
        }else {
            handler.sendEmptyMessageDelayed(100, 2500);
        }
//        if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
//            handler.sendEmptyMessageDelayed(200, 2500);
//        } else {
//            handler.sendEmptyMessageDelayed(100, 2500);
//        }
    }
//
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int id = msg.what;
            if (id == 100) {
                Intent i;
                i = new Intent(Splash.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            } else if (id == 200) {
                Intent i = new Intent(Splash.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }else if(id==300){
                Intent loca = new Intent(Splash.this, ZipCode.class);
                startActivity(loca);
                finish();
            }
        }
    };
}
