/*  
 * Copyright 2011, Asamm soft, s.r.o.
 * 
 * This file is part of LocusAddonPublicLibSample.
 * 
 * LocusAddonPublicLibSample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * LocusAddonPublicLibSample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with LocusAddonPublicLibSample.  If not, see <http://www.gnu.org/licenses/>.
 */

package menion.android.locus.addon.publiclib.sample;

import java.io.File;
import java.util.ArrayList;

import menion.android.locus.addon.publiclib.DisplayData;
import menion.android.locus.addon.publiclib.LocusUtils;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointGeocachingData;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import menion.android.locus.addon.publiclib.geoData.Track;
import menion.android.locus.addon.publiclib.utils.RequiredVersionMissingException;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

public class SampleCalls {

	private static final String TAG = "";
	
	private final File tempGPXFile = new File("/mnt/sdcard/Locus/_test/temporary_path.gpx");
	
	private Activity activity;
	
	public SampleCalls(Activity activity) {
		this.activity = activity;
	}
	
	public void callSendOnePoint() {
		PointsData pd = new PointsData("callSendOnePoint");
		pd.addPoint(generatePoint(0));
		if (DisplayData.sendData(activity, pd, true)) {
			Log.w(TAG, "point sended succesfully");
		} else {
			Log.w(TAG, "problem with sending");
		}
	}
	
