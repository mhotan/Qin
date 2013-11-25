package org.risa.android.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the internal content of a particular interactable item
 * or target.  Content is intended to be flexible allowing 
 * clients to implement what they wish.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public abstract class Content implements Parcelable {

	/**
	 * Represented name of this content.  Generally all content should
	 * have name but in some cases it does not.
	 */
	private String mName;
	
	
	
	/**
	 * Set the name associated with this content.
	 * 
	 * @param name Name to set content to.
	 */
	protected void setName(String name) {
		if (name == null)
			throw new NullPointerException(Content.class.getSimpleName() + ".setName() Illegal name input: null");
		mName = name;
	}
	
	/**
	 * Returns the name associated with this content if it exists
	 * 
	 * @return Name of the Content, null if no name exists.
	 */
	public String getName() {
		return mName;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public abstract void writeToParcel(Parcel dest, int flags);
}
