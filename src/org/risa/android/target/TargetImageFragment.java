package org.risa.android.target;

import org.risa.android.data.Interactable;
import org.risa.android.data.Target;
import org.risa.android.target.TargetTouchListener.OnInteractionOccurredListener;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aqt.qin.R;

/**
 * Fragment that handles the presentation of a target image.
 * I.E. Once the user was able to successfully target a 2 dimensional image, the application
 * will use this fragment to display the image the user identified.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetImageFragment extends Fragment implements 
OnInteractionOccurredListener {

	
	/**
	 * Listening activity or object using this fragment.
	 */
	private TargetImageListener mListener;

	/**
	 * Container that holds Image View
	 */
	private FrameLayout mImageContainer;

	/**
	 * PhotoView attacher that controls view manipulation.
	 */
	private PhotoViewAttacher mAttacher;
	
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
		Target target = mListener.getTarget();
		mImageContainer.removeAllViews();
		ImageView imgView = new TargetImageView(target, getActivity(), this);
		mImageContainer.addView(imgView);		
	}

	/**
	 * Interface that allows communication back to the activity.
	 * 
	 *  <br> Notifies when the user selects something on the image.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface TargetImageListener extends OnInteractionOccurredListener {

		/**
		 * @return Target to present within this fragment.
		 */
		public Target getTarget();
		
	}

	@Override
	public void onInteractableSelected(Interactable interactable) {
		mListener.onInteractableSelected(interactable);
	}

	@Override
	public void onClearSelected() {
		mListener.onClearSelected();
	}


}
