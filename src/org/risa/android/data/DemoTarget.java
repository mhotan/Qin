package org.risa.android.data;

import android.widget.ImageView;


/**
 * Class that represents a identifiable target.  Just used for demo purposes at this point. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTarget extends Target {

	private final int mResourceID;

	/**
	 * Create a demo target using an image resource in Android.
	 * 
	 * @param uniqueName Unique name known to moodstocks.
	 * @param resourceID Resource ID from reference resource I.E. R.raw.<name>
	 */
	public DemoTarget(String uniqueName, int resourceID) {
		super(uniqueName);
		this.mResourceID = resourceID;
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
	
}
