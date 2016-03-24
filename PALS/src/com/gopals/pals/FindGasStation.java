package com.gopals.pals;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class FindGasStation extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
    private ProgressDialog pDialog;
    private static final String GET_SPBU = 
			"http://gopals.netau.net/get_spbu.php";
    public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_GAS_STATION = "spbu";
	public static final String TAG_ID = "id_spbu";
    public static final String TAG_NAME = "spbu_name";
    public static final String TAG_ADDRESS = "spbu_address";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    
    private JSONArray spbuJSON = null;
    private ArrayList<String> spbuNameList, spbuAddressList, spbuLatList, 
    				spbuLongList, spbuDistanceList;
    private ArrayList<Location> locationList;
    String company;
    Location currentLocation;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_gas_station);
		
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
        
        String[] companySpr=null;
        String[] radiusSpr=null;
        companySpr = getResources().getStringArray(R.array.company);
        radiusSpr = getResources().getStringArray(R.array.radius);
        ArrayAdapter<String> adapterCompany = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_dropdown_item, companySpr);
        ArrayAdapter<String> adapterRadius = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_dropdown_item, radiusSpr);
        
        final Spinner SpinnerCompany = (Spinner) findViewById(R.id.spr_company);
        final Spinner SpinnerRadius = (Spinner) findViewById(R.id.spr_radius);
        SpinnerCompany.setAdapter(adapterCompany);
        SpinnerRadius.setAdapter(adapterRadius);
        
        Button findButton = (Button)findViewById(R.id.gasStationFindButton);
        findButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				company = (String) SpinnerCompany.getSelectedItem();
				
				if(company.equals("Select Company")){
					Toast.makeText(FindGasStation.this, "Please Select The Company", 
							Toast.LENGTH_SHORT).show();
				} else {
					new GetGasStation().execute();
					
				}			
			}
		});
	}
	
	@Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }
	
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }
	
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
    
    @Override
	public void onConnectionFailed(ConnectionResult connectionResult) {}

	@Override
	public void onConnected(Bundle bundle) {
		startLocationUpdates();
		currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	}

	@Override
	public void onConnectionSuspended(int i) {}

	@Override
	public void onLocationChanged(Location location) {}
	
	private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_help:
        	Intent help = new Intent(FindGasStation.this, Help.class);
        	startActivity(help);
        	break;
        case R.id.action_about:
        	Intent about = new Intent(FindGasStation.this, About.class);
        	startActivity(about);
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
	
	protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
	
	protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(
	            mGoogleApiClient, mLocationRequest, this);
	}
	
	public class GetGasStation extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindGasStation.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			spbuNameList = new ArrayList<String>();
			spbuAddressList = new ArrayList<String>();
			spbuLatList = new ArrayList<String>();
			spbuLongList = new ArrayList<String>();
			locationList = new ArrayList<Location>();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company", company));
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(GET_SPBU, params);
	        Log.d("All Data: ", json.toString());
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					spbuJSON = json.getJSONArray(TAG_GAS_STATION);
					for (int i = 0; i < spbuJSON.length(); i++) {
						JSONObject c = spbuJSON.getJSONObject(i);
						String spbuName = c.getString(TAG_NAME);
						String spbuAddress = c.getString(TAG_ADDRESS);
						Double spbuLatitude = c.getDouble(TAG_LATITUDE);
						Double spbuLongitude = c.getDouble(TAG_LONGITUDE);
						Location spbuLocation = new Location("spbu location");
						spbuLocation.setLatitude(spbuLatitude);
						spbuLocation.setLongitude(spbuLongitude);
						
					    spbuNameList.add(spbuName);
					    spbuAddressList.add(spbuAddress);
					    spbuLatList.add(String.valueOf(spbuLatitude));
					    spbuLongList.add(String.valueOf(spbuLongitude));   
					    locationList.add(spbuLocation); 
					} 
				} 
			} catch (JSONException e) {
				 e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			spbuDistanceList = new ArrayList<String>();
			if(locationList.size()>0){
				for(int i=0; i<locationList.size(); i++){
					Double dist = CalculateRadius(currentLocation, locationList.get(i));
					Log.d("Distance", dist.toString());
					spbuDistanceList.add(String.valueOf(dist));
				}
			}
			String[] arrayName = new String[spbuNameList.size()];
			String[] arrayAddress = new String[spbuAddressList.size()];
			String[] arrayLat = new String[spbuLatList.size()];
			String[] arrayLong = new String[spbuLongList.size()];
			String[] arrayDistance = new String[spbuDistanceList.size()];
			
			arrayName = spbuNameList.toArray(arrayName);
			arrayAddress = spbuAddressList.toArray(arrayAddress);
			arrayLat = spbuLatList.toArray(arrayLat);
			arrayLong = spbuLongList.toArray(arrayLong);
			arrayDistance = spbuDistanceList.toArray(arrayDistance);
			
			pDialog.dismiss();
			
			Intent listResult = new Intent(FindGasStation.this, ListResultActivity.class);
			listResult.putExtra("category", "gas_station");
			listResult.putExtra("spbu_name", arrayName);
			listResult.putExtra("spbu_address", arrayAddress);
			listResult.putExtra("spbu_company", company);
			listResult.putExtra("spbu_lat", arrayLat);
			listResult.putExtra("spbu_long", arrayLong);
			listResult.putExtra("spbu_distance", arrayDistance);
			startActivity(listResult);
		}
	}
	
	public double CalculateRadius(Location curLoc, Location destLoc) {
        int Radius = 6371; // radius of earth in Km
        double curLat = curLoc.getLatitude();
        double destLat = destLoc.getLatitude();
        double curLong = curLoc.getLongitude();
        double destLong = destLoc.getLongitude();
        double dLat = Math.toRadians(destLat - curLat);
        double dLon = Math.toRadians(destLong - curLong);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(curLat))
                * Math.cos(Math.toRadians(destLat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        NumberFormat newFormat = new DecimalFormat("#.##");
        double result = 2 * Math.asin(Math.sqrt(a));
           
        return Double.valueOf(newFormat.format(Radius * result));
    }
	
}

