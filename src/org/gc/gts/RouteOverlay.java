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
import android.util.Log;

public class RouteOverlay extends
		OpenStreetMapViewItemizedOverlay<OpenStreetMapViewOverlayItem> {

	private final static double E6 = 1000000;

	public RouteOverlay(Context ctx, List<OpenStreetMapViewOverlayItem> aList) {
		super(ctx, aList,
				ctx.getResources().getDrawable(R.drawable.bullet_red),
				new Point(8, 8), null, null);
	}

	public static RouteOverlay getInstance(final Context ctx,
			OpenStreetMapView osmv, GeoPoint from, GeoPoint to) {
		ArrayList<OpenStreetMapViewOverlayItem> route = new ArrayList<OpenStreetMapViewOverlayItem>();

		try {
			URL url = new URL(
					"http://www.yournavigation.org/api/1.0/gosmore.php?flat="
							+ from.getLatitudeE6() / E6 + "&flon="
							+ from.getLongitudeE6() / E6 + "&tlat="
							+ to.getLatitudeE6() / E6 + "&tlon="
							+ to.getLongitudeE6() / E6);
			
			Log.i("URL", url.toString());

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String line;

			double lon = 0, lat = 0;
			boolean readCoordinates = false;

			while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("<coordinates>")) {
					line = line.substring(line.indexOf(">") + 1).trim();
					readCoordinates = true;
				}
				if (line.trim().startsWith("</coordinates>")) {
					readCoordinates = false;
				}
				if (readCoordinates) {
					lon = Double.parseDouble(line.substring(0, line
							.indexOf(',')));

					line = line.substring(line.indexOf(',') + 1);

					lat = Double.parseDouble(line);

					route.add(new OpenStreetMapViewOverlayItem("n", "node",
							new GeoPoint(lat, lon)));
				}
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RouteOverlay(ctx, route);
	}

	public void onDraw(final Canvas c, final OpenStreetMapView mapView) {
		OpenStreetMapViewProjection pj = mapView.getProjection();

		Point from = new Point();
		Point to = new Point();

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(5);
		paint.setAlpha(100);

		for (int i = 0; i < mItemList.size(); i++) {
			pj.toMapPixels(mItemList.get(i).mGeoPoint, from);

			if (i > 0) {
				pj.toMapPixels(mItemList.get(i - 1).mGeoPoint, to);

				c.drawLine(from.x, from.y, to.x, to.y, paint);
			}

			onDrawItem(c, i, from);
		}
	}
}
