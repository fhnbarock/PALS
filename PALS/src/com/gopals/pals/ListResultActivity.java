package com.gopals.pals;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListResultActivity extends ListActivity{
	
	public static final String TAG_NAME = "placeName";
    public static final String TAG_ADDRESS = "placeAddress";
    public static final String TAG_DISTANCE = "placeDistance";
    ArrayList<HashMap<String, String>> placeList; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_result_activity);
		RelativeLayout noData = (RelativeLayout)findViewById(R.id.no_data_layout);
		
		ListView lv = getListView();
	
		placeList = new ArrayList<HashMap<String, String>>();
		
		final String[] from = {TAG_NAME, TAG_ADDRESS, TAG_DISTANCE};
		String[] placeName = {"ATM BCA MARGONDA", "ATM BCA BEJI", "ATM BCA JUANDA", "ATM BCA TANAH BARU"};
		String[] placeAddress = {"Margonda, Depok", "Beji, Depok", "Juanda, Depok", "Tanah Baru, Depok"};
		String[] placeDistance = {"500 m", "200 m", "750 m", "400 m"};
		
		final int[] to = {R.id.placeName, R.id.placeAddress, R.id.placeDistance};
		
		int i = 0;
		for(int j=0; j<placeName.length; j++){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[i], placeName[j]);
			map.put(from[i+1], placeAddress[j]);
			map.put(from[i+2], placeDistance[j]);
			placeList.add(map);
		}
		
		if(placeList.size()>0){	
			runOnUiThread(new Runnable() {
	            public void run() {
	            	ListAdapter adapter = new SimpleAdapter(getApplicationContext(), placeList,
	        				R.layout.list_item, from, to);	
	        		setListAdapter(adapter);
	            }
			});
		} else {
			noData.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
		}
		
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					Intent mapsActivity = new Intent(getApplicationContext(), MapsActivity.class);
					startActivity(mapsActivity);
					
				} else 	Toast.makeText(getApplicationContext(), "Maaf tidak ada koneksi internet", 
							Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	
	
}
