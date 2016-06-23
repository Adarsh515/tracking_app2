package com.example.tracking_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
	 @Override
	 public void onReceive(Context context, Intent intent) {
	  if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
	   Intent pushIntent = new Intent(context, Track1.class);
	   context.startService(pushIntent);
	  }
	 }
	}
