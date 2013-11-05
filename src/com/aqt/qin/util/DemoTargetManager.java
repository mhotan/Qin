package com.aqt.qin.util;

import java.util.HashSet;
import java.util.Set;

import com.aqt.qin.R;
import com.aqt.qin.TargetInformation;

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
		TargetInformation info = new TargetInformation(versace_uid);
		// TODO: Add content to info
		mTargets.add(new DemoTarget(info, R.raw.versace_ad));
		
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
			if (target.getUniqueName().equals(uniqueId))
				return target;
		return null;
	}
	
}
