/*  
 * Copyright 2011, Asamm soft, s.r.o.
 * 
 * This file is part of LocusAddonPublicLibSample.
 * 
 * LocusAddonPublicLibSample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * LocusAddonPublicLibSample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with LocusAddonPublicLibSample.  If not, see <http://www.gnu.org/licenses/>.
 */

package menion.android.locus.addon.publiclib.sample;

import java.util.ArrayList;

import menion.android.locus.addon.publiclib.geoData.PointsData;
import menion.android.locus.addon.publiclib.utils.DataCursor;
import menion.android.locus.addon.publiclib.utils.DataStorage;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

public class DataStorageProvider extends ContentProvider {

//	private final static String TAG = "DataStorageProvider";
	
	@Override
	public Cursor query(Uri aUri, String[] aProjection, String aSelection,
			String[] aSelectionArgs, String aSortOrder) {
		
		DataCursor cursor = new DataCursor(new String[] {"data"});
		
		ArrayList<PointsData> data = DataStorage.getData();
		if (data == null || data.size() == 0)
			return cursor;
		
		for (int i = 0; i < data.size(); i++) {
			// get byte array
			Parcel par = Parcel.obtain();
			data.get(i).writeToParcel(par, 0);
			byte[] byteData = par.marshall();
			// add to row
			cursor.addRow(new Object[] {byteData});
		}
		// data filled to cursor, clear reference to prevent some memory issue
		DataStorage.clearData();
		// now finally return filled cursor
		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
}
