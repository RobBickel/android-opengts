package org.gc.gts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlayItem;

import android.util.Log;

public class DeviceTraceOverlayItem extends OpenStreetMapViewOverlayItem
		implements GtsConstants {

	public final ArrayList<GeoPoint> mPath;

	private int precision;

	private DeviceTraceOverlayItem(String aTitle, String aDescription,
			GeoPoint aGeoPoint, ArrayList<GeoPoint> path) {
		super(aTitle, aDescription, aGeoPoint);
		this.mPath = path;
	}

	public static DeviceTraceOverlayItem getInstance(OpenStreetMapView osmv,
			String server, String account, String user, String password,
			String device) {
		DeviceTraceOverlayItem deviceItem = null;

		Log.i("device", "start");

		try {
			URL url = new URL("http://" + server + "/events/path.kml?a=" + account + "&u="
					+ user + "&p=" + password + "&d=" + device + "&l=10");

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String line;

			double lon = 0, lat = 0;
			int precision = 0;

			ArrayList<GeoPoint> path = new ArrayList<GeoPoint>();

			while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("<Point><coordinates>")) {
					line = line.substring(line.indexOf("es>") + 3);

					lon = Double.parseDouble(line.substring(0, line
							.indexOf(',')));

					line = line.substring(line.indexOf(',') + 1);

					lat = Double.parseDouble(line.substring(0, line
							.indexOf(',')));

					line = line.substring(line.indexOf(',') + 1);

					precision = Integer.parseInt(line.substring(0, line
							.indexOf("</")));

					Log.i("device", "lat=" + lat + ", lon=" + lon
							+ ", precision=" + precision);

					path.add(new GeoPoint(lat, lon));
				}
			}

			deviceItem = new DeviceTraceOverlayItem(device, "precision "
					+ precision + " meters",
					new GeoPoint(lat, lon), path);
			deviceItem.setPrecision(precision);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		deviceItem.startThread(osmv, server, account, user, password, device);

		return deviceItem;
	}

	public void startThread(final OpenStreetMapView osmv, final String server,
			final String account, final String user, final String password,
			final String device) {
		Thread thread = new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						Thread.sleep(7 * MINUTE);

						Log.i("device", "awake");

						URL url = new URL(server + "/traseu.kml?a=" + account
								+ "&u=" + user + "&p=" + password + "&d="
								+ device + "&l=10");

						BufferedReader reader = new BufferedReader(
								new InputStreamReader(url.openStream()));

						String line;

						double lon = 0, lat = 0;

						mPath.clear();

						while ((line = reader.readLine()) != null) {
							if (line.trim().startsWith("<Point><coordinates>")) {
								line = line.substring(line.indexOf("es>") + 3);

								lon = Double.parseDouble(line.substring(0, line
										.indexOf(',')));

								line = line.substring(line.indexOf(',') + 1);

								lat = Double.parseDouble(line.substring(0, line
										.indexOf(',')));

								line = line.substring(line.indexOf(',') + 1);

								precision = Integer.parseInt(line.substring(0,
										line.indexOf("</")));

								mPath.add(new GeoPoint(lat, lon));
							}
						}

						Log.i("device", "lat=" + lat + ", lon=" + lon
								+ ", precision=" + precision);

						mGeoPoint.setCoordsE6((int) (lat * 1E6),
								(int) (lon * 1E6));

						reader.close();

						// osmv.invalidate();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});

		thread.start();
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getPrecision() {
		return precision;
	}

}
