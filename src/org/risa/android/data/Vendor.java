package org.risa.android.data;

import java.util.HashSet;
import java.util.Set;

import org.risa.android.util.ImageSource;
import org.risa.android.util.ResourceImageLoader;

import com.aqt.qin.R;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a specific vendor or provider of a item.  
 * For the purposes of a demo there have been a couple defaulted 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class Vendor implements Parcelable {

	/**
	 * Name of this Vendor
	 */
	private String mName;

	/**
	 * Uri of this particular 
	 */
	private Uri mURL;
	
	/**
	 * Logo of the vendor
	 */
	private ImageSource mLogo;

	public static final Vendor MACYS = new Vendor("Macy's", "http://www.macys.com/");
	public static final Vendor NORDSTROMS = new Vendor("Nordstrom", "http://shop.nordstrom.com/");
	public static final Vendor AMAZON = new Vendor("Amazon", "http://www.amazon.com/");
	private static final Set<Vendor> VENDORS = new HashSet<Vendor>();
	static {
		MACYS.setLogo(new ResourceImageLoader(R.drawable.ic_macys_logo));
		NORDSTROMS.setLogo(new ResourceImageLoader(R.drawable.ic_nordstrom_logo));
		AMAZON.setLogo(new ResourceImageLoader(R.drawable.ic_amazon_logo));
		VENDORS.add(MACYS);
		VENDORS.add(NORDSTROMS);
		VENDORS.add(AMAZON);
	}
	
	/**
	 * Return the vendor with the same name.  Case insensitive.
	 * 
	 * @param name Name of vendor to find
	 * @return Vendor with the same name as argument
	 */
	public static Vendor valueOf(String name) {
		for (Vendor vendor: VENDORS) {
			if (vendor.mName.toLowerCase().equals(name.toLowerCase()))
				return vendor;
		}
		return null;
	}
	
	/**
	 * Return the vendor with the same name.  Case insensitive.
	 * 
	 * @param name Name of vendor to find
	 * @return Vendor with the same name as argument
	 */
	public static Vendor valueOf(String name, String url) {
		Vendor v = new Vendor(name, url);
		if (!VENDORS.contains(v))
			VENDORS.add(v);
		return v;
	}
	
	/**
	 * Creates a vendor with this name and url.
	 * 
	 * @param name Human readable name of the Vendor.
	 * @param url String representation of the home domain of this vendor
	 */
	private Vendor(String name, String url) {
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Illegal name of Vendor: " + name);
		if (url == null || url.isEmpty())
			throw new IllegalArgumentException("Illegal name of Vendor: " + url);
		// TODO Check for valid URL.
		mName = name;
		mURL = Uri.parse(url);
	}

	private Vendor(Parcel in) {
		mName = in.readString();
		mURL = in.readParcelable(Uri.class.getClassLoader());
	}

	/**
	 * Sets the logo of this item. Null will remove any existing image source.
	 * 
	 * @param source Source of the image of this vendor
	 */
	public void setLogo(ImageSource source) {
		mLogo = source;
	}
	
	/**
	 * Returns any saved logo, or image source.
	 * 
	 * @return The logo, or null if no logo is found.
	 */
	public ImageSource getLogo() {
		return mLogo;
	}
	
	/**
	 * @return Returns the name of this Vendor.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @return Returns the URL of this Vendor
	 */
	public Uri getURL() {
		return mURL;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeParcelable(mURL, 0);
	}

	public static final Parcelable.Creator<Vendor> CREATOR
	= new Parcelable.Creator<Vendor>() {
		public Vendor createFromParcel(Parcel in) {
			return new Vendor(in);
		}

		public Vendor[] newArray(int size) {
			return new Vendor[size];
		}
	};
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		Vendor v = (Vendor)o;
		return v.mName.equals(mName) && v.mURL.equals(mURL);
	}

	public int hashCode() {
		return mName.hashCode() * 3 + mURL.hashCode() * 7;
	}
}
