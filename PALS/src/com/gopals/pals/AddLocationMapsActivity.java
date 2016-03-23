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
import com.google.android.gms.maps.OnMapReadyCallback;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class AddLocationMapsActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	final Context context = this;
    
	public static final String TAG = AddLocationMapsActivity.class
			.getSimpleName();
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private GoogleMap gMap; // Might be null if Google Play services APK is not
							// available.
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps_add_location);
		setUpMap();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(3 * 1000) // 3 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds
		gMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				// TODO Auto-generated method stub
				MarkerOptions markerOptions = new MarkerOptions();

				// Setting the position for the marker
				markerOptions.position(latLng);

				// Setting the title for the marker.
				// This will be displayed on taping the marker
				

				// Clears the previously touched position
				gMap.clear();

				// Animating to the touched position
				gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

				// Placing a marker on the touched position
				gMap.addMarker(markerOptions);

			}
		});
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(context);
    			dialog.setContentView(R.layout.activity_confirm_add_location);
    			dialog.setTitle("Confirmation");
    			Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);
    			Button close = (Button) dialog.findViewById(R.id.btn_cancel);
    			confirm.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(AddLocationMapsActivity.this, AddLocationActivity.class);
						in.putExtra("latitude", marker.getPosition().latitude);
						in.putExtra("longitude", marker.getPosition().longitude);
						startActivity(in);
					}
				});
    			close.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
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
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (gMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			gMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map_add_Location	)).getMap();
			// Check if we were successful in obtaining the map.
			if (gMap != null) {
				// mMap.addMarker(new MarkerOptions().position(new LatLng(0,
				// 0)).title("Marker"));

				gMap.getUiSettings().setRotateGesturesEnabled(false);
			}
		}
	}

	private void handleNewLocation(Location location) {
		Log.d(TAG, location.toString());

		double currentLatitude = location.getLatitude();
		double currentLongitude = location.getLongitude();

		LatLng latLng = new LatLng(currentLatitude, currentLongitude);

		// mMap.addMarker(new MarkerOptions().position(new
		// LatLng(currentLatitude,
		// currentLongitude)).title("Current Location"));
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
			handleNewLocation(location);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			Log.i(TAG, "Location services connection failed with code "
					+ connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		handleNewLocation(location);
	}
}