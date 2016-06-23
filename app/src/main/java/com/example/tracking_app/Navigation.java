package com.example.tracking_app;

import java.util.ArrayList;
import java.util.List;

import CheckInternet.CheckInternet;
import SessionManager.SessionManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
public class Navigation extends AppCompatActivity{
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	List<DrawerItem> dataList;
	CustomDrawerAdapter adapter;
	

String[] list=new String[]{"Today Task","Pending Task","InProgress Task","Complete Task","Log Out"};
		
SessionManager session;
CheckInternet checknet;
@Override
public void onBackPressed() 
{
	
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    startActivity(intent);
    
}


		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.navigation);
			
			
			checknet=new CheckInternet(Navigation.this);
			
			
			session=new SessionManager(Navigation.this);
		
			dataList = new ArrayList<DrawerItem>();
			
			mTitle = mDrawerTitle = getTitle();
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = (ListView) findViewById(R.id.left_drawer);

			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);
       //"Today Task","Pending Task","InProgress Task","Complete Task","Log Out"};
			
			dataList.add(new DrawerItem(R.drawable.today_task1,"Today Task"));
			dataList.add(new DrawerItem(R.drawable.pending1,"Pending Task"));
			dataList.add(new DrawerItem(R.drawable.work_in_progress1,"InProgress Task"));
			dataList.add(new DrawerItem(R.drawable.complete_task1,"Complete Task"));
			dataList.add(new DrawerItem(R.drawable.logout,"Log Out"));
			
			
			adapter = new CustomDrawerAdapter(getApplicationContext(),dataList);
			mDrawerList.setAdapter(adapter);
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);
			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			getSupportActionBar().setHomeAsUpIndicator( R.drawable.drawer_icon);


			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);

			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.drawable.ic_drawer, R.string.drawer_open,
					R.string.drawer_close) {
				public void onDrawerClosed(View view) {
					getSupportActionBar().setTitle(mTitle);
					invalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
				}

				public void onDrawerOpened(View drawerView) {
					getSupportActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
				}
			};

			mDrawerLayout.setDrawerListener(mDrawerToggle);

			if (savedInstanceState == null) {
				SelectItem(0);
			}
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

		public void SelectItem(int possition) {

			Fragment fragment = null;
			Bundle args = new Bundle();
			
			switch (possition) {
			case 0:
				
				fragment = new TodayTask();
				args.putString(TodayTask.ITEM_NAME, dataList.get(possition).getItemName());
				fragment.setArguments(args);
				
				sendActivity(possition,fragment);
				
				break;
			case 1:
				
				fragment = new PendingTask();
				args.putString(PendingTask.ITEM_NAME, dataList.get(possition).getItemName());
                fragment.setArguments(args);
				
                sendActivity(possition,fragment);
				
				break;
			case 2:
				
				fragment = new InProgressTask();
				args.putString(InProgressTask.ITEM_NAME, dataList.get(possition).getItemName());
                fragment.setArguments(args);
                sendActivity(possition,fragment);

				
				break;
				
			case 3:
			
				fragment = new CompleteTask();
				args.putString(CompleteTask.ITEM_NAME, dataList.get(possition).getItemName());
                fragment.setArguments(args);
                sendActivity(possition,fragment);

				
				break;
			
			case 4:


				    
				    session.Savepreferences("MobileNoo", "");
				    Intent fs = new Intent(Navigation.this,MainActivity.class);
	      			startActivity(fs);
	      			finish();
	      			Toast.makeText(getApplicationContext(), "Successfully LogOut", Toast.LENGTH_LONG).show();
	       
	      			break;
			
			
			default:
				break;
			}

			
		}
		public void sendActivity(int possition ,Fragment fragment){
			FragmentManager frgManager = getFragmentManager();
			frgManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(possition, true);
			setTitle(dataList.get(possition).getItemName());
			mDrawerLayout.closeDrawer(mDrawerList);
			
		}

		
		@Override
		public void setTitle(CharSequence title) {
			mTitle = title;
			getSupportActionBar().setTitle(mTitle);
		}

		@Override
		protected void onPostCreate(Bundle savedInstanceState) {
			super.onPostCreate(savedInstanceState);
			// Sync the toggle state after onRestoreInstanceState has occurred.
			mDrawerToggle.syncState();
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
			// Pass any configuration change to the drawer toggles
			mDrawerToggle.onConfigurationChanged(newConfig);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// The action bar home/up action should open or close the drawer.
			// ActionBarDrawerToggle will take care of this.
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}

			return false;
		}

		private class DrawerItemClickListener implements
				ListView.OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				SelectItem(position);

			}
		}
}