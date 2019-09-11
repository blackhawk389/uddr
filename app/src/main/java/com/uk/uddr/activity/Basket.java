package com.uk.uddr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uk.uddr.R;
import com.uk.uddr.adapter.CartAdapter;
import com.uk.uddr.model.CartModel;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Basket extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    ArrayList<CartModel> arrayList;
    RecyclerView recycleview;
    Button btn_selectAddress, btn_newAddress;
    private static RelativeLayout btn_checkout,rel_main;
    private static TextView txt_total,txt_noitem;
    private static float sum=0,total=0;
    private static DBHelper dbHelper;
    TextView txt_address,txt_datetime,txt_timeslot,txt_storeaname,txt_storename;
    String selected_address,AM_PM;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Context mContext;
    RelativeLayout relativetime,rel_timeslot,relativetimeslot,rel_deliveryadd,rel_storeaddress,rel_days;
    String t_id,s_id,selectedtimeslot="0",selectedday="0",s_name="";
    Spinner spinner_timeslot,spinner_days;
    String[] timeslotidList,timeslotList,daysidList,daysList;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        mContext=this;
        t_id=getIntent().getStringExtra("t_id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        dbHelper=new DBHelper(this);
        init();
    }

    private void init() {
        rel_main=(RelativeLayout)findViewById(R.id.rel_main);
        rel_timeslot=(RelativeLayout)findViewById(R.id.rel_timeslot);
        relativetimeslot=(RelativeLayout)findViewById(R.id.relativetimeslot);
        rel_storeaddress=(RelativeLayout)findViewById(R.id.rel_storeaddress);
        //txt_aname= (TextView)view.findViewById(R.id.txt_aname);
        txt_address=(TextView)findViewById(R.id.txt_address);
        relativetime=(RelativeLayout) findViewById(R.id.relativetime);
        rel_days=(RelativeLayout)findViewById(R.id.rel_days);
        rel_deliveryadd=(RelativeLayout)findViewById(R.id.rel_deliveryadd);
        img_back=(ImageView)findViewById(R.id.img_back);

        //txt_date_time = (TextView)view.findViewById(R.id.txt_date_time);
        txt_datetime = (TextView)findViewById(R.id.txt_datetime);
        txt_timeslot = (TextView)findViewById(R.id.txt_timeslot);
        txt_storeaname= (TextView)findViewById(R.id.txt_storeaname);
        txt_storename = (TextView)findViewById(R.id.txt_storename);
        txt_noitem= findViewById(R.id.txt_noitem);
        progressBar = findViewById(R.id.progressBar);
        recycleview = findViewById(R.id.recycleview);
        btn_selectAddress = findViewById(R.id.btn_selectAddress);
        btn_newAddress = findViewById(R.id.btn_newAddress);
        btn_checkout = findViewById(R.id.btn_checkout);
        txt_total=findViewById(R.id.txt_total);
        spinner_timeslot=findViewById(R.id.spinner_timeslot);
        spinner_days=findViewById(R.id.spinner_daysslot);

        btn_selectAddress.setOnClickListener(clickListener);
        btn_newAddress.setOnClickListener(clickListener);
        relativetime.setOnClickListener(clickListener);
        btn_checkout.setOnClickListener(clickListener);
        relativetimeslot.setOnClickListener(clickListener);
        img_back.setOnClickListener(clickListener);

       /* selected_address=sharedPreferences.getString(Utils.address_id,"");
        if(!selected_address.equals("")){
            txt_aname.setVisibility(View.VISIBLE);
            txt_address.setVisibility(View.VISIBLE);
            txt_aname.setText(sharedPreferences.getString(Utils.address_name,""));
            txt_address.setText(sharedPreferences.getString(Utils.address_line,""));
        }else{
            txt_aname.setVisibility(View.GONE);
            txt_address.setVisibility(View.GONE);
        }*/
        if(t_id.equals("-1")){
            setRecylwerview();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(100);
        }


        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        if(mMonth<10){
            //txt_date_time.setText(mDay + "-0" + (mMonth + 1) + "-" + mYear);
        }else{
            //txt_date_time.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
        }


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {

                case R.id.img_back:
                    finish();
                    break;
                case R.id.btn_newAddress:
                    Intent intent = new Intent(mContext, NewAddress.class);
                    startActivity(intent);
                    break;

                case R.id.btn_selectAddress:
                    Intent selectAddress = new Intent(mContext, SelectAddress.class);
                    startActivity(selectAddress);
                    break;
                case R.id.btn_checkout:
                    if(sum>0){
                        if(!selected_address.equals("")){
                            if(!selected_address.equals("0")){
                                progressBar.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(200);
                            }else{
                                t_id="-1";
                                Intent checkOut = new Intent(mContext, CheckOutActivity.class);
                                checkOut.putExtra(Utils.total,String.format("%.2f",(sum+0.49)));
                                checkOut.putExtra(Utils.d_time,selectedday);
                                checkOut.putExtra("s_name",s_name);
                                checkOut.putExtra(Utils.timeslot,selectedtimeslot);
                                startActivity(checkOut);
                                Log.e("total_p",sum+" - "+selectedtimeslot);
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Alert");
                            builder.setMessage("Please select delivery address.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
                    break;

                case R.id.relativetime:
                    //getdate();
                    break;
//
//                case R.id.relativetimeslot:
//                    //getdate();
//                    break;

            }
        }
    };

    void getdate(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if(monthOfYear<10){
                            // txt_date_time.setText(dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                        }else{
                            //txt_date_time.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                        Log.e("date",dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                        progressBar.setVisibility(View.VISIBLE);
//                        handler.sendEmptyMessage(400);
                        //getTime();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
/*
    void getTime(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String da=txt_date_time.getText().toString().trim();
                        if(mHour<12){
                            AM_PM="AM";
                        }else{
                            AM_PM="PM";
                        }
                        txt_date_time.setText(da+" , "+hourOfDay + ":" + minute+" "+AM_PM);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
*/

    public static void updatevalue(){
        sum=0;
        total=0;
        Cursor cursor=dbHelper.selectallproduct();
        cursor.moveToFirst();
        if (cursor != null) {
            do {
                if(cursor.getCount()>0){
                    float p_price=cursor.getFloat(cursor.getColumnIndex("PRICE"));
                    float p_count=Float.parseFloat(cursor.getInt(cursor.getColumnIndex("COUNT"))+"");
                    sum=sum+(p_price*p_count);
                }

            }while (cursor.moveToNext());
        }
        txt_total.setText("£"+String.format("%.2f",sum+0.49));
        if(cursor.getCount()>0){
            rel_main.setVisibility(View.VISIBLE);
            txt_noitem.setVisibility(View.GONE);
            btn_checkout.setVisibility(View.VISIBLE);
        }else{
            rel_main.setVisibility(View.GONE);
            txt_noitem.setVisibility(View.VISIBLE);
            btn_checkout.setVisibility(View.GONE);
        }

    }

    private void setRecylwerview() {
        arrayList = new ArrayList<>();
        Cursor cursor=dbHelper.selectallproduct();
        cursor.moveToFirst();
        if (cursor != null) {
            do {
                if(cursor.getCount()>0){
                    String p_id=cursor.getString(cursor.getColumnIndex("PID"))+"";
                    String p_name=cursor.getString(cursor.getColumnIndex("NAME"))+"";
                    String p_image=cursor.getString(cursor.getColumnIndex("IMAGE"))+"";
                    String p_price=String.format("%.2f",cursor.getFloat(cursor.getColumnIndex("PRICE")))+"";
                    String p_count=cursor.getInt(cursor.getColumnIndex("COUNT"))+"";
                    String p_desr=cursor.getString(cursor.getColumnIndex("PDESC"))+"";
                    s_id=cursor.getString(cursor.getColumnIndex(DBHelper.S_ID))+"";
                    arrayList.add(new CartModel(p_id,p_name,p_desr,p_price,p_image,p_count));
                    Log.e("all image ",cursor.getCount()+" - "+cursor.getString(cursor.getColumnIndex("IMAGE"))+" - ");
                }

            }while (cursor.moveToNext());
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycleview.setLayoutManager(mLayoutManager);
        recycleview.setItemAnimator(new DefaultItemAnimator());
        CartAdapter adapeter = new CartAdapter(mContext, arrayList,s_id);
        recycleview.setAdapter(adapeter);
        updatevalue();
        HomeActivity.updatecart();
        if(arrayList.size()>0){
            handler.sendEmptyMessage(500);
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("TransactionID",t_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "orderDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("order_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                //offset = response.getString("offset");
                                String prodImageURL = response.getString("prodImageURL");
                                JSONObject data = response.getJSONObject("data");
                                String TransactionID=data.getString("TransactionID");
                                JSONArray items=data.getJSONArray("itemsArr");
                                for(int i=0;i<items.length();i++){
                                    JSONObject prod=items.getJSONObject(i);
                                    String product_id=prod.getString("product_id");
                                    String product_name=prod.getString("product_name");
                                    String product_description=prod.getString("product_description");
                                    String store_id=prod.getString("store_id");
                                    String Price=prod.getString("Price");
                                    String Quantity=prod.getString("Quantity");
                                    String image=prod.getString("product_image");
                                    if(dbHelper.checkproduct(product_id)==0){
                                        Log.e("getting value",dbHelper.fetchproduct(product_id)+"" );
                                        dbHelper.addproduct(product_id,product_name,prodImageURL+image,Price,Quantity,store_id,"storename",product_description);
                                    }

                                }
                                setRecylwerview();

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
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else  if (msg.what == 200) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("store_id",s_id);
                    sendata.put("address_id",selected_address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "checkStore", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("order_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            //float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                t_id="-1";
                                Intent checkOut = new Intent(mContext, CheckOutActivity.class);
                                checkOut.putExtra(Utils.total,String.format("%.2f",(sum+0.49)));
                                checkOut.putExtra(Utils.d_time,selectedday);
                                checkOut.putExtra(Utils.timeslot,selectedtimeslot);
                                startActivity(checkOut);
                                Log.e("total_p",sum+"");
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
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else if (msg.what == 300) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("TransactionID",t_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "orderDetail", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("order_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                //offset = response.getString("offset");
                                String prodImageURL = response.getString("prodImageURL");
                                JSONObject data = response.getJSONObject("data");
                                String TransactionID=data.getString("TransactionID");
                                JSONArray items=data.getJSONArray("itemsArr");
                                for(int i=0;i<items.length();i++){
                                    JSONObject prod=items.getJSONObject(i);
                                    String product_id=prod.getString("product_id");
                                    String product_name=prod.getString("product_name");
                                    String product_description=prod.getString("product_description");
                                    String store_id=prod.getString("store_id");
                                    String Price=prod.getString("Price");
                                    String Quantity=prod.getString("Quantity");
                                    String image=prod.getString("product_image");
                                    if(dbHelper.checkproduct(product_id)==0){
                                        Log.e("getting value",dbHelper.fetchproduct(product_id)+"" );
                                        dbHelper.addproduct(product_id,product_name,prodImageURL+image,Price,Quantity,store_id,"storename",product_description);
                                    }

                                }
                                setRecylwerview();

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
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else  if (msg.what == 400) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("store_id",s_id);
                    sendata.put("date",selectedday);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "checkAvailabilityTimeslots", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("order_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                //offset = response.getString("offset");
                                String availability = response.getString("availability");
                                if(availability.equals("1")){
                                    JSONArray data = response.getJSONArray("data");
                                    timeslotList=new String[data.length()];
                                    timeslotidList=new String[data.length()];
                                    for(int i=0;i<data.length();i++){
                                        JSONObject timeslot=data.getJSONObject(i);
                                        String id=timeslot.getString("id");
                                        String store_id=timeslot.getString("store_id");
                                        String day=timeslot.getString("day");
                                        String timeslots=timeslot.getString("timeslots");
                                        String timesloct=timeslot.getString("timesloct");
                                        timeslotidList[i]=timeslots;
                                        timeslotList[i]=timesloct;
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, timeslotList);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner_timeslot.setAdapter(dataAdapter);
                                    spinner_timeslot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            selectedtimeslot=timeslotidList[i];
                                            Log.e("selectedtimeslot_check",selectedtimeslot+"");

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                            Log.e("itemSelected","0");
                                        }
                                    });
                                }else{

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
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else if (msg.what == 500) {
                JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("store_id",s_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getStoreCategories", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("disresponse", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");
                            Log.e("Status", status);
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject store=response.getJSONObject("store_info");
                                String s_id = store.getString("store_id");
                                String store_name = store.getString("store_name");
                                String distance = store.getString("distance");
                                String store_address = store.getString("store_address");
                                String store_image = store.getString("store_image");
                                String store_logo = store.getString("store_logo");
                                String postcode = store.getString("postcode");
                                String latitude = store.getString("latitude");
                                String longitude = store.getString("longitude");
                                String days_availability = store.getString("days_availability");
                                String time_availability = store.getString("time_availability");
                                String is_delivery = store.getString("is_delivery");
                                txt_storename.setText(store_name);
                                s_name=store_name;
                                Log.e("check_delivery",days_availability+" - "+is_delivery+" - "+time_availability);
                                if(is_delivery.equals("1")){
                                    if(days_availability.equals("1")){
                                        if(time_availability.equals("0")){
                                            txt_datetime.setText("Choose delivery day");
                                            rel_timeslot.setVisibility(View.GONE);
                                            //txt_timeslot.setText("");
                                        }else{
                                            txt_datetime.setText("Choose delivery day");
                                            txt_timeslot.setText("Choose delivery time slot");
                                            rel_timeslot.setVisibility(View.VISIBLE);
                                            //handler.sendEmptyMessage(400);
                                        }
                                        rel_days.setVisibility(View.VISIBLE);
                                        handler.sendEmptyMessage(600);
                                    }else{
                                        if(time_availability.equals("0")){
                                            txt_datetime.setText("Choose delivery day");
                                            rel_timeslot.setVisibility(View.GONE);
                                            //txt_timeslot.setText("");
                                        }else{
                                            txt_datetime.setText("Choose delivery day");
                                            txt_timeslot.setText("Choose delivery time slot");
                                            rel_timeslot.setVisibility(View.VISIBLE);
                                            // handler.sendEmptyMessage(400);
                                        }
                                        rel_days.setVisibility(View.VISIBLE);
                                        handler.sendEmptyMessage(600);
                                    }
                                    rel_deliveryadd.setVisibility(View.VISIBLE);
                                    rel_storeaddress.setVisibility(View.GONE);
                                    selected_address=sharedPreferences.getString(Utils.address_id,"");
                                    if(!selected_address.equals("")){
                                        //txt_aname.setVisibility(View.VISIBLE);
                                        txt_address.setVisibility(View.VISIBLE);
                                        //txt_aname.setText(sharedPreferences.getString(Utils.address_name,""));
                                        txt_address.setText(sharedPreferences.getString(Utils.address_line,""));
                                    }else{
                                        //txt_aname.setVisibility(View.GONE);
                                        txt_address.setVisibility(View.GONE);
                                    }

                                }else{
                                    selected_address="0";
                                    editor=sharedPreferences.edit();
                                    editor.putString(Utils.address_id,"0");
                                    editor.commit();
                                    if(days_availability.equals("1")){
                                        if(time_availability.equals("0")){
                                            txt_datetime.setText("Choose day");
                                            rel_timeslot.setVisibility(View.GONE);
                                            //txt_timeslot.setText("");
                                        }else{
                                            txt_datetime.setText("Choose day");
                                            txt_timeslot.setText("Choose time slot");
                                            rel_timeslot.setVisibility(View.VISIBLE);
                                            //handler.sendEmptyMessage(400);
                                        }
                                        rel_days.setVisibility(View.VISIBLE);
                                        handler.sendEmptyMessage(600);
                                    }else{
                                        if(time_availability.equals("0")){
                                            txt_datetime.setText("Choose day");
                                            rel_timeslot.setVisibility(View.GONE);
                                            //txt_timeslot.setText("");
                                        }else{
                                            txt_datetime.setText("Choose day");
                                            txt_timeslot.setText("Choose time slot");
                                            rel_timeslot.setVisibility(View.VISIBLE);
                                            //handler.sendEmptyMessage(400);
                                        }
                                        rel_days.setVisibility(View.VISIBLE);
                                        handler.sendEmptyMessage(600);
                                    }
                                    rel_deliveryadd.setVisibility(View.GONE);
                                    rel_storeaddress.setVisibility(View.VISIBLE);
                                    txt_storeaname.setText(store_address);
                                }
                                Log.e("store_address",store_address+"");
                            } else {

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
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Unknown Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setShouldCache(false);
                Volley.newRequestQueue(mContext).add(jsonObjectRequest);
            }else  if (msg.what == 600) {
                final JSONObject sendata=new JSONObject();
                try {
                    sendata.put("user_id",sharedPreferences.getString(Utils.user_id,""));
                    sendata.put("loginToken",sharedPreferences.getString(Utils.loginToken,""));
                    sendata.put("store_id",s_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("senddata",sendata.toString());

                arrayList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.base_url + "getAvailableDays", sendata, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("order_response", response.toString());
                            String status = response.getString("status");
                            String messaege = response.getString("message");

                            Log.e("Status", status);
                            float sum=0;
                            if (status.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                //offset = response.getString("offset");
                                String availability = response.getString("availability");
                                if(availability.equals("1")){
                                    JSONArray data = response.getJSONArray("data");
                                    daysidList=new String[data.length()];
                                    daysList=new String[data.length()];
                                    for(int i=0;i<data.length();i++){
                                        JSONObject timeslot=data.getJSONObject(i);
                                        String id=timeslot.getString("id");
                                        String store_id=timeslot.getString("store_id");
                                        String day_id=timeslot.getString("day_id");
                                        String date=timeslot.getString("date");
                                        //String timesloct=timeslot.getString("timesloct");
                                        daysidList[i]=day_id;
                                        daysList[i]=date;
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, daysList);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner_days.setAdapter(dataAdapter);
                                    spinner_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            selectedday=daysList[i].split(" ")[1];
                                            Log.e("selectedday",selectedday+"");
                                            handler.sendEmptyMessage(400);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                            Log.e("itemSelected","0");
                                        }
                                    });
                                }else{

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
                    }                }, new Response.ErrorListener() {
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
