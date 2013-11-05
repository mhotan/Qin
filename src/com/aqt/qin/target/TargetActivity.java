package com.aqt.qin.target;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.aqt.qin.R;
import com.aqt.qin.target.TargetImageFragment.TargetImageListener;
import com.aqt.qin.util.DemoTarget;
import com.aqt.qin.util.DemoTargetManager;

/**
 * This manages the views that present the target to the users.
 * Depending on the device and screen size this activity manages its layout
 * by providing the most information to the user it the simplest way.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetActivity extends FragmentActivity implements TargetImageListener {

	/**
	 * Argument key for ID of image.
	 */
	public static final String ARG_IMAGE_NAME = TargetActivity.class.getSimpleName() + "_ARG_IMAGE_ID";
	
	private DemoTarget mTarget;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target);
		
		TargetImageFragment frag = (TargetImageFragment) getSupportFragmentManager().findFragmentById(R.id.target_image_fragment);
		if (frag == null || !frag.isInLayout()) 
			throw new IllegalStateException("TargetActivity, can't find ");
	
		String imageName = null;
		Intent i = getIntent();
		if (i == null) {
			if (savedInstanceState == null || savedInstanceState.getString(ARG_IMAGE_NAME) != null) {
				imageName = savedInstanceState.getString(ARG_IMAGE_NAME);
			} 
		} else {
			imageName = i.getStringExtra(ARG_IMAGE_NAME);
		}
		mTarget = DemoTargetManager.getDemoTarget(imageName);
		if (mTarget == null) 
			throw new IllegalArgumentException("Unable to find target named " + imageName);
	}

	@Override
	public void onContentSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DemoTarget getTarget() {
		return mTarget;
	}
	
	
	
}
