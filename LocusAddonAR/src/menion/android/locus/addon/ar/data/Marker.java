package menion.android.locus.addon.ar.data;

import menion.android.locus.addon.ar.ArContext;
import menion.android.locus.addon.ar.AugmentedView;
import menion.android.locus.addon.ar.Main;
import menion.android.locus.addon.ar.R;
import menion.android.locus.addon.ar.utils.Utils;
import menion.android.locus.addon.ar.utils.UtilsGeo;
import menion.android.locus.addon.ar.utils.Vector3D;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.utils.UtilsAddonAR;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

public class Marker {

	private static final String TAG = "Marker";
	
	// max distance to behave as center
	private static final float MAX_CENTER_DIST = Main.getDpPixels(30.0f) * Main.getDpPixels(30.0f); 
	// circle radius if no icon available
	private static final float CIRCLE_RADIUS = Main.getDpPixels(15.0f);
	
	// market id
	private long id;
	// marker variables
	private String mText;
	// icon for this marker
	private Bitmap mImg;
	// label object
	private MarkerLabel mLabel;

	// geo location
	private Location mLocationGeo;
	// location computed in vector to actual position
	protected Vector3D mLocationVec;
	// actual distance
	private double mDistance;

	// draw properties
	private boolean isVisible;
	// is marker in screen center
	private boolean isLookingAt;
	
	private Vector3D cMarker = new Vector3D();
	private Vector3D signMarker = new Vector3D();
	
	// temp properties
	private Vector3D tmpA = new Vector3D();
	private Vector3D tmpB = new Vector3D();
	private Vector3D tmpC = new Vector3D();
	
	private Vector3D origin = new Vector3D(0, 0, 0);
	private Vector3D upV = new Vector3D(0, 1, 0);
	
	// paint if no icon available 
	private static Paint mPaintCircle;
	static {
		mPaintCircle = new Paint();
		mPaintCircle.setColor(Color.RED);
		mPaintCircle.setStrokeWidth(3.0f);
		mPaintCircle.setStyle(Paint.Style.STROKE);
	}
	
	public Marker(Point p, Bitmap img) {
		id = 0L; // p.getId(); TODO
		mText = p.getName();
		mImg = img;
		mLocationGeo = p.getLocation();
		mLocationVec = new Vector3D();
	}

	public String getText(){
		return mText;
	}
	
	private static boolean pointInside(float P_x, float P_y, float r_x,
			float r_y, float r_w, float r_h) {
		return (P_x > r_x && P_x < r_x + r_w && P_y > r_y && P_y < r_y + r_h);
	}
	
	public boolean isLookingAt() {
		return isVisible && isLookingAt;
	}

	public void calcPaint(AugmentedView.Camera viewCam) {
		// calculate point position
		tmpA.set(origin);
		tmpA.add(mLocationVec);
		tmpA.sub(viewCam.lco);
		tmpA.prod(viewCam.transform);
		viewCam.projectPoint(tmpA, tmpB); //6
		cMarker.set(tmpB); //7
		
		tmpC.set(upV); 
		tmpC.add(mLocationVec);
		tmpC.sub(viewCam.lco);
		tmpC.prod(viewCam.transform);
		viewCam.projectPoint(tmpC, tmpB); //6
		signMarker.set(tmpB); //7
		
		// calculate camera
		isVisible = false;
		isLookingAt = false;
//		deltaCenter = Float.MAX_VALUE;

		if (cMarker.z < -1f) {
			isVisible = true;

			if (pointInside(cMarker.x, cMarker.y, 0, 0,
					viewCam.width, viewCam.height)) {

				float xDist = cMarker.x - viewCam.width / 2;
				float yDist = cMarker.y - viewCam.height / 2;
				float dist = xDist * xDist + yDist * yDist;

//				deltaCenter = (float) Math.sqrt(dist);
				if (dist < MAX_CENTER_DIST) {
					isLookingAt = true;
				}
			}
		}
	}
	
