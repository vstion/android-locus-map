package menion.android.locus.addon.publiclib.sample;

import menion.android.locus.addon.publiclib.DisplayData;
import menion.android.locus.addon.publiclib.PeriodicUpdate;
import menion.android.locus.addon.publiclib.PeriodicUpdate.UpdateContainer;
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
		
		// get valid instance of PeriodicUpdate object
		PeriodicUpdate pu = PeriodicUpdate.getInstance();

		// set notification of new locations to 10m
		pu.setLocNotificationLimit(10.0);
		
		// handle event
		pu.onReceive(context, intent, new PeriodicUpdate.OnUpdate() {
			
			@Override
			public void onIncorrectData() {
				Toast.makeText(context, "onIncorrectData()", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onUpdate(UpdateContainer update) {
				Log.i(TAG, "onUpdate(" + update + ")");
				
				// sending data back to locus based on events if new map center and map is visible!
				if (!update.newMapCenter || !update.mapVisible)
					return;
				
				Toast.makeText(context, "ZoomLevel:" + update.mapZoomLevel, Toast.LENGTH_LONG).show();
				
				try {
					// sending back few points near received
					Location mapCenter = PeriodicUpdate.getInstance().getLastMapCenter();
					PointsData pd = new PointsData("send_point_silently");
					for (int i = 0; i < 10; i++) {
						Location loc = new Location(TAG);
						loc.setLatitude(mapCenter.getLatitude() + (Math.random() - 0.5) / 100.0);
						loc.setLongitude(mapCenter.getLongitude() + (Math.random() - 0.5) / 100.0);
						// point name determine if 
						pd.addPoint(new Point("Testing point - " + i, loc));
					}

					DisplayData.sendDataSilent(context, pd);
				} catch (RequiredVersionMissingException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
