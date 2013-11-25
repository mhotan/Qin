package org.risa.android.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Resource Image loader.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ResourceImageLoader implements ImageSource, Parcelable {

	/**
	 * Resource ID.
	 */
	private final int mResourceID;

	/**
	 * Creates an ImageLoader that is stored as a resource id.
	 * @param resourceId Resource ID of the image.
	 */
	public ResourceImageLoader(int resourceId) {
		mResourceID = resourceId;
	}

	/**
	 * Creates a Resource Image Loader from Parcel.
	 * 
	 * @param in parcel containing this.
	 */
	private ResourceImageLoader(Parcel in) {
		mResourceID = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mResourceID);
	}

	public static final Parcelable.Creator<ResourceImageLoader> CREATOR
	= new Parcelable.Creator<ResourceImageLoader>() {
		public ResourceImageLoader createFromParcel(Parcel in) {
			return new ResourceImageLoader(in);
		}

		public ResourceImageLoader[] newArray(int size) {
			return new ResourceImageLoader[size];
		}
	};

	@Override
	public void loadImage(Context context, ImageView view) {
		view.setImageResource(mResourceID);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		ResourceImageLoader r = (ResourceImageLoader) o;
		return mResourceID == r.mResourceID;
	}
	
	@Override
	public int hashCode() {
		return mResourceID;
	}

}
