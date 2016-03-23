package com.gopals.pals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		String versionName = getResources().getString(R.string.versionName);
		TextView version = (TextView) findViewById(R.id.versionName);
		version.setText(versionName);

		LinearLayout rate = (LinearLayout) findViewById(R.id.rateApps);
		rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Intent googlePlay = new Intent(Intent.ACTION_VIEW,
				 * Uri.parse("market://details?id=com.gopals.pals"));
				 * startActivity(googlePlay);
				 */
			}
		});

	}

}
