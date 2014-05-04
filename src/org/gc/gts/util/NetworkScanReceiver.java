package org.gc.gts.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
//import android.util.Log;
import android.util.Log;

public class NetworkScanReceiver extends BroadcastReceiver {
	private static String TAG = "NetworkScanReceiver";

	private static long lastCalled = -1;

	private static final int MIN_PERIOD_BTW_CALLS = 10 * 1000;// 10 Seconds
	// Constants used for different security types
	public static final String WPA2 = "WPA2";
	public static final String WPA = "WPA";
	public static final String WEP = "WEP";
	public static final String OPEN = "Open";

	private int mPreferredNetworkID = -1;

	@Override
	public void onReceive(Context context, Intent intent) {
		long now = System.currentTimeMillis();

		 Log.d(TAG, "Action Received: " + intent.getAction() +
		 " From intent: "
		 + intent);
		if (lastCalled == -1)
			lastCalled = now;// react at first scan after MIN_PERIOD_BTW_CALLS
		if (now - lastCalled > MIN_PERIOD_BTW_CALLS) {
			lastCalled = now;
			boolean autoConnectEnabled = true;

			if (autoConnectEnabled) {
				WifiManager wm = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wm.getConnectionInfo();

				//Log.d(TAG, "connectionInfo.getSupplicantState():"
				//		+ connectionInfo.getSupplicantState());

				if (connectionInfo.getSupplicantState().equals(
						SupplicantState.SCANNING)) {
					if (!isAnyPreferedNetworkAvailable(wm)) {
						ScanResult wifiScanResult = getUsableNetwork(wm
								.getScanResults());
						if (wifiScanResult != null) {
						//	Log.d(TAG, "Scan result found:" + wifiScanResult);
							WifiConfiguration wifiNetwork = lookupConfigurationByScanResult(
									wm.getConfiguredNetworks(), wifiScanResult);
						//	Log.d(TAG, "Configured Network found:"
						//			+ wifiNetwork);
							if (wifiNetwork == null) {


								wifiNetwork = new WifiConfiguration();
								wifiNetwork.BSSID = wifiScanResult.BSSID;
								wifiNetwork.priority = 40;
								wifiNetwork.SSID = "\"".concat(
										wifiScanResult.SSID).concat("\"");

								wifiNetwork.allowedGroupCiphers
										.set(WifiConfiguration.GroupCipher.TKIP);
								wifiNetwork.allowedAuthAlgorithms
										.set(WifiConfiguration.AuthAlgorithm.OPEN);
								wifiNetwork.allowedKeyManagement
										.set(WifiConfiguration.KeyMgmt.NONE);

								wifiNetwork.status = WifiConfiguration.Status.ENABLED;
						//		Log
						//				.d(TAG, "Adding new Network: "
						//						+ wifiNetwork);

								wifiNetwork.networkId = wm
										.addNetwork(wifiNetwork);
								wm.saveConfiguration();

								int updateNetworkResult = wm
										.updateNetwork(wifiNetwork);
						//		Log.d(TAG, "New WIFI Network:"
						//				+ updateNetworkResult + "::"
						//				+ wifiNetwork);
								if (updateNetworkResult < 0) {
									cleanWiFiConfigurations(wm);
								}

							}

							if (wifiNetwork.networkId != -1) {
								Log.d(TAG, "Enable Network:" + wifiNetwork);

								wm.enableNetwork(wifiNetwork.networkId, true);
							}

							lastCalled = System.currentTimeMillis();
							Log.d(TAG, "Trying to connect");
						}
					} else {

						if (mPreferredNetworkID >= 0) {
							//Log.d(TAG, "Connecting to PreferredNetwork: "+ mPreferredNetworkID);
							// wm.enableNetwork(mPreferredNetworkID, true);

						}
					}
				}
			}

		} else {
			//Log.d(TAG, "Events to close, ignoring.");
		}

	}

