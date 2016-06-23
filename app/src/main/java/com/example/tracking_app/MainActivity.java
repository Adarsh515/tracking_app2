package com.example.tracking_app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import CheckInternet.CheckInternet;
import SessionManager.SessionManager;
import WebService.SendSms;
import WebService.Service1;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.Sampler.Value;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	GoogleCloudMessaging gcm;
	Databaseclass d;
	LocationManager locationManager;
	EditText txtname;
	EditText txtmobile;
	Button btnsubmit,btnlogin;
	public String name,mobile;
	Connection conn=null;
	Statement stmnt=null;
	String check;
	int checkstatus=0;
	 private ProgressDialog prgDialog;
	 public static final int progress_bar_type = 0;
	CheckAppPermission checkAppPermission;
	 SessionManager session;
		CheckInternet checknet;
		Service1 ws;
		SendSms sendsms;
		ProgressDialog progress;
		Dialog dialog;
		Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkAppPermission=new CheckAppPermission(MainActivity.this);
		session =new SessionManager(MainActivity.this);
		checknet=new CheckInternet(MainActivity.this);
		ws=new Service1();
		context = getApplicationContext();
		sendsms=new SendSms(MainActivity.this);
		try
		{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();		
		StrictMode.setThreadPolicy(policy);
		d=new Databaseclass(MainActivity.this);
		

		
		/*PackageManager packageManager = getPackageManager();
		List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        if(!list.isEmpty()){
            for(int i=0;i<list.size();i++){
                if(list.get(i).toString()!=null && list.get(i).toString().equals("YOUR_APPLICATION_PACKAGE_NAME")){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                  String x=list.get(i).toString();
                	//  new ListenActivities(context).start();

                }
            }
        }*/
		
		locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);		
		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		SQLiteDatabase db = openOrCreateDatabase("tracking_db",MODE_PRIVATE,null);
		db.execSQL("create table IF NOT EXISTS user_details(name TEXT,mobileno TEXT)");	
		db.execSQL("create table IF NOT EXISTS location(lat TEXT,lon TEXT,address TEXT,flag TEXT,locationdate DATETIME)");	
		//db.execSQL("create table IF NOT EXISTS location(lat TEXT,lon TEXT,address TEXT,flag TEXT,locationdate DATETIME DEFAULT DATETIME('now','localtime'))");	

				if (!checkAppPermission.checkPermissionForLocation()) {
					checkAppPermission.requestPermissionForLocation();
				}

			if(!session.GetMobileNo().equals(""))
		{
		startService(new Intent(MainActivity.this, Track1.class));
		//startService(new Intent(MainActivity.this, Status_service.class));	
		Intent i=new Intent(MainActivity.this,Navigation.class);
		startActivity(i);			
		}
		else
		{
			
        	
           /* boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(!isGPSEnabled)
			{
				//showGPSDisabledAlertToUser();
			}
			else
			{*/
		txtname=(EditText)findViewById(R.id.txtusername);
		txtmobile=(EditText)findViewById(R.id.txtmobile);
		btnsubmit=(Button)findViewById(R.id.button1);
		btnlogin=(Button)findViewById(R.id.btnlogin);
		btnlogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent ise=new Intent(MainActivity.this,Login.class);
				startActivity(ise);
				finish();
			}
		});
		btnsubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				    name=txtname.getText().toString();
		            mobile=txtmobile.getText().toString();
				if(TextUtils.isEmpty(name))
				{
					
					txtname.setError("Please enter a name");
					
				}
				else if(TextUtils.isEmpty(mobile) || mobile.length()!=10)
				{
					txtmobile.setError("Please enter a valid mobile number");
				}
				else
				{
		        	if(checknet.isNetworkAvailable())
		        	{
		                boolean isGPSEnablednew = locationManager
		                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
		    			if(!isGPSEnablednew)
		    			{
		    				showGPSDisabledAlertToUser();
		    			}
		    			else
		    			{
		    				session.Savepreferences("Name", txtname.getText().toString());
		        		new ValidateAccount(txtmobile.getText().toString()).execute();
		    			}
						
		        	}
		        	else
		        	{
		        		Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
		        	}
				
				}
			}
		});
		//}
		}
		}
		catch (Exception e) {			
            e.printStackTrace();
             Log.e("Error", e.toString());
         }
         finally {
            if (stmnt != null) try { stmnt.close(); } catch(Exception e) {}
            if (conn != null) try { conn.close(); } catch(Exception e) {}
         }
		
		
	}
	
	
	
	
	
	private class ValidateAccount extends AsyncTask<Void, Void, Void> {

		 JSONObject userList=null ;
	     JSONArray ds=null;
		String MOBB="";
		ValidateAccount(String mob){
			this.MOBB=mob;
		}
	     
	     @Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = new ProgressDialog(MainActivity.this);
				progress.setMessage("Loading...");
				progress.setCancelable(false);
				progress.show();
			}
	     
	     @Override
		protected Void doInBackground(Void... params) {
			try{//{"ErrorMessage":"Invalid object name 'TRACKING_APP_USER_DETAILS'.","Successful":false}
				userList=ws.user_login(MOBB);
			

					ds=userList.getJSONArray("Value");


              
				}catch(Exception e){
					e.getMessage();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progress.dismiss();
			
			if (ds != null && ds.length() > 0) {
				Toast.makeText(getApplicationContext(),"User Allready Registerd", Toast.LENGTH_LONG).show();
				
              }else{
            	  SendSms2 dsms=new SendSms2(MOBB);
                  dsms.execute();
                  Toast.makeText(getApplicationContext(),"OTP Sent On Mobile No", Toast.LENGTH_LONG).show();
                     
                
              }
			 

			super.onPostExecute(result);
		}

	}
	private class SendSms2 extends AsyncTask<Void, Void, Void> {

		String MMob="",otp="";
		SendSms2(String mobb){
			this.MMob=mobb;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(MainActivity.this);
			progress.setMessage("Sending OTP...");
			progress.setCancelable(false);
			progress.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			final Random myRandom = new Random(System.currentTimeMillis());
			String ns=String.valueOf(10000 + myRandom.nextInt(20000));
			session.Savepreferences("SaveOTP", ns);
			  otp=session.GetName()+" "+"Your OTP is  "+session.GetSaveOTP();
			try{
				//our OTP is  24382
               
		//		RegList=     sendsms.sendmsg(otp, session.GetMobileNo());
				
				
				}catch(Exception e){
					e.getMessage();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				progress.dismiss();
			 Toast.makeText(MainActivity.this,otp,Toast.LENGTH_LONG).show();
				otpcompleet(MMob);
				
			}catch(Exception e){
				
			}

			super.onPostExecute(result);
		}

	}

	public void otpcompleet(final String mob) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.otp_diloge, null);

		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
		builder.setTitle("OTP");
		builder.setView(promptView);
		builder.setIcon(R.drawable.otp);
		//dialog = new Dialog(MainActivity.this);
		//dialog.setContentView(R.layout.otp_diloge);
		//dialog.setTitle("OTP");
		//dialog.getWindow().setBackgroundDrawableResource(R.color.Black);
	   // dialog.setCancelable(false);
		String positiveText=getString(android.R.string.ok);
		final EditText pv = (EditText)promptView.findViewById(R.id.edtdialog);
		pv.requestFocus();
		pv.setHint("Enter OTP");
		pv.setTextColor(Color.BLACK);
		//final EditText pv = (EditText) dialog.findViewById(R.id.edtdialog);
		//final TextView timee=(TextView)dialog.findViewById(R.id.textView1time);
		//final Button compleet=(Button) promptView.findViewById(R.id.buttcompleat);
		builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(pv.getText().toString().trim().length()!=0){
					if(pv.getText().toString().equals(session.GetSaveOTP())){



						if(checknet.isNetworkAvailable()){
							dialog.dismiss();
							GetRegister fd=new GetRegister(mob);
							fd.execute();
						}else{
							Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
						}

					}else{
						Toast.makeText(getApplicationContext(), "Fill Correct OTP", Toast.LENGTH_LONG).show();
					}

				}else{
					Toast.makeText(getApplicationContext(), "Please Fill OTP", Toast.LENGTH_LONG).show();
				}
			}
		});
		/*compleet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pv.getText().toString().trim().length()!=0){	
					if(pv.getText().toString().equals(session.GetSaveOTP())){
						
						
						
						if(checknet.isNetworkAvailable()){
							dialog.dismiss();
							GetRegister fd=new GetRegister();
							fd.execute();
						}else{
							Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
						}
						
					}else{
						Toast.makeText(getApplicationContext(), "Fill Correct OTP", Toast.LENGTH_LONG).show();
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "Please Fill OTP", Toast.LENGTH_LONG).show();
				}
			}
		});*/
		/*final Button cencela = (Button) dialog.findViewById(R.id.btncompleet);
		cencela.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//whis is not
				dialog.dismiss();
			
			}
			
		});*/
		String negativeText=getString(android.R.string.cancel);
		builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(checknet.isNetworkAvailable()){
					dialog.dismiss();
					SendSms2 dsa=new SendSms2(mob);
					dsa.execute();
					//	Toast.makeText(getApplicationContext(),"OTP ReSent On Mobile No", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),"Internet Not Available", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		/*final Button Regenrate = (Button) promptView.findViewById(R.id.btnRegen);
		Regenrate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checknet.isNetworkAvailable()){
					dialog.dismiss();
				SendSms2 dsa=new SendSms2();
				dsa.execute();
			//	Toast.makeText(getApplicationContext(),"OTP ReSent On Mobile No", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),"Internet Not Available", Toast.LENGTH_LONG).show();
				}
				}
			
		});*/



		AlertDialog dialog = builder.create();
		// display dialog
		dialog.show();
		int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = dialog.findViewById(dividerId);
		divider.setBackgroundColor(getResources().getColor(R.color.transparent));
	}
	
	
	 private class GetRegister extends AsyncTask<Void, Void, Void> {

			JSONObject RegList=null ;
			String result1="",regId1="",Mmob;
		 GetRegister(String mm){
			 this.Mmob=mm;
		 }
			
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = new ProgressDialog(MainActivity.this);
				progress.setMessage("Loading...");
				progress.setCancelable(false);
				progress.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				try{
	                try {
	                    if (gcm == null) {
	                        gcm = GoogleCloudMessaging.getInstance(context);
	                    }
	                    regId1 = gcm.register(Config.GOOGLE_PROJECT_ID);
	                    Log.d("RegisterActivity", "registerInBackground - regId: "
	                            + regId1);
	                    String fdgfdgd=regId1;
	                   String msg = "Device registered, registration ID=" + regId1;


	                } catch (IOException ex) {

	                }

//{"Successful":true}

					RegList=ws.reg_new_user(session.GetName(),Mmob,regId1);

	                result1 = RegList.getString("Successful");

	               
					}catch(Exception e){
						e.getMessage();
					}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progress.dismiss();
				if(result1.equals("true")){
					session.Savepreferences("LoginRemember", "1");
					session.Savepreferences("MobileNoo",Mmob);
					startService(new Intent(MainActivity.this, Track1.class));
					//startService(new Intent(MainActivity.this, Status_service.class));	
					Intent i=new Intent(MainActivity.this,Navigation.class);
					startActivity(i);
					Toast.makeText(getApplicationContext(),"Successfully Registered", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),"Not Registered", Toast.LENGTH_LONG).show();
				}

				super.onPostExecute(result);
			}

		}
		
	
	@Override
	public void onBackPressed() {
		
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        
	}
	// Show Dialog Box with Progress bar
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type:
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Registering your details...");
            prgDialog.setIndeterminate(false);
            prgDialog.setCancelable(true);
            prgDialog.show();
            return prgDialog;
        default:
            return null;
        }
    }
	
	

	// function to check GPS on/off status
		  private void showGPSDisabledAlertToUser()
		  {
		        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this); //changed
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
