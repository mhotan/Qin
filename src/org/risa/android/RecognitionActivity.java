package org.risa.android;

import org.risa.android.data.DemoTarget;
import org.risa.android.target.TargetActivity;
import org.risa.android.util.DemoTargetManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.aqt.qin.R;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.ScannerSession;

/**
 * Activity that handles the recognition of two dimensional images within a video frame.
 * 
 * <b>Currently this application utilizes moodstocks api to conduct image recognition with their cloud. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class RecognitionActivity extends Activity implements ScannerSession.Listener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognize);

		// get the camera preview surface & result text view
		SurfaceView preview = (SurfaceView) findViewById(R.id.preview);

		// Create a scanner session
		try {
			mSession = new ScannerSession(this, this, preview);
		} catch (MoodstocksError e) {
			e.log();
		}

		// set session options
		mSession.setOptions(ScanOptions);
		
		// Text view that shows result.
		mResultTextView = (TextView) findViewById(R.id.scan_result);
		mResultTextView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// close the scanner session
		mSession.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// start the scanner session
		mSession.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// pause the scanner session
		mSession.pause();
	}

	@Override
	public void onScanComplete(Result result) {
		if (result != null) {
			String uniqueName = result.getValue();
			
			// If we have already registered a demo target then launch the 
			DemoTarget target = DemoTargetManager.
					getInstance(getApplicationContext()).getDemoTarget(uniqueName);
			if (target != null) {
				Intent i = new Intent(this, TargetActivity.class);
				i.putExtra(TargetActivity.ARG_IMAGE_NAME, uniqueName);
				startActivity(i);
			} else {
				mResultTextView.setText(String.format("Scan result: %s", uniqueName));
				mResultTextView.setVisibility(View.VISIBLE);
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

}
