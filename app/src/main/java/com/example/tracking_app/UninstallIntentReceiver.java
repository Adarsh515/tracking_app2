package com.example.tracking_app;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


public class UninstallIntentReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // fetching package names from extras
       /* String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES"); 
        
        if(packageNames!=null){
            for(String packageName: packageNames){
                if(packageName!=null && packageName.equals("package:com.example.tracking_app")){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenActivities(context).start();

                }
            }
        }*/
    	PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.example.tracking_app", PackageManager.GET_ACTIVITIES);
            new ListenActivities(context).start();
            //app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
           // app_installed = false;
        }
        
    }

}
