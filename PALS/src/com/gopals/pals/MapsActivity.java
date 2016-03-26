package com.gopals.pals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	public static final String TAG = MapsActivity.class.getSimpleName();
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private GoogleMap gMap;
	Polyline line;

	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String placeName, placeAddress, placeDistance;
	Double placeLat, placeLong;
	final Context context = this;
	private String category;
	private LatLng origin;
	Network download = new Network();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		placeName = getIntent().getStringExtra("place_name");
		placeAddress = getIntent().getStringExtra("place_address");
		placeLat = Double.valueOf(getIntent().getStringExtra("place_lat"));
		placeLong = Double.valueOf(getIntent().getStringExtra("place_long"));
		placeDistance = getIntent().getStringExtra("place_distance");
		category = getIntent().getStringExtra("category");
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

		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.activity_info_location);
				dialog.setTitle("Information");
				Button getDirections = (Button) dialog
						.findViewById(R.id.btn_get_direction);
				Button close = (Button) dialog.findViewById(R.id.btn_close);
				ImageView img = (ImageView) dialog
						.findViewById(R.id.imgDescriptor);
				TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
				TextView tvAddress = (TextView) dialog
						.findViewById(R.id.tv_address);
				if (category.equals("gas_station")) {
					tvName.setText("Name\t : " + placeName);
					tvAddress.setText("Address\t : " + placeAddress);
					img.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_launcher));
				} else if (category.equals("atm")) {

				} else if (category.equals("repair_shop")) {

				}

				getDirections.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Network network = new Network();
						if (isNetworkConnected(getApplicationContext())) {
							try {
								String url = getDirectionsUrl(origin,
										new LatLng(placeLat, placeLong));
								DownloadTask downloadTask = new DownloadTask();
								// Start downloading json data from Google
								// Directions
								// API
								downloadTask.execute(url);
								dialog.dismiss();
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										"Slow Internet Connection",
										Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(getApplicationContext(),
									"No Internet Connection", Toast.LENGTH_LONG)
									.show();
						}
						
					}

					private boolean isNetworkConnected(Context ctx) {
						// TODO Auto-generated method stub
						ConnectivityManager cm = (ConnectivityManager) ctx
								.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo ni = cm.getActiveNetworkInfo();
						if (ni == null)
							return false;
						if (!ni.isConnected())
							return false;
						if (!ni.isAvailable())
							return false;
						return true;
					}
				});
				close.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});
				if (!marker.getTitle().equals("Your Position")) {
					dialog.show();

				}
				return true;
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
		Log.d(TAG,
				"isConnected ...............: "
						+ mGoogleApiClient.isConnected());
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
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (gMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			gMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (gMap != null) {
				gMap.addMarker(new MarkerOptions().position(
						new LatLng(placeLat, placeLong)).title(placeName));
				gMap.setMyLocationEnabled(true);
				gMap.getUiSettings().setRotateGesturesEnabled(false);
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						new LatLng(placeLat, placeLong), 12);
				gMap.animateCamera(cameraUpdate);
			}
		}
	}

	private void handleNewLocation(Location location) {
		Log.d(TAG, location.toString());

		double currentLatitude = location.getLatitude();
		double currentLongitude = location.getLongitude();
		LatLng latLng = new LatLng(currentLatitude, currentLongitude);
		origin = latLng;

		// mMap.addMarker(new MarkerOptions().position(new
		// LatLng(currentLatitude,
		// currentLongitude)).title("Current Location"));
		MarkerOptions options = new MarkerOptions().position(latLng).title(
				"Your Position");
		gMap.addMarker(options);
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
		// handleNewLocation(location);
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		String sensor = "sensor=false";
		String mode = "mode=walking";

		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
			// TODO Auto-generated method stub
			String data = "";

			// Fetching the data from web service
			try {
				data = downloadUrl(url[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			// TODO Auto-generated method stub
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			String distance = "";
			String duration = "";

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(4);
				lineOptions.color(Color.RED);
			}

			// Drawing polyline in the Google Map for the i-th route
			gMap.addPolyline(lineOptions);

		}

	}

}
