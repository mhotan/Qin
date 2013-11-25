package org.risa.android.target;

import org.risa.android.data.Interactable;
import org.risa.android.data.Target;
import org.risa.android.target.TargetImageFragment.TargetImageListener;
import org.risa.android.target.TargetInformationFragment.InformationListener;
import org.risa.android.util.DemoTargetManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aqt.qin.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

/**
 * This manages the views that present the target to the users.
 * Depending on the device and screen size this activity manages its layout
 * by providing the most information to the user it the simplest way.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetActivity extends FragmentActivity implements 
TargetImageListener, InformationListener, PanelSlideListener {

	/**
	 * Argument key for ID of image.
	 */
	public static final String ARG_IMAGE_NAME = TargetActivity.class.getSimpleName() + "_ARG_IMAGE_ID";

	/**
	 * This is the amount of exposed pixels in the view that
	 * slides out and shows the user content
	 */
	private static final int PANEL_HEIGHT = 10;
	
	/**
	 * Amount of space covered by the panel when it is expanded
	 */
	private static final float EXPANDED_COVERAGE = .25f;
	
	/**
	 * The Target to Draw with this activity.
	 */
	private Target mTarget;

	/**
	 * Fragment manager that handles exchanging fragment.
	 */
	private FragmentManager mFragManager;
	
	/**
	 * The Target information fragment
	 */
	private TargetInformationFragment mInfoFrag;
	
	/**
	 * The target image fragment to present
	 */
	private TargetImageFragment mImageFrag;
	
	/**
	 * For Vertical orientation use this view to slide up.
	 */
	private SlidingUpPanelLayout mSlideUpPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target);

		TargetImageFragment frag = (TargetImageFragment) getSupportFragmentManager().findFragmentById(R.id.target_image_fragment);
		if (frag == null || !frag.isInLayout()) 
			throw new IllegalStateException("TargetActivity, can't find ");

		String imageName = null;
		Intent i = getIntent();
		if (i != null) {
			imageName = i.getStringExtra(ARG_IMAGE_NAME);
			mTarget = DemoTargetManager.getInstance(getApplicationContext()).getDemoTarget(imageName);
		}

		mFragManager = getSupportFragmentManager();

		// Find a reference to the fragments
		mImageFrag = (TargetImageFragment) mFragManager.findFragmentById(R.id.target_image_fragment);
		mInfoFrag = (TargetInformationFragment) mFragManager.findFragmentById(R.id.target_information_fragment);
		mSlideUpPanel = (SlidingUpPanelLayout) findViewById(R.id.slider);
		
		// The sliding ability makes it to obstrusive and confusing to for users to 
		mSlideUpPanel.setSlidingEnabled(false);
		mSlideUpPanel.setPanelSlideListener(this);
		mSlideUpPanel.setPanelHeight(PANEL_HEIGHT);
		mSlideUpPanel.setAnchorPoint(EXPANDED_COVERAGE);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		// TODO Save State of Target
		savedInstanceState.putString(ARG_IMAGE_NAME, mTarget.getUID());

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can restore the view hierarchy
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState == null) return;

		// TODO Restore state members from saved instance
		String uid = savedInstanceState.getString(ARG_IMAGE_NAME);
		mTarget = DemoTargetManager.getInstance(getApplicationContext()).getDemoTarget(uid);
	}

	@Override
	public Target getTarget() {
		if (mTarget == null) 
			throw new IllegalStateException("Unable to find target");
		return mTarget;
	}

	@Override
	public void onInteractableSelected(Interactable interactable) {
		// This notification is when the user selects an item within an
		// image to look at in more detail.  Therefore we have to present them with
		// the correct view.

		Toast.makeText(this, "Selected " + interactable.toString(), Toast.LENGTH_SHORT).show();

		// Set the interactable content to display
		mInfoFrag.setInteractableToDisplay(interactable);
		mSlideUpPanel.expandPane(EXPANDED_COVERAGE);
	}

	@Override
	public void onClearSelected() {
		Toast.makeText(this, "Clear Selected", Toast.LENGTH_SHORT).show();
		mSlideUpPanel.collapsePane();
	}

	@Override
	public void onBackPressed() {
		// Use the fragment manager to get the fragment we are looking for
		if (mSlideUpPanel.isExpanded()) {
			onClearSelected();
		} else {
			super.onBackPressed();
		}
	}
	
	// Slide Panel Listener

	@Override
	public void onPanelAnchored(View arg0) {
		// Do nothing as of right now
	}

	@Override
	public void onPanelCollapsed(View arg0) {
		mSlideUpPanel.setSlidingEnabled(false);
	}

	@Override
	public void onPanelExpanded(View arg0) {
		// Do nothing as of right now		
	}

	@Override
	public void onPanelSlide(View arg0, float arg1) {
		// Do nothing as of right now
	}

}
