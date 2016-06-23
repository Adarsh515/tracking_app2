package WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import CheckInternet.CheckInternet;
import JSONParser.JSONParser;
import SessionManager.SessionManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class SendSms {


        SessionManager session;
        CheckInternet checknet;
        Context context;
        JSONParser jsonparser;

        public SendSms(Context cntxt) {
            this.context = cntxt;
            jsonparser = new JSONParser();
            checknet = new CheckInternet(context);
            session = new SessionManager(context);
        }
      

     
        public String sendmsg(String msg,String num) {
         	String LoginArray = "";
         //http://promotionalsms.rannlab.com/sendSMS?username=7503764145&test12&sendername=rannlb&smstype=TRANS&numbers=9716430901&apikey=4e7e8325-72d3-498a-a9ef-038c363207b3	
         	String url1="http://promotionalsms.rannlab.com/sendSMS?username=7503764145&message="+msg+"&sendername=rannlb&smstype=TRANS&numbers="+num+"&apikey=4e7e8325-72d3-498a-a9ef-038c363207b3";
            
         	http://promotionalsms.ranlab.com/sendSMS?username=75303764145&message=virendra Your API Added Sucessfully&sendername=rannlb&smstype=TRANS&numbers=9716430901&apikey=4e7e8325-72d3-498a-a9ef-038c363207b3
             url1 = url1.replace(" ", "%20");
             try {
                 LoginArray = jsonparser.GetSimpleData(url1);
             } catch (Exception e) {

                 e.printStackTrace();
             }
             return LoginArray;
         }
        
        public String SendmsgURL(String url) {
         	String LoginArray = "";
         	
         	
             url = url.replace(" ", "%20");
             try {
                 LoginArray = jsonparser.GetSimpleData(url);
             } catch (Exception e) {

                 e.printStackTrace();
             }
             return LoginArray;
         }
        
       
}
