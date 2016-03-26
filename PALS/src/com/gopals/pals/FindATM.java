package com.gopals.pals;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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

public class FindATM extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
    String bankName, radiusStr;
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
    String[] radiusAmount=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_atm);
		
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
        
        String[] bankNameSpr=null;
        String[] radiusSpr=null;
        
        bankNameSpr = getResources().getStringArray(R.array.bank_name);
        radiusSpr = getResources().getStringArray(R.array.radius_display);
        radiusAmount = getResources().getStringArray(R.array.radius);
        ArrayAdapter<String> adapterBank = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_dropdown_item, bankNameSpr);
        ArrayAdapter<String> adapterRadius = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_dropdown_item, radiusSpr);
        
        final Spinner SpinnerBank = (Spinner) findViewById(R.id.spr_bank);
        final Spinner SpinnerRadius = (Spinner) findViewById(R.id.spr_radius);
        SpinnerBank.setAdapter(adapterBank);
        SpinnerRadius.setAdapter(adapterRadius);
        
        Button findButton = (Button)findViewById(R.id.atmFindButton);
        findButton.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
				bankName = (String) SpinnerBank.getSelectedItem();
				radiusStr = radiusAmount[SpinnerRadius.getSelectedItemPosition()];
				
				Intent listResult = new Intent(FindATM.this, ListResultActivity.class);
				startActivity(listResult);
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
        	Intent help = new Intent(FindATM.this, Help.class);
        	startActivity(help);
        	break;
        case R.id.action_about:
        	Intent about = new Intent(FindATM.this, About.class);
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
	
	protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
	
}
