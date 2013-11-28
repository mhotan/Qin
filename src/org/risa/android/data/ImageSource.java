package org.risa.android.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is class that manages and remembers where images originate from.
 * Images can originate at different locations whether it be local resources, local disk, cloud storage.
 * This class helps manage images that are referred to in the data context to where they physically are
 * in actuality.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ImageSource implements Parcelable {

	/**
	 * Right now our application has access to all the 
	 * 
	 * We will store images in the following essential locations
	 * 
	 * 1. Local res/raw folder for demo purposes
	 * 2. Local internal storage for long term storage.  This can include any image that the users save
	 * 3. Cloud based reference.  Standard absolute storage of an image utilized by our application. 
	 */
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Standard source types for image representation.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	private enum SOURCE_TYPE {
		RESOURCE(0), INTERNAL_STORAGE(1), CLOUD(2)
		;
		
		private final int mIndex;
		
		private SOURCE_TYPE(int i) {
			mIndex = i;
		}
		
		public int getIndex() {
			return mIndex;
		}
		
	}
}
