package org.risa.android.data;

import org.risa.android.util.ImageUtil;

import android.R.color;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.aqt.qin.R;

/**
 * A point within an image that is able to be selected and interact with.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class FocalPoint implements Interactable {

	/**
	 * Center point of this point on the target image.
	 */
	private final Point mCoordinate;

	/**
	 * Image that represents focal point
	 */
	private Bitmap mImage;
	
	/**
	 * Paint class that is in charge of drawing the image
	 */
	private final Paint mPaint;
	
	/**
	 * Original dimensions of the target image this point is on. 
	 */
	private final RectangularDimension mOrigDimension;
	
	/**
	 * The quandrant where this focal plane lies in.
	 */
	private final Quandrant mQuandrant;
	
	/**
	 * Focal point set at a final deterministic location.
	 * 
	 * @param targetDimension Dimensions of the target 
	 * @param xCoord X Coordinate of the this focal points
	 * @param yCoord Y coordinate of the this focal points
	 */
	public FocalPoint(RectangularDimension targetDimension, int xCoord, int yCoord) {
		if (xCoord < 0) throw new IllegalArgumentException("Can't have negative x coordinate");
		if (yCoord < 0) throw new IllegalArgumentException("Can't have negative y coordinate");
		mCoordinate = new Point(xCoord, yCoord);
		
		// Paint in charge of drawing the focal point
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		// The dimension of the target image this point is on
		mOrigDimension = targetDimension;
		
		if (xCoord <= targetDimension.getMidpoint().x && yCoord < targetDimension.getMidpoint().y) {
			// Quadrant 1
			mQuandrant = Quandrant.ONE;
		} else if (xCoord > targetDimension.getMidpoint().x && yCoord <= targetDimension.getMidpoint().y) {
			// Quandrant 2
			mQuandrant = Quandrant.TWO;
		} else if (xCoord >= targetDimension.getMidpoint().x && yCoord > targetDimension.getMidpoint().y) {
			// Quandrant 3
			mQuandrant = Quandrant.THREE;
		} else {
			// Quandrant 4
			mQuandrant = Quandrant.FOUR;
		}
	}
	
	/**
	 * The Focal point is determined at the time the target is annotated.
	 * Therefore the points will always correspond to the raw location of the focal
	 * point.
	 * 
	 * @return Raw location of the focal point 
	 */
	public Point getLocation() {
		return new Point(mCoordinate);
	}

	/**
	 * Performs lazy instantiation of Bitmap image
	 * 
	 * @param res Resources to get image
	 * @return Bitmap image of focal point
	 */
	private Bitmap getImage(Resources res) {
		if (mImage == null) {
			mImage = BitmapFactory.decodeResource(res, R.raw.focal_point);
//			mImage = ImageUtil.highlightImage(mImage);
		}
		return mImage;
	}

	/**
	 * Focal Points are usually drawn with respect to a specific 2 Dimensional range.
	 * However, there are many times where the image is scaled and altered due to hardware specifications.  
	 * This reference to the original target dimensions 
	 * 
	 * @return The dimensions of the target image
	 */
	protected RectangularDimension getOrigTargetDimensions() {
		return mOrigDimension;
	}
	
	@Override
	public void onDrawSelf(Resources res, Canvas canvas) {
		
		// Obtain a reference to the image to draw. 
		Bitmap image = getImage(res);
		
		canvas.drawColor(color.black);
		
		// Calculate the scaled point of myself.
		RectangularDimension scaledDimension = new RectangularDimension(canvas.getWidth(), canvas.getHeight());
		float scale = mOrigDimension.getScale(scaledDimension);
		int scaledX, scaledY;
		
//		scaledX = (int) ((scaledDimension.getWidth() - mOrigDimension.getWidth() * scale) / 2) + Math.round(scale * mCoordinate.x);
//		scaledY = (int) ((scaledDimension.getHeight() - mOrigDimension.getHeight() * scale) / 2) + Math.round(scale * mCoordinate.y);
		
		// Assign the scaled X Coordinate based on which quandrant we are looking at.
		switch (mQuandrant) {
		case ONE:
		case FOUR:
			int xScaleMid = scaledDimension.getMidpoint().x;
			int xOrigMid = mOrigDimension.getMidpoint().x;
			int origDistFromMid = xOrigMid - mCoordinate.x;
			float scaledDistant = origDistFromMid * scale;
			scaledX = (int) (xScaleMid - scaledDistant);
			break;
		default:
			scaledX = (int) (scaledDimension.getMidpoint().x + (mCoordinate.x - mOrigDimension.getMidpoint().x) * scale);
		}
		
		// Assign the Scaled Y Coordinate based on which quandrant the focal point is in.
		switch (mQuandrant) {
		case ONE:
		case TWO:
			scaledY = (int) (scaledDimension.getMidpoint().y - (mOrigDimension.getMidpoint().y - mCoordinate.y) * scale);
			break;
		default:
			scaledY = (int) (scaledDimension.getMidpoint().y + (mCoordinate.y - mOrigDimension.getMidpoint().y) * scale);
		}
		
		// Draw the focal point on the the scaled image.
		canvas.drawBitmap(image, Math.max(0, Math.min(canvas.getWidth() - image.getWidth(), scaledX - image.getWidth() / 2)), 
				Math.min(canvas.getHeight() - image.getHeight(), scaledY - image.getHeight() / 2), mPaint);
	}
	
}
