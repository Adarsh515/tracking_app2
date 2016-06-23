package com.example.tracking_app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.R.array;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class Databaseclass extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	 public static final String DB_NAME = "tracking_db";
	 private static final String TABLE_NAME = "user_details";
	 private static final String TABLE_NAME2 = "location";
	
	
	 public Databaseclass(Context context) {
		super(context,DB_NAME, null, DB_VERSION);
		
	}
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
	
	}
	public void insertnewuser(String uname,String umobile)
	{
		 SQLiteDatabase db = this.getWritableDatabase();
		  ContentValues values = new ContentValues();
		  values.put("name",uname);
		  values.put("mobileno",umobile);
		
		  try{    
			  db.insert(TABLE_NAME, null, values);
			    db.close();
			}
			catch (Exception e)
			{
			    String error =  e.getMessage().toString();
			}
		
	}
	
	
	
	public String getmobile()
	{
		String num = null;
		try 
		{
		
		  String selectQuery = "select mobileno from user_details";
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor cursor = db.rawQuery(selectQuery, null);
		  if (cursor.moveToFirst()) {
			   do {
				   num=cursor.getString(0);
			
			   } while (cursor.moveToNext());
			  }
			  cursor.close();
			  db.close();
			  
		}
		
		catch (Exception e) {
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
		return num;
         
		
	}
	public String getname()
	{String num = null;
		try
		{
		
		 String selectQuery = "select name from user_details";
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor cursor = db.rawQuery(selectQuery, null);
		  if (cursor.moveToFirst()) {
			   do {
				   num=cursor.getString(0);
			
			   } while (cursor.moveToNext());
			  }
			  cursor.close();
			  db.close();
			  
		}
		catch (Exception e) {
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
		return num;
		
	}
	
	public void insertlocation(String lat,String lon,String address,String flag,String cdate)
	{
		try
		{
		 SQLiteDatabase db = this.getWritableDatabase();
		  ContentValues values = new ContentValues();
		  values.put("lat",lat);
		  values.put("lon",lon);
		  values.put("address",address);
		  values.put("flag",flag);
		  values.put("locationdate",cdate);
		
		  try{    
			  db.insert(TABLE_NAME2, null, values);
			    db.close();
			}
			catch (Exception e)
			{
			    String error =  e.getMessage().toString();
			}
		}
		catch (Exception e) {
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
		
		
	}
	public List<ArrayList<String>> getlocation()
	{
		ArrayList<String> a1=new ArrayList<String>();
		ArrayList<String> a2=new ArrayList<String>();
		ArrayList<String> a3=new ArrayList<String>();
		ArrayList<String> a4=new ArrayList<String>();
		List<ArrayList<String>> a5 = new ArrayList<ArrayList<String>>();
		try
		{
		
		 String selectQuery = "select lat,lon,address,locationdate from "+TABLE_NAME2+" where flag='0'";
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor cursor = db.rawQuery(selectQuery, null);
		  if (cursor.moveToFirst()) {
			   do {
				   a1.add(cursor.getString(0)); 
				   a2.add(cursor.getString(1)); 
				   a3.add(cursor.getString(2)); 
				   a4.add(cursor.getString(3));
			
			   } while (cursor.moveToNext());
			   a5.add(a1);
			   a5.add(a2);
			   a5.add(a3);
			   a5.add(a4);
			  }
			  cursor.close();
			  db.close();
			  
			  
		}
		catch (Exception e) {
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
		return a5;
		
	}
	

	//function to delete location from database
	public void deletelocation()
	{
		try{
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_NAME2,null, null);
		}
		catch(Exception e)
		{
			String ex=e.toString();
		}
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		
	}
	public ArrayList<Double> fetchlastlocation() {
		
		ArrayList<Double> latlon=new ArrayList<Double>();
		try
		{
		
		 String selectQuery = "select lat,lon from "+TABLE_NAME2+" where locationdate=(SELECT MAX(locationdate) FROM "+TABLE_NAME2+")";
		  SQLiteDatabase db = this.getReadableDatabase();
		  Cursor cursor = db.rawQuery(selectQuery, null);
		  if (cursor.moveToFirst()) {
			   do {
				   double lat=Double.parseDouble(cursor.getString(0));
				   latlon.add(lat);
				   double lon=Double.parseDouble(cursor.getString(1));
				   latlon.add(lon);
		
			   } while (cursor.moveToNext());
			  
			  }
			  cursor.close();
			  db.close();
			  
			  
		}
		catch (Exception e) {
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
		return latlon;
	}

}
