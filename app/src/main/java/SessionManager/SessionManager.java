package SessionManager;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;



public class SessionManager {
	
	SharedPreferences pref;
	Editor editor;


   String MobileNo;
	String SaveOTP;
	String Name;
	String login;
	String Email;
	String LoginRemember;
	String userid;

	Context context;

    public SessionManager(Context cntxt) {
		this.context = cntxt;
		pref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void Savepreferences(String key, String Value) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		editor = pref.edit();
		editor.putString(key, Value);
		editor.commit();
	}
	
	public String GetLoginRemember() {
		LoginRemember = (pref.getString("LoginRemember", ""));
		return LoginRemember;
	}
	public String GetEmail() {
		Email = (pref.getString("Email", ""));
		return Email;
	}
	public String GetMobileNo() {
		MobileNo = (pref.getString("MobileNoo", ""));
		return MobileNo;
	}
	
	public String GetName() {
		Name = (pref.getString("Name", ""));
		return Name;
	}
	
	public String GetSaveOTP() {
		SaveOTP = (pref.getString("SaveOTP", ""));
		return SaveOTP;
	}
	public String Getuserid() {
		userid = (pref.getString("userid", ""));
		return userid;
	}

	
}
