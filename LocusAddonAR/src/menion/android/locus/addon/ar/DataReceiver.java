package menion.android.locus.addon.ar;

import menion.android.locus.addon.publiclib.utils.UtilsAddonAR;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.w("DataReceiver()", "receivedBroadCast()");
		if (arg1.getBooleanExtra(UtilsAddonAR.EXTRA_END_AR, false)) {
			// kill AR is called by parent
			final Main main = Main.arContext.getMain();
			main.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Intent inte = new Intent();
					main.setResult(Activity.RESULT_OK, inte);
					main.finish();
				}
			});
		} else {
			Main.arContext.handleIntent(arg1);
		}
	}
}
