package org.risa.android.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.widget.ImageView;

/**
 * Base class that classifies an identifiable target.  For the sake 
 * of our scope targets are general tow dimensional images that 
 * can be identified using mood stocks api.
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
	 * Collection of items that can be interacted with within the target.
	 */
	private final Set<Interactable> mInteractables;
	
	/**
	 * Creates a target with the associated Global unique id.
	 * 
	 * @param uid Global Universal ID
	 */
	public Target(String uid) {
		mUID = uid;
		mInteractables = new HashSet<Interactable>();
	}
	
	/**
	 * 
	 * 
	 * @return All the interactable objects within the target
	 */
	public Collection<Interactable> getInteractables() {
		return new HashSet<Interactable>(mInteractables);
	}
	
	/**
	 * Adds a interactable item or overwrites any equal item within this
	 * Target.
	 * 
	 * @param item item to add that is interactable.
	 */
	public void addInteractable(Interactable item) {
		mInteractables.add(item);
	}
	
	/**
	 * Removes an interactable item if it exists within this target.
	 * 
	 * @param item Item to remove
	 */
	public void removeInteractable(Interactable item) {
		mInteractables.remove(item);
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

	/**
	 * Every image has a bounding area.  This function returns the boundings area
	 * 
	 * @return The original bounding area of this target
	 */
	public abstract RectangularDimension getDimensions();
		
}
