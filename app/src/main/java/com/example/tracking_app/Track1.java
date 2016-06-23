package com.example.tracking_app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import SessionManager.SessionManager;
import WebService.Service1;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Track1 extends Service {
    LocationManager locationManager;
    private static final String TAG = "MyService";
    String lat = "", lon = "";
    double latd, lond;
    Databaseclass db;
    TextView tv;
    String address;
    double dblat;
    double dblon;
    int firsttime = 0;
    List<ArrayList<String>> listlocation;
    ArrayList<String> allat = new ArrayList<String>();
    ArrayList<String> allon = new ArrayList<String>();
    ArrayList<String> aladdress = new ArrayList<String>();
    ArrayList<String> aldate = new ArrayList<String>();
    ArrayList<String> NewLocationList = new ArrayList<String>();
    String slat, slon, saddress, sdate;
    //Timer mTimer;
    long REFRESH_TIME = 60 * 1000;
    Connection conn = null;
    Statement stmnt = null;
    String name, mobile,cdate;

    SessionManager session;
    GpsTracker1 gpsTracker;
    Service1 ws;
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
        session = new SessionManager(Track1.this);
        ws = new Service1();
        try {
            db = new Databaseclass(Track1.this);
            db.getReadableDatabase();
            gpsTracker = new GpsTracker1(this);

            mobile = db.getmobile();
            if (mTimer != null) {
                mTimer.cancel();
            } else {
                // recreate new
                mTimer = new Timer();
            }
            mTimer.scheduleAtFixedRate(new TimerTask()


            {


                @Override
                public void run() {

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);


                            // Acquire a reference to the system Location Manager
                            locationManager = (LocationManager) Track1.this.getSystemService(Context.LOCATION_SERVICE);
                            ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                            boolean connectedmobile = networkInfo != null && networkInfo.isConnected();
                            NetworkInfo networkInfo2 = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            boolean connectedwifi = networkInfo2 != null && networkInfo2.isConnected();
                            // getting GPS status
                            boolean isGPSEnabled = locationManager
                                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
                            // Define a listener that responds to location updates
//							if ( Build.VERSION.SDK_INT >= 23 &&
//									ContextCompat.checkSelfPermission(Track.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//									ContextCompat.checkSelfPermission(Track.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//								return;
//							}
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (!isGPSEnabled && !connectedmobile && !connectedwifi) {

                                    try {
                                        notification();
                                        Time today = new Time(Time.getCurrentTimezone());
                                        today.setToNow();
                                        String cdate = today.year + "-" + (today.month + 1) + "-" + today.monthDay + " " + today.format("%k:%M:%S");
                                        db.insertlocation("0", "0", "Location off", "0", cdate);
                                    } catch (Exception ex) {
                                        String e = ex.toString();
                                    }
                                } else {
                                    if (/*isGPSEnabled &&*/ (!connectedwifi )) {
                                        try {

                                            for(int a=0;a<NewLocationList.size();a++){
                                                listlocation = 	(List<ArrayList<String>>)NewLocationList.clone();	//db.getlocation();
                                            }


                                            if(listlocation.size()>0){
                                                allat = listlocation.get(0);
                                                allon = listlocation.get(1);
                                                aladdress = listlocation.get(2);
                                                aldate = listlocation.get(3);

                                                new insertLocation("0").execute();

                                                db.deletelocation();
                                                listlocation.clear();
                                            }
                                            if (!listlocation.isEmpty()) {
                                                allat = listlocation.get(0);
                                                allon = listlocation.get(1);
                                                aladdress = listlocation.get(2);
                                                aldate = listlocation.get(3);
                                                new insertLocation("0").execute();

                                                db.deletelocation();
                                                listlocation.clear();
                                            }
                                            LocationListener locationListener = new LocationListener() {
                                                public void onLocationChanged(Location location) {
                                                    // Called when a new location is found by the network location provider.


                                                    NewLocationList.add(String.valueOf(location.getLatitude()));
                                                    NewLocationList.add(String.valueOf(location.getLongitude()));
                                                    String add123=GetAddress(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                                                    NewLocationList.add(add123);
                                                    Time today1 = new Time(Time.getCurrentTimezone());
                                                    today1.setToNow();
                                                    String DateNew = today1.year + "-" + (today1.month + 1) + "-" + today1.monthDay + " " + today1.format("%k:%M:%S");
                                                    NewLocationList.add(DateNew);

                                                }

                                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                                }

                                                public void onProviderEnabled(String provider) {
                                                }

                                                public void onProviderDisabled(String provider) {
                                                }
                                            };

// Register the listener with the Location Manager to receive location updates
                                            if (ActivityCompat.checkSelfPermission(Track1.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Track1.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);


                                            /*LocationListener locationListener = new LocationListener() {
                                                public void onLocationChanged(Location location) {
                                                    // Called when a new location is found by the network location provider.
                                                    latd = location.getLatitude();
                                                    lond = location.getLongitude();
                                                    lat = Double.toString(latd);
                                                    lon = Double.toString(lond);

                                                    new fetchlastlocation1().execute();
                                                    double a = distance(latd, lond, dblat, dblon);
                                                    if (a > 100) {
                                                        address = GetAddress(lat, lon);
                                                        //insertlocation(1);
                                                        new insertLocation("1").execute();
                                                    }


                                                }

                                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                                }

                                                public void onProviderEnabled(String provider) {
                                                }

                                                public void onProviderDisabled(String provider) {
                                                }
                                            };


                                            if (ActivityCompat.checkSelfPermission(Track1.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Track.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener); */
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    } else if (connectedmobile ) {
                                        try {

                                            LocationListener locationListener = new LocationListener() {
                                                public void onLocationChanged(Location location) {
                                                    // Called when a new location is found by the network location provider.


                                                    NewLocationList.add(String.valueOf(location.getLatitude()));
                                                    NewLocationList.add(String.valueOf(location.getLongitude()));
                                                    String add123=GetAddress(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                                                    NewLocationList.add(add123);
                                                    Time today1 = new Time(Time.getCurrentTimezone());
                                                    today1.setToNow();
                                                    String DateNew = today1.year + "-" + (today1.month + 1) + "-" + today1.monthDay + " " + today1.format("%k:%M:%S");
                                                    NewLocationList.add(DateNew);

                                                    latd = location.getLatitude();
                                                    lond = location.getLongitude();
                                                    lat = Double.toString(latd);
                                                    lon = Double.toString(lond);
                                                    new fetchlastlocation1().execute();
                                                    double a = distance(latd, lond, dblat, dblon);
                                                    if (a > 100) {
                                                        address = GetAddress(lat, lon);
                                                        //insertlocation(1);
                                                        new insertLocation("1").execute();
                                                    }

                                                }

                                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                                }

                                                public void onProviderEnabled(String provider) {
                                                }

                                                public void onProviderDisabled(String provider) {
                                                }
                                            };

// Register the listener with the Location Manager to receive location updates
                                            if (ActivityCompat.checkSelfPermission(Track1.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Track1.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                                            listlocation = db.getlocation();

                                            if (!listlocation.isEmpty()) {
                                                allat = listlocation.get(0);
                                                allon = listlocation.get(1);
                                                aladdress = listlocation.get(2);
                                                aldate = listlocation.get(3);
                                                //insertlocation(0);
                                                new insertLocation("0").execute();
                                                db.deletelocation();
                                            }
                                            /*  LocationListener locationListener = new LocationListener() {
                                                public void onLocationChanged(Location location) {
                                                    // Called when a new location is found by the network location provider.

                                                    latd = location.getLatitude();
                                                    lond = location.getLongitude();
                                                    lat = Double.toString(latd);
                                                    lon = Double.toString(lond);
                                                    new fetchlastlocation1().execute();
                                                    double a = distance(latd, lond, dblat, dblon);
                                                    if (a > 100) {
                                                        address = GetAddress(lat, lon);
                                                        //insertlocation(1);
                                                        new insertLocation("1").execute();
                                                    }


                                                }

                                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                                }

                                                public void onProviderEnabled(String provider) {
                                                }

                                                public void onProviderDisabled(String provider) {
                                                }
                                            };

                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 1000, locationListener);*/
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    } else if (isGPSEnabled && connectedwifi) {
                                        try {
                                            if (gpsTracker.isGPSEnabled)
                                            {
                                                String stringLatitude = String.valueOf(gpsTracker.getLatitude());


                                                String stringLongitude = String.valueOf(gpsTracker.getLongitude());


                                                String country = gpsTracker.getCountryName(Track1.this);


                                                String city = gpsTracker.getLocality(Track1.this);

                                                String postalCode = gpsTracker.getPostalCode(Track1.this);

                                                String addressLine = gpsTracker.getAddressLine(Track1.this);


                                                String AddressLine="Offline Mode :-\n"+addressLine+"\n"+city+"\n"+country+"\n"+postalCode;
                                                Time today = new Time(Time.getCurrentTimezone());
                                                today.setToNow();
                                                String cdate = today.year + "-" + (today.month + 1) + "-" + today.monthDay + " " + today.format("%k:%M:%S");
                                                db.insertlocation(stringLatitude, stringLongitude, AddressLine, "0", cdate);
                                                new insertLocation("0").execute();
                                            }
                                            else
                                            {
                                                // can't get location
                                                // GPS or Network is not enabled
                                                // Ask user to enable GPS/network in settings
                                                gpsTracker.showSettingsAlert();
                                            }
                                        } catch (Exception ex) {
                                            String e = ex.toString();
                                        }
                                    }
                                }
                            } else {
                                notification();
                            }
                        }
                    });
                }
            }, 0, REFRESH_TIME);


            Log.d(TAG, "onCreate");


            //	Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
        } finally {
            if (stmnt != null) try {
                stmnt.close();
            } catch (Exception e) {
            }
            if (conn != null) try {
                conn.close();
            } catch (Exception e) {
            }
        }


    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service has been Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        //player.start();
    }


    private class insertLocation extends AsyncTask<Void, Void, Void> {

        JSONObject RegList = null;

        String FLAG = "";

        insertLocation(String flag) {
            this.FLAG = flag;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            String cdate = today.year + "-" + (today.month + 1) + "-" + today.monthDay + " " + today.format("%k:%M:%S");
            try {
                //(String lat,String lon,String address1,String locationdate,String name,String mobile)

                if (FLAG.equals("1")) {
                    //   String SQL = "INSERT INTO ["+name+mobile+"](lat,lon,address) VALUES('" + lat + "', '"+ lon + "', '"+address+"');";
                    //   stmnt.execute(SQL);
                    RegList = ws.insert_loc(lat, lon, address, cdate, session.GetName(), session.GetMobileNo());
                    // ws.insert_loc(NewLocationList.get(0).toString(), NewLocationList.get(1).toString(), NewLocationList.get(2).toString(), NewLocationList.get(3).toString(), session.GetName(), session.GetMobileNo());

                } else {
                    for (int i = 0; i < allat.size(); i++) {
                        slat = allat.get(i).toString();
                        slon = allon.get(i).toString();
                        saddress = aladdress.get(i).toString();
                        sdate = aldate.get(i).toString();

                        RegList = ws.insert_loc(slat, slon, saddress, sdate, session.GetName(), session.GetMobileNo());
                        //ws.insert_loc(NewLocationList.get(i+1).toString(), NewLocationList.get(i+2).toString(), NewLocationList.get(i).toString(), NewLocationList.get(i+3).toString(), session.GetName(), session.GetMobileNo());


                    }

                }

            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }


    public String GetAddress(String lat, String lon) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Cannot get Address";
        }
        return ret;
    }

    // function to check GPS on/off status
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Track1.this); //changed
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

    public void notification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(Track1.this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Location service is disabled")
                        .setOngoing(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentText("Touch to enable your location");

        // Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(Track.this, Home.class);
        Intent resultIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(Track1.this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Home.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }

    //function to calculate distance b/w two coordinates
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344 * 1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private class fetchlastlocation1 extends AsyncTask<Void, Void, Void> {

        JSONObject lastLoc = null;
        String result1 = "";
        JSONArray ds = null;


        @Override
        protected Void doInBackground(Void... params) {
            //"ErrorMessage":"Invalid object name 'virendra9716430901'.","Successful":false}
            try {

                lastLoc = ws.last_loc(session.GetName(), session.GetMobileNo());

                ds = lastLoc.getJSONArray("Value");
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ds != null) {

                if (ds.length() > 0) {
                    try {
                        //emailid":"virendralmitm008@gmail.com","mobile":"8555778899","name":"bv"
                        for (int i = 0; i < ds.length(); i++) {
                            JSONObject obj;
                            obj = ds.getJSONObject(i);

                            dblat = Double.parseDouble(obj.getString("lat"));
                            dblon = Double.parseDouble(obj.getString("lon"));
                        }
                    } catch (Exception e) {

                    }
                }
            }
            super.onPostExecute(result);
        }

    }
}
