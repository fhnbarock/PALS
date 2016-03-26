package com.gopals.pals;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MenuActivity extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		TextView atmLbl = (TextView)findViewById(R.id.atmLbl);
		TextView gasStationLbl = (TextView)findViewById(R.id.gasStationLbl);
		TextView repairShopLbl = (TextView)findViewById(R.id.repairShopLbl);
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		atmLbl.setTypeface(bariol);
		gasStationLbl.setTypeface(bariol);
		repairShopLbl.setTypeface(bariol);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
        
        LinearLayout btnATM = (LinearLayout)findViewById(R.id.findATM);
        btnATM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent findATM = new Intent(MenuActivity.this, FindATM.class);
				startActivity(findATM);
			}
		});
        
        LinearLayout btnGasStation = (LinearLayout)findViewById(R.id.findGasStation);
        btnGasStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent findGasStation = new Intent(MenuActivity.this, FindGasStation.class);
				startActivity(findGasStation);
			}
		});
        
        LinearLayout btnRepairShop = (LinearLayout)findViewById(R.id.findRepairShop);
        /*
        btnRepairShop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent findRepairShop = new Intent(MenuActivity.this, FindRepairShop.class);
				startActivity(findRepairShop);
			}
		});
		*/
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
    /*
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    */
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
        	Intent help = new Intent(MenuActivity.this, Help.class);
        	startActivity(help);
        	break;
        case R.id.action_about:
        	Intent about = new Intent(MenuActivity.this, About.class);
        	startActivity(about);
        	break;
        case R.id.action_add_location:
        	Intent addLocation = new Intent(MenuActivity.this, AddLocationMapsActivity.class);
        	startActivity(addLocation);
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
