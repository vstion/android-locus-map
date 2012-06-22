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

import menion.android.locus.addon.publiclib.LocusConst;
import menion.android.locus.addon.publiclib.LocusIntents;
import menion.android.locus.addon.publiclib.LocusUtils;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
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
	 * come handy to others, let me know on locus@asamm.com and I'll add it to this API
	 */
	
	private SampleCalls calls;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // create calls object
        calls = new SampleCalls(this);
        
        Button btn01 = (Button) findViewById(R.id.button1);
        btn01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendOnePoint();
			}
		});
        
        Button btn02 = (Button) findViewById(R.id.button2);
        btn02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendOnePointWithIcon();
			}
		});
        
        Button btn03 = (Button) findViewById(R.id.button3);
        btn03.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendMorePoints();
			}
		});
        
        Button btn04 = (Button) findViewById(R.id.button4);
        btn04.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendMorePointsWithIcons();
			}
		});
        
        
        Button btn05 = (Button) findViewById(R.id.button5);
        btn05.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendOnePointGeocache();
			}
		});
        
        Button btn06 = (Button) findViewById(R.id.button6);
        btn06.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendMorePointsGeocacheIntentMehod();
			}
		});
        
        Button btn07 = (Button) findViewById(R.id.button7);
        btn07.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendMorePointsGeocacheContentProviderMethod();
			}
		});

        Button btn08 = (Button) findViewById(R.id.button8);
        btn08.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendFileToSystem();
			}
		});
        
        Button btn09 = (Button) findViewById(R.id.button9);
        btn09.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendFileToLocus();
			}
		});
        
        Button btn10 = (Button) findViewById(R.id.button10);
        btn10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendDateOverFile();
			}
		});
        
        Button btn11 = (Button) findViewById(R.id.button11);
        btn11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendOnePointWithCallbackOnDisplay();
			}
		});
        
        Button btn12 = (Button) findViewById(R.id.button12);
        btn12.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendOneTrack();
			}
		});
        
        Button btn13 = (Button) findViewById(R.id.button13);
        btn13.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.callSendMultipleTracks();
			}
		});
        
        Button btn14 = (Button) findViewById(R.id.button14);
        btn14.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calls.pickLocation();
			}
		});
        
        Button btn15 = (Button) findViewById(R.id.button15);
        btn15.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// filter data so only visible will be GPX and KML files
				LocusUtils.intentPickFile(LocusAddonPublicLibSampleActivity.this,
						0, "Give me a FILE!!",
						new String[] {".gpx", ".kml"});
			}
		});
        
        Button btn16 = (Button) findViewById(R.id.button16);
        btn16.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocusUtils.intentPickDir(LocusAddonPublicLibSampleActivity.this, 1);
			}
		});
        
        Button btn17 = (Button) findViewById(R.id.button17);
        btn17.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	        	new AlertDialog.Builder(LocusAddonPublicLibSampleActivity.this).
	        	setTitle("Locus Root directory").
	        	setMessage("dir:" + calls.getRootDirectory() +
	        			"\n\n'null' means no required version (206) installed or different problem").
	        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
		});
        
        Button btn18 = (Button) findViewById(R.id.button18);
        btn18.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					LocusIntents.callAddNewWmsMap(LocusAddonPublicLibSampleActivity.this,
							"http://wms.geology.cz/wmsconnector/com.esri.wms.Esrimap/CGS_Geomagnetic_Field");
				} catch (Exception e) {
					Log.e(TAG, "onClick()", e);
				}
			}
		});
        
        /*************************/
        /*    NOW CHECK INTENT   */
        /*************************/
        
        Intent intent = getIntent();
        Log.d(TAG, "received intent:" + intent);
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
        	final Point p = LocusIntents.handleIntentOnPointAction(intent);
        	if (p == null) {
        		Toast.makeText(LocusAddonPublicLibSampleActivity.this, "Wrong INTENT - no point!", Toast.LENGTH_SHORT).show();
        	} else {
            	new AlertDialog.Builder(this).
            	setTitle("Intent - On Point action").
            	setMessage("Received intent with point:\n\n" + p.getName() + "\n\nloc:" + p.getLocation() + 
            			"\n\ngcData:" + (p.getGeocachingData() == null ? "sorry, but no..." : p.getGeocachingData().cacheID)).
            	setPositiveButton("Close", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
						// because current test version is registered on geocache data,
						// I'll send as result updated geocache
    					p.setDescription(p.getDescription() + " - UPDATED!");
    					p.getLocation().setLatitude(p.getLocation().getLatitude() + 0.001);
    					p.getLocation().setLongitude(p.getLocation().getLongitude() + 0.001);
    					
    					Intent retInent = new Intent();
    					retInent.putExtra(LocusConst.EXTRA_POINT, p);
    					setResult(RESULT_OK, retInent);
    					finish();
    				}
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
        } else if (LocusIntents.isIntentSearchList(intent)) {
        	LocusIntents.handleIntentSearchList(intent,
        			new LocusIntents.OnIntentMainFunction() {
				@Override
				public void onLocationReceived(boolean gpsEnabled, Location locGps,
						Location locMapCenter) {
		        	new AlertDialog.Builder(LocusAddonPublicLibSampleActivity.this).
		        	setTitle("Intent - Search list").
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
        } else if (LocusIntents.isIntentPointsScreenTools(intent)) {
        	ArrayList<PointsData> points = LocusIntents.handleIntentPointsScreenTools(intent);
        	if (points == null || points.size() == 0) {
	        	new AlertDialog.Builder(LocusAddonPublicLibSampleActivity.this).
	        	setTitle("Intent - Points screen (Tools)").
	        	setMessage("Problem with loading points").
	        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
        	} else {
	        	new AlertDialog.Builder(LocusAddonPublicLibSampleActivity.this).
	        	setTitle("Intent - Points screen (Tools)").
	        	setMessage("Loaded from file:" + points.get(0).getPoints().size() + " points!").
	        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
        	}
        } else if (intent.hasExtra("myOnDisplayExtraActionId")) {
        	String value = intent.getStringExtra("myOnDisplayExtraActionId");

        	// now create full point version and send it back for returned value
			PointsData pd = new PointsData("test2");
			pd.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hide_default));
			Point p = calls.generatePoint(0);
			p.setName("Improved version!");
			p.setDescription("Extra description to ultra improved point!, received value:" + value);
			
			Intent retInent = LocusIntents.prepareResultExtraOnDisplayIntent(p, true);
			setResult(RESULT_OK, retInent);
			finish();
			// or you may set RESULT_CANCEL if you don't have improved version of Point, then locus
			// just show current available version
        } else if (LocusIntents.isIntentReceiveLocation(intent)) {
        	Point p = LocusIntents.handleActionReceiveLocation(intent);
        	if (p != null) {
            	new AlertDialog.Builder(this).
            	setTitle("Intent - PickLocation").
            	setMessage("Received intent with point:\n\n" + p.getName() + "\n\nloc:" + p.getLocation() + 
            			"\n\ngcData:" + (p.getGeocachingData() == null ? "sorry, but no..." : p.getGeocachingData().cacheID)).
            	setPositiveButton("Close", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {}
    			}).show();
        	} else {
        		Log.w(TAG, "request PickLocation, canceled");
        	}
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    	if (requestCode == 0) { 
    		// pick file
    		if (resultCode == RESULT_OK && data != null) {
    			File file = new File(data.getData().toString());
    			Toast.makeText(this, "Process successful\n\nFile:" + file.getName() + 
    					", valid:" + file.exists(), Toast.LENGTH_SHORT).show();
    		} else {
    			Toast.makeText(this, "Process unsuccessful", Toast.LENGTH_SHORT).show();
    		}
    	}
    	
    	else if (requestCode == 1) { 
    		// pick directory
    		if (resultCode == RESULT_OK && data != null) {
    			File dir = new File(data.getData().toString());
    			Toast.makeText(this, "Process successful\n\nDir:" + dir.getName() + 
    					", valid:" + dir.exists(), Toast.LENGTH_SHORT).show();
    		} else {
    			Toast.makeText(this, "Process unsuccessful", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}