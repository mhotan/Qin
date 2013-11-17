package org.risa.android.target;

import org.risa.android.data.Target;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;


/**
 * ImageView that presents the target to the user.  Allows the user to select content of the image. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageView extends ImageView {

	/**
	 * The target to be drawn on this ImageView.
	 */
	private final Target mTarget;
	
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
		
		// Load the image on this view.
		mTarget.loadImage(this);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Use the information about the target to draw on the image with all the content.
	}
	
}
