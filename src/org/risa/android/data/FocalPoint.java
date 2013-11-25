package org.risa.android.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

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

		// Get the boundaries of the Focal Point Image
		Rect bounds = getBounds(res, canvas);
		
		// Draw the focal point on the the scaled image.
		canvas.drawBitmap(image, bounds.left, bounds.top, mPaint);
	}

	@Override
	public Rect getBounds(Resources res, Canvas canvas) {
		// Obtain a reference to the image to draw. 
		Bitmap image = getImage(res);

		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
		// Calculate the scaled point of myself.
		RectangularDimension scaledDimension = new RectangularDimension(canvasWidth, canvasHeight);
		float scale = mOrigDimension.getScale(scaledDimension);
		int scaledX, scaledY;

		// Assign the scaled X Coordinate based on which quandrant we are looking at.
		switch (mQuandrant) {
		case ONE:
		case FOUR:
			scaledX = (int) (scaledDimension.getMidpoint().x - ((mOrigDimension.getMidpoint().x - mCoordinate.x) * scale));
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
		
		// Make sure the combination of the center point and  boundaries of the image fits within the bounds
		// of the image.
		int left = Math.max(0, Math.min(canvasWidth - imgWidth, scaledX - imgWidth / 2));
		int top = Math.min(canvasHeight - imgHeight, scaledY - imgHeight / 2);
		return new Rect(left, top, left + imgWidth, top + imgHeight);
	}
	
	@Override
	public String toString() {
		return "{ X: " + mCoordinate.x + ", Y: " + mCoordinate.y + " }";
	}

}
