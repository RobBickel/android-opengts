package org.gc.gts.logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gc.gts.R;
import org.gc.gts.util.AppException;
import org.gc.gts.util.Const;
import org.gc.gts.util.NetworkScanReceiver;
import org.gc.gts.util.StatusCodes;
import org.gc.gts.util.WifiStateReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OpenGTStracker extends Service{

	protected String TAG = "OpenGTStracker";

	protected int mLoggingState;
	// protected Object mPrecision;
	// Logging precision, defined in resources, changed by settings
	private static final int LOGGING_FINE = 0;
	private static final int LOGGING_NORMAL = 1;
	private static final int LOGGING_COARSE = 2;
	private static final int LOGGING_GLOBAL = 3;
	private static final int MAX_REASONABLE_SPEED = 60;

	private Context mContext;
	private LocationManager mLocationManager;

	private NotificationManager mNotificationManager;
	// Number of satellites
	private int mSatellites = 0;
	private float mMaxAcceptableAccuracy = 5000;

	// list of locations
	private LinkedList<Location> mLocationTrack = new LinkedList<Location>();
	private Location mPreviousLocation;
	private Notification mNotification;
	// power manager wake status
	private PowerManager.WakeLock mWakeLock;
	// wifi manager
	private WifiManager.WifiLock mWifiLock;

	private static final int HTTP_STATUS_OK = 200;
	private static String sUserAgent = "aGTS2.0";

	private static String mHeaderValue;

	private int mPrecision;
	private SharedPreferences mSharedPreferences;
	private String mImei;
	private boolean mUse_Wifi = true;
	private boolean mSpeedSanityCheck = true;

	private WifiStateReceiver mWifiStateReceiver;
	private NetworkScanReceiver mNetworkScanReceiver;

	private String account = "sysadmin";
	private String server = "track.skytrace.com:8080";
	private String servletPath = "/gprmc";

	private int mStatusCode;

	private final Timer timer = new Timer();
	private final Handler handler = new Handler();

	private TimerTask checkLocationListener = new TimerTask() {

		@Override
		public void run() {
			// TODO
			if (mLoggingState == Const.LOGGING) {
				handler.post(new Runnable() {
					public void run() {
						Log.d(TAG, "Restart Logging by TimerTask.");
						
						Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						long timeDelta = 0;
						if(lastKnownLocation != null)
							timeDelta = Math.abs(System.currentTimeMillis()-lastKnownLocation.getTime());
						else
							timeDelta = 0;
						
						if(lastKnownLocation.hasAltitude() && (timeDelta < 1000L)) setNormalAccuracy(); else setNetworkAccuracy();
						stopLogging();
						startLogging();
					}
				});
				
			} 
		}
	};

	private void netUpdatePreferences(String preferences) {
		if (preferences.contains("LOGGING")) {			
			mPrecision = LOGGING_FINE;
			Log.w(TAG, "Device Notes applied:" + mPrecision);
			Editor pfEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			pfEditor.putString(Const.PRECISION, Integer.toString(mPrecision));
			if (preferences.contains("WIFI"))
				pfEditor.putBoolean(Const.USE_WIFI, true);
			pfEditor.commit();
			updateWifiStateReceiver();
		}
	};

	private OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(TAG, "Shared preferences changed: " + key);
			if (key.equals(Const.PRECISION)) {
				requestLocationUpdates();
				setupNotification();

			} else if (key.equals(Const.USE_WIFI)) {
				//is force Wifi state is unknow, do not use, will improve battery
				mUse_Wifi = sharedPreferences.getBoolean(Const.USE_WIFI, false);
				updateWifiStateReceiver();
			}
		}
	};

	private IBinder mBinder = new org.gc.gts.OpenGTSRemote.Stub() {
		public int loggingState() throws RemoteException {

			return mLoggingState;
		}

		public void startLogging() throws RemoteException {
			OpenGTStracker.this.startLogging();

		}

		public void pauseLogging() throws RemoteException {
			OpenGTStracker.this.pauseLogging();
		}

		public void resumeLogging() throws RemoteException {
			OpenGTStracker.this.resumeLogging();

		}

		public void stopLogging() throws RemoteException {
			OpenGTStracker.this.stopLogging();
		}

	};

	private Listener mStatusListener = new GpsStatus.Listener() {
		int mPrevEvent = -1;

	public synchronized void onGpsStatusChanged(int event) {
			if (mPrevEvent > 0 && event != mPrevEvent) {
				mPrevEvent = event;
				switch (event) {
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					GpsStatus status = mLocationManager.getGpsStatus(null);
					mSatellites = 0;
					Iterable<GpsSatellite> list = status.getSatellites();
					for (GpsSatellite satellite : list) {
						if (satellite.usedInFix()) {
							mSatellites++;
						}
					}
					Log.d(TAG, "New GPS Event: " + event);
					// change adaptive resolution with sattelite numbers
					updateNotification();
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {

		return this.mBinder;
	}

	protected void updateNotification() {
		// TODO Auto-generated method stub

		CharSequence contentTitle = getResources().getString(org.gc.gts.R.string.app_name)	+ " - " + getResources().getString(R.string.account);

		String precision = getResources().getStringArray(org.gc.gts.R.array.precision_choices)[mPrecision];
		String state = getResources().getStringArray(org.gc.gts.R.array.state_choices)[mLoggingState - 1];
		CharSequence contentText;
		switch (mPrecision) {
		case (LOGGING_GLOBAL):
			contentText = getResources().getString(
					R.string.service_networkstatus, state, precision);
			break;
		default:
			contentText = getResources().getString(R.string.service_gpsstatus,
					state, precision, mSatellites);
			break;
		}
		Intent notificationIntent = new Intent(this,
				org.gc.gts.GtsActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(R.layout.main, mNotification);
		Log.d(TAG, "updateNotification: " + mNotification);
	}

	protected void stopLogging() {
		// TODO Auto-generated method stub
		Log.d(TAG, "stopLogging");
		this.mLoggingState = Const.STOPPED;
		updateWifiLock();
		updateWakeLock();
		updateNotification();

	}

	protected void resumeLogging() {
		// TODO Auto-generated method stub
		Log.d(TAG, "resumeLogging");
		this.mLoggingState = Const.LOGGING;

		updateWifiLock();
		updateWakeLock();
		updateNotification();
	}

	protected void pauseLogging() {
		// TODO Auto-generated method stub
		Log.d(TAG, "pauseLogging");
		this.mLoggingState = Const.PAUSED;
		updateWifiLock();
		updateWakeLock();
		updateNotification();

	}

	protected void startLogging() {
		// TODO Auto-generated method stub
		Log.d(TAG, "startLogging");
		requestLocationUpdates();
		this.mLocationManager.addGpsStatusListener(this.mStatusListener);
		this.mLoggingState = Const.LOGGING;
		updateWakeLock();
		updateWifiLock();
		setupNotification();

	}

	private void setupNotification() {
		// TODO Auto-generated method stub
		Log.d(TAG, "setupNotification");
		mNotificationManager.cancel(org.gc.gts.R.layout.main);

		int icon = org.gc.gts.R.drawable.small_icon;
		CharSequence tickerText = getResources().getString(
				org.gc.gts.R.string.service_start);
		long when = System.currentTimeMillis();

		mNotification = new Notification(icon, tickerText, when);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;

		updateNotification();
	}

	private void updateWakeLock() {
		// TODO Auto-generated method stub
		Log.d(TAG, "updateWakeLock");
		if (this.mLoggingState == Const.LOGGING) {
			// PreferenceManager.getDefaultSharedPreferences( this.mContext
			// ).registerOnSharedPreferenceChangeListener(
			// mSharedPreferenceChangeListener );
			PowerManager pm = (PowerManager) this.mContext
					.getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			this.mWakeLock.acquire();
		} else {
			if (this.mWakeLock != null) {
				this.mWakeLock.release();
				this.mWakeLock = null;
			}
		}
	}

	private void updateWifiLock() {
		// TODO Auto-generated method stub

		if (this.mLoggingState == Const.LOGGING) {
			// PreferenceManager.getDefaultSharedPreferences( this.mContext
			// ).registerOnSharedPreferenceChangeListener(
			// mSharedPreferenceChangeListener );
			WifiManager wm = (WifiManager) this.mContext
					.getSystemService(Context.WIFI_SERVICE);
			this.mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
			if (this.mWifiLock != null) {
				this.mWifiLock.acquire();
				Log.d(TAG, "updateWifiLock to Acquire");
			}

		} else {
			if (this.mWifiLock != null) {
				this.mWifiLock.release();
				this.mWifiLock = null;
				Log.d(TAG, "updateWifiLock to release");
			}
		}
	}

	/**
	 * here is registering location handlers
	 */
	private void requestLocationUpdates() {
		// TODO Auto-generated method stub
		Log.d(TAG, "requestLocationUpdates");		
		this.mLocationManager.removeUpdates(this.mLocationListener);
		
		mPrecision = new Integer(PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(Const.PRECISION, "4")).intValue();
		
		Log.d(TAG, "requestLocationUpdates to precision " + mPrecision);
		
		switch (mPrecision) {
			case (LOGGING_FINE): // Fine
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000l, 5F,this.mLocationListener);
				mMaxAcceptableAccuracy = 20f;
			break;
			case (LOGGING_NORMAL): // Normal
				mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 15000l, 10F,
					this.mLocationListener);
				mMaxAcceptableAccuracy = 50f;
			break;
			case (LOGGING_COARSE): // Coarse
				mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 30000l, 25F,
					this.mLocationListener);
				mMaxAcceptableAccuracy = 200f;
			break;
			case (LOGGING_GLOBAL): // Global
				mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 300000l, 500F,
					this.mLocationListener);
				mMaxAcceptableAccuracy = 5000f;
				if (!isNetworkConnected()) {
					disabledProviderNotification(R.string.service_connectiondisabled);
				}
			break;	
		default:
			Log.e(TAG, "Unknown precision " + mPrecision+" ,starting LOGGING_GLOBAL");
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 300000l, 500F,this.mLocationListener);
				mMaxAcceptableAccuracy = 5000f;
			if (!isNetworkConnected()) {
					disabledProviderNotification(R.string.service_connectiondisabled);
			}
			break;
		}
	}

	private void disabledProviderNotification(int resId) {
		int icon = R.drawable.small_icon;
		CharSequence tickerText = getResources().getString(resId);
		long when = System.currentTimeMillis();
		Notification gpsNotification = new Notification(icon, tickerText, when);
		gpsNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = getResources().getString(R.string.app_name)
				+ " - " + getResources().getString(R.string.account);
		
		CharSequence contentText = getResources().getString(resId);
		Intent notificationIntent = new Intent(this,org.gc.gts.GtsActivity.class);
		// notificationIntent.setData( ContentUris.withAppendedId(
		// Tracks.CONTENT_URI, mTrackId ) );
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		gpsNotification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(R.drawable.icon, gpsNotification);
	}

	private LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged");
			Location filteredLocation = locationFilter(location);
			if (filteredLocation != null) {
				SendLocation(filteredLocation);
			}
		}

		public void onProviderDisabled(String provider) {
			Log.d(TAG, "onProviderDisabled: " + provider);
			if (provider.equals("gps")) {
				// switching to
				// network
				// location
				// provider
				mPrecision = LOGGING_COARSE;
				Log.w(TAG, "Accuracy changed to:" + mPrecision);
				Editor pfEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
				pfEditor.putString(Const.PRECISION, Integer.toString(mPrecision));
				pfEditor.commit();			
			}

		}
		
		

		public void onProviderEnabled(String provider) {
			Log.d(TAG, "onProviderEnabled: " + provider);
			if (provider.equals("gps")) {
				
				setNormalAccuracy();
				
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.w(TAG, String.format("Provider %s changed to status %d",
					provider, status));
		}
	};

	public Location locationFilter(Location proposedLocation) {
		Log.d(TAG, "locationFilter()");

		if (mPreviousLocation == null) {
			mPreviousLocation = proposedLocation;
			return proposedLocation;
		}

		if (proposedLocation != null
				&& (proposedLocation.getAccuracy() > mMaxAcceptableAccuracy)) {
			Log.w(TAG,
					String.format(
							"A weak location was recieved, lots of inaccuracy... (%f is more then max %f)",
							proposedLocation.getAccuracy(),
							mMaxAcceptableAccuracy));
			return null;
		}

		// Do not log a waypoint which might be on any side of the previous
		// waypoint
		if (proposedLocation != null
				&& mPreviousLocation != null
				&& (proposedLocation.getAccuracy() > mPreviousLocation
						.distanceTo(proposedLocation))) {
			Log.w(TAG,
					String.format(
							"A weak location was recieved, not quite clear from the previous waypoint... (%f more then max %f)",
							proposedLocation.getAccuracy(),
							mPreviousLocation.distanceTo(proposedLocation)));
			mPreviousLocation = proposedLocation;
			return null;
		}

		// Speed checks for NETWORK logging, check if the proposed location
		// could be reached from the previous one in sane speed
		if (mSpeedSanityCheck && proposedLocation != null
				&& mPreviousLocation != null) {
			// To avoid near instant teleportation on network location or
			// glitches cause continent hopping
			float meters = proposedLocation.distanceTo(mPreviousLocation);
			long seconds = (proposedLocation.getTime() - mPreviousLocation
					.getTime()) / 1000L;

			if (meters / seconds > MAX_REASONABLE_SPEED) {
				Log.w(TAG,
						"A strange location was recieved, a really high speed, prob wrong..."
								+ proposedLocation.toString());
				return null;
			}
		}
		mPreviousLocation = proposedLocation;
		return proposedLocation;

	}

	protected void SendLocation(Location loc) {
		// TODO Auto-generated method stub
		Log.d(TAG, "SendLocation");
		// check IMEI - maybe is a special device
		boolean ex = false;
		// test if is data in queue, send it
		// aici se trimit datele catre serverul OpenGTS, deocamdata pe HTTP
		int size = mLocationTrack.size();

		while (size > 0) {

			String url = getLocationURL(mLocationTrack.peek());
			mStatusCode = StatusCodes.STATUS_WAYMARK_0;

			try {
				ex = false;
				--size;
				setOpenGTSLocation(url);

			} catch (org.gc.gts.util.AppException e) {
				Log.w(TAG, "Error sending from queue: " + url);
				mStatusCode = StatusCodes.STATUS_WAYMARK_1;
				ex = true;
				size = 0;

			}
			if (!ex) {
				Log.w(TAG, "Sent from queue: " + url);
				mLocationTrack.remove();

			}
		}
		if (loc != null) {
			String url = getLocationURL(loc);
			// start here a new background task?
			try {
				mStatusCode = StatusCodes.STATUS_LOCATION;
				setOpenGTSLocation(url);
			} catch (org.gc.gts.util.AppException e) {
				// error, put the data in queue
				e.printStackTrace();
				Log.w(TAG, "Error, adding to queue: " + loc.toString());
				mLocationTrack.add(loc);
				Log.w(TAG, "QSize= " + mLocationTrack.size());
			}
			if (mHeaderValue != null) {
				//read headers and change software preferences
				netUpdatePreferences(mHeaderValue);
			}
			;
		}
	}

	private String getLocationURL(Location loc) {
		// TODO Auto-generated method stub
		String device = mImei;
		if (device == null)
			device = "android";

		String url = "http://" + server + servletPath + "/Data?acct=" + account
				+ "&dev=" + device;
		url = url + "&lon=" + loc.getLongitude() + "&lat=" + loc.getLatitude()
				+ "&head=" + loc.getBearing() + "&speed="
				+ (loc.getSpeed() * 3.6) + "&alt=" + loc.getAltitude()
				+ "&time=" + loc.getTime() + "&code=" + mStatusCode;
		Log.w(TAG, "getLocationURL: " + url);

		return url;
	}

	protected static synchronized void setOpenGTSLocation(String url)
			throws AppException {

		// Create client and set our specific user-agent string
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", sUserAgent);

		try {
			HttpResponse response = client.execute(request);

			// Check if server response is valid
			StatusLine status = response.getStatusLine();

			if (status.getStatusCode() != HTTP_STATUS_OK) {
				throw new AppException("Invalid response from server: "
						+ status.toString());
			}

			Header header = response.getLastHeader("DeviceNotes");
			if(header != null)
				mHeaderValue = header.getValue();

		} catch (IOException e) {
			throw new AppException("Problem communicating with API", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "OpenGTStracker.java onCreate()");
		super.onCreate();
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//device ID is phone Imei
		mImei = telephonyManager.getDeviceId();
		
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		//init some variables, account name and server name
		account = mPrefs.getString("account", "sysadmin");
		server = getResources().getString(R.string.server);

		//if is a tablet, does not have Imei, then use ANDORID_ID as uniq ID
		try
		{
			if (Double.parseDouble(mImei) == 0) {
				final String androidId;
				androidId = ""
						+ android.provider.Settings.Secure.getString(
								getContentResolver(),
								android.provider.Settings.Secure.ANDROID_ID);
				mImei = androidId.trim();
			}
		}
		catch(Exception ex)
		{}
		//just for debugging
		Log.d(TAG, "account: " + account);
		Log.d(TAG, "server: " + server);
		Log.d(TAG, "Imei: " + mImei);
	  
		//LoggingState is STOPPED
		mLoggingState = Const.STOPPED;
		mContext = getApplicationContext();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		//register a shared preferences Listener, it will be called at every preferences change 
		mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
		// SharedPreferences sharedPreferences =
		// PreferenceManager.getDefaultSharedPreferences( this.mContext );
		mLocationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
		// get the NotificationManager and cancel all old notifications of this app
		mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		//at first installation does not activate agressive wifi
		mUse_Wifi = mSharedPreferences.getBoolean(Const.USE_WIFI, false);
		
		//list of locations
		mLocationTrack = new LinkedList<Location>();
		
		//OpenGTS status code
		mStatusCode = StatusCodes.STATUS_LOCATION;
		// check status every 5 minutes, after 5 minutes from start
		timer.schedule(checkLocationListener, 5000 * 60, 5000 * 60);
		
		//uses mUse_Wifi shared preference to update Wifi state
		updateWifiStateReceiver();
		//actually start the logging
		startLogging();

	}

	
	private void updateWifiStateReceiver() {
		// TODO Auto-generated method stub
		if (mUse_Wifi) {
			
			mWifiStateReceiver = new WifiStateReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
			intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			
			mContext.registerReceiver(mWifiStateReceiver, intentFilter);
			mNetworkScanReceiver = new NetworkScanReceiver();
			
			IntentFilter scanFilter = new IntentFilter();
			scanFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			
			mContext.registerReceiver(mNetworkScanReceiver, scanFilter);
			Log.d(TAG, "Wifi receivers registered.");
		} else {
			try {
				mContext.unregisterReceiver(mNetworkScanReceiver);
				mContext.unregisterReceiver(mWifiStateReceiver);
				Log.d(TAG, "Wifi receivers Unregistered.");
			} catch (Exception e) {
				Log.d(TAG, "Wifi exception, already unregistered");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopLogging();
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this.mSharedPreferenceChangeListener);
		if (mUse_Wifi) {
			mContext.unregisterReceiver(mWifiStateReceiver);
		}
		super.onDestroy();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	

	private void setNetworkAccuracy() {
		mPrecision = LOGGING_GLOBAL;
		Log.w(TAG, "Accuracy changed to:" + mPrecision);
		Editor pfEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		pfEditor.putString(Const.PRECISION, Integer.toString(mPrecision));
		pfEditor.commit();
		updateWifiStateReceiver();		
	}

	private void setNormalAccuracy() {
		mPrecision = LOGGING_NORMAL;
		Log.w(TAG, "Accuracy changed to:" + mPrecision);
		Editor pfEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		pfEditor.putString(Const.PRECISION, Integer.toString(mPrecision));
		pfEditor.commit();
		updateWifiStateReceiver();		
	}

}