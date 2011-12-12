package menion.android.locus.addon.publiclib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import menion.android.locus.addon.publiclib.geoData.PointsData;
import menion.android.locus.addon.publiclib.utils.DataStorage;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

public class DisplayData {

	private static final String TAG = "DisplayData";
	
	/**
	 * Simple way how to send data over intent to Locus. Count that intent in
	 * Android have some size limits so for larger data, use another method
	 * @param context actual {@link Context}
	 * @param data {@link PointsData} object that should be send to Locus
	 * @param callImport whether import with this data should be called after Locus starts
	 * @return true if success
	 */
	public static boolean sendData(Context context, PointsData data, boolean callImport) {
		if (data == null)
			return false;
		Intent intent = new Intent();
		intent.putExtra(LocusConst.EXTRA_POINTS_DATA, data);
		return sendData(context, intent, callImport);
	}
	
	/**
	 * Simple way how to send ArrayList<PointsData> object over intent to Locus. Count that
	 * intent in Android have some size limits so for larger data, use another method
	 * @param context actual {@link Context}
	 * @param data {@link ArrayList} of data that should be send to Locus
	 * @return true if success
	 */
	public static boolean sendData(Context context, ArrayList<PointsData> data, boolean callImport) {
		if (data == null)
			return false;
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(LocusConst.EXTRA_POINTS_DATA_ARRAY, data);
		return sendData(context, intent, callImport);
	}
	
	private static final int FILE_VERSION = 1;
	
	/**
	 * Allow to send data to locus, by storing serialized version of data into file. This method
	 * can have advantage over cursor in simplicity of implementation and also that filesize is
	 * not limited as in Cursor method. On second case, need permission for disk access and should
	 * be slower due to IO operations. Be careful about size of data. This method can avoid OutOfMemory
	 * error on Locus side if data are too big
	 *   
	 * @param context
	 * @param data
	 * @param filepath
	 * @param callImport
	 * @return
	 */
	public static boolean sendDataFile(Context context, ArrayList<PointsData> data, String filepath, boolean callImport) {
		if (data == null || data.size() == 0)
			return false;
		
		FileOutputStream os = null;
		DataOutputStream dos = null;
		try {
			File file = new File(filepath);
			file.getParentFile().mkdirs();

			if (file.exists())
				file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}

			os = new FileOutputStream(file, false);
			dos = new DataOutputStream(os);
	
			// write current version
			dos.writeInt(FILE_VERSION);
			
			// write data
			for (int i = 0; i < data.size(); i++) {
				// get byte array
				Parcel par = Parcel.obtain();
				data.get(i).writeToParcel(par, 0);
				byte[] byteData = par.marshall();
				
				// write data
				dos.writeInt(byteData.length);
				dos.write(byteData);
			}
				
			os.flush();
		} catch (Exception e) {
			Log.e(TAG, "saveBytesInstant(" + filepath + ", " + data + ")", e);
			return false;
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "saveBytesInstant(" + filepath + ", " + data + ")", e);
			}
		}
		
		// store data to file
		Intent intent = new Intent();
		intent.putExtra(LocusConst.EXTRA_POINTS_FILE_PATH, filepath);
		return sendData(context, intent, callImport);
	}
	
	public static ArrayList<PointsData> getDataFile(String filepath) {
		ArrayList<PointsData> returnData = new ArrayList<PointsData>();
		
		// check file
		File file = new File(filepath);
		if (!file.exists())
			return returnData;
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(file));
			
			// check version
			if (dis.readInt() != FILE_VERSION) {
				Log.e(TAG, "getDataFile(" + filepath + "), unsupported (old) version!");
				return returnData;
			}
			
			while (true) {
				if (dis.available() == 0)
					break;
				
				int size = dis.readInt();
				byte[] data = new byte[size];
				dis.read(data);
				
				Parcel par = Parcel.obtain();
				par.unmarshall(data, 0, data.length);
				par.setDataPosition(0);
				
				PointsData pd = PointsData.CREATOR.createFromParcel(par);
				if (pd != null)
					returnData.add(pd);
			}
			return returnData;
		} catch (Exception e) {
			Log.e(TAG, "getDataFile(" + filepath + ")", e);
			return null;
		} finally {
			try {
				if (dis != null)
					dis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Way how to send ArrayList<PointsData> object over intent to Locus. Data are
	 * stored in ContentProvider so don't forget to register it in manifest. More in
	 * sample application. This is recommended way how to send huge data to Locus 
	 * @param context actual context
	 * @param data ArrayList of data that should be send to Locus
	 * @param callImport whether import with this data should be called after Locus starts
	 * @return true if success
	 */
	public static boolean sendDataCursor(Context context, PointsData data, String uri, boolean callImport) {
		if (data == null)
			return false;
		// set data
		DataStorage.setData(data);
		Intent intent = new Intent();
		intent.putExtra(LocusConst.EXTRA_POINTS_CURSOR_URI, uri);
		return sendData(context, intent, callImport);
	}
	
	/**
	 * Way how to send ArrayList<PointsData> object over intent to Locus. Data are
	 * stored in ContentProvider so don't forget to register it in manifest. More in
	 * sample application. This is recommended way how to send huge data to Locus 
	 * @param context actual context
	 * @param data ArrayList of data that should be send to Locus
	 * @return true if success
	 */
	public static boolean sendDataCursor(Context context, ArrayList<PointsData> data,
			String uri, boolean callImport) {
		if (data == null)
			return false;
		// set data
		DataStorage.setData(data);
		Intent intent = new Intent();
		intent.putExtra(LocusConst.EXTRA_POINTS_CURSOR_URI, uri);
		return sendData(context, intent, callImport);
	}

	// private final call to Locus
	private static boolean sendData(Context context, Intent intent, boolean callImport) {
		// really exist locus?
		if (!LocusUtils.isLocusAvailable(context)) {
			Log.w(TAG, "Locus is not installed");
			return false;
		}
		
		// check intent firstly
		if (!hasData(intent)) {
			Log.w(TAG, "Intent 'null' or not contain any data");
			return false;
		}
		
		intent.putExtra(LocusConst.EXTRA_CALL_IMPORT, callImport);
		
		// create intent with right calling method
		intent.setAction(LocusConst.INTENT_DISPLAY_DATA);
		// finally start activity
		context.startActivity(intent);
		return true;
	}
	
	private static boolean hasData(Intent intent) {
		if (intent == null)
			return false;
		
		return !(
				intent.getParcelableArrayListExtra(LocusConst.EXTRA_POINTS_DATA_ARRAY) == null && 
				intent.getParcelableExtra(LocusConst.EXTRA_POINTS_DATA) == null &&
				intent.getStringExtra(LocusConst.EXTRA_POINTS_CURSOR_URI) == null && 
				intent.getStringExtra(LocusConst.EXTRA_POINTS_FILE_PATH) == null);
	}
}
