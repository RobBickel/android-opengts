package org.gc.gts.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	private final static String TAG = "NetworkStateReceiver";
@Override
public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub


    if (intent.getAction().equals(
            android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
    	 Log.d(TAG, "action: "
                 + intent.getAction());

        // do something..
    	 
    }
}
}