	public void callSendOnePointWithIcon() {
		PointsData pd = new PointsData("callSendOnePointWithIcon");
		pd.setBitmap(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_hide_default));
		pd.addPoint(generatePoint(0));
		DisplayData.sendData(activity, pd, true);
	}
	
	/* send more points - LIMIT DATA TO MAX 1000 (really max 1500),
     *  more cause troubles. It easy and fast method but depend on data size, so intent
     *  with lot of geocaches will be really limited */
	public void callSendMorePoints() {
		PointsData pd = new PointsData("callSendMorePoints");
		for (int i = 0; i < 1000; i++)
			pd.addPoint(generatePoint(i));
		DisplayData.sendData(activity, pd, true);
	}
	
	/* similar to previous method. Every PointsData object have defined icon that is
	 * applied on every points. So if you want to send more points with various icons,
	 * you have to define for every pack specific PointsData object */
	public void callSendMorePointsWithIcons() {
		ArrayList<PointsData> data = new ArrayList<PointsData>();
		
		PointsData pd1 = new PointsData("test4a");
		pd1.setBitmap(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_hide_default));
		for (int i = 0; i < 100; i++)
			pd1.addPoint(generatePoint(i));
		
		PointsData pd2 = new PointsData("test4b");
		pd2.setBitmap(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_cancel_default));
		for (int i = 0; i < 100; i++)
			pd2.addPoint(generatePoint(i));
		
		
		data.add(pd1);
		data.add(pd2);
		DisplayData.sendData(activity, data, false);
	}
	
	public void callSendOnePointGeocache() {
		PointsData pd = new PointsData("test5");
		pd.addPoint(generateGeocache(0));
		DisplayData.sendData(activity, pd, false);
	}
	
	/* limit here is much more tight! Intent have limit on data size (around 1MB, so if you want to send
     * geocaches, don't rather use this method */
	public void callSendMorePointsGeocacheIntentMehod() {
		PointsData pd = new PointsData("test6");
		for (int i = 0; i < 100; i++)
			pd.addPoint(generateGeocache(i));
		DisplayData.sendData(activity, pd, false);
	}
	
    /* This used custom DataStorageProvider class. Look at it. 
     * 
     * It's really simple ContentProvider that allow Locus to download data. So I suggest
     * copy menion.android.locus.addon.publiclib.sample.DataStorageProvider class into
     * your program, into own package. Also do not forget to declare it in Manifest file!
     * 
     * Data will be added to DataStorageProvider class automatically as static Object during
     * Locus call. When Locus grab data, data will be removed to prevent some memory issue. 
     */ 
	public void callSendMorePointsGeocacheContentProviderMethod() {
		PointsData pd = new PointsData("test07");
		for (int i = 0; i < 1000; i++)
			pd.addPoint(generateGeocache(i));
		
		ArrayList<PointsData> data = new ArrayList<PointsData>();
		data.add(pd);
		
		DisplayData.sendDataCursor(activity, data,
				"content://" + DataStorageProvider.class.getCanonicalName().toLowerCase(), false);
	}
	
	public void callSendFileToSystem() {
		if (LocusUtils.importFileSystem(activity, tempGPXFile)) {
			Log.w(TAG, "export succesfully");
		} else {
			Log.w(TAG, "export failed");
		}
	}
	
	public void callSendFileToLocus() {
		if (LocusUtils.importFileLocus(activity, tempGPXFile, false)) {
			Log.w(TAG, "export succesfully");
		} else {
			Log.w(TAG, "export failed");
		}
	}
	
	/* send date with method, that store byte[] data in raw file and send locus link to this file */
	public void callSendDateOverFile() {
		// get filepath
		File externalDir = Environment.getExternalStorageDirectory();
		if (externalDir == null || !(externalDir.exists())) {
			Log.e(TAG, "problem with obtain of External dir");
			return;
		}
		
		String filePath = externalDir.getAbsolutePath();
		if (!filePath.endsWith("/"))
			filePath += "/";
		filePath += "/Android/data/menion.android.locus.addon.publiclib.sample/files/testFile.locus";

		PointsData pd = new PointsData("test07");
		for (int i = 0; i < 1000; i++)
			pd.addPoint(generateGeocache(i));
		
		ArrayList<PointsData> data = new ArrayList<PointsData>();
		data.add(pd);
		
		DisplayData.sendDataFile(activity,
				data,
				filePath,
				false);
	}
	
    /* allow to display special point, that when shown, will call back to this application. You may use this
     * for loading extra data. So you send simple point and when show, you display extra information */ 
	public void callSendOnePointWithCallbackOnDisplay() {
		PointsData pd = new PointsData("test2");
		Point p = generatePoint(0);
		p.setExtraOnDisplay(
				"menion.android.locus.addon.publiclib.sample",
				"menion.android.locus.addon.publiclib.sample.LocusAddonPublicLibSampleActivity",
				"myOnDisplayExtraActionId",
				"0");
		pd.addPoint(p);
		DisplayData.sendData(activity, pd, false);
	}
	
	public void callSendOneTrack() {
		try {
			Track track = new Track();
			track.setName("track from API");
			track.setDescription("simple track bla bla bla ...");
			
			// set style
			track.setStyle(Color.CYAN, 7.0f);

			// generate points
			double lat = 50.0;
			double lon = 15.0;
			ArrayList<Location> locs = new ArrayList<Location>();
			for (int i = 0; i < 1000; i++) {
				lat += ((Math.random() - 0.5) * 0.1);
				lon += (Math.random() * 0.01);
				Location loc = new Location(TAG);
				loc.setLatitude(lat);
				loc.setLongitude(lon);
				locs.add(loc);
			}
			track.setLocations(locs);
			
			// set some points as highlighted wpts
			ArrayList<Point> pts = new ArrayList<Point>();
			pts.add(new Point("p1", locs.get(100)));
			pts.add(new Point("p2", locs.get(300)));
			pts.add(new Point("p3", locs.get(800)));
			track.setPoints(pts);
			
			DisplayData.sendData(activity, track, false);
		} catch (RequiredVersionMissingException e) {
			Log.e(TAG, "callSendOneTrack()", e);
		}
	}
	
	/*****************************/
	/*       PRIVATE TOOLS       */
	/*****************************/
	
    protected Point generatePoint(int id) {
		// create one simple point with location
		Location loc = new Location(TAG);
		loc.setLatitude(Math.random() + 50.0);
		loc.setLongitude(Math.random() + 14.0);
		return new Point("Testing point - " + id, loc);
    }
    
    protected Point generateGeocache(int id) {
    	Point p = generatePoint(id);
    	
    	// generate new data
    	PointGeocachingData gcData = new PointGeocachingData();
    	// fill data with variables
    	gcData.cacheID = "GC2Y0RJ"; // REQUIRED
    	gcData.name = "Kokotín"; // REQUIRED
    	// rest is optional so fill as you want - should work
    	gcData.type = (int) (Math.random() * 13);
    	gcData.owner = "Menion1";
    	gcData.placedBy = "Menion2";
    	gcData.shortDescription = "bla bla, this is some short description also with <b>HTML tags</b>";
    	for (int i = 0; i < 5; i++) {
    		gcData.longDescription += "Oh, what a looooooooooooooooooooooooong description, never imagine it could be sooo<i>oooo</i>long!<br /><br />Oh, what a looooooooooooooooooooooooong description, never imagine it could be sooo<i>oooo</i>long!";
    	}
    	// set data and return point
    	p.setGeocachingData(gcData);
    	return p;
    }
}
