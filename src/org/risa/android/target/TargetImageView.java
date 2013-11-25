package org.risa.android.target;

import java.util.Collection;

import org.risa.android.data.Interactable;
import org.risa.android.data.Target;
import org.risa.android.target.TargetTouchListener.OnInteractionOccurredListener;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


/**
 * ImageView that presents the target to the user.  Allows the user to select content of the image. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageView extends ImageView implements 
OnMatrixChangedListener, OnPhotoTapListener {

	private static final String LOG_TAG = TargetImageView.class.getSimpleName(); 
	
	/**
	 * TOOD Turn this canvas into an interactive canvas
	 * Allow users to pinch and pan into different pieces of the image. 
	 */

	/**
	 * The target to be drawn on this ImageView.
	 */
	private final Target mTarget;

	/**
	 * Context this image view is in.
	 */
	private final Context mCtx;
	
	/**
	 * PhotoView attacher that controls view manipulation.
	 */
	private PhotoViewAttacher mAttacher;
	
	/**
	 * The Listener that manages touching focal points.
	 */
	private final TargetTouchListener mTouchListener;
	
	/**
	 * Creates a target image view from a resource id.
	 * 
	 * @param id Resource ID of the target image
	 * @param context Context to create view with
	 * @param attrs attributes of the image
	 */
	public TargetImageView(Target target, Context context, OnInteractionOccurredListener listener) {
		super(context);
		// Set the context to draw this.
		mCtx = context;
		mTarget = target;

		// Set the scale type so all images are fully confined within the canvas
		// TODO Make this an interactive IMage View.
		setScaleType(ImageView.ScaleType.CENTER_INSIDE);

		// Load the image on this view.
		mTarget.loadImage(this);
		
		// Allocate the Touch Listener.
		mTouchListener = new TargetTouchListener();
		mTouchListener.setOnInteractionOccurredListener(listener);
		setOnTouchListener(mTouchListener);
		
//	    mAttacher = new PhotoViewAttacher(this);
//	    mAttacher.setOnMatrixChangeListener(this);
//	    mAttacher.setOnPhotoTapListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// TODO This is the easiest way update all the points.
		// Because the underlying data structure is a map adding points 
		// is not bad.   However we need to check if we have to recalculate the points every time.
		mTouchListener.clear();
		
		// Draw all the points as well as register the space that users can select.
		Collection<Interactable> interactables = mTarget.getInteractables();
		for (Interactable i: interactables) {
			i.onDrawSelf(getResources(), canvas);
			mTouchListener.registerInteractable(i.getBounds(getResources(), canvas), i);
		}
	}
	

	@Override
	public void onPhotoTap(View view, float x, float y) {
		Log.i(LOG_TAG, "Photo Tapped X: " + x + " Y: " + y);
	}

	@Override
	public void onMatrixChanged(RectF rect) {
		Log.i(LOG_TAG, "Bounds are TL: { " + rect.left + ", " + rect.top + 
				" } W: " + rect.width() + " H: " + rect.height());
	}

}
