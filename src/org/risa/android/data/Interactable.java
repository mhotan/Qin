package org.risa.android.data;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Interface that defines an interactable object.  IE. An image can have different components to interact with.  
 * Therefore each of these components within the image would implement this interface. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public interface Interactable {
	
	/**
	 * Mandates to the interactible object to be able to draw itself on the given canvas.
	 * 
	 * @param res Resources that can be used to get static data
	 * @param canvas Canvas that can be used to draw 
	 */
	public void onDrawSelf(Resources res, Canvas canvas);
	
	// TODO create a mandatory interaction handler for all implementing classes.
	
	/**
	 * Returns the boundary for which it would be drawn in.
	 * 
	 * @param res Resources that can be used to get static data
	 * @param canvas Canvas that can be used to draw 
	 * @return The Boundary to draw the resources.
	 */
	public Rect getBounds(Resources res, Canvas canvas);
	
	
}
