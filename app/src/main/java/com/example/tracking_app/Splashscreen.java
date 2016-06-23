package com.example.tracking_app;

import SessionManager.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Splashscreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;
    String a;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager=new SessionManager(Splashscreen.this);
        TextView txtvw=(TextView)findViewById(R.id.txtvw);
        Typeface typeface=Typeface.createFromAsset(this.getAssets(),"fonts/montserrat_regular.otf");
        txtvw.setTypeface(typeface);
        a=sessionManager.GetLoginRemember();


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (a.equals("1")) {
                    Intent i = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Splashscreen.this, Navigation.class);
                    startActivity(i);
                }
                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

}
