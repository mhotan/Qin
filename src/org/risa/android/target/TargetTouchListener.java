package org.risa.android.target;

import java.util.HashMap;
import java.util.Map;

import org.risa.android.data.Interactable;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Class that manages the touch interactions on an image view that contains a target.
 * This classes then translates touches to specific interaction details.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetTouchListener implements OnTouchListener {

	/**
	 * Mapping of all the taken rectangular bounderies.  Each rectangular boundary must
	 * map to an Interactable.  This way boundaries can only be defined to 
	 * have some kind of actionable behavior.  Or it is not defined at all.
	 * 
	 */
	private final Map<Rect, Interactable> mMapping; 

	/**
	 * Class that wishes to listen to specific interaction request.
	 */
	private OnInteractionOccurredListener mListener;

	/**
	 * Object that indentifies the location of last original press down.
	 * Note that this null before any press down.
	 */
	private Rect mLastDownBounds;

	/**
	 * Creates a Target Touch Listener without any registered interactable points.
	 */
	public TargetTouchListener() {
		mMapping = new HashMap<Rect, Interactable>();
	}

	/**
	 * Sets the Interact Listeners
	 * 
	 * @param list Interaction listener, or null to remove interaction listener
	 */
	public void setOnInteractionOccurredListener(OnInteractionOccurredListener list) {
		mListener = list;
	}

	/**
	 * Returns the interaction listener associated with this touch listener.
	 * 
	 * @return Interaction Listener if one existed, null otherwise
	 */
	public OnInteractionOccurredListener getInteractionListener() {
		return mListener;
	}

	/**
	 * Attempts to add an interactable item to a cosntrained boundary.
	 * 
	 * @param bounds Bounds to set to the interactable item
	 * @param item Interactable item to reference upon selection.
	 * @return true if we were able to register an interactable item, false otherwise.
	 */
	public boolean registerInteractable(Rect bounds, Interactable item) {
		if (bounds == null) 
			throw new NullPointerException("TargetTouchListener.registerInteractable(): " +
					"Can't register null boundary for selection");
		if (item == null)
			throw new NullPointerException("TargetTouchListener.registerInteractable(): " +
					"Can't register null interactable item");


		// Mapping is not allowed to have any overlapping boundaries.
		// Currently, it is impossible to determine which interactable 
		// event has priority over another if they intersect.  So best to 
		// not let it happen at all.
		for (Rect rect : mMapping.keySet()) {
			if (rect.intersect(bounds))
				return false;
		}

		// Place the bounds and the interactable.
		mMapping.put(bounds, item);
		return true;
	}

	/**
	 * Clear all the elements within the mapping.
	 */
	public void clear() {
		mMapping.clear();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Point p = new Point((int)event.getX(), (int)event.getY());
		Rect bounds = getContainingBoundary(p);

		
		switch (event.getAction()) {

		// On a down press intialize the bounds we are currently in.
		case MotionEvent.ACTION_DOWN:
			mLastDownBounds = bounds;
			break;
		case MotionEvent.ACTION_UP:
			if (mLastDownBounds == null || !mLastDownBounds.equals(bounds)) {
				mLastDownBounds = null;
				mListener.onClearSelected();
			} else { 
				// We have a bounds match between the down and up press.
				// This shows direct intent on whatever area we are looking in.
				Interactable i = mMapping.get(bounds);
				mListener.onInteractableSelected(i);
			}
		}

		// We want to consume the event because all touches 
		// represent a strict intent to select an item.
		return true;
	}

	/**
	 * Attempts to find a registered boundary that contains argument Point p.
	 * <b>If none can be found then null is returned.
	 * 
	 * @param p Point that will be contained in return boundary.
	 * @return Return containing boundary or null if there is no containing boundary.
	 */
	private Rect getContainingBoundary(Point p) {
		for (Rect r: mMapping.keySet()) {
			if (r.contains(p.x, p.y))
				return r;
		}
		return null;
	}

	/**
	 * Any class wishes to be notified when a definitive interactions 
	 * can implement this interface and register self with an instance of this. 
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface OnInteractionOccurredListener {

		/**
		 * A notification that an interactable item was selected on the screen.
		 * 
		 * @param interactable Interactable item selected, null if non was selected.
		 */
		public void onInteractableSelected(Interactable interactable);

		/**
		 * Clear any selected item.
		 */
		public void onClearSelected();

	}

}
