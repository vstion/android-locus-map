package menion.android.locus.addon.ar.data;

import menion.android.locus.addon.ar.AugmentedView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.location.Location;

public class ScreenText extends ScreenObject {

	private String mText;

	private Paint mPaintBg;
	private Paint mPaintBgBorder;
	private Paint mPaintText;
	
	private float mWidth;
	private float mHeight;
	private RectF mRect;
	
	private float padding = 5.0f;
	private float radius = 5.0f;
	
	public ScreenText(int maxLength) {
		mText = "";
		
		mPaintBg = new Paint();
		mPaintBg.setColor(Color.WHITE);
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStyle(Paint.Style.FILL);
		
		mPaintBgBorder = new Paint();
		mPaintBgBorder.setColor(Color.BLACK);
		mPaintBgBorder.setAntiAlias(true);
		mPaintBgBorder.setStyle(Paint.Style.STROKE);
		
		mPaintText = new Paint();
		mPaintText.setColor(Color.BLACK);
		mPaintText.setAntiAlias(true);
		mPaintText.setStyle(Paint.Style.FILL);
		mPaintText.setTextSize(16.0f);
		mPaintText.setTextAlign(Align.CENTER);
		
		String text = "";
		for (int i = 0; i < maxLength; i++)
			text += "0";
		mWidth = mPaintText.measureText(text);
		mHeight = mPaintText.getTextSize() + 2 * padding;
		mRect = new RectF(0, 0, mWidth, mHeight);
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInRange() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void paint(AugmentedView av, Canvas c) {
		c.drawRoundRect(mRect, radius, radius, mPaintBg);
		c.drawRoundRect(mRect, radius, radius, mPaintBgBorder);
		c.drawText(mText, mWidth / 2, padding - mPaintText.ascent(), mPaintText);
	}

	@Override
	public float getWidth() {
		return mWidth;
	}

	@Override
	public float getHeight() {
		return mHeight;
	}
}
