package menion.android.locus.addon.ar;

import java.util.ArrayList;

import menion.android.locus.addon.ar.data.Marker;
import menion.android.locus.addon.ar.data.MarkersRadar;
import menion.android.locus.addon.ar.data.GuideLine;
import menion.android.locus.addon.ar.data.ScreenObject;
import menion.android.locus.addon.ar.data.ScreenText;
import menion.android.locus.addon.ar.utils.Matrix;
import menion.android.locus.addon.ar.utils.Utils;
import menion.android.locus.addon.ar.utils.Vector3D;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

public class AugmentedView extends View {

//	private static final String TAG = "AugmentedView";
	
	// width of actual view
	public int screenW;
	// height of actual view
	public int screenH;
	// transformation camera class
	private Camera cam;
	// radar for markers
	private MarkersRadar mRadar;
	// screen text for range
	private ScreenText mTextDistance;
	// screen text for bearing
	private ScreenText mTextBearing;
	// guide line
	private GuideLine mGuideLine;
	
	// array for highlighted markers
	private ArrayList<Marker> highlighted;
	
	// last location
	private Location lastLoc;
	
	// actual screen click events
	private ArrayList<ClickEvent> uiEvents = new ArrayList<ClickEvent>();
	
	public AugmentedView(Context context) {
		super(context);
		basicInit();
	}
	
    public AugmentedView(Context context, AttributeSet attr) {
    	super(context, attr);
    	basicInit();
    }
    
    private void basicInit() {
    	highlighted = new ArrayList<Marker>();
    	mRadar = new MarkersRadar();
    	mTextDistance = new ScreenText(8);
    	mTextBearing = new ScreenText(8);
    }

	@Override
	public void draw(Canvas c) {
		super.draw(c);

		// set actual screen
		screenW = getWidth();
		screenH = getHeight();
		
		// set canvas for all mPaint methods
		this.c = c;
		
		// initialize core
		if (cam == null) {
			cam = new Camera(screenW, screenH, true);
		}

		// transform to camera
		ArContext ar = Main.arContext;
		ar.getRM(cam.transform);
		ar.calcPitchBearing(cam.transform);

		// set location
		if (lastLoc == null) {
			lastLoc = ar.actualLoc;
		}
		boolean updateLoc = false;
		if (lastLoc != ar.actualLoc) {
			lastLoc = ar.actualLoc;
			updateLoc = true;
		}
		
		// update and draw markers
		highlighted.clear();
		Marker guidedMarker = null;
		int size = ar.getMarkers().size();
		
		// initialize markers and find nearest one
		for (int i = 0; i < size; i++) {
			Marker ma = ar.getMarkers().get(i);
			if (ma.isInRange()) {
				ma.calcPaint(cam);
				if (ar.isMarkerGuided(ma)) {
					guidedMarker = ma;
				} else {
					if (ma.isLookingAt())
						highlighted.add(ma);
					else
						ma.paint(this, c);
				}
			}
		}
		
		// draw highlighted markers now
		for (Marker ma : highlighted) {
			ma.paint(this, c);
		}
		
		// draw guided marker on the end
		if (guidedMarker != null) {
			guidedMarker.paint(this, c);

			if (mGuideLine == null) {
				mGuideLine = new GuideLine(guidedMarker.getLocation());
				mGuideLine.onLocationChanged(ar.actualLoc);
			}

			if (updateLoc)
				mGuideLine.onLocationChanged(lastLoc);
			mGuideLine.calculatePoints(cam);
			mGuideLine.paint(this, c);
		}
		
		// set radar position
		int rx = getResources().getDimensionPixelSize(R.dimen.title_height) + 5;
		int ry = (int) (screenH - 2 * MarkersRadar.RADIUS) - 5;
		
		// draw radar
		paintObj(mRadar, rx, ry, -ArContext.getCurBearing(), 1);

		// draw bearing angle
		mTextBearing.setText(((int) ArContext.getCurBearing()) + "°");
		paintObj(mTextBearing, rx + MarkersRadar.RADIUS - mTextBearing.getWidth() / 2,
				screenH - 2 * MarkersRadar.RADIUS - mTextBearing.getHeight() - 5, 0, 1);
		
		// draw distance value
		mTextDistance.setText(Utils.formatDist(ArContext.getRadius()));
		paintObj(mTextDistance, rx + MarkersRadar.RADIUS - mTextDistance.getWidth() / 2,
				screenH - mTextDistance.getHeight() - 5, 0, 1);

		// handle all click events
		ClickEvent evt = null;
		synchronized (uiEvents) {
			if (uiEvents.size() > 0) {
				evt = uiEvents.get(0);
				uiEvents.remove(0);
			}
		}
		if (evt != null) {
			handleClickEvent(evt);
		}
 	}

	private boolean handleClickEvent(ClickEvent evt) {
//Log.d("AugmentedView", "handleClickEvent(" + evt.x + ", " + evt.y + ")");
		boolean evtHandled = false;
		ArContext ar = Main.arContext;
		for (int i = ar.getMarkers().size() - 1; i >= 0; i--) {
			Marker pm = ar.getMarkers().get(i);
			evtHandled = pm.fClick(evt.x, evt.y);
			if (evtHandled)
				return evtHandled;
		}
		return evtHandled;
	}
	
	public void clickEvent(float x, float y) {
		synchronized (uiEvents) {
			uiEvents.add(new ClickEvent(x, y));
		}
	}

	public void clearEvents() {
		synchronized (uiEvents) {
			uiEvents.clear();
		}
	}
	
	/********************************/
	/*         PAINT PART           */
	/********************************/
	
	private Canvas c;
	private static Paint mPaintImg;

	static {
		mPaintImg = new Paint();
		mPaintImg.setAntiAlias(true);
	}

	private static android.graphics.Matrix matrix = new android.graphics.Matrix();

	public void paintImg(Bitmap img, float x, float y, float rotation, float scale) {
		matrix.reset();
		matrix.preScale(scale, scale);
		matrix.postTranslate(x - (img.getWidth() / 2 * scale), y - (img.getHeight() * scale));
		matrix.postRotate(rotation, x, y);
		c.drawBitmap(img, matrix, mPaintImg);
	}

	public void paintObj(ScreenObject obj, float x, float y, float rotation, float scale) {
		c.save();
		c.translate(x + obj.getWidth() / 2, y + obj.getHeight() / 2);
		c.rotate(rotation);
		c.scale(scale, scale);
		c.translate(- (obj.getWidth() / 2), - (obj.getHeight() / 2));
		obj.paint(this, c);
		c.restore();
	}

	public class Camera {
		
		public int width;
		public int height;

		public Matrix transform = new Matrix();
		public Vector3D lco = new Vector3D();

		private float viewAngle;
		private float dist;

		public Camera(int width, int height, boolean init) {
			this.width = width;
			this.height = height;

			transform.toIdentity();
			lco.set(0, 0, 0);
			
			this.viewAngle = (float) Math.toRadians(AugmentedCamera.angleH);
			this.dist = (this.width / 2) / (float) Math.tan(viewAngle / 2);
		}

		public void projectPoint(Vector3D orgPoint, Vector3D prjPoint) {
			prjPoint.x = dist * orgPoint.x / -orgPoint.z;
			prjPoint.y = dist * orgPoint.y / -orgPoint.z;
			prjPoint.z = orgPoint.z;
			prjPoint.x = prjPoint.x + width / 2;
			prjPoint.y = -prjPoint.y + height / 2;
		}
	}
	
	class ClickEvent {
		public float x, y;

		public ClickEvent(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}
