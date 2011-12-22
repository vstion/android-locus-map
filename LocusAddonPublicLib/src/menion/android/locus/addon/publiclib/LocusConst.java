/*  
 * Copyright 2011, Asamm soft, s.r.o.
 * 
 * This file is part of LocusAddonPublicLib.
 * 
 * LocusAddonPublicLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * LocusAddonPublicLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with LocusAddonPublicLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package menion.android.locus.addon.publiclib;

public class LocusConst {

	/* 
	 * these intent are used for extending functionality of Locus. All description is 
	 * in 'LocusIntents' class 
	 */
	public static final String INTENT_GET_LOCATION = "menion.android.locus.GET_POINT";
	public static final String INTENT_ON_POINT_ACTION = "menion.android.locus.ON_POINT_ACTION";
	public static final String INTENT_MAIN_FUNCTION = "menion.android.locus.MAIN_FUNCTION";

	/**
	 * basic intent used for display data. Use API for creating intent and not directly
	 */
	public static final String INTENT_DISPLAY_DATA = "android.intent.action.LOCUS_PUBLIC_LIB_DATA";

	/* one PointData object, send over intent */
	public static final String EXTRA_POINTS_DATA = "EXTRA_POINTS_DATA";
	/* array of PointData objects, send over intent */
	public static final String EXTRA_POINTS_DATA_ARRAY = "EXTRA_POINTS_DATA_ARRAY";
	/* data stored in ContentProvider and send request as URI over intent */
	public static final String EXTRA_POINTS_CURSOR_URI = "EXTRA_POINTS_CURSOR_URI";
	/* sends points data serialized as byte[] through file stored on SD card */
	public static final String EXTRA_POINTS_FILE_PATH = "EXTRA_POINTS_FILE_PATH";
	
	/* sends one single track to locus */
	public static final String EXTRA_TRACKS_SINGLE = "EXTRA_TRACKS_SINGLE";
	
	/**
	 * extra parameter that set if data should be firstly imported. This is used in intent 
	 * that sends also 
	 */ 
	public static final String EXTRA_CALL_IMPORT = "EXTRA_CALL_IMPORT";
	
	/**
	 * if you set to any point "setExtraOnDisplay" callback, then when Locus display points and
	 * ask for extended version, return result as Point object included in extra place in intent
	 */
	public static final String EXTRA_POINT = "EXTRA_POINT";

}
