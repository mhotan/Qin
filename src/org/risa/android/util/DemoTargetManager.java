package org.risa.android.util;

import java.util.HashSet;
import java.util.Set;

import org.risa.android.data.DemoTarget;
import org.risa.android.data.FocalPoint;

import android.content.Context;

import com.aqt.qin.R;

/**
 * Class that handles the storage of demo targets.  Statically add more target images in here 
 * if you want to add more to the demo.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTargetManager {

	/**
	 * Complete collection of targets.
	 */
	private final Set<DemoTarget> mTargets;

	private static DemoTargetManager mInstance;


	private DemoTargetManager(Context ctx) {
		mTargets = new HashSet<DemoTarget>();

		// Add the Versace advertisement.
		String versace_uid = "versace_ad_1";
		
		// TODO: Add content to info
		DemoTarget t = new DemoTarget(versace_uid, ctx.getResources(), R.raw.versace_ad);
		t.addInteractable(new FocalPoint(t.getDimensions(), 400, 372));
		t.addInteractable(new FocalPoint(t.getDimensions(), 69, 575));
		t.addInteractable(new FocalPoint(t.getDimensions(), 81, 191));
		t.addInteractable(new FocalPoint(t.getDimensions(), 354, 144));
		mTargets.add(t);
		
		// TODO add more targets.

	}

	public static DemoTargetManager getInstance(Context ctx) {
		if (mInstance == null)
			mInstance = new DemoTargetManager(ctx);
		return mInstance;
	} 

	/**
	 * Attempts to get a preregistered DemoTarget by the unique name
	 * 
	 * @param uniqueId Unique ID to look for
	 * @return null if id is not found, DemoTarget with same unique name otherwise.
	 */
	public DemoTarget getDemoTarget(String uniqueId) {
		for (DemoTarget target : mTargets)
			if (target.getUID().equals(uniqueId))
				return target;
		return null;
	}

}
