package com.uk.uddr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    ImageView img_back;
    EditText edt_card_name, edt_card_number, edt_cvv,edt_comment;
    Button btn_continue;
    private EditText ph_months, ph_years;
    private ArrayList<String> years;
    private int ll_width, ll_width_year, ll_width_environment;
    private LayoutInflater mInflater;
    private PopupWindow mDropdown = null;
    private RelativeLayout rl_month, rl_years,rl_submit, rl_live_envirnment;
    private List<String> Lines = null;
    private List<String> Lines_env = null;
    ProgressBar progress_bar_credit_card;

    String strip_token="",s_name="";
    private String test_env = "pk_test_2O8rkx8zP0auuNLrx3biltnc";
    private String live_env = "pk_live_xHxMgVy20MjunjxbFtVxLuNW";
    SharedPreferences sharedPreferences;
    String sum,datetime,store_id,selectedTimeslot;
    JSONArray transaction;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        datetime=getIntent().getStringExtra(Utils.d_time);
        sum=getIntent().getStringExtra(Utils.total);
        selectedTimeslot=getIntent().getStringExtra(Utils.timeslot);
        s_name=getIntent().getStringExtra("s_name");

        Log.e("total_price",sum+"");
        dbHelper=new DBHelper(this);
        gettransaction();
        init();
    }

    private void init() {
        progress_bar_credit_card = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        btn_continue = findViewById(R.id.btn_continue);
        edt_card_name = findViewById(R.id.edt_card_name);
        edt_card_number = findViewById(R.id.edt_card_number);
        edt_comment= findViewById(R.id.edt_comment);
        //edt_date = findViewById(R.id.edt_date);
        edt_cvv = findViewById(R.id.edt_cvv);
        btn_continue.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);

        //edt_card_number.setText("4242424242424242");




        //Log.i("access_token",sharedPreferences.getString("access_token",""));
        ph_months = (EditText) findViewById(R.id.ph_months);
        ph_years = (EditText) findViewById(R.id.ph_years);
        // ph_live = (TextView) findViewById(R.id.ph_live);
        edt_cvv = (EditText) findViewById(R.id.edt_cvv);
        //  edt_amount = (TextView) findViewById(R.id.edt_amount);
        rl_month = (RelativeLayout) findViewById(R.id.rl_month);
        rl_years = (RelativeLayout) findViewById(R.id.rl_year);
        rl_month.setOnClickListener(clickListener);
        img_back.setOnClickListener( clickListener);
        rl_years.setOnClickListener(clickListener);
