package menion.android.locus.addon.ar.data;

import menion.android.locus.addon.ar.AugmentedView;
import android.graphics.Canvas;
import android.location.Location;

public abstract class ScreenObject {

	public abstract void onLocationChanged(Location loc); 
	
	public abstract boolean isInRange();
	
	public abstract void paint(AugmentedView av, Canvas c);
	
	public abstract float getHeight();
	
	public abstract float getWidth();
}
