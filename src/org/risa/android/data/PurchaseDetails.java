package org.risa.android.data;

import java.text.NumberFormat;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the details that are required to purchase a single item.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class PurchaseDetails implements Parcelable {

	private final Vendor mVendor;
	private final float mCost;
	private final Uri mUri;

	/**
	 * Creates an instant of details of how to purchase a single item.
	 * 
	 * @param vendor Vendor that is providing the ability to purchase
	 * @param cost Cost of the item
	 * @param uri Uri that points to the specific web page of the item.
	 */
	public PurchaseDetails(Vendor vendor, float cost, Uri uri) {
		mVendor = vendor;
		mCost = cost;
		mUri = uri;
	}

	public PurchaseDetails(Parcel in) {
		mVendor = in.readParcelable(Vendor.class.getClassLoader());
		mCost = in.readFloat();
		mUri = in.readParcelable(Uri.class.getClassLoader());
	}

	/**
	 * Returns the vendor for this purchase
	 * 
	 * @return Vendor to return.
	 */
	public Vendor getVendor() {
		return mVendor;
	}

	/**
	 * Return the cost of this item
	 * 
	 * @return Cost of this item
	 */
	public float getCost() {
		return mCost;
	}

	/**
	 * Returns the human readable string value.
	 * 
	 * @return Human readable cost.
	 */
	public String getReadableCost() {
		NumberFormat format = NumberFormat.getCurrencyInstance();
		return format.format(mCost);
	}

	public Uri getUri() {
		return mUri;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		PurchaseDetails p = (PurchaseDetails) o;
		boolean costEquals = Math.abs( p.mCost - mCost) < .001;
		return mVendor.equals(p.mVendor) && mUri.equals(p.mUri) && costEquals;
	}

	public int hashCode() {
		return mVendor.hashCode() + 3 * mUri.hashCode() + ((int)mCost) * 11;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mVendor, 0);
		dest.writeFloat(mCost);
		dest.writeParcelable(mUri, 0);
	}

	public static final Parcelable.Creator<PurchaseDetails> CREATOR
	= new Parcelable.Creator<PurchaseDetails>() {
		public PurchaseDetails createFromParcel(Parcel in) {
			return new PurchaseDetails(in);
		}

		public PurchaseDetails[] newArray(int size) {
			return new PurchaseDetails[size];
		}
	};
}
