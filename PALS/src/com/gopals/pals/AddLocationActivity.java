package com.gopals.pals;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AddLocationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		TextView tv = (TextView) findViewById(R.id.tvTest);
		tv.setText("Category = "+ getIntent().getStringExtra("category")
				+ "\nlatitude = " + getIntent().getDoubleExtra("latitude", 0)
				+ "\nlongitude = " + getIntent().getDoubleExtra("longitude", 0));

	}

}
