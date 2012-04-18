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

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AugmentedCamera extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "AugmentedCamera";

	private SurfaceHolder mHolder;
	private Camera mCamera;

	public static float angleH;
	protected static float angleV;

	public AugmentedCamera(Context context) {
		super(context);
		init();
	}
	
    public AugmentedCamera(Context context, AttributeSet attr) {
    	super(context, attr);
    	init();
    }
    
    private void init() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(TAG, "surfaceCreated()");
		try {
			cleanCamera();
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
			Log.e(TAG, "surfaceCreated()", e);
			cleanCamera();
		}
	}

	private void cleanCamera() {
		if (mCamera != null) {
			try {
				mCamera.stopPreview();
			} catch (Exception ignore) {
			}
			try {
				mCamera.release();
			} catch (Exception ignore) {
			}
			mCamera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.w(TAG, "surfaceChanged()");
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			try {
				// get supported size
				List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();

				// preview form factor
				float ff = (float) w / h;
//Log.d(TAG, "Screen res: w:" + w + " h:" + h + " aspect ratio:" + ff);

				// holder for the best form factor and size
				float bff = 0;
				int bestw = 0;
				int besth = 0;
				Iterator<Camera.Size> itr = supportedSizes.iterator();

				// we look for the best preview size, it has to be the closest to the
				// screen form factor, and be less wide than the screen itself
				// the latter requirement is because the HTC Hero with update 2.1 will
				// report camera preview sizes larger than the screen, and it will fail
				// to initialize the camera other devices could work with previews
				// larger than the screen though
				while (itr.hasNext()) {
					Camera.Size element = itr.next();
					// current form factor
					float cff = (float) element.width / element.height;
					// check if the current element is a candidate to replace
					// the best match so far
					// current form factor should be closer to the bff
					// preview width should be less than screen width
					// preview width should be more than current bestw
					// this combination will ensure that the highest resolution
					// will win
//Log.d(TAG, "Candidate camera element: w:" + element.width + " h:" + element.height + " aspect ratio:" + cff);
					if ((ff - cff <= ff - bff) && (element.width <= w) && (element.width >= bestw)) {
						bff = cff;
						bestw = element.width;
						besth = element.height;
					}
				}
//Log.d(TAG, "Chosen camera element: w:" + bestw + " h:" + besth + " aspect ratio:" + bff);
				// Some Samsung phones will end up with bestw and besth = 0
				// because their minimum preview size is bigger then the screen size.
				// In this case, we use the default values: 480x320
				if ((bestw == 0) || (besth == 0)) {
					bestw = 480;
					besth = 320;
//Log.d(TAG, "Using default camera parameters! " + bestw + "x" + besth);
				}
				parameters.setPreviewSize(bestw, besth);
			} catch (Exception ex) {
				parameters.setPreviewSize(480, 320);
			}

			mCamera.setParameters(parameters);
			mCamera.startPreview();

			// set angles to degree
			if (Integer.valueOf(Build.VERSION.SDK) >= 8) {
				angleH = mCamera.getParameters().getHorizontalViewAngle();
				angleV = mCamera.getParameters().getVerticalViewAngle();	
			} else {
				angleH = (float) Math.toDegrees((0.863938 / 800) * parameters.getPreviewSize().width);
				angleV = (float) Math.toDegrees((0.6632251 / 480) * parameters.getPreviewSize().height);
			}
//Log.d(TAG, "set angles:" + angleH + ", " + angleV);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(TAG, "surfaceDestroyed()");
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		cleanCamera();
	}
}
