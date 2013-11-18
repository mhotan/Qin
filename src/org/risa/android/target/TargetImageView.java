package org.risa.android.target;

import java.util.Collection;

import org.risa.android.data.Interactable;
import org.risa.android.data.RectangularDimension;
import org.risa.android.data.Target;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * ImageView that presents the target to the user.  Allows the user to select content of the image. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageView extends ImageView {

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
	 * Creates a target image view from a resource id.
	 * 
	 * @param id Resource ID of the target image
	 * @param context Context to create view with
	 * @param attrs attributes of the image
	 */
	public TargetImageView(Target target, Context context) {
		super(context);
		mTarget = target;

		// Set the scale type so all images are fully confined within the canvas
		// TODO M
		setScaleType(ImageView.ScaleType.CENTER_INSIDE);

		// Load the image on this view.
		mTarget.loadImage(this);
		
		mCtx = context;
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(mCtx, "X: " + event.getX() + " Y: " + event.getY(), Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		
		Log.i(getClass().getSimpleName(), "Dimensions of the screen W: " 
				+ getWidth() + " H: " + getHeight());
		
		Log.i(getClass().getSimpleName(), "Dimensions of the canvas W: " 
				+ canvas.getWidth() + " H: " + canvas.getHeight());
		
		
		Collection<Interactable> interactables = mTarget.getInteractables();
		for (Interactable i: interactables) {
			i.onDrawSelf(getResources(), canvas);
		}
	}

}
