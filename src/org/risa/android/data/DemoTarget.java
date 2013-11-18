package org.risa.android.data;

import org.risa.android.util.ImageUtil;

import android.content.res.Resources;
import android.widget.ImageView;


/**
 * Class that represents a identifiable target.  Just used for demo purposes at this point. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTarget extends Target {

	/**
	 * The Resource ID of this demo target
	 */
	private final int mResourceID;

	/**
	 * The dimensions of the target image
	 */
	private final RectangularDimension mDimensions;
	
	/**
	 * Resources that contains the image at the established resource ID.
	 */
	private final Resources mResources;
	
	/**
	 * Create a demo target using an image resource in Android.
	 * 
	 * @param uniqueName Unique name known to moodstocks.
	 * @param resourceID Resource ID from reference resource I.E. R.raw.<name>
	 */
	public DemoTarget(String uniqueName, Resources resources, int resourceID) {
		super(uniqueName);
		this.mResourceID = resourceID;
		this.mResources = resources;
		this.mDimensions = ImageUtil.getDimensions(mResources, mResourceID);
	}

	/**
	 * Returns the resource ID.
	 * 
	 * @return Resource ID of this image
	 */
	public int getResourceID() {
		return mResourceID;
	}

	@Override
	public void loadImage(ImageView view) {
		view.setImageResource(mResourceID);
	}

	@Override
	public RectangularDimension getDimensions() {
		return mDimensions;
	}
	
}
