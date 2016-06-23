package CheckInternet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {

	Context context;

	public CheckInternet(Context cntxt) {
		this.context = cntxt;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		
		return activeNetworkInfo != null && activeNetworkInfo.isConnected()
				&& activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public static boolean isNetworkPresent(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		
		return activeNetworkInfo != null && activeNetworkInfo.isConnected()
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

}
