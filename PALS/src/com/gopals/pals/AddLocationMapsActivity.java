package com.gopals.pals;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddLocationMapsActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	final Context context = this;
    
	public static final String TAG = AddLocationMapsActivity.class
			.getSimpleName();
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private GoogleMap gMap;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_add_location);
		
		category = getIntent().getStringExtra("category");
		
		setUpMap();
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(3 * 1000) // 3 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds
		gMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				
				gMap.clear();
				gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				gMap.addMarker(markerOptions);
			}
		});
		
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				final Dialog dialog = new Dialog(context);
    			dialog.setContentView(R.layout.dialog_add_location);
    			dialog.setTitle("Confirmation");
    			Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);
    			Button close = (Button) dialog.findViewById(R.id.btn_cancel);
    			
    			confirm.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent in = new Intent(AddLocationMapsActivity.this, AddLocationActivity.class);
						in.putExtra("category", category);
						in.putExtra("latitude", marker.getPosition().latitude);
						in.putExtra("longitude", marker.getPosition().longitude);
						startActivity(in);
					}
				});
    			
    			close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
    			dialog.show();
				return true;
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMap();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	private void setUpMap() {
		if (gMap == null) {
			gMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map_add_Location	)).getMap();
			if (gMap != null) {
				gMap.getUiSettings().setRotateGesturesEnabled(false);
			}
		}
	}

	private void handleView(Location location) {
		Log.d(TAG, location.toString());

		double currentLatitude = location.getLatitude();
		double currentLongitude = location.getLongitude();

		LatLng latLng = new LatLng(currentLatitude, currentLongitude);

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				15);
		gMap.animateCamera(cameraUpdate);
	}

	@Override
	public void onConnected(Bundle bundle) {
		Location location = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
		} else {
			handleView(location);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);	
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "Location services connection failed with code "
					+ connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {}
}