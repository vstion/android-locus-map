package menion.android.locus.addon.publiclib.sample;

import java.io.File;
import java.util.ArrayList;

import menion.android.locus.addon.publiclib.DisplayData;
import menion.android.locus.addon.publiclib.LocusConst;
import menion.android.locus.addon.publiclib.LocusIntents;
import menion.android.locus.addon.publiclib.LocusUtils;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointGeocachingData;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class LocusAddonPublicLibSampleActivity extends Activity {
	
	private static final String TAG = "LocusAddonPublicLibSampleActivity";
	
	/*
	 * Useful TIPS:
	 * 
	 * * check LocusUtils class, that contain some interesting functions, mainly
	 *  - ability to check if is Locus installed (thanks to Arcao)
	 *  - ability to send some file from fileSystem to Locus (for import)
	 * 
	 * * check LocusIntents class, that contain main info how to integrate some
	 * call-backs from Locus application
	 * 
	 * * check DisplayData class, that contain all functions required for sending
	 * various data into Locus. Samples contain mainly calling of this function
	 *
	 * * if you miss some function, or you wrote something simple and nice that should
	 * come handy to others, let me know on locus@asamm.cz and I'll add it to this API
	 */
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // send one point
        Button btn01 = (Button) findViewById(R.id.button1);
        btn01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test1");
				pd.addPoint(generatePoint(0));
				if (DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, true)) {
					Log.w(TAG, "point sended succesfully");
				} else {
					Log.w(TAG, "problem with sending");
				}
			}
		});
        
        // send one point with icon
        Button btn02 = (Button) findViewById(R.id.button2);
        btn02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test2");
				pd.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hide_default));
				pd.addPoint(generatePoint(0));
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, true);
			}
		});
        
        /* send more points - LIMIT DATA TO MAX 1000 (really max 1500),
         *  more cause troubles. It easy and fast method but depend on data size, so intent
         *  with lot of geocaches will be really limited */
        Button btn03 = (Button) findViewById(R.id.button3);
        btn03.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test3");
				for (int i = 0; i < 1000; i++)
					pd.addPoint(generatePoint(i));
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, true);
			}
		});
        
        /* similar to above, but with various icons */
        Button btn04 = (Button) findViewById(R.id.button4);
        btn04.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<PointsData> data = new ArrayList<PointsData>();
				
				PointsData pd1 = new PointsData("test4a");
				pd1.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hide_default));
				for (int i = 0; i < 100; i++)
					pd1.addPoint(generatePoint(i));
				
				PointsData pd2 = new PointsData("test4b");
				pd2.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_cancel_default));
				for (int i = 0; i < 100; i++)
					pd2.addPoint(generatePoint(i));
				
				
				data.add(pd1);
				data.add(pd2);
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, data, false);
			}
		});
        
        /* send one geocache */
        Button btn05 = (Button) findViewById(R.id.button5);
        btn05.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test5");
				pd.addPoint(generateGeocache(0));
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, false);
			}
		});
        
        
        /*
         * limit here is much more tight! Intent have limit on data size (around 1MB, so if you want to send
         * geocaches, don't rather use this method
         */
        Button btn06 = (Button) findViewById(R.id.button6);
        btn06.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test6");
				for (int i = 0; i < 100; i++)
					pd.addPoint(generateGeocache(i));
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, false);
			}
		});
        
        /* This used custom DataStorageProvider class. Look at it. 
         * 
         * It's really simple ContentProvider that allow Locus to download data. So I suggest
         * copy menion.android.locus.addon.publiclib.sample.DataStorageProvider class into
         * your program, into own package. Also do not forget to declare it in Manifest file!
         * 
         * Data will be added to DataStorageProvider class automatically as static Object during
         * Locus call. When Locus grab data, data will be removed to prevent some memory issue. 
         */ 
        Button btn07 = (Button) findViewById(R.id.button7);
        btn07.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test07");
				for (int i = 0; i < 1000; i++)
					pd.addPoint(generateGeocache(i));
				
				ArrayList<PointsData> data = new ArrayList<PointsData>();
				data.add(pd);
				
				DisplayData.sendDataCursor(LocusAddonPublicLibSampleActivity.this, data,
						"content://" + DataStorageProvider.class.getCanonicalName().toLowerCase(), false);
			}
		});

        final File tempGPXFile = new File("/mnt/sdcard/Locus/_test/temporary_path.gpx");
        
        /* send GPX file to system */
        Button btn08 = (Button) findViewById(R.id.button8);
        btn08.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocusUtils.importFileSystem(LocusAddonPublicLibSampleActivity.this, tempGPXFile)) {
					Log.w(TAG, "export succesfully");
				} else {
					Log.w(TAG, "export failed");
				}
				
			}
		});
        
        /* send GPX file directly to Locus system */
        Button btn09 = (Button) findViewById(R.id.button9);
        btn09.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocusUtils.importFileLocus(LocusAddonPublicLibSampleActivity.this, tempGPXFile)) {
					Log.w(TAG, "export succesfully");
				} else {
					Log.w(TAG, "export failed");
				}
			}
		});
        
        /* send date with method, that store byte[] data in raw file and send locus link to this file */ 
        Button btn10 = (Button) findViewById(R.id.button10);
        btn10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get filepath
				File externalDir = Environment.getExternalStorageDirectory();
				if (externalDir == null || !(externalDir.exists())) {
					Log.e(TAG, "problem with obtain of External di");
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
				
				DisplayData.sendDataFile(LocusAddonPublicLibSampleActivity.this,
						data,
						filePath,
						false);
			}
		});
        
        /* allow to display special point, that when shown, will call back to this application. You may use this
         * for loading extra data. So you send simple point and when show, you display extra information */ 
        Button btn11 = (Button) findViewById(R.id.button11);
        btn11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PointsData pd = new PointsData("test2");
				Point p = generatePoint(0);
				p.setExtraOnDisplay(
						"menion.android.locus.addon.publiclib.sample",
						"menion.android.locus.addon.publiclib.sample.LocusAddonPublicLibSampleActivity",
						"myOnDisplayExtraActionId",
						"0");
				pd.addPoint(p);
				DisplayData.sendData(LocusAddonPublicLibSampleActivity.this, pd, false);
			}
		});
        
        /*************************/
        /*    NOW CHECK INTENT   */
        /*************************/
        
        Intent intent = getIntent();
        if (intent == null)
        	return;
        
        if (LocusIntents.isIntentGetLocation(intent)) {
        	new AlertDialog.Builder(this).
        	setTitle("Intent - Get location").
        	setMessage("By pressing OK, dialog disappear and to Locus will be returned some location!").
        	setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!LocusIntents.sendGetLocationData(LocusAddonPublicLibSampleActivity.this,
							"Non sence", Math.random() * 85, 
							Math.random() * 180, 0.0, 0.0)) {
						Toast.makeText(LocusAddonPublicLibSampleActivity.this, "Wrong data to send!", Toast.LENGTH_SHORT).show();
					}
				}
			}).show();
        } else if (LocusIntents.isIntentOnPointAction(intent)) {
        	Point p = LocusIntents.handleIntentOnPointAction(intent);
        	if (p == null) {
        		Toast.makeText(LocusAddonPublicLibSampleActivity.this, "Wrong INTENT - no point!", Toast.LENGTH_SHORT).show();
        	} else {
            	new AlertDialog.Builder(this).
            	setTitle("Intent - On Point action").
            	setMessage("Received intent with point:\n\n" + p.getName() + "\n\nloc:" + p.getLocation() + 
            			"\n\ngcData:" + (p.getGeocachingData() == null ? "sorry, but no..." : p.getGeocachingData().cacheID)).
            	setPositiveButton("Close", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {}
    			}).show();
        	}
        } else if (LocusIntents.isIntentMainFunction(intent)) {
        	LocusIntents.handleIntentMainFunction(intent,
        			new LocusIntents.OnIntentMainFunction() {
				@Override
				public void onLocationReceived(boolean gpsEnabled, Location locGps,
						Location locMapCenter) {
		        	new AlertDialog.Builder(LocusAddonPublicLibSampleActivity.this).
		        	setTitle("Intent - Main function").
		        	setMessage("GPS location:" + gpsEnabled + "\n\n" + locGps + "\n\nmapCenter:" + locMapCenter).
		        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
				}
				
				@Override
				public void onFailed() {
					Toast.makeText(LocusAddonPublicLibSampleActivity.this, "Wrong INTENT!", Toast.LENGTH_SHORT).show();
				}
			});
        } else if (intent.hasExtra("myOnDisplayExtraActionId")) {
        	String value = intent.getStringExtra("myOnDisplayExtraActionId");

        	// now create full point version and send it back for returned value
			PointsData pd = new PointsData("test2");
			pd.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hide_default));
			Point p = generatePoint(0);
			p.setName("Improved version!");
			p.setDescription("Extra description to ultra improved point!, received value:" + value);
			
			Intent retInent = new Intent();
			retInent.putExtra(LocusConst.EXTRA_POINT, p);
			setResult(RESULT_OK, retInent);
			finish();
			// or you may set RESULT_CANCEL if you don't have improved version of Point, then locus
			// just show current available version
        }
    }
    
    private Point generatePoint(int id) {
		// create one simple point with location
		Location loc = new Location(TAG);
		loc.setLatitude(Math.random() + 50.0);
		loc.setLongitude(Math.random() + 14.0);
		return new Point("Testing point - " + id, loc);
    }
    
    private Point generateGeocache(int id) {
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