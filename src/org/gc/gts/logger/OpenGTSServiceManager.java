package org.gc.gts.logger;

import org.gc.gts.util.Const;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class OpenGTSServiceManager {

	private static final String TAG = "OpenGTSServiceManager";
	private static final String REMOTE_EXCEPTION = "REMOTE_EXCEPTION";
	private Context mCtx;
	private org.gc.gts.OpenGTSRemote mOpenGTSRemote;
	private final Object mStartLock = new Object();
	private boolean mStarted = false;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mServiceConnection = null;

	public OpenGTSServiceManager(Context context) {
		// TODO Auto-generated constructor stub
		Log.d(TAG, "OpenGTSServiceManager(Context context)");
		this.mCtx = context;
	}

	public void startup() {
		// TODO Auto-generated method stub
		Log.w(TAG, "connectToGPSLoggerService()");
		if (!mStarted) {
			this.mServiceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					synchronized (mStartLock) {
						Log.d(TAG, "onServiceConnected()");
						OpenGTSServiceManager.this.mOpenGTSRemote = org.gc.gts.OpenGTSRemote.Stub
								.asInterface(service);
						mStarted = true;
					}
				}

				public void onServiceDisconnected(ComponentName className) {
					synchronized (mStartLock) {
						Log.d(TAG, "onServiceDisconnected()");
						OpenGTSServiceManager.this.mOpenGTSRemote = null;
						mStarted = false;
					}
				}
			};
			this.mCtx.bindService(new Intent(Const.SERVICENAME),
					this.mServiceConnection, Context.BIND_AUTO_CREATE);
		} else {
			Log.w(TAG, "Attempting to connect whilst connected");
		}
	}


	public void pauseLogging()
	   {
	      synchronized (mStartLock)
	      {
	         if( mStarted )
	         {
	            try
	            {
	               if( this.mOpenGTSRemote != null )
	               {
	                  this.mOpenGTSRemote.pauseLogging();
	               }
	            }
	            catch (RemoteException e)
	            {
	               Log.e( TAG, "Could not pause Tracking Service.", e );
	            }
	         }
	      }
	   }

	   public void resumeLogging()
	   {
	      synchronized (mStartLock)
	      {
	         if( mStarted )
	         {
	            try
	            {
	               if( this.mOpenGTSRemote != null )
	               {
	                  this.mOpenGTSRemote.resumeLogging();
	               }
	            }
	            catch (RemoteException e)
	            {
	               Log.e( TAG, "Could not resume  Tracking Service.", e );
	            }
	         }

	      }
	   }


	public void shutdown() {
		// TODO Auto-generated method stub
		Log.d(TAG, "shutdown()");
		try {
			this.mCtx.unbindService(this.mServiceConnection);
		} catch (IllegalArgumentException e) {
			Log
					.e(
							TAG,
							"Failed to unbind a service, prehaps the service disapearded?",
							e);
		}
	}

	public int getLoggingState() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getLoggingState()");
		synchronized (mStartLock) {
			int logging = Const.UNKNOWN;
			try {
				if (this.mOpenGTSRemote != null) {
					logging = this.mOpenGTSRemote.loggingState();
					// Log.d( TAG, "mGPSLoggerRemote tells state to be "+logging
					// );
				} else {
					Log.w(TAG,
							"Remote interface to logging service not found. Started: "
									+ mStarted);
				}
			} catch (RemoteException e) {
				Log.e(TAG, "Could stat GPSLoggerService.", e);
			}
			return logging;
		}

	}

}