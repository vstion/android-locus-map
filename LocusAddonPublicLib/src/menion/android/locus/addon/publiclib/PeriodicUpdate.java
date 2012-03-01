package menion.android.locus.addon.publiclib;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class PeriodicUpdate {

	private Location mLastMapCenter;
	private Location mLastGps;
	
	private double mLocMinDistance;
	
	private static PeriodicUpdate mPU;
	public static PeriodicUpdate getInstance() {
		if (mPU == null) {
			mPU = new PeriodicUpdate();
		}
		return mPU;
	}
	
	private PeriodicUpdate() {
		this.mLocMinDistance = 1.0; 
	}
	
	/**
	 * Set notification limit used for check if distance between previous and
	 * new location is higher than this value. So new locations is market as NEW 
	 * @param minDistance distance in metres
	 */
	public void setLocNotificationLimit(double locMinDistance) {
		this.mLocMinDistance = locMinDistance;
	}
	
	public Location getLastMapCenter() {
		return mLastMapCenter;
	}
	
	public Location getLastGps() {
		return mLastGps;
	}
	
	public interface OnUpdate {
		
		public void onVisibility(boolean mapVisible);
		
		/**
		 * When new location is available in intent, grab and return them
		 * @param mapCenter Current map center location
		 * @param gps Current GPS location or null if GPS is OFF
		 */
		public void onLocation(boolean newMapCenter, boolean newGps);
		
		public void onTrackRecord(boolean recording, boolean paused,
				double recordedDist, long recordedTime, int recordedPoints);
		
		public void onIncorrectData();
	}
	
	public void onReceive(final Context context, Intent intent, OnUpdate handler) {
		if (context == null || intent == null || handler == null)
			throw new IllegalArgumentException("Incorrect arguments");
		
		if (!intent.getAction().equals(LocusConst.ACTION_PERIODIC_UPDATE)) {
			handler.onIncorrectData();
			return;
		}
		
		// check VISIBILITY
		
		handler.onVisibility(intent.getBooleanExtra(
				LocusConst.PUE_VISIBILITY_MAP_SCREEN, false));
		
		// check LOCATIONS
		
		boolean newMapCenter = false;
		if (intent.hasExtra(LocusConst.PUE_LOCATION_MAP_CENTER)) {
			Location loc = intent.getParcelableExtra(LocusConst.PUE_LOCATION_MAP_CENTER);
			if (mLastMapCenter == null ||
					mLastMapCenter.distanceTo(loc) > mLocMinDistance) {
				mLastMapCenter = loc;
				newMapCenter = true;
			} 
		}

		boolean newGpsLocation = false;
		if (intent.hasExtra(LocusConst.PUE_LOCATION_GPS)) {
			Location loc = intent.getParcelableExtra(LocusConst.PUE_LOCATION_GPS);
			if (mLastGps == null ||
					mLastGps.distanceTo(loc) > mLocMinDistance) {
				mLastGps = loc;
				newGpsLocation = true;
			} 
		}
		
		handler.onLocation(newMapCenter, newGpsLocation);
		
		// check TRACK RECORD
		
		if (intent.hasExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_RECORDING)) {
			handler.onTrackRecord(
					intent.getBooleanExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_RECORDING, false),
					intent.getBooleanExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_PAUSED, false),
					intent.getDoubleExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_DISTANCE, 0.0),
					intent.getLongExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_TIME, 0L),
					intent.getIntExtra(LocusConst.PUE_ACTIVITY_TRACK_RECORD_POINTS, 0));
		}
	}
}
