package org.risa.android.util;

import java.util.HashSet;
import java.util.Set;

import org.risa.android.data.DemoTarget;

import com.aqt.qin.R;

/**
 * Class that handles the storage of demo targets.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTargetManager {

	/**
	 * Complete collection of targets.
	 */
	private static Set<DemoTarget> mTargets = new HashSet<DemoTarget>();
	
	static {
		// Add the Versace advertisement.
		String versace_uid = "versace_ad_1";
		// TODO: Add content to info
		mTargets.add(new DemoTarget(versace_uid, R.raw.versace_ad));
		
		// TODO add more targets.
	}
	
	/**
	 * Attempts to get a preregistered DemoTarget by the unique name
	 * 
	 * @param uniqueId Unique ID to look for
	 * @return null if id is not found, DemoTarget with same unique name otherwise.
	 */
	public static DemoTarget getDemoTarget(String uniqueId) {
		for (DemoTarget target : mTargets)
			if (target.getUID().equals(uniqueId))
				return target;
		return null;
	}
	
}