//        rl_live_envirnment = (RelativeLayout) findViewById(R.id.rl_live_envirnment);

        //payableamount= (int) Float.parseFloat(amount);
        years = new ArrayList<String>();
        years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0 ; i <= 15; i++) {
            years.add(""+(thisYear++));
        }
        progress_bar_credit_card.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(300);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_continue:
                    validation();
                    break;

            }
        }
    };




    private void validation() {

        if (edt_card_name.getText().toString().equals("")) {
            Toast.makeText(CheckOutActivity.this, "Please Enter name on card", Toast.LENGTH_SHORT).show();
        } else if (edt_card_number.getText().toString().equals("")) {
            Toast.makeText(CheckOutActivity.this, "Please Enter card number", Toast.LENGTH_SHORT).show();
        }else if (ph_months.getText().toString().trim().equalsIgnoreCase("Month") || ph_months.getText().toString().trim().length() == 0) {
            Toast.makeText(CheckOutActivity.this, "Choose valid Expiry month", Toast.LENGTH_LONG).show();
        } else if (ph_years.getText().toString().trim().equalsIgnoreCase("Year") || ph_years.getText().toString().trim().length() == 0) {
            Toast.makeText(CheckOutActivity.this, "Choose valid Expiry Year", Toast.LENGTH_LONG).show();
        } else if(edt_cvv.getText().toString().equals("")){
            Toast.makeText(CheckOutActivity.this,"Please Enter CVV",Toast.LENGTH_SHORT).show();
        } else {
            progress_bar_credit_card.setVisibility(View.VISIBLE);

            final Card card = Card.create(
                    edt_card_number.getText().toString().trim(), Integer.parseInt(ph_months.getText().toString().trim().toString()),
                    Integer.parseInt(ph_years.getText().toString().trim().toString()),
                    edt_cvv.getText().toString().trim()
            );

            card.validateNumber();
            card.validateCVC();
            //Card card = new Card(edt_card_number.getText().toString().trim(), Integer.parseInt(ph_months.getText().toString().trim().toString()), Integer.parseInt(ph_years.getText().toString().trim().toString()), edt_cvv.getText().toString().trim(),edt_card_name.getText().toString().trim(),"","","","","","",s_name,"","","","","","");
            if (!card.validateNumber()) {
                // Show errors
                progress_bar_credit_card.setVisibility(View.GONE);
                Toast.makeText(CheckOutActivity.this, "Enter a valid card", Toast.LENGTH_LONG).show();
            } else if (!card.validateCVC()) {
                progress_bar_credit_card.setVisibility(View.GONE);
                Toast.makeText(CheckOutActivity.this, "Enter a valid CVV", Toast.LENGTH_LONG).show();
            } else if (!card.validateExpMonth()) {
                progress_bar_credit_card.setVisibility(View.GONE);
                Toast.makeText(CheckOutActivity.this, "Enter a valid Expiry Month", Toast.LENGTH_LONG).show();
            }
//            else if (!card.validateExpYear()) {
//                progress_bar_credit_card.setVisibility(View.GONE);
//                Toast.makeText(CheckOutActivity.this, "Enter a valid expiry year", Toast.LENGTH_LONG).show();
//            }

            else {
                try {
                } catch (Exception e) {

                }
                boolean validation = card.validateCard();
                if (validation) {
                    Stripe stripe = new Stripe(CheckOutActivity.this);
                    stripe.setDefaultPublishableKey(live_env);
                    //rl_submit.setVisibility(View.GONE);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    //progress_bar_credit_card.setVisibility(View.VISIBLE);
                                    strip_token=token.getId();
                                    handler.sendEmptyMessage(200);
                                    //  stripePayment(token.getId());
                                    //   Toast.makeText(Payment.this, "Payment Sucessful, token:"+token.getId(), Toast.LENGTH_LONG).show();

                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    progress_bar_credit_card.setVisibility(View.GONE);
                                    Toast.makeText(CheckOutActivity.this, "Your card was declined. Your request was in live mode, but used a known test card.", Toast.LENGTH_LONG).show();
//                                                        Global.alertDialog(CardDetailActivity.this, error.getLocalizedMessage(), 1 ,"");
                                }
                            }
                    );
                } else {
                    progress_bar_credit_card.setVisibility(View.GONE);
                    Toast.makeText(CheckOutActivity.this, "The card details that you entered are invalid", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    JSONArray gettransaction(){
        transaction=new JSONArray();
        Cursor cursor=dbHelper.selectallproduct();
        cursor.moveToFirst();
        if (cursor != null) {
            do {
                if(cursor.getCount()>0){
                    float p_price=cursor.getFloat(cursor.getColumnIndex("PRICE"));
                    float p_count=Float.parseFloat(cursor.getInt(cursor.getColumnIndex("COUNT"))+"");
                    String pid=cursor.getString(cursor.getColumnIndex("PID"));
                    store_id=cursor.getString(cursor.getColumnIndex(DBHelper.S_ID));
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("ProductID",pid);
                        jsonObject.put("Quantity",p_count);
                        jsonObject.put("Price",p_price);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    transaction.put(jsonObject);
                }
            }while (cursor.moveToNext());
        }
        Log.e("jsonarray",transaction.toString());
        return transaction;
    }



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){

                JSONObject senddata=new JSONObject();
                try {
                    senddata.put("TransactionItems",gettransaction());
                    senddata.put("userID",sharedPreferences.getString(Utils.user_id,""));
                    senddata.put("Total",sum);
                    senddata.put("address_id",sharedPreferences.getString(Utils.address_id,""));
                    senddata.put("timeslots_id",selectedTimeslot);
                    senddata.put("comments",edt_comment.getText().toString().trim());
                    senddata.put("stripeToken",strip_token);
                    senddata.put("dilivery_time",datetime);
                    senddata.put("store_id",store_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("paymentsendata",senddata.toString()+" - "+store_id);

                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Utils.base_url + "saveOrder", senddata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("PaymentResponse",jsonObject.toString());
                        try {
                            String success=jsonObject.getString("status");
                            if(success.equals("1")){
                                progress_bar_credit_card.setVisibility(View.GONE);
                                dbHelper.clearDB();
                                Intent receipt=new Intent(CheckOutActivity.this,OrderDetailActivity.class);
                                receipt.putExtra("t_id",jsonObject.getString("TransactionID"));
                                startActivity(receipt);
                                finish();

                                //handler.sendEmptyMessageDelayed(200,1000);
                            }else{
                                progress_bar_credit_card.setVisibility(View.GONE);
                                String message=jsonObject.getString("message");
                                AlertDialog.Builder builder=new AlertDialog.Builder(CheckOutActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage(message);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                        progress_bar_credit_card.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(CheckOutActivity.this).add(jsonObjectRequest);

            }else if(msg.what==200){
                JSONObject senddata=new JSONObject();
                try {
                    senddata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    senddata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));
                    senddata.put("card_name",edt_card_name.getText().toString());
                    senddata.put("card_number",edt_card_number.getText().toString());
                    senddata.put("exp_month",ph_months.getText().toString().trim());
                    senddata.put("exp_year",ph_years.getText().toString().trim());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("paymentsendata",senddata.toString());

                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Utils.base_url + "saveCardDetail", senddata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("Payment Response",jsonObject.toString());
                        try {
                            String success=jsonObject.getString("status");

                            if(success.equals("1")){
                                progress_bar_credit_card.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(100);
                                //handler.sendEmptyMessageDelayed(200,1000);
                            }else{
                                progress_bar_credit_card.setVisibility(View.GONE);
                                String message=jsonObject.getString("message");
                                AlertDialog.Builder builder=new AlertDialog.Builder(CheckOutActivity.this);
                                builder.setTitle("Alert");
                                builder.setMessage(message);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                        progress_bar_credit_card.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(CheckOutActivity.this).add(jsonObjectRequest);
            }else if(msg.what==300){
                JSONObject senddata=new JSONObject();
                try {
                    senddata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    senddata.put("loginToken", sharedPreferences.getString(Utils.loginToken, ""));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("paymentsendata",senddata.toString());

                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getCardDetail", senddata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("PaymentResponse",jsonObject.toString());
                        try {
                            String success=jsonObject.getString("status");
                            //progress_bar_credit_card.setProgress(View.GONE);
                            Log.e("success",success+"");
                            if(success.equals("1")){
                                progress_bar_credit_card.setVisibility(View.GONE);
                                JSONObject data=jsonObject.getJSONObject("data");
                                String  card_name = data.getString("card_name");
                                String  card_number = data.getString("card_number");
                                //String  cvv = data.getString("cvv");
                                String  exp_month = data.getString("exp_month");
                                String  exp_year = data.getString("exp_year");

                                edt_card_name.setText(card_name);
                                edt_card_number.setText(card_number);
                                ph_years.setText(exp_year);
                                ph_months.setText(exp_month);
                                //handler.sendEmptyMessageDelayed(200,1000);
                            }else{
                                Log.e("success",success+"not working");
                                progress_bar_credit_card.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progress_bar_credit_card.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(CheckOutActivity.this).add(jsonObjectRequest);
            }
        }
    };
}
