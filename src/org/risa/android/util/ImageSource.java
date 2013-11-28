package org.risa.android.util;

import android.content.Context;
import android.widget.ImageView;

/**
 * Interface that defines the requirements to support image generation.
 * <b> Images can be defined within different areas.  This includes local resources,
 *  local disk, cloud storage.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public interface ImageSource {
	
	/**
	 * Loads image into this image view.  Can potentially do this asyncronously. 
	 * 
	 * @param context Context that the image view exists in
	 * @param view View to load the image in
	 */
	public void loadImage(Context context, ImageView view);
	
}
