/*
 * Copyright 2012, Asamm Software, s. r. o.
 * 
 * This file is part of Locus - add-on AR project (LocusAddonAR).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package menion.android.locus.addon.ar.utils;

import menion.android.locus.addon.publiclib.utils.UtilsAddonAR;
import android.location.Location;

public class UtilsGeo {

	public static void convLocToVec(Location org, Location gp, Vector3D v) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), gp
				.getLatitude(), org.getLongitude(), z);
		float[] x = new float[1];
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), org
				.getLatitude(), gp.getLongitude(), x);
		// set correct altitude
		double y;
		if (gp.getAltitude() == 0.0 || gp.getAltitude() == UtilsAddonAR.NO_ALTITUDE)
			y = 0;
		else
			y = gp.getAltitude() - org.getAltitude();
		
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;

		v.set(x[0], (float) y, z[0]);
	}
	
//	public static void convertVecToLoc(Vector3D v, Location org, Location gp) {
//		double brngNS = 0;
//		double brngEW = 90;
//		if (v.z > 0)
//			brngNS = 180;
//		if (v.x < 0)
//			brngEW = 270;
//
//		PhysicalPlace tmp1Loc = new PhysicalPlace();
//		PhysicalPlace tmp2Loc = new PhysicalPlace();
//		PhysicalPlace.calcDestination(org.getLatitude(), org.getLongitude(), brngNS,
//				Math.abs(v.z), tmp1Loc);
//		PhysicalPlace.calcDestination(tmp1Loc.getLatitude(), tmp1Loc.getLongitude(),
//				brngEW, Math.abs(v.x), tmp2Loc);
//
//		gp.setLatitude(tmp2Loc.getLatitude());
//		gp.setLongitude(tmp2Loc.getLongitude());
//		gp.setAltitude(org.getAltitude() + v.y);
//	}
	
//	public static void calcDestination(double lat1Deg, double lon1Deg,
//			double bear, double d, PhysicalPlace dest) {
//		double brng = Math.toRadians(bear);
//		double lat1 = Math.toRadians(lat1Deg);
//		double lon1 = Math.toRadians(lon1Deg);
//		double R = 6371.0 * 1000.0; 
//
//		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R)
//				+ Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
//		double lon2 = lon1
//				+ Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
//						Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));
//
//		dest.setLatitude(Math.toDegrees(lat2));
//		dest.setLongitude(Math.toDegrees(lon2));
//	}
}
