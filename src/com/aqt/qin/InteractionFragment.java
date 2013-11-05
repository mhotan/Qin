package com.aqt.qin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Fragment View that presents options to user about what methods of interactions are available
 * 
 * <br> Currently the fragment has the capability to display options to use NFC, QR, or Video
 * <br> Note: every activity class that uses this, must implement InteractionFragment.InteractionListener
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class InteractionFragment extends Fragment {

	/**
	 * Listener for selecting interaction type
	 */
	private InteractionListener mListener;
	
	/**
	 * Buttons that initiates recognition activity.
	 */
	private ImageButton videoButton, nfcButton, qrButton;
	
	/**
	 * Boolean flag to check if device is compatible.
	 */
	private boolean mCompatible = false;
	
	/**
	 * Bundle argument that references if this device is Moodstocks compatible
	 */
	public final static String ARG_IS_COMPATIBLE = InteractionFragment.class.getSimpleName() + "_ARG_IS_COMPATIBLE";
	
	/**
	 * Classification from type of interaction.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public enum INTERACTION_TYPE { NFC, QR, VIDEO};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.interaction_view,
				container, false);
		
		// Reference video, nfc, qr buttons
		videoButton = (ImageButton) view.findViewById(R.id.videoiconButton);
		nfcButton = (ImageButton) view.findViewById(R.id.nfciconButton);
		qrButton = (ImageButton) view.findViewById(R.id.qriconButton);
		
		videoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onRequestedType(INTERACTION_TYPE.VIDEO);
			}
		});
		qrButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onRequestedType(INTERACTION_TYPE.QR);
			}
		});
		
		return view;
	}
	
	/**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if we already checked for compatibility
        if (mCompatible) return;
        
        mCompatible = getArguments() != null ? getArguments().getBoolean(ARG_IS_COMPATIBLE, false): false;
        if (!mCompatible) {
        	Log.w(getClass().getSimpleName(), "Device is not compatible");
        	return;
        }
        
        if (mCompatible) {
        	enableRecognitionButtons();
        } else {
        	disableRecognitionButtons();
        }
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof InteractionListener) {
			mListener = (InteractionListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet InteractionFragment.InteractionListener");
		}
	}
	
	/**
	 * Disable the buttons that initiates recognition 
	 */
	private void disableRecognitionButtons() {
		qrButton.setVisibility(View.GONE);
		videoButton.setVisibility(View.GONE);
	}
	
	/**
	 * Enables the buttons to allows user to recognize.
	 */
	private void enableRecognitionButtons() {
		qrButton.setVisibility(View.VISIBLE);
		videoButton.setVisibility(View.VISIBLE);
	}

	/**
	 * Listener that Activities must implement to handle the 
	 * interactions from the users.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface InteractionListener {

		/**
		 * Called when interaction is requested.
		 * 
		 * @param type type of interaction requested
		 */
		public void onRequestedType(INTERACTION_TYPE type);

	}
	

}
