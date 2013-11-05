package com.aqt.qin.util;

import com.aqt.qin.TargetInformation;

/**
 * Class that represents a identifiable target.  Just used for demo purposes at this point. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTarget {

	private final int mResourceID;
	private final TargetInformation mInfo;

	/**
	 * Create a demo target using an image resource in Android.
	 * 
	 * @param uniqueName Unique name known to moodstocks.
	 * @param resourceID Resource ID from reference resource I.E. R.raw.<name>
	 */
	public DemoTarget(String uniqueName, int resourceID) {
		this.mInfo = new TargetInformation(uniqueName);
		this.mResourceID = resourceID;
	}

	public DemoTarget(TargetInformation info, int resourceID) {
		if (info == null)
			throw new NullPointerException("DemoTarget(), Target Information cannot null");
		this.mInfo = info;
		this.mResourceID = resourceID;
	}
	
	/**
	 * @return Unique name of this target.
	 */
	public String getUniqueName() {
		return mInfo.getUniqueName();
	}

	/**
	 * @return Resource ID of this image
	 */
	public int getResourceID() {
		return mResourceID;
	}

	/**
	 * @return the information regarding this target.
	 */
	public TargetInformation getInfo() {
		return mInfo;
	}
}
