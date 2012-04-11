package menion.android.locus.addon.ar.data;

public class ScreenLine2D {
	
	public float x, y;

	public ScreenLine2D() {
		set(0, 0);
	}

	public ScreenLine2D(float x, float y) {
		set(x, y);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void rotate(double t) {
		// recompute to radian
		t = Math.toRadians(t);
		float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
		float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

		x = xp;
		y = yp;
	}

	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}
}
