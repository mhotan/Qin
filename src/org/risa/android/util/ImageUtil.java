package org.risa.android.util;

import org.risa.android.data.RectangularDimension;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;

/**
 * Handles general image utility functionality.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ImageUtil {

	private ImageUtil() {}
	
	/**
	 * Returns a Rect with a top left coordinate of 0,0.  The bottom right 
	 * coordinates determine the actual dimensions
	 * 
	 * @param res Resources that contain the image
	 * @param id ID of the image
	 * @return Pair representing dimensions of the image. first = width, second = height.
	 */
	public static RectangularDimension getDimensions(Resources res, int id) {
		BitmapFactory.Options dimensions = new BitmapFactory.Options(); 
		dimensions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, id, dimensions);
		return new RectangularDimension(dimensions.outWidth, dimensions.outHeight);
	}

	/**
	 * Highlights the image src and returns the new image.
	 * 
	 * @param src Image to highlight
	 * @return Highlighted source
	 */
	public static Bitmap highlightImage(Bitmap src) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Bitmap.Config.ARGB_8888);
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));
        int[] offsetXY = new int[2];
        // capture alpha into a bitmap
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        // create a color paint
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        // paint color for captured alpha region (bitmap)
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        // free memory
        bmAlpha.recycle();

        // paint the image source
        canvas.drawBitmap(src, 0, 0, null);

        // return out final image
        return bmOut;
    }
	
	/**
	 * Gets a 
	 * 
	 * @param glowColor The color to glow
	 * @return Paint instance that can be used for glowing
	 */
	public static Paint getPaintForGlow(int glowColor) {
		Paint mPaintForGlow = new Paint();
		mPaintForGlow.setDither(true);
		mPaintForGlow.setAntiAlias(true);
		mPaintForGlow.setFilterBitmap(true);  
		ColorFilter colorFilterTint = new LightingColorFilter(Color.WHITE, glowColor);
		mPaintForGlow.setColorFilter(colorFilterTint);
		return mPaintForGlow;
	}
	
}
