/*
 *   Written by Tom van Braeckel @ http://code.google.com/u/tomvanbraeckel/
 *
 *   This file is part of OpenGPSTracker.
 *
 *   OpenGPSTracker is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenGPSTracker is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenGPSTracker.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.gc.gts.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private final static String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d( TAG,
		// "BootReceiver.onReceive(), probably ACTION_BOOT_COMPLETED" );
		String action = intent.getAction();

		// start on BOOT_COMPLETED
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			// Log.d( TAG, "BootReceiver received ACTION_BOOT_COMPLETED" );

			context.startService(new Intent(Const.SERVICENAME));

		} else {
			Log.w(TAG, "OpenGPSTracker's BootReceiver received " + action
					+ ", but it's only able to respond to "
					+ Intent.ACTION_BOOT_COMPLETED );
		}
	}
}
