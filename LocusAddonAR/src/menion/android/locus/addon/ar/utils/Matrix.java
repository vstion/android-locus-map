/*
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

public class Matrix {
	public float a1, a2, a3;
	public float b1, b2, b3;
	public float c1, c2, c3;

	public void set(float a1, float a2, float a3, float b1, float b2,
			float b3, float c1, float c2, float c3) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;

		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;

		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
	}

	public void set(Matrix m) {
		this.a1 = m.a1;
		this.a2 = m.a2;
		this.a3 = m.a3;

		this.b1 = m.b1;
		this.b2 = m.b2;
		this.b3 = m.b3;

		this.c1 = m.c1;
		this.c2 = m.c2;
		this.c3 = m.c3;
	}

	public void toIdentity() {
		set(1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	public void toXRot(float angleX) {
		set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math
				.sin(angleX), 0f, (float) Math.sin(angleX), (float) Math
				.cos(angleX));
	}

	public void toYRot(float angleY) {
		set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY), 0f, 1f,
				0f, (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));
	}

	public void toZRot(float angleZ) {
		set((float) Math.cos(angleZ), (float) -Math.sin(angleZ), 0f,
				(float) Math.sin(angleZ), (float) Math.cos(angleZ), 0f, 0f, 0f,
				1f);
	}

	public void toScale(float scale) {
		set(scale, 0, 0, 0, scale, 0, 0, 0, scale);
	}

	public void toAt(Vector3D cam, Vector3D obj) {
		Vector3D worldUp = new Vector3D(0, 1, 0);

		Vector3D dir = new Vector3D();
		dir.set(obj);
		dir.sub(cam);
		dir.mult(-1f);
		dir.norm();

		Vector3D right = new Vector3D();
		right.cross(worldUp, dir);
		right.norm();

		Vector3D up = new Vector3D();
		up.cross(dir, right);
		up.norm();

		set(right.x, right.y, right.z, up.x, up.y, up.z, dir.x, dir.y, dir.z);
	}

	public void adj() {
		float a11 = a1;
		float a12 = a2;
		float a13 = a3;

		float a21 = b1;
		float a22 = b2;
		float a23 = b3;

		float a31 = c1;
		float a32 = c2;
		float a33 = c3;

		a1 = det2x2(a22, a23, a32, a33);
		a2 = det2x2(a13, a12, a33, a32);
		a3 = det2x2(a12, a13, a22, a23);

		b1 = det2x2(a23, a21, a33, a31);
		b2 = det2x2(a11, a13, a31, a33);
		b3 = det2x2(a13, a11, a23, a21);

		c1 = det2x2(a21, a22, a31, a32);
		c2 = det2x2(a12, a11, a32, a31);
		c3 = det2x2(a11, a12, a21, a22);
	}

	public void invert() {
		float det = this.det();

		adj();
		mult(1 / det);
	}

	public void transpose() {
		float a11 = a1;
		float a12 = a2;
		float a13 = a3;

		float a21 = b1;
		float a22 = b2;
		float a23 = b3;

		float a31 = c1;
		float a32 = c2;
		float a33 = c3;
		
		b1 = a12;
		a2 = a21;
		b3 = a32;
		c2 = a23;
		c1 = a13;
		a3 = a31;
		
		a1 = a11;
		b2 = a22;
		c3 = a33;
		
	}

	private float det2x2(float a, float b, float c, float d) {
		return (a * d) - (b * c);
	}

	public float det() {
		return (a1 * b2 * c3) - (a1 * b3 * c2) - (a2 * b1 * c3)
				+ (a2 * b3 * c1) + (a3 * b1 * c2) - (a3 * b2 * c1);
	}

	public void mult(float c) {
		a1 = a1 * c;
		a2 = a2 * c;
		a3 = a3 * c;

		b1 = b1 * c;
		b2 = b2 * c;
		b3 = b3 * c;

		c1 = c1 * c;
		c2 = c2 * c;
		c3 = c3 * c;
	}

	public void add(Matrix n) {
		a1 += n.a1;
		a2 += n.a2;
		a3 += n.a3;

		b1 += n.b1;
		b2 += n.b2;
		b3 += n.b3;

		c1 += n.c1;
		c2 += n.c2;
		c3 += n.c3;
	}

	public void prod(Matrix n) {
		Matrix m = new Matrix();
		m.set(this);

		a1 = (m.a1 * n.a1) + (m.a2 * n.b1) + (m.a3 * n.c1);
		a2 = (m.a1 * n.a2) + (m.a2 * n.b2) + (m.a3 * n.c2);
		a3 = (m.a1 * n.a3) + (m.a2 * n.b3) + (m.a3 * n.c3);

		b1 = (m.b1 * n.a1) + (m.b2 * n.b1) + (m.b3 * n.c1);
		b2 = (m.b1 * n.a2) + (m.b2 * n.b2) + (m.b3 * n.c2);
		b3 = (m.b1 * n.a3) + (m.b2 * n.b3) + (m.b3 * n.c3);

		c1 = (m.c1 * n.a1) + (m.c2 * n.b1) + (m.c3 * n.c1);
		c2 = (m.c1 * n.a2) + (m.c2 * n.b2) + (m.c3 * n.c2);
		c3 = (m.c1 * n.a3) + (m.c2 * n.b3) + (m.c3 * n.c3);
	}

	@Override
	public String toString() {
		return "[ (" + a1 + "," + a2 + "," + a3 + ") (" + b1 + "," + b2 + ","
				+ b3 + ") (" + c1 + "," + c2 + "," + c3 + ") ]";
	}
}
