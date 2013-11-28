package org.risa.android;

import org.risa.android.data.DemoTarget;
import org.risa.android.util.DemoTargetManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aqt.qin.R;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.ScannerSession;

/**
 * Fragment that is used to display the screen preview for
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class RecognitionFragment extends Fragment implements ScannerSession.Listener {

	/**
	 * Handles both image and QRCode recognition.
	 */
	private int ScanOptions = Result.Type.IMAGE | Result.Type.QRCODE;

	/**
	 * Session that handles providing the preview image frames and 
	 * the image processing in the background.
	 */
	private ScannerSession mSession;

	/**
	 * The text view showing the result
	 */
	private TextView mResultTextView;

	/**
	 * Listener to notify when target is found.
	 */
	private FoundTargetListener mFoundListener;

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_recognize, container, false);


		// get the camera preview surface & result text view
		SurfaceView preview = (SurfaceView) view.findViewById(R.id.preview);

		// Create a scanner session
		try {
			mSession = new ScannerSession(getActivity(), this, preview);
		} catch (MoodstocksError e) {
			e.log();
		}

		// set session options
		mSession.setOptions(ScanOptions);
		mSession.noPartialMatching = true;
		
		// Text view that shows result.
		mResultTextView = (TextView) view.findViewById(R.id.scan_result);
		mResultTextView.setVisibility(View.INVISIBLE);

		return view;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);

		// Make sure owning activity implements correct interface
		try {
			mFoundListener = (FoundTargetListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement " 
					+ getClass().getSimpleName() + ".FoundTargetListener");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// close the scanner session
		mSession.close();
	}

	@Override
	public void onResume() {
		super.onResume();

		// start the scanner session
		mSession.resume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// pause the scanner session
		mSession.pause();
	}

	@Override
	public void onScanComplete(Result result) {
		if (result != null) {
			String uniqueName = result.getValue();
			mResultTextView.setText(String.format("Scan result: %s", uniqueName));
			mResultTextView.setVisibility(View.VISIBLE);
			
			DemoTarget target = DemoTargetManager.getInstance(
					getActivity().getApplicationContext()).getDemoTarget(uniqueName);
			if (target != null) {
				// Notify the activity
				mFoundListener.onFoundDemoTarget(target);
			} 
		}
	}

	@Override
	public void onScanFailed(MoodstocksError error) {
		mResultTextView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onApiSearchStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onApiSearchComplete(Result result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onApiSearchFailed(MoodstocksError e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Interface that any class planning to use this fragment should implement.
	 * If the activity does not implement this interface then 
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface FoundTargetListener {

		/**
		 * Notifies activity that a target was found.
		 * 
		 * @param target Target that was found
		 */
		public void onFoundDemoTarget(DemoTarget target);

	}

}