	public double getDistance() {
		return mDistance;
	}
	
	public void onLocationChanged(Location loc) {
		UtilsGeo.convLocToVec(loc, mLocationGeo, mLocationVec);
		mDistance = loc.distanceTo(mLocationGeo);
Log.d(TAG, "marker:" + mText + ", lcoationChanged, dist:" + mDistance + ", vec:" + mLocationVec + ", " + mLocationGeo.toString());
	}
	
	public boolean isInRange() {
		return mDistance < ArContext.getRadius();
	}

	public void paint(AugmentedView av, Canvas c) {
		if (isVisible) {
			float scale = 1.0f;
			if (isLookingAt) {
				scale = 2.0f;
			} else {
				// set scale by distance from 0.50 - 1.50
				scale = (ArContext.getRadius() + cMarker.z) / ArContext.getRadius() + 0.50f;
			}
			
			// compute actual screen angle
			float currentAngle = Utils.getAngle(cMarker.x, cMarker.y, signMarker.x, signMarker.y) + 90;

			// draw label first
			if (mLabel == null) {
				mLabel = new MarkerLabel(mText);
			}
			av.paintImg(mLabel.getTextImage(), signMarker.x, signMarker.y, currentAngle,
					scale == 2.0f ? 2.0f : 1.0f);

			// paint img or circle when no image available
			if (mImg == null) {
				c.drawCircle(cMarker.x, cMarker.y, CIRCLE_RADIUS, mPaintCircle);
			} else {
				av.paintImg(mImg, cMarker.x, cMarker.y, currentAngle, scale);
			}
		}
	}

	public long getId() {
		return id;
	}

	public Location getLocation() {
		return mLocationGeo;
	}
	
	public boolean fClick(float x, float y) {
		boolean evtHandled = false;
		if (isClickValid(x, y)) {
			Log.d(TAG, "handleClick:" + mText);
			evtHandled = true;
		}
		return evtHandled;
	}
	
	ScreenLine2D pPt = new ScreenLine2D();

	//isClickValid(261.14685, 172.47269), 248.48828, 1108.4265, -40723.86, 46.11125, 149.0, 82.0

	private boolean isClickValid(float x, float y) {
		if (!isVisible)
			return false;
		
		float currentAngle = Utils.getAngle(cMarker.x, cMarker.y,
				signMarker.x, signMarker.y);

		pPt.x = x - signMarker.x;
		pPt.y = y - signMarker.y;
		pPt.rotate(Math.toRadians(-(currentAngle + 90)));
		pPt.x += signMarker.x;
		pPt.y += signMarker.y;
		
		float objX, objY, objW, objH;
		if (mImg != null) {
			objX = cMarker.x - mImg.getWidth();
			objY = cMarker.y - mImg.getHeight();
			objW = mImg.getWidth() * 2;
			objH = mImg.getHeight() * 2;	
		} else {
			objX = cMarker.x - CIRCLE_RADIUS;
			objY = cMarker.y - CIRCLE_RADIUS;
			objW = CIRCLE_RADIUS * 2;
			objH = CIRCLE_RADIUS * 2;
		}
		
Log.d(TAG, "isClickValid(" + x + ", " +y + "), " + pPt.x + ", " + pPt.y + ", " + objX + ", " + objY + ", " + objW + ", " + objH);
		if (pPt.x > objX && pPt.x < objX + objW && pPt.y > objY
				&& pPt.y < objY + objH) {
			
			Activity activity = Main.arContext.getMain();
			new AlertDialog.Builder(activity).
			setCancelable(true).
			setTitle(R.string.select).
			setMessage(activity.getString(R.string.select_marker, mText)).
			setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Main main = Main.arContext.getMain();
							Intent intent = new Intent();
							intent.putExtra(UtilsAddonAR.RESULT_WPT_ID, id);
							main.setResult(Activity.RESULT_OK, intent);
							main.finish();
						}
					}).
			setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).
			show();
			return true;
		} else {
			return false;
		}
	}
}
