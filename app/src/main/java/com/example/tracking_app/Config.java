package com.example.tracking_app;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://192.169.233.202:8058/PushService.asmx/Post_AndroidRegId?regid=1";

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";
   // static final String SERVER_URL = "http://10.0.2.2:10739/GCM-server.asmx/PostRegistration-Id";

    // Google Project Number
	static final String GOOGLE_PROJECT_ID = "293994527001";
	static final String MESSAGE_KEY = "message";

}
