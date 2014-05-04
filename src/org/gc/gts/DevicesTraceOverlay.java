// Created by plusminus on 23:18:23 - 02.10.2008
package org.gc.gts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlayItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

/**
 * Draws a list of {@link OpenStreetMapViewOverlayItem} as markers to a map. The
 * item with the lowest index is drawn as last and therefore the 'topmost'
 * marker. It also gets checked for onTap first. This class is generic, because
 * you then you get your custom item-class passed back in onTap().
 * 
 * @author Nicolas Gramlich
 * 
 * @param <T>
 */
public class DevicesTraceOverlay extends
		OpenStreetMapViewItemizedOverlay<DeviceTraceOverlayItem> {

	private static ArrayList<GeoPoint>	path;

	/**
	 * @return the path
	 */
	public static ArrayList<GeoPoint> getPath() 
	{
		if (path == null)
			path = new ArrayList<GeoPoint>();
		if(path.isEmpty()) {
			GeoPoint sp = new GeoPoint(46.1828, 21.3234);
			path.add(sp);
		}
		else 
		{
			Log.d("aGTS", "StartPoints = " + path.toString());
		}
		return path;
	}

	private DevicesTraceOverlay(Context ctx,
			List<DeviceTraceOverlayItem> aList, Drawable pMarker,
			Point pMarkerHotspot,
			OnItemTapListener<DeviceTraceOverlayItem> aOnItemTapListener) {
		super(ctx, aList, pMarker, pMarkerHotspot, aOnItemTapListener, null);
	}

	public static DevicesTraceOverlay getInstance(final Context ctx,
			OpenStreetMapView osmv, String server, String account, String user,
			String password) {
		final ArrayList<DeviceTraceOverlayItem> devices = new ArrayList<DeviceTraceOverlayItem>();

		try {
			
			URL url = new URL("http://" + server + "/events/Data.kml?a=" + account + "&u=" + user
					+ "&p=" + password + "&l=1&g=all");

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			double lon = 0, lat = 0;
			int precision = 0;
			// start Path, ArryList of all GeoPoints
			ArrayList<GeoPoint> sPath = new ArrayList<GeoPoint>();

			while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("<name>")) {
					line = line.substring(line.indexOf("me>") + 3);

					String name = line.substring(0, line.indexOf('<'));
					Log.d("aGTS", "DevicesTraceOverlay name " + name);
					devices.add(DeviceTraceOverlayItem.getInstance(osmv,
							server, account, user, password, name));
				}

				if (line.trim().startsWith("<Point><coordinates>")) {
					line = line.substring(line.indexOf("es>") + 3);

					lon = Double.parseDouble(line.substring(0,
							line.indexOf(',')));

					line = line.substring(line.indexOf(',') + 1);

					lat = Double.parseDouble(line.substring(0,
							line.indexOf(',')));

					line = line.substring(line.indexOf(',') + 1);

					precision = Integer.parseInt(line.substring(0,
							line.indexOf("</")));

					Log.i("device", "lat=" + lat + ", lon=" + lon
							+ ", precision=" + precision);

					sPath.add(new GeoPoint(lat, lon));
				}
			}

			reader.close();
			path = sPath;

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return new DevicesTraceOverlay(
				ctx,
				devices,
				ctx.getResources().getDrawable(R.drawable.car),
				new Point(8, 8),
				new DevicesTraceOverlay.OnItemTapListener<DeviceTraceOverlayItem>() {
					public boolean onItemTap(int index,
							DeviceTraceOverlayItem item) {
						Log.i("aGTS", "" + item.mTitle + ": "
								+ item.mDescription);
						Toast.makeText(ctx,
								"" + item.mTitle + ": " + item.mDescription,
								Toast.LENGTH_LONG).show();
						return true; // We 'handled' this event.
					}
				});
	}

	public void onDraw(final Canvas c, final OpenStreetMapView mapView) {
		final OpenStreetMapViewProjection pj = mapView.getProjection();

		final Point curScreenCoords = new Point();

		/*
		 * Draw in backward cycle, so the items with the least index are on the
		 * front.
		 */
		for (int i = this.mItemList.size() - 1; i >= 0; i--) {
			DeviceTraceOverlayItem item = this.mItemList.get(i);

			Point pathCoords = new Point();

			Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setStrokeWidth(5);

			for (int j = 1; j < item.mPath.size(); j++) {
				pj.toMapPixels(item.mPath.get(j - 1), pathCoords);
				pj.toMapPixels(item.mPath.get(j), curScreenCoords);

				paint.setAlpha(50 + j * 15);

				c.drawLine(pathCoords.x, pathCoords.y, curScreenCoords.x,
						curScreenCoords.y, paint);
			}

			pj.toMapPixels(item.mGeoPoint, curScreenCoords);

			Paint paintPrecision = new Paint();
			paintPrecision.setColor(Color.GREEN);
			paintPrecision.setAlpha(50);

			float radius = pj.metersToEquatorPixels(item.getPrecision());

			c.drawCircle(curScreenCoords.x, curScreenCoords.y, radius,
					paintPrecision);

			onDrawItem(c, i, curScreenCoords);
		}
	}

	public GeoPoint getLocation(int i) {
		return this.mItemList.get(i).mGeoPoint;
	}

	public CharSequence[] getItems() {

		CharSequence[] items = new CharSequence[mItemList.size()];

		for (int i = 0; i < this.mItemList.size(); i++)
			items[i] = this.mItemList.get(i).mTitle;

		return items;
	}
}
