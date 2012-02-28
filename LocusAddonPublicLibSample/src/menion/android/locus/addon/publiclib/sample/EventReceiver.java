package menion.android.locus.addon.publiclib.sample;

import menion.android.locus.addon.publiclib.DisplayData;
import menion.android.locus.addon.publiclib.LocusConst;
import menion.android.locus.addon.publiclib.LocusIntents;
import menion.android.locus.addon.publiclib.geoData.Point;
import menion.android.locus.addon.publiclib.geoData.PointsData;
import menion.android.locus.addon.publiclib.utils.RequiredVersionMissingException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class EventReceiver extends BroadcastReceiver {

	private static final String TAG = "EventReceiver";
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;
		
		if (intent.getAction().equals(LocusConst.ACTION_MAP_MOVED_TO_LOCATION)) {
			LocusIntents.getLocationFromIntent(intent, 
					new LocusIntents.OnIntentMainFunction() {
						@Override
						public void onLocationReceived(boolean gpsEnabled, Location locGps,
								Location locMapCenter) {
							Log.w(TAG, "onLocationReceived(" + gpsEnabled + ", " + locGps + ", " + locMapCenter + ")");
							Toast.makeText(context, "onLocationReceived(" + gpsEnabled +
									", " + locGps + ", " + locMapCenter + ")", Toast.LENGTH_LONG).show();

							try {
								// sending back few points near received
								PointsData pd = new PointsData("send_point_silently");
								for (int i = 0; i < 10; i++) {
									Location loc = new Location(TAG);
									loc.setLatitude(locMapCenter.getLatitude() + (Math.random() - 0.5) / 100.0);
									loc.setLongitude(locMapCenter.getLongitude() + (Math.random() - 0.5) / 100.0);
									// point name determine if 
									pd.addPoint(new Point("Testing point - " + i, loc));
								}

								DisplayData.sendDataSilent(context, pd, true);
							} catch (RequiredVersionMissingException e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailed() {
							Log.w(TAG, "onFailed()");
						}
					});
		}
	}

}
