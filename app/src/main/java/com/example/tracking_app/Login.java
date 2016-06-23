package com.example.tracking_app;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import CheckInternet.CheckInternet;
import SessionManager.SessionManager;
import WebService.SendSms;
import WebService.Service1;

public class Login extends AppCompatActivity{

	LocationManager locationManager;
	EditText edtmobile;
	Button btnlog;
	SessionManager session;
	CheckInternet checknet;
	Service1 ws;
	SendSms sendsms;
	ProgressDialog progress;
	
	@Override
	public void onBackPressed() {
		
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        finish();
       
        super.onBackPressed();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		edtmobile=(EditText)findViewById(R.id.txtloginmobile);
		btnlog=(Button)findViewById(R.id.btnlogin1);
		session =new SessionManager(Login.this);
		checknet=new CheckInternet(Login.this);
		ws=new Service1();
		sendsms=new SendSms(Login.this);
		
		locationManager = (LocationManager) Login.this.getSystemService(Context.LOCATION_SERVICE);
		btnlog.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(edtmobile.getText().toString()) || edtmobile.getText().toString().length()!=10)
				{
					edtmobile.setError("Please enter a valid mobile number");
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
		    				new ValidateAccount(edtmobile.getText().toString()).execute();
		    			}
						
		        	}
		        	else
		        	{
		        		Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
		        	}
				
				}
			}
		});
	}
	
	private class ValidateAccount extends AsyncTask<Void, Void, Void> {

		 JSONObject userList=null ;
	     JSONArray ds=null;
	     String MOBILE="";
	     ValidateAccount(String mobile){
	    	 this.MOBILE=mobile;
	     }
	     @Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = new ProgressDialog(Login.this);
				progress.setMessage("Loading...");
				progress.setCancelable(false);
				progress.show();
			}
	     
	     @Override
		protected Void doInBackground(Void... params) {
			try{
				userList=ws.user_login(MOBILE);

						ds=userList.getJSONArray("Value");





			}catch(Exception e){
					e.getMessage();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progress.dismiss();
			
			if (ds != null) {
               
               if (ds.length() > 0) {
               try {
                     
                      for (int i = 0; i < ds.length(); i++) {
                               JSONObject obj;
                       	    obj = ds.getJSONObject(i);
                       	    String gpname=obj.getString("name");

               				session.Savepreferences("Name", gpname);
               				
                           
                             }
                      SendSms2 dsms=new SendSms2(MOBILE);
                      dsms.execute();
                  //    Toast.makeText(getApplicationContext(),"OTP Sent On Mobile No", Toast.LENGTH_LONG).show();
                   } catch (JSONException e) {
                   
                       e.printStackTrace();
                   }

              // }
               }else{
               	Toast.makeText(getApplicationContext(),"Invalid User", Toast.LENGTH_LONG).show();
               }
               }else{
               	Toast.makeText(getApplicationContext(),"Invalid User", Toast.LENGTH_LONG).show();
               }
		

			super.onPostExecute(result);
		}

	}
	private class SendSms2 extends AsyncTask<Void, Void, Void> {

		String RegList="",MOBILEsm="";
		
		SendSms2(String mobile){
			this.MOBILEsm=mobile;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(Login.this);
			progress.setMessage("Sending OTP...");
			progress.setCancelable(false);
			progress.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			final Random myRandom = new Random(System.currentTimeMillis());
			String ns=String.valueOf(10000 + myRandom.nextInt(20000));
			session.Savepreferences("SaveOTP", ns);
			String otp=session.GetName()+" "+"Your OTP is  "+session.GetSaveOTP();
			try{
				
                
          //       sendsms.sendmsg(otp, MOBILEsm);
				
				
				
				}catch(Exception e){
					e.getMessage();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//mob1,msg1,session.GetAPIid(),RegList);
			try{
				progress.dismiss();
				
				Toast.makeText(getApplicationContext(),session.GetSaveOTP(), Toast.LENGTH_LONG).show();
				otpcompleet(MOBILEsm);
			}catch(Exception e){
				
			}

			super.onPostExecute(result);
		}

	}

	public void otpcompleet(final String mob) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.otp_diloge, null);

		final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this,R.style.MyDialogTheme);
		//progress.setContentView(R.layout.otp_diloge);
		builder.setTitle("OTP");
		builder.setView(promptView);
	    builder.setIcon(R.drawable.otp);

		String positiveText=getString(android.R.string.ok);
		//dialog.getWindow().setBackgroundDrawableResource(R.color.Black);
		final EditText pv = (EditText)promptView.findViewById(R.id.edtdialog);
		pv.requestFocus();
		pv.setHint("Enter OTP");
		pv.setTextColor(Color.BLACK);

		//final Button compleet=(Button) promptView.findViewById(R.id.buttcompleat);
		/*compleet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pv.getText().toString().trim().length()!=0){
					if(pv.getText().toString().equals(session.GetSaveOTP())){


						progress.dismiss();
						if(checknet.isNetworkAvailable()){
							session.Savepreferences("LoginRemember", "1");
							startService(new Intent(Login.this, Track.class));
							//startService(new Intent(MainActivity.this, Status_service.class));
							session.Savepreferences("MobileNoo", mob);
							Intent i=new Intent(Login.this,Navigation.class);
							startActivity(i);
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

		builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (pv.getText().toString().trim().length() != 0) {
					if (pv.getText().toString().equals(session.GetSaveOTP())) {


						dialog.dismiss();
						if (checknet.isNetworkAvailable()) {
							session.Savepreferences("LoginRemember", "1");
							startService(new Intent(Login.this, Track1.class));
							session.Savepreferences("MobileNoo",mob);
							//startService(new Intent(MainActivity.this, Status_service.class));
							Intent i = new Intent(Login.this, Navigation.class);
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
						}

					} else {
						Toast.makeText(getApplicationContext(), "Fill Correct OTP", Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), "Please Fill OTP", Toast.LENGTH_LONG).show();
				}
			}


		});


		/*final Button cancela = (Button) dialog.findViewById(R.id.btncompleet);
		cancela.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//whis is not
				
			dialog.dismiss();
				
			}
			
		});*/
		
		/*final Button Regenrate = (Button) promptView.findViewById(R.id.btnRegen);
		Regenrate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checknet.isNetworkAvailable()){
					progress.dismiss();
				SendSms2 dsa=new SendSms2(session.GetMobileNo());
				dsa.execute();
				Toast.makeText(getApplicationContext(),"OTP ReSent On Mobile No", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),"Internet Not Available", Toast.LENGTH_LONG).show();
				}
				
				
				
			
				
			}
			
		});*/
		String negativeText=getString(android.R.string.cancel);
		builder.setNegativeButton(negativeText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// negative button logic
						if (checknet.isNetworkAvailable()) {
							progress.dismiss();
							SendSms2 dsa = new SendSms2(session.GetMobileNo());
							dsa.execute();
							Toast.makeText(getApplicationContext(), "OTP ReSent On Mobile No", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
						}
					}
				});



		AlertDialog dialog = builder.create();
		// display dialog
		dialog.show();
		int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = dialog.findViewById(dividerId);
		divider.setBackgroundColor(getResources().getColor(R.color.transparent));
	}
	
	 private void showGPSDisabledAlertToUser()
	  {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this); //changed
	        alertDialogBuilder.setMessage("Location services is disabled in your device,please enable it continue")
	        .setCancelable(false)
	        .setPositiveButton("Go to Settings to enable location",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
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
	
}
