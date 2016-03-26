package com.gopals.pals;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    String placeName, placeAddress, placeLat, placeLong, placeDistance;
	private String category;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_result);
		RelativeLayout noData = (RelativeLayout)findViewById(R.id.no_data_layout);
		
		ListView lv = getListView();
		
		final Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		TextView resultLbl = (TextView)findViewById(R.id.resultLbl);
		TextView noDataLbl = (TextView)findViewById(R.id.label_no_data);
		resultLbl.setTypeface(bariol, Typeface.BOLD);
		noDataLbl.setTypeface(bariol, Typeface.BOLD);
		
		placeList = new ArrayList<HashMap<String, String>>();
		final String[] from = {TAG_NAME, TAG_ADDRESS, TAG_DISTANCE, TAG_LAT, TAG_LONG};
		
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		if(bundle==null){
			noData.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
		} else {
			if (bundle.getString("category").equals("gas_station")){
				category = bundle.getString("category");
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
		        				R.layout.list_item, from, to){
		            		
							@Override
		                    public View getView(int position, View convertView, ViewGroup parent){
								if(convertView== null){
					                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					                convertView=vi.inflate(R.layout.list_item, null);
					            }
		                        TextView nameLbl = (TextView)convertView.findViewById(R.id.placeName);
				        		TextView addressLbl = (TextView)convertView.findViewById(R.id.placeAddress);
				        		TextView latLbl = (TextView)convertView.findViewById(R.id.placeLat);
				        		TextView longLbl = (TextView)convertView.findViewById(R.id.placeLong);
				        		TextView distanceLbl = (TextView)convertView.findViewById(R.id.placeDistance);
				        		
				        		nameLbl.setText(placeList.get(position).get(TAG_NAME));
				        		addressLbl.setText(placeList.get(position).get(TAG_ADDRESS));
				        		latLbl.setText(placeList.get(position).get(TAG_LAT));
				        		longLbl.setText(placeList.get(position).get(TAG_LONG));
				        		distanceLbl.setText(placeList.get(position).get(TAG_DISTANCE));
				        		
				        		nameLbl.setTypeface(bariol, Typeface.BOLD);
				        		addressLbl.setTypeface(bariol);
				        		distanceLbl.setTypeface(bariol);
		                        return convertView;
		                    }
		            	};
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
					placeName = ((TextView) view.findViewById(R.id.placeName)).getText().toString();
					placeAddress = ((TextView) view.findViewById(R.id.placeAddress)).getText().toString();
					placeLat = ((TextView) view.findViewById(R.id.placeLat)).getText().toString();
					placeLong = ((TextView) view.findViewById(R.id.placeLong)).getText().toString();
					placeDistance = ((TextView)view.findViewById(R.id.placeDistance)).getText().toString();
					
					Intent mapsActivity = new Intent(getApplicationContext(), MapsActivity.class);
					mapsActivity.putExtra("place_name", placeName);
					mapsActivity.putExtra("place_address", placeAddress);
					mapsActivity.putExtra("place_lat", placeLat);
					mapsActivity.putExtra("place_long", placeLong);
					mapsActivity.putExtra("place_distance", placeDistance);
					mapsActivity.putExtra("category", category);
					startActivity(mapsActivity);
					
				} else 	Toast.makeText(getApplicationContext(), "Maaf tidak ada koneksi internet", 
							Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	
	
}
