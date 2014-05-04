package org.gc.gts.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {
	private final static String TAG = "WifiStateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		final String action = intent.getAction();
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			handleNetworkStateChanged((NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));
		} else if (action
				.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			handleSupplicantConnectionChanged(intent.getBooleanExtra(
					WifiManager.EXTRA_SUPPLICANT_CONNECTED, false));
		} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			handleWifiStateChanged(intent.getIntExtra(
					WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));
		}

	}

	private void handleWifiStateChanged(int intExtra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "handle WifiStateChanged " + intExtra);

	}

	private void handleSupplicantConnectionChanged(boolean booleanExtra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "handle SupplicantConnectionChanged " + booleanExtra);

	}

	private void handleNetworkStateChanged(NetworkInfo parcelableExtra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "handle NetworkStateChanged "
				+ parcelableExtra.getState());
		State ns = parcelableExtra.getState();
		if(ns == NetworkInfo.State.DISCONNECTED){
			//handle network disconnected

		}

	}

}
