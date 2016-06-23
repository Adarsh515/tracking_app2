package com.example.tracking_app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Status_service extends Service 
{
	LocationManager locationManager;
	private static final String TAG = "Status_service";
	
	//Timer mTimer;
	long REFRESH_TIME=2*60*1000;
	
	 private Handler mHandler = new Handler();
	    // timer handling
	    private Timer mTimer = null;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	 @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.e(TAG, "onStartCommand");
	        return START_STICKY;
	    }
	
	@Override
	public void onCreate() {
		
		try
		{
		
		 if(mTimer != null) {
	            mTimer.cancel();
	        } else {
	            // recreate new
	            mTimer = new Timer();
	        }
		 mTimer.scheduleAtFixedRate(new TimerTask()
		 
		 
		 {
			
		     
			 
			 @Override
			 public void run(){
				 
				 mHandler.post(new Runnable() {
					 
		                @Override
		                public void run() {
		        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();		
				StrictMode.setThreadPolicy(policy);
				
				
				        	// Acquire a reference to the system Location Manager
				    		locationManager = (LocationManager) Status_service.this.getSystemService(Context.LOCATION_SERVICE);
				    		
				    		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				        	NetworkInfo networkInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				        	boolean connectedmobile = networkInfo != null && networkInfo.isConnected();
				        	NetworkInfo networkInfo2=mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				        	boolean connectedwifi=networkInfo2 != null && networkInfo2.isConnected();
				    		// getting GPS status
				            boolean isGPSEnabled = locationManager
				                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
				            if(!isGPSEnabled)
			    			{
			    				showGPSDisabledAlertToUser();
			    			}
				    		

		                }
		                
		            });
			 }	 
		 }, 0, REFRESH_TIME);
		
		
		
				    		Log.d(TAG, "onCreate");
				
		        
		   
	//	Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
				    		
		}
		 catch (Exception e) {
             e.printStackTrace();
              Log.e("Error", e.toString());
          }
         
		
		
	}

	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service has been Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		sendBroadcast(new Intent("YouWillNeverKillMe"));
	}
	
	// function to check GPS on/off status
	  private void showGPSDisabledAlertToUser()
	  {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Status_service.this); //changed
	        alertDialogBuilder.setMessage("Location services is disabled in your device,please enable it continue")
	        .setCancelable(false)
	        .setPositiveButton("Go to Settings to enable location",
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                Intent callGPSSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                startActivity(callGPSSettingIntent);
	            }
	        });
	       /* alertDialogBuilder.setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                dialog.cancel();
	                Intent i=new Intent(MainActivity.this,MainActivity.class);
	    			startActivity(i);
	            }
	        });*/
	        AlertDialog alert = alertDialogBuilder.create();
	        alert.show();
	    }
	@Override
	public void onStart(Intent intent, int startid) {
		//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		//player.start();
	}
	
	 

		
}
