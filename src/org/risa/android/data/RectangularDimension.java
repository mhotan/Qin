package org.risa.android.data;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Immutable class that represents a a rectangular dimension.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class RectangularDimension {

	/**
	 * The bounds in which the image lies.
	 */
	private final Rect mBounds;
	
	/**
	 * Create a rectangular dimension with width and height.
	 * 
	 * @param width Width of this rectangular dimension.
	 * @param height Hiehgt of this rectangular dimension
	 */
	public RectangularDimension(int width, int height) {
		mBounds = new Rect(0,0,width, height);
	}
	
	/**
	 * Every bounding box contains a mid point. This function
	 * return the midpoint in the form of a point
	 * 
	 * @return Midpoint of this dimension.
	 */
	public Point getMidpoint() {
		return new Point(mBounds.centerX(), mBounds.centerY()); 
	}
	
	/**
	 * Returns the scale factor that allows the rectangular dimensions to
	 * fill completely inside the other one and maximum size. 
	 * 
	 * Given a scale S, this.dimensions * S = other.dimension
	 *  
	 * @param other The other dimensions that  
	 * @return Scale between other and this.
	 */
	public float getScale(RectangularDimension other) {
		if (this.equals(other)) return 1f;
		float horScale = (float) other.getWidth() / (float) this.getWidth();
		float verScale = (float) other.getHeight() / (float) this.getHeight();
		return Math.min(horScale, verScale);
	}
	
	/**
	 * Returns the width of this rectangular dimension
	 * 
	 * @return width of this
	 */
	public int getWidth() {
		return mBounds.right;
	}
	
	/**
	 * Returns the height of this rectangular dimension.
	 * 
	 * @return height of this
	 */
	public int getHeight() {
		return mBounds.bottom;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(this.getClass())) return false;
		RectangularDimension dim = (RectangularDimension) o;
		return this.getWidth() == dim.getWidth() && this.getHeight() == dim.getHeight();
	}
	
	@Override
	public int hashCode() {
		return 79 + getWidth() * 3 + getHeight() * 7;
	}
	
	@Override
	public String toString() {
		return "{ W: " + getWidth() + ", " + " H: " + getHeight() + "}";
	}
}