	private WifiConfiguration lookupConfigurationByScanResult(
			List<WifiConfiguration> configuredNetworks, ScanResult scanResult) {
		boolean found = false;
		WifiConfiguration wifiConfiguration = null;
		Iterator<WifiConfiguration> it = configuredNetworks.iterator();
		while (!found && it.hasNext()) {
			wifiConfiguration = it.next();
			// Log.v(TAG, wifiConfiguration.SSID + " equals " + "\""
			// + scanResult.SSID + "\"");
			if (wifiConfiguration.SSID != null) {
				found = wifiConfiguration.SSID.equals("\"" + scanResult.SSID
						+ "\"");
			}
		}

		if (!found) {
			wifiConfiguration = null;
		}

		return wifiConfiguration;
	}

	private ScanResult getUsableNetwork(List<ScanResult> scanResults) {
		ScanResult scanResult = null;
		boolean found = false;

		if (scanResults != null) {
			Iterator<ScanResult> it = scanResults.iterator();
			while (!found && it.hasNext()) {
				scanResult = it.next();
				found = isOpenNetwork(scanResult);
			}
			if (!found) {
				scanResult = null;
			}
		}

		return scanResult;
	}

	private boolean isOpenNetwork(ScanResult scanResult) {
		// TODO Auto-generated method stub
		if (getScanResultSecurity(scanResult).contains(OPEN)) {
			//Log.d(TAG, "Found Open Network:" + scanResult);
			return true;
		}
		//Log.d(TAG, "Unsuported Network: " + scanResult);
		return false;
	}

	private boolean isAnyPreferedNetworkAvailable(WifiManager wm) {
		Set<String> scanResultsKeys = new HashSet<String>();

		List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
		if (!configuredNetworks.isEmpty()) {
			List<ScanResult> scanResults = wm.getScanResults();
			if (scanResults != null && !scanResults.isEmpty()) {
				for (ScanResult scanResult : scanResults) {
					scanResultsKeys.add("\"" + scanResult.SSID + "\"");
					//Log.v(TAG, "Adding scanResultKey:" + scanResult.SSID);
				}

				Set<String> wifiConfigurationsKeys = new HashSet<String>();
				for (WifiConfiguration wifiConfiguration : configuredNetworks) {
					wifiConfigurationsKeys.add(wifiConfiguration.SSID);
					//Log.v(TAG, "Adding wifiConfigurationsKey:"
					//		+ wifiConfiguration.SSID);
				}
				scanResultsKeys.retainAll(wifiConfigurationsKeys);

				if (!scanResultsKeys.isEmpty()) {
					Iterator<String> it = scanResultsKeys.iterator();

					if (it.hasNext()) {
						String pf = it.next();
						//Log.d(TAG, "Connecting to prefered network: " + pf);

						boolean disableOthers = true;
						mPreferredNetworkID = getPfNetID(pf, configuredNetworks);
						wm.enableNetwork(mPreferredNetworkID, disableOthers);
					}
				}

			}
		}

		return !scanResultsKeys.isEmpty();
	}

	private int getPfNetID(String pf, List<WifiConfiguration> configuredNetworks) {
		Iterator<WifiConfiguration> it = configuredNetworks.iterator();

		while (it.hasNext()) {
			WifiConfiguration net = it.next();
			if (net.SSID.equals(pf))
				return net.networkId;
		}
		return 0;
	}

	private void cleanWiFiConfigurations(WifiManager wm) {
		List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
		for (WifiConfiguration wifiConfiguration : configuredNetworks) {
			if (wifiConfiguration.SSID == null || wifiConfiguration.allowedAuthAlgorithms.cardinality() == 0) {
				Log.v(TAG, "Removing null wifiConfiguration:"
						+ wifiConfiguration);
				wm.removeNetwork(wifiConfiguration.networkId);
			}

		}
	}

	// Comparator to order Scanresults from high signal level to low
	class ScanResultComparator implements Comparator<ScanResult> {
		public int compare(ScanResult scanResult1, ScanResult scanResult2) {
			return scanResult2.level - scanResult1.level;
		}
	}

	/**
	 * @return The security of a given {@link ScanResult}.
	 */
	public static String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { WEP, WPA, WPA2 };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				//Log.d(TAG, "Security mode:" + cap);
				return securityModes[i];
			}
		}
		//Log.d(TAG, "Open network found: " + scanResult);
		return OPEN;
	}

}