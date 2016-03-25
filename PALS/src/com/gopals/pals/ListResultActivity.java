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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListResultActivity extends ListActivity{
	
	public static final String TAG_NAME = "placeName";
    public static final String TAG_ADDRESS = "placeAddress";
    public static final String TAG_LAT = "placeLat";
    public static final String TAG_LONG = "placeLong";
    public static final String TAG_DISTANCE = "placeDistance";
    ArrayList<HashMap<String, String>> placeList; 
    String[] arrPlaceName, arrPlaceAddress, arrPlaceLat, arrPlaceLong, arrPlaceDistance;
    String company;
    String placeName, placeAddress, placeLat, placeLong;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_result_activity);
		RelativeLayout noData = (RelativeLayout)findViewById(R.id.no_data_layout);
		
		ListView lv = getListView();
	
		placeList = new ArrayList<HashMap<String, String>>();
		final String[] from = {TAG_NAME, TAG_ADDRESS, TAG_DISTANCE, TAG_LAT, TAG_LONG};
		
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		if(bundle==null){
			/*
			arrPlaceName = new String[]{"ATM BCA MARGONDA", "ATM BCA BEJI", "ATM BCA JUANDA", "ATM BCA TANAH BARU"};
			arrPlaceAddress = new String[]{"Margonda, Depok", "Beji, Depok", "Juanda, Depok", "Tanah Baru, Depok"};
			arrPlaceLat = new String[]{"1", "10", "20", "30"};
			arrPlaceLong = new String[]{"1", "10", "20", "30"};
			arrPlaceDistance = new String[]{"0.5", "0.2", "0.75", "0.4"};
			*/
			noData.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
		} else {
			if (bundle.getString("category").equals("gas_station")){
				arrPlaceName = bundle.getStringArray("spbu_name");
				company = bundle.getString("spbu_company");
				arrPlaceAddress = bundle.getStringArray("spbu_address");
				arrPlaceLat = bundle.getStringArray("spbu_lat");
				arrPlaceLong = bundle.getStringArray("spbu_long");
				arrPlaceDistance = bundle.getStringArray("spbu_distance");
			}
			
			final int[] to = {R.id.placeName, R.id.placeAddress, R.id.placeDistance, R.id.placeLat, R.id.placeLong};
			
			int x = 0;
			for(int j=0; j<arrPlaceName.length; j++){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(from[x], arrPlaceName[j]);
				map.put(from[x+1], arrPlaceAddress[j]);
				map.put(from[x+2], arrPlaceDistance[j] + " km");
				map.put(from[x+3], arrPlaceLat[j]);
				map.put(from[x+4], arrPlaceLong[j]);
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
			} 
		}
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					placeName = ((TextView) view.findViewById(R.id.placeName)).
							getText().toString();
					placeAddress = ((TextView) view.findViewById(R.id.placeAddress)).
							getText().toString();
					placeLat = ((TextView) view.findViewById(R.id.placeLat)).
							getText().toString();
					placeLong = ((TextView) view.findViewById(R.id.placeLong)).
							getText().toString();
					
					Intent mapsActivity = new Intent(getApplicationContext(), MapsActivity.class);
					mapsActivity.putExtra("place_name", placeName);
					mapsActivity.putExtra("place_address", placeAddress);
					mapsActivity.putExtra("place_lat", placeLat);
					mapsActivity.putExtra("place_long", placeLong);
					startActivity(mapsActivity);
					
				} else 	Toast.makeText(getApplicationContext(), "Maaf tidak ada koneksi internet", 
							Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	
	
}
