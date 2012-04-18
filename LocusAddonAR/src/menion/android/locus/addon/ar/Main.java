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

import java.util.List;

import menion.android.locus.addon.ar.utils.Matrix;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;

public class Main extends Activity implements SensorEventListener, OnTouchListener {

	private static final String TAG = "Main";

	// public context of whole AR
	public static ArContext arContext;
	// main view for displaying
	private AugmentedView aView;
	// zoom in button
	private ImageButton btnZoomIn; 
	// zoom out button
	private ImageButton btnZoomOut;
	
	private float RTmp[] = new float[9];
	private float mR[] = new float[9];
	private float I[] = new float[9];
	private float grav[] = new float[3];
	private float mag[] = new float[3];

	private SensorManager sensorMgr;
	private List<Sensor> sensors;
	private Sensor sensorGrav, sensorMag;

	private int rHistIdx = 0;
	private Matrix tempR = new Matrix();
	private Matrix finalR = new Matrix();
	private Matrix smoothR = new Matrix();
	private Matrix histR[] = new Matrix[50];
	private Matrix m1 = new Matrix();
	private Matrix m2 = new Matrix();
	private Matrix m3 = new Matrix();

	private WakeLock mWakeLock;

	private static float density = -1.0f;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_main);
		
		arContext = new ArContext(this);
		aView = (AugmentedView) findViewById(R.id.augmentedView);

		// set density for recompute sizes
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		density = metrics.density;
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = pm.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);

		// initialize imageButtons
		ImageButton btnHome = (ImageButton) findViewById(R.id.btn_home);
		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Main.this.finish();
			}
		});
			
		btnZoomIn = (ImageButton) findViewById(R.id.btn_zoom_in);
		btnZoomIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArContext.zoomIn();
				manageZoomButtons();
			}
		});
		
		btnZoomOut = (ImageButton) findViewById(R.id.btn_zoom_out);
		btnZoomOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArContext.zoomOut();
				manageZoomButtons();
			}
		});
		manageZoomButtons();
		arContext.handleIntent(getIntent());
	}
	
	private void manageZoomButtons() {
		btnZoomIn.setEnabled(ArContext.isZoomInAllowed());
		btnZoomOut.setEnabled(ArContext.isZoomOutAllowed());
	}
	
	public static float getDpPixels(float pixels) {
		if (density == -1.0f)
			return pixels;
		return density * pixels;
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		arContext.handleIntent(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		try {
			this.mWakeLock.release();

			try {
				sensorMgr.unregisterListener(this, sensorGrav);
			} catch (Exception ignore) {}
			try {
				sensorMgr.unregisterListener(this, sensorMag);
			} catch (Exception ignore) {}
			sensorMgr = null;
		} catch (Exception e) {
			Log.e(TAG, "onPause()", e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			this.mWakeLock.acquire();

			// clear all events
			aView.clearEvents();

			// Phone
//			double angleX = Math.toRadians(-90);
//			double angleY = Math.toRadians(-90);
			
			// Tablet
			double angleX = Math.toRadians(-90);
			double angleY = Math.toRadians(0);

			m1.set(1f, 0f, 0f,
					0f, (float) Math.cos(angleX), (float) -Math.sin(angleX),
					0f, (float) Math.sin(angleX), (float) Math.cos(angleX));
			m2.set(1f, 0f, 0f,
					0f, (float) Math.cos(angleX), (float) -Math.sin(angleX),
					0f, (float) Math.sin(angleX), (float) Math.cos(angleX));
			m3.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
					0f, 1f, 0f,
					(float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));

			for (int i = 0; i < histR.length; i++) {
				histR[i] = new Matrix();
			}

			sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

			sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
			if (sensors.size() > 0) {
				sensorGrav = sensors.get(0);
			}

			sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
			if (sensors.size() > 0) {
				sensorMag = sensors.get(0);
			}

			sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_GAME);
			sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_GAME);
		} catch (Exception e) {
			Log.e(TAG, "onResume()", e);
			try {
				if (sensorMgr != null) {
					sensorMgr.unregisterListener(this, sensorGrav);
					sensorMgr.unregisterListener(this, sensorMag);
					sensorMgr = null;
				}
			} catch (Exception ignore) {}
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		// clear all variables
		arContext.destroy();
	}

	public void onSensorChanged(SensorEvent evt) {
		try {
			if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				grav[0] = evt.values[0];
				grav[1] = evt.values[1];
				grav[2] = evt.values[2];
			} else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				mag[0] = evt.values[0];
				mag[1] = evt.values[1];
				mag[2] = evt.values[2];
			}

			SensorManager.getRotationMatrix(RTmp, I, grav, mag);
			SensorManager.remapCoordinateSystem(RTmp,
					SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, mR);

			tempR.set(mR[0], mR[1], mR[2],
					mR[3], mR[4], mR[5],
					mR[6], mR[7], mR[8]);

			finalR.toIdentity();
			finalR.prod(arContext.m4);
			finalR.prod(m1);
			finalR.prod(tempR);
			finalR.prod(m3);
			finalR.prod(m2);
			finalR.invert(); 

			histR[rHistIdx].set(finalR);
			rHistIdx++;
			if (rHistIdx >= histR.length)
				rHistIdx = 0;

			smoothR.set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
			for (int i = 0; i < histR.length; i++) {
				smoothR.add(histR[i]);
			}
			smoothR.mult(1 / (float) histR.length);

			synchronized (arContext.rotationM) {
				arContext.rotationM.set(smoothR);
			}
			
			aView.postInvalidate();
		} catch (Exception e) {
			Log.e(TAG, "onSensorCahnged()", e);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		try {
			float xPress = me.getX();
			float yPress = me.getY();
			if (me.getAction() == MotionEvent.ACTION_UP) {
				aView.clickEvent(xPress, yPress);
			}
			
			return true;
		} catch (Exception ex) {
			return super.onTouchEvent(me);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
