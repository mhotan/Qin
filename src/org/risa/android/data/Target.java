package org.risa.android.data;

import android.widget.ImageView;

/**
 * Base class that classifies an identifiable target.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public abstract class Target {

	/**
	 * Global Unique ID.
	 * Note: That UIDs are set by the server.
	 */
	private final String mUID;
	
	/**
	 * Creates a target with the associated Global unique id.
	 * 
	 * @param uid Global Universal ID
	 */
	public Target(String uid) {
		mUID = uid;
	}
	
	/**
	 * Returns the Global Unique ID for this target.
	 * 
	 * @return The Global Unique ID
	 */
	public String getUID() {
		return mUID;
	}
	
	/**
	 * Passes the responsibility to all subclasses to load an image within this view.
	 * This must be a synchronous operation. Therefore classes must pre load all images before 
	 * setting the views image.
	 * 
	 * @param view View to load  
	 */
	public abstract void loadImage(ImageView view);
	
}
