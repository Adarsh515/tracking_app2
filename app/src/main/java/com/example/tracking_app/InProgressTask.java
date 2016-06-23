package com.example.tracking_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import CheckInternet.CheckInternet;
import SessionManager.SessionManager;
import WebService.Service1;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InProgressTask extends Fragment {

	private String TASK_ID="task_id";
	private String TASK_NAME="task_name";
	private  String TASK_DISC="task_desc";
	private  String TASK_DATE="assigned_date";
	ProgressDialog progress;
	SessionManager session;
	Service1 ws;
	CheckInternet checknet;
	ListView lv;
	TextView norecord;
	public static final String ITEM_NAME = "itemName";
	@Override
	public void onResume() {
	    super.onResume();
	    // Set title
	    getActivity()
	        .setTitle("InProgress Task");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.task_list, container,
				false);

		session =new SessionManager(getActivity());
		checknet=new CheckInternet(getActivity());
		ws=new Service1();
		lv=(ListView)view.findViewById(R.id.listViewTask);
		norecord=(TextView)view.findViewById(R.id.textnorecord);
		
		if(checknet.isNetworkAvailable()){
			getInprogressTaskList dw=new getInprogressTaskList();
			dw.execute();
			
		}else{
			lv.setVisibility(View.GONE);
        	norecord.setVisibility(View.VISIBLE);
        	norecord.setText("Internet Not Available");
       //	 Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
		}
		
		OnItemClickListener itemClickListener = new OnItemClickListener() {
		            
		            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		            	
		                TextView tskname=(TextView)arg1.findViewById(R.id.texttaskname);
		                
		                TextView tskdisc=(TextView)arg1.findViewById(R.id.texttaskdisc);
		                TextView taskdate=(TextView)arg1.findViewById(R.id.texttaskDate);
		                TextView taskid=(TextView)arg1.findViewById(R.id.texttaskid);
		               
		                viewPendingTaskDetail(tskname.getText().toString(),tskdisc.getText().toString(),taskdate.getText().toString(),taskid.getText().toString());
		           	
		            }
		        };
		        
		        lv.setOnItemClickListener(itemClickListener);
		        view.setFocusableInTouchMode(true);
		        view.requestFocus();

		       
		        view.setOnKeyListener(new OnKeyListener() {
		                public boolean onKey(View v, int keyCode, KeyEvent event) {
		                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
		                            if (keyCode == KeyEvent.KEYCODE_BACK) {
		                            	Fragment fragment = null;
		                    			fragment = new TodayTask();
		                    			FragmentManager frgManager = getFragmentManager();
		                    			frgManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		                            return true;
		                            }
		                        }
		                        return false;
		                    }
		                });
				return view;
			}
			
			public void viewPendingTaskDetail(String tskname,String tskdisc,String tskDate ,final String tskId) {
				LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
				View promptView = layoutInflater.inflate(R.layout.task_detail, null);
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme3);
				//final Dialog dialog = new Dialog(getActivity());
				//dialog.setContentView(R.layout.task_detail);
				//dialog.setTitle("Complete Task detail");
				builder.setTitle("InProgress Task Detail");
				builder.setView(promptView);
				builder.setIcon(R.drawable.inprogress_task);
				final AlertDialog dialog = builder.create();
				// display dialog
				dialog.show();
			
				
		           TextView tasknamet=(TextView)promptView.findViewById(R.id.textdTaskname);
		           tasknamet.setText(tskname);
		           TextView taskdisct=(TextView)promptView.findViewById(R.id.textdTaskDis);
		           taskdisct.setText(tskdisc);
		           
		           TextView taskdatet=(TextView)promptView.findViewById(R.id.textassignDate);
		           taskdatet.setText(tskDate);
		           
		           final Button submit=(Button)promptView.findViewById(R.id.buttonTaskSubmit);
		          submit.setText("Task Complete");
		           submit.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(checknet.isNetworkAvailable()){
						dialog.dismiss();
						updatePendingTaskStatus dsa=new updatePendingTaskStatus( tskId);
						dsa.execute();
						}else{
							 Toast.makeText(getActivity(), "Internet Not Available", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				final Button btnsave = (Button) promptView.findViewById(R.id.button1taskCancel);
				btnsave.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();


					}

				});






			}
			
			private class updatePendingTaskStatus extends AsyncTask<Void, Void, Void> {

				 JSONObject userList=null ;
			     JSONArray ds=null;
			     String TASK_ID="",value="";
			     updatePendingTaskStatus(String ids){
			    	 this.TASK_ID=ids;
			     }
			     
			     @Override
					protected void onPreExecute() {
						super.onPreExecute();
						progress = new ProgressDialog(getActivity());
						progress.setMessage("Loading...");
						progress.setCancelable(false);
						progress.show();
					}
			     
			     @Override
				protected Void doInBackground(Void... params) {
					try{//{"assigned_date":"04\/01\/2016","task_id":"1","task_desc":"complete it by today","task_name":"sms application"},
						userList=ws.update_status(TASK_ID, "3");
//{"Successful":true}
						Iterator iter = userList.keys();
						while(iter.hasNext()){
							String key = (String)iter.next();
							if(key.equals("Successful")) {
								  value = userList.getString(key);

							}
						}


					}catch(Exception e){
							e.getMessage();
						}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					progress.dismiss();
					if(value.equals("true")){
						Toast.makeText(getActivity(), "Update Successfull", Toast.LENGTH_LONG).show();
						Fragment fragment = null;
            			fragment = new InProgressTask();
            			FragmentManager frgManager = getFragmentManager();
            			frgManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					}else{
						Toast.makeText(getActivity(), "Update UnSuccessfull", Toast.LENGTH_LONG).show();
					}
					
					super.onPostExecute(result);
				}

			}
	private class getInprogressTaskList extends AsyncTask<Void, Void, Void> {

		 JSONObject userList=null ;
	     JSONArray ds=null;
	     
	     @Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = new ProgressDialog(getActivity());
				progress.setMessage("Loading...");
				progress.setCancelable(false);
				progress.show();
			}
	     
	     @Override
		protected Void doInBackground(Void... params) {
			try{//{"assigned_date":"04\/01\/2016","task_id":"1","task_desc":"complete it by today","task_name":"sms application"},
				userList=ws.list_task(session.GetMobileNo(),"2");

				Iterator iter = userList.keys();
				while(iter.hasNext()){
					String key = (String)iter.next();
					if(key.equals("Value")) {
						String value = userList.getString(key);
						ds=new JSONArray(value);
					}


			}		}catch(Exception e){
					e.getMessage();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progress.dismiss();
			ArrayList<HashMap<String, String>> RegisterList = new ArrayList<HashMap<String, String>>();
			
			if (ds != null) {
               
               if (ds.length() > 0) {
               try {
                     
                      for (int i = 0; i < ds.length(); i++) {
                               JSONObject obj;
                       	    obj = ds.getJSONObject(i);
                       	    try{
                       	    String taskid=obj.getString(TASK_ID);
                          String taskname=obj.getString(TASK_NAME);
                          String taskdate=obj.getString(TASK_DATE);
                          String taskdisc=obj.getString(TASK_DISC);
                          
                               HashMap<String,String> map=new HashMap<String,String>();
                               map.put(TASK_ID, taskid);
                             map.put(TASK_NAME, taskname);
                             map.put(TASK_DATE, taskdate);
                             map.put(TASK_DISC, taskdisc);
                            
                               RegisterList.add(map);
                       	    }catch(Exception e){
                       	    	e.getMessage();
                       	    }
                             }
				   SimpleAdapter adapter = new SimpleAdapter(getActivity(), RegisterList, R.layout.task_list_item,
						   new String[]{ TASK_ID,TASK_NAME,TASK_DATE,TASK_DISC},new int[]{R.id.texttaskid, R.id.texttaskname,
						   R.id.texttaskDate,R.id.texttaskdisc});

				   lv.setAdapter(adapter);
                   } catch (JSONException e) {
                   
                       e.printStackTrace();
                   }

              // }
               }else{
            	   lv.setVisibility(View.GONE);
            	   norecord.setVisibility(View.VISIBLE);
               	norecord.setText("No InProgress Task");
              	 Toast.makeText(getActivity(), "No InProgress Task", Toast.LENGTH_LONG).show();
               }
               }

			super.onPostExecute(result);
		}

	}
}
