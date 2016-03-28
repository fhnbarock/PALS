package com.gopals.pals;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class FindRepairShop extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		AdapterView.OnItemSelectedListener {
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
    private ProgressDialog pDialog;
    private static final String GET_BENGKEL = 
			"http://gopals.netau.net/get_bengkel.php";
    public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_REPAIR_SHOP = "bengkel";
	public static final String TAG_ID = "id_bengkel";
    public static final String TAG_NAME = "bengkel_name";
    public static final String TAG_ADDRESS = "bengkel_address";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    
    private JSONArray bengkelJSON = null;
    private ArrayList<String> bengkelNameList, bengkelAddressList, bengkelLatList, 
    				bengkelLongList; 
    private ArrayList<Double> bengkelRadiusList;
    private ArrayList<Location> locationList;
    String vehicleType, brand, radiusStr;
    Location currentLocation;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
    Spinner SpinnerVehicle, SpinnerBrand, SpinnerRadius;
    
    String[] vehicleTypeSpr, radiusSpr;
    String[] radiusAmount=null;
    HashMap<String, String []> hash_brand = new HashMap<String, String []>();
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		generateBrand();
        
		setContentView(R.layout.find_repair_shop);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
        
        vehicleTypeSpr= new String[] { "Select Vehicle Type", "Car", "Motorcycle" };
        radiusSpr=null;
        
        radiusSpr = getResources().getStringArray(R.array.radius_display);
        radiusAmount = getResources().getStringArray(R.array.radius);
        
        MySpinnerAdapter adapterVehicle = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, vehicleTypeSpr);
        MySpinnerAdapter adapterRadius = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, radiusSpr);
        
        SpinnerVehicle = (Spinner) findViewById(R.id.spr_vehicle);
        SpinnerBrand = (Spinner) findViewById(R.id.spr_brand);
        SpinnerRadius = (Spinner) findViewById(R.id.spr_radius);
        SpinnerVehicle.setAdapter(adapterVehicle);
        SpinnerRadius.setAdapter(adapterRadius);
        
        SpinnerVehicle.setOnItemSelectedListener(this);
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
        	Intent help = new Intent(FindRepairShop.this, Help.class);
        	startActivity(help);
        	break;
        case R.id.action_about:
        	Intent about = new Intent(FindRepairShop.this, About.class);
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

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
		fillSpinnerBrand(vehicleTypeSpr[position]);
	}
	
	public void onNothingSelected(AdapterView<?> parent){}
	
	private void generateBrand(){ 
		hash_brand.put("Select Vehicle Type", new String[] {"Select Brand"});
		hash_brand.put("Car", new String[] {"Select Brand", "Toyota", "Honda", 
				"Daihatsu", "Suzuki"});
		hash_brand.put("Motorcycle", new String[] {"Select Brand", "Honda", "Yamaha", 
				"Suzuki", "Kawasaki"});
	}
	
	private void fillSpinnerBrand(String vehicle){
		String[] brandSpr=null;
        MySpinnerAdapter adapterBrand = null;
		try {
			brandSpr = hash_brand.get(vehicle);
			adapterBrand = new MySpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, 
					brandSpr);
		} catch (NullPointerException e){
			Log.d("error", e.toString());
		}
		SpinnerBrand.setAdapter(adapterBrand);
	}

	
}
