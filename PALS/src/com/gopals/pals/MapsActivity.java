package com.gopals.pals;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap gMap;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String placeName, placeAddress;
    Double placeLat, placeLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        
        placeName = getIntent().getStringExtra("place_name");
        placeAddress = getIntent().getStringExtra("place_address");
        placeLat = Double.valueOf(getIntent().getStringExtra("place_lat"));
        placeLong = Double.valueOf(getIntent().getStringExtra("place_long"));
        setUpMap();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 3 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
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
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
        	LocationServices.FusedLocationApi.requestLocationUpdates(
    	            mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "Location update resumed .....................");
        }
    }
    
    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (gMap != null) {
            	gMap.addMarker(new MarkerOptions().position(new LatLng(placeLat, placeLong)).title(placeName));
            	gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setRotateGesturesEnabled(false);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(placeLat, placeLong), 12);
                gMap.animateCamera(cameraUpdate);
            }
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Current Location");
        gMap.addMarker(options);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }
}
