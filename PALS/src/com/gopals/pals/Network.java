package com.gopals.pals;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
	
	public boolean isNetworkConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) return false;
		if (!ni.isConnected()) return false;
		if (!ni.isAvailable()) return false;
		return true;
	}
}
