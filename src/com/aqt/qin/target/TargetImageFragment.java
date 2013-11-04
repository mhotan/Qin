package com.aqt.qin.target;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aqt.qin.R;
import com.aqt.qin.util.DemoTarget;

/**
 * Fragment that handles the presentation of a target image.
 * I.E. Once the user was able to successfully target a 2 dimensional image, the application
 * will use this fragment to display the image the user identified.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageFragment extends Fragment {

	/**
	 * Listening activity or object using this fragment.
	 */
	private TargetImageListener mListener;

	private DemoTarget target;

	private FrameLayout mImageContainer;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Make sure owning activity implements correct interface
		try {
			mListener = (TargetImageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement " 
					+ getClass().getSimpleName() + ".TargetImageListener");
		}
	}

	@Override 
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_target_img,
				container, false);
		mImageContainer = (FrameLayout) view.findViewById(R.id.image_container);
		return view;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		DemoTarget target = mListener.getTarget();
		mImageContainer.addView(new TargetImageView(
				target.getResourceID(), target.getInfo(), getActivity()));
	}

	/**
	 * Interface that allows communication back to the activity.
	 * 
	 *  <br> Notifies when the user selects something on the image.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface TargetImageListener {

		/**
		 * Notifies listener that context within the image is selected 
		 */
		public void onContentSelected(/*Place content data structure in here*/);

		/**
		 * @return Target to present within this fragment.
		 */
		public DemoTarget getTarget();

	} 

}
