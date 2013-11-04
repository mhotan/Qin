package com.aqt.qin.target;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;

import com.aqt.qin.TargetInformation;

/**
 * ImageView that presents the target to the user.  Allows the user to select content of the image. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageView extends ImageView {

	private final TargetInformation mInfo;
	
	/**
	 * Creates a target image view from a resource id.
	 * 
	 * @param id Resource ID of the target image
	 * @param context Context to create view with
	 * @param attrs attributes of the image
	 */
	public TargetImageView(int id, TargetInformation info, Context context) {
		super(context);
		if (id < 0) 
			throw new IllegalArgumentException("Illegal resource id for image: " + id);
		super.setImageResource(id);
		mInfo = info;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Use the information about the target to draw on the image with all the content.
	}
	
}
