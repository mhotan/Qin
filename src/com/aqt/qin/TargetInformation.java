package com.aqt.qin;

import com.aqt.qin.util.DemoTarget;

/**
 * This is a general container class for information that pertains
 * to information about a target. 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetInformation {

	/*
	 * TODO: Make this data structure compatible with back cloud data servers.
	 */
	
	private final String mUID;
	
	/**
	 * Creates a basic target information 
	 * 
	 * @param uniqueName Unique name that will be used as ID for this target.
	 */
	public TargetInformation(String uniqueName) {
		if (uniqueName == null || uniqueName.isEmpty())
			throw new IllegalArgumentException("Illegal unique ID: \"" + uniqueName + "\"");
		mUID = uniqueName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		TargetInformation d = (TargetInformation) o;
		return d.mUID.equals(mUID);
	}

	@Override
	public int hashCode() {
		return mUID.hashCode() * 3;
	}

	/**
	 * @return Unique ID/Name of this target.
	 */
	public String getUniqueName() {
		return mUID;
	}
	
}
