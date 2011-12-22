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

import menion.android.locus.addon.publiclib.LocusConst;
import menion.android.locus.addon.publiclib.LocusIntents;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class LocusAddonPublicLibSampleActivity extends Activity {
	
//	private static final String TAG = "LocusAddonPublicLibSampleActivity";
	
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
			Point p = calls.generatePoint(0);
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
}