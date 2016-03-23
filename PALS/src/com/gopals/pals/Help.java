package com.gopals.pals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		Toast.makeText(
				getApplicationContext(),
				"latitude = " + getIntent().getDoubleExtra("latitude", 0)
						+ "\nlongitude = "
						+ getIntent().getDoubleExtra("longitude", 0),
				Toast.LENGTH_LONG).show();
	}
}
