/* JSON API for android appliation */
package WebService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class Service1 {
    private final String urlString = "http://104.238.126.224:8003/handler1.ashx";

    private static String convertStreamToUTF8String(InputStream stream) throws IOException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[4096];
            int readedChars = 0;
            while (readedChars != -1) {
                readedChars = reader.read(buffer);
                if (readedChars > 0)
                    sb.append(buffer, 0, readedChars);
            }
            result = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String load(String contents) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(60000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream());
        w.write(contents);
        w.flush();
        InputStream istream = conn.getInputStream();
        String result = convertStreamToUTF8String(istream);
        return result;
    }


    private Object mapObject(Object o) {
        Object finalValue = null;
        if (o.getClass() == String.class) {
            finalValue = o;
        }
        else if (Number.class.isInstance(o)) {
            finalValue = String.valueOf(o);
        } else if (Date.class.isInstance(o)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", new Locale("en", "USA"));
            finalValue = sdf.format((Date)o);
        }
        else if (Collection.class.isInstance(o)) {
            Collection<?> col = (Collection<?>) o;
            JSONArray jarray = new JSONArray();
            for (Object item : col) {
                jarray.put(mapObject(item));
            }
            finalValue = jarray;
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            Method[] methods = o.getClass().getMethods();
            for (Method method : methods) {
                if (method.getDeclaringClass() == o.getClass()
                        && method.getModifiers() == Modifier.PUBLIC
                        && method.getName().startsWith("get")) {
                    String key = method.getName().substring(3);
                    try {
                        Object obj = method.invoke(o, null);
                        Object value = mapObject(obj);
                        map.put(key, value);
                        finalValue = new JSONObject(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return finalValue;
    }

    public JSONObject insert_loc(String lat,String lon,String address1,String locationdate,String name,String mobile) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "insert_loc");
        p.put("lat",mapObject(lat));
        p.put("lon",mapObject(lon));
        p.put("address1",mapObject(address1));
        p.put("locationdate",mapObject(locationdate));
        p.put("name",mapObject(name));
        p.put("mobile",mapObject(mobile));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject user_login(String mobile) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "user_login");
        p.put("mobile",mapObject(mobile));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject reg_new_user(String name,String mobile,String RegId) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "reg_new_user");
        p.put("name",mapObject(name));
        p.put("mobile",mapObject(mobile));
        p.put("RegId",mapObject(RegId));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject last_loc(String name,String mobile) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "last_loc");
        p.put("name",mapObject(name));
        p.put("mobile",mapObject(mobile));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject list_task(String mobile,String status_flag) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "list_task");
        p.put("mobile",mapObject(mobile));
        p.put("status_flag",mapObject(status_flag));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject update_status(String task_id,String status_flag) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "update_status");
        p.put("task_id",mapObject(task_id));
        p.put("status_flag",mapObject(status_flag));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject todays_list_task(String mobile,String status_flag) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","Service1");
        o.put("method", "todays_list_task");
        p.put("mobile",mapObject(mobile));
        p.put("status_flag",mapObject(status_flag));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

}


