/*
 * Copyright 2012, Asamm Software, s. r. o.
 * 
 * This file is part of Locus - add-on AR project (LocusAddonAR).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package menion.android.locus.addon.ar;

import java.util.ArrayList;

import menion.android.locus.addon.ar.data.Marker;
import menion.android.locus.addon.ar.utils.Matrix;
import menion.android.locus.addon.ar.utils.Utils;
import menion.android.locus.addon.ar.utils.Vector3D;
import menion.android.locus.addon.publiclib.LocusConst;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import menion.android.locus.addon.publiclib.utils.UtilsAddonAR;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.util.Log;

public class ArContext {
	
	private static final String TAG = "ArContext";
	
	// main class of whole AR
	private Main main;
	
	// actual addon location
	protected Location actualLoc;
	// current declination
	protected float declination = 0f;
	// matrix for 
	protected Matrix m4;
	// main rotation matrix
	protected Matrix rotationM;
	
	// actual loaded points
	private ArrayList<Marker> markers;
	// actual guiding on marker id
	private long markerGuideId;
	
	// minimum radius modify value (mRadiusDefault/RADIUS_MIN)
	public static final int RADIUS_MIN = 1;
	// maximum radius modify value (mRadiusDefault/RADIUS_MAX)
	public static final int RADIUS_MAX = 5;
	// actual radius modify value
	private static int radiusModify = RADIUS_MAX;
	
	// radius in metres to draw points
	private static float mRadiusDefault = 20000.0f;
	// actual radius
	private static float mRadiusActual;
	
	// actual bearing in degree
	private static float curBearing;
	// actual pitch in degree
	private static float curPitch;
	
	public ArContext(Main main) {
		this.main = main;
		// initialize rotation matrix
		rotationM = new Matrix();
		rotationM.toIdentity();
		// create default empty data holder
		markers = new ArrayList<Marker>();
		// create default location
		actualLoc = new Location(TAG);
		// create rotation matrix
		m4 = new Matrix();
	}
	
	public void destroy() {
		main = null;
	}
	
	private static ArrayList<PointsData> getDataFromIntent(Intent intent) {
		if (intent == null)
			return new ArrayList<PointsData>();
		
		ArrayList<PointsData> data = null;
		if (intent.hasExtra(LocusConst.EXTRA_POINTS_DATA_ARRAY)) {
			data = intent.getParcelableArrayListExtra(LocusConst.EXTRA_POINTS_DATA_ARRAY);
		}
		if (intent.hasExtra(LocusConst.EXTRA_POINTS_DATA)) {
			if (data == null)
				data = new ArrayList<PointsData>();
			data.add((PointsData) intent.getParcelableExtra(LocusConst.EXTRA_POINTS_DATA));
		}
		if (data == null)
			data = new ArrayList<PointsData>();
		return data;
	}
	
	public void handleIntent(Intent intent) {
		// set new location
		synchronized (actualLoc) {
			if (intent.hasExtra(UtilsAddonAR.EXTRA_LOCATION)) {
				actualLoc = intent.getParcelableExtra(UtilsAddonAR.EXTRA_LOCATION);
Log.d(TAG, "newLocation:" + actualLoc.getLatitude() + ", " + actualLoc.getLongitude() + ", " + actualLoc.getAltitude());
			}
		}
		
		// initialize and load data from intent
		synchronized (markers) {
			ArrayList<PointsData> data = getDataFromIntent(intent);
Log.d(TAG, "handleIntent(), data:" + data.size());
			if (data.size() > 0) {
				// convert to markers
				markers.clear();
				for (PointsData pd : data) {
					ArrayList<Point> points = pd.getPoints();
Log.d(TAG, "handleIntent(), pd:" + points.size());
					for (Point p : points) {
Log.d(TAG, "handleIntent(), p:" + p);
						if (p == null)
							continue;
						markers.add(new Marker(p, pd.getBitmap()));
					}
				}
			}
		}

		// load marker guide id
		if (intent.hasExtra(UtilsAddonAR.EXTRA_GUIDING_ID)) {
			markerGuideId = intent.getLongExtra(UtilsAddonAR.EXTRA_GUIDING_ID, -1L);
Log.d(TAG, "markerGuideId:" + markerGuideId);
		}
		
		// now init location with actual points
		initLocation();
	}
	
	public void getRM(Matrix dest) {
		synchronized (rotationM) {
			dest.set(rotationM);
		}
	}

	public Location getLocation() {
		synchronized (actualLoc) {
			return actualLoc;
		}
	}
	
	private boolean radiusInitialized = false;
	
	private void initLocation() {
		synchronized (actualLoc) {
			if (actualLoc.getLatitude() == 0.0 && actualLoc.getLongitude() == 0.0) {
				return;
			}
			// update location of all items
			int size = markers.size();
			for (int i = 0; i < size; i++) {
				markers.get(i).onLocationChanged(actualLoc);
			}
			
			// if first attempt to display data, set new radius
			if (!radiusInitialized && markers.size() > 0) {
				mRadiusDefault = (float) (markers.get(0).getDistance() * 1.1f);
				setRadius();
				radiusInitialized = true;
			}
			
			// compute this only if needed
			GeomagneticField gmf = new GeomagneticField(
					(float) actualLoc.getLatitude(),
					(float) actualLoc.getLongitude(),
					(float) actualLoc.getAltitude(),
					System.currentTimeMillis());

			double angleY = Math.toRadians(-gmf.getDeclination());
			m4.toIdentity();
//Log.w(TAG, "setM4:" + actualLoc.getLatitude() + ", " + actualLoc.getLongitude() + ", " + actualLoc.getAltitude());
//Log.w(TAG, "setM4:" + angleY + ", " + gmf.getDeclination());
			m4.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
					0f, 1f, 0f,
					(float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));
			declination = gmf.getDeclination();
Log.d(TAG, "initLocation() - dec:" + declination);
		}
	}
	
	public ArrayList<Marker> getMarkers() {
		return markers;
	}
	
	public static float getCurBearing() {
		return curBearing;
	}
	
	public static float getCurPitch() {
		return curPitch;
	}
	
	public void calcPitchBearing(Matrix rotationM) {
		Vector3D looking = new Vector3D();
		rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		ArContext.curBearing = (int) (Utils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360;
		
		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		ArContext.curPitch = -Utils.getAngle(0, 0, looking.y, looking.z);
//Log.d("ArContext", "angles:" + curBearing + ", " + curPitch);
	}
	
	public boolean isMarkerGuided(Marker ma) {
		return ma.getId() == markerGuideId;
	}
	
	/**********************************/
	/*       ZOOMING FUNCTIONS        */
	/**********************************/
	
	public static float getRadius() {
		return ArContext.mRadiusActual;
	}
	
	public static void zoomIn() {
		if (radiusModify < RADIUS_MAX)
			radiusModify++;
		setRadius();
	}
	
	public static void zoomOut() {
		if (radiusModify > RADIUS_MIN)
			radiusModify--;
		setRadius();
	}
	
	public static boolean isZoomInAllowed() {
		return radiusModify < RADIUS_MAX;
	}
	
	public static boolean isZoomOutAllowed() {
		return radiusModify > RADIUS_MIN;
	}
	
	private static void setRadius() {
		mRadiusActual = mRadiusDefault * (radiusModify * 1.0f / RADIUS_MAX); 
	}

	public Main getMain() {
		return main;
	}
}
