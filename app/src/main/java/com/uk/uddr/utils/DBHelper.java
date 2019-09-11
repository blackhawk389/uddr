package com.uk.uddr.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="mycart.db";
    public static final String TABLE_NAME = "mycart";
    public static final String P_ID = "PID";
    public static final String P_NAME = "NAME";
    public static final String S_NAME = "SNAME";
    public static final String S_ID = "SID";
    public static final String P_IMAGE = "IMAGE";
    public static final String P_DESC = "PDESC";
    public static final String PRICE = "PRICE";
    public static final String COUNT = "COUNT";



    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createtable="CREATE TABLE "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT ,"+P_ID+" TEXT ,"+P_IMAGE+" TEXT ,"
                +P_NAME+" TEXT ,"+PRICE+" DECIMAL , "+COUNT+" INTEGER,"+S_ID+" TEXT ,"+S_NAME+" TEXT ,"+P_DESC+" TEXT)";
        db.execSQL(createtable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addproduct(String p_id,String p_name,String p_image,String p_price,String p_count,String s_id,String s_name,String p_descr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(P_ID, p_id);
        contentValues.put(P_NAME, p_name);
        contentValues.put(P_IMAGE, p_image);
        contentValues.put(PRICE, p_price);
        contentValues.put(COUNT, p_count);
        contentValues.put(S_ID, s_id);
        contentValues.put(S_NAME, s_name);
        if(p_descr.equals("")){
            contentValues.put(P_DESC, "-");
        }else{
            contentValues.put(P_DESC, p_descr);
        }
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }else{
            Log.d("DB", "true");
            return true;
        }
    }

    public int checkproduct(String p_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor codeEx = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE PID= '" + p_id + "'", null);
        Log.e("coloum count",codeEx.getColumnCount()+" - "+codeEx.getColumnName(8));
        return codeEx.getCount();
    }

    public int fetchproduct(String p_id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor codeEx=db.rawQuery("SELECT * FROM "+TABLE_NAME +" WHERE PID= '"+p_id+"'",null);
        if(codeEx.moveToNext()){
            int value=codeEx.getInt(codeEx.getColumnIndex("COUNT"));
            Log.e("value count",value+"");
            return value;
        }else{
            Log.e("value count","--000");
            return 0;
        }
    }

    public String getStoreId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor codeEx = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        Log.e("coloum count",codeEx.getColumnCount()+" - "+codeEx.getColumnName(8));
        if(codeEx.moveToNext()){
            String id=codeEx.getString(codeEx.getColumnIndex(S_ID));
            Log.e("store_id",id+"");
            return id;
        }else{
            Log.e("store_id","--000");
            return "-1";
        }

    }

    public Cursor selectallproduct(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor codeEx = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return codeEx;
//        int value=codeEx.getInt(codeEx.getColumnIndex("COUNT"));
    }




    public void updateproduct(String p_id,String p_price,String p_count){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery("UPDATE " + TABLE_NAME +" SET PRICE = '"+Float.parseFloat(p_price)+"', COUNT = '"+Integer.parseInt(p_count)+ "' WHERE PID= '" + p_id + "'", null);
        Log.i("updated","product"+p_id+" - "+p_price+" - "+cursor.getCount());
        db.close();
    }

    public void deleteproduct(String p_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery("DELETE FROM " + TABLE_NAME + " WHERE PID= '" + p_id + "'", null);
        Log.i("DELETED","product "+p_id+" "+cursor.getCount());
        db.close();
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor=db.execSQL("DELETE FROM " + TABLE_NAME , null);
        db.delete(TABLE_NAME,null,null);
        db.close();
    }


}
