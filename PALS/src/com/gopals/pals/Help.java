package com.gopals.pals;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Help extends Activity{
	
	LinearLayout helpDesc1, helpDesc2, helpDesc3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		TextView helpLbl = (TextView)findViewById(R.id.helpLbl);
		TextView help1Lbl = (TextView)findViewById(R.id.help1Lbl);
		TextView help2Lbl = (TextView)findViewById(R.id.help2Lbl);
		TextView help3Lbl = (TextView)findViewById(R.id.help3Lbl);
		TextView helpDesc1Lbl = (TextView)findViewById(R.id.helpDesc1Lbl);
		TextView helpDesc2Lbl = (TextView)findViewById(R.id.helpDesc2Lbl);
		TextView helpDesc3Lbl = (TextView)findViewById(R.id.helpDesc3Lbl);
		
		helpLbl.setTypeface(bariol, Typeface.BOLD);
		help1Lbl.setTypeface(bariol, Typeface.BOLD);
		help2Lbl.setTypeface(bariol, Typeface.BOLD);
		help3Lbl.setTypeface(bariol, Typeface.BOLD);
		helpDesc1Lbl.setTypeface(bariol);
		helpDesc2Lbl.setTypeface(bariol);
		helpDesc3Lbl.setTypeface(bariol);
		
		final LinearLayout help1 = (LinearLayout)findViewById(R.id.help1);
		final LinearLayout help2 = (LinearLayout)findViewById(R.id.help2);
		final LinearLayout help3 = (LinearLayout)findViewById(R.id.help3);
		final LinearLayout helpDesc1 = (LinearLayout)findViewById(R.id.helpDesc1);
		final LinearLayout helpDesc2 = (LinearLayout)findViewById(R.id.helpDesc2);
		final LinearLayout helpDesc3 = (LinearLayout)findViewById(R.id.helpDesc3);
		
		help1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				help2.setBackgroundResource(R.drawable.side_border_top);
				helpDesc1.setVisibility(View.VISIBLE);
				helpDesc2.setVisibility(View.GONE);
				helpDesc3.setVisibility(View.GONE);
			}
		});
		
		help2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				help2.setBackgroundResource(R.drawable.side_border_bottom);
				helpDesc1.setVisibility(View.GONE);
				helpDesc2.setVisibility(View.VISIBLE);
				helpDesc3.setVisibility(View.GONE);
			}
		});
		
		help3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				help2.setBackgroundResource(R.drawable.side_border);
				helpDesc1.setVisibility(View.GONE);
				helpDesc2.setVisibility(View.GONE);
				helpDesc3.setVisibility(View.VISIBLE);
			}
		});
		
	}

	
}
