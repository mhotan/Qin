package org.risa.android.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.risa.android.util.ImageSource;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * An Item represents a single item within an advertisement images.  Items are generally
 * found within targets and are selectable.  Subclasses control how these
 * items are represented on the image.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public abstract class Item implements Interactable {

	/**
	 * Name of the item within the advertisement.
	 */
	private final String mName;
	
	/**
	 * List of all purchase details with this item.
	 */
	private final List<PurchaseDetails> mPurchaseDetails;
	
	/**
	 * List of all images with this item.
	 */
	private final List<ImageSource> mImages;

	/**
	 * Details about this particular event.
	 */
	private String mDetails;
	
	/**
	 * Creates an item without any purchase capability or images.
	 * 
	 * @param name Name of this item.
	 */
	public Item(String name) {
		this(name, null, null);
	}
	
	/**
	 * Creates an item with associated with purchase details and images.
	 * 
	 * @param name Name of this item.
	 * @param purchDetails Purchase Details of this item.
	 * @param images Images to attribute to this item.
	 */
	public Item(String name, List<PurchaseDetails> purchDetails, List<ImageSource> images) {
		if (name == null)
			throw new NullPointerException(Item.class.getSimpleName() + "() Illegal null Name");
		mName = name;
		mPurchaseDetails = purchDetails == null ? new ArrayList<PurchaseDetails>() : 
			new ArrayList<PurchaseDetails>(purchDetails);
		mImages = images == null ? new ArrayList<ImageSource>() : 
			new ArrayList<ImageSource>(images);
	}
	
	/**
	 * @return Name of this item.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Adds an image to this item.
	 * 
	 * @param image Source of this image to load.
	 */
	public void addImage(ImageSource image) {
		if (mImages.contains(image)) return;
		if (image == null) return;
		mImages.add(image);
	}
	
	/**
	 * Attempts to remove image from this.
	 * 
	 * @param image Removes image from this
	 * @return whether the image was found in this
	 */
	public boolean removeImage(ImageSource image) {
		return mImages.remove(image);
	}
	
	/**
	 * @return List of Images that represent this.
	 */
	public List<ImageSource> getImages() {
		List<ImageSource> list = new ArrayList<ImageSource>(mImages);
		return list;
	}
	
	/**
	 * Adds purchase details to this item.
	 * 
	 * @param details Details of the purchase
	 */
	public void addPurchaseDetails(PurchaseDetails details) {
		if (details == null) return;
		if (mPurchaseDetails.contains(details)) return;
		mPurchaseDetails.add(details);
	}
	
	/**
	 * Attempts to remove purchase details from this.
	 * 
	 * @param details Purchase details of this purchase.
	 * @return Whether the purchase details was found in this.
	 */
	public boolean removePurchaseDetails(PurchaseDetails details) {
		return mPurchaseDetails.remove(details);
	}
	
	/**
	 * @return Return list of purchase details in order of lowest cost.
	 */
	public List<PurchaseDetails> getPurchaseDetails() {
		List<PurchaseDetails> details = new ArrayList<PurchaseDetails>(mPurchaseDetails);
		Collections.sort(details, new LowCostComparator());
		return details;
	}
	
	/**
	 * Returns the lowest cost of the item.
	 * @return Lowest cost Purchase Details.
	 */
	public PurchaseDetails getLowestCost() {
		List<PurchaseDetails> details = getPurchaseDetails();
		if (details.isEmpty()) return null;
		return details.get(0);
	}
	
	/**
	 * SEt the details of this item.
	 * 
	 * @param details Details of this item.
	 */
	public void setDetails(String details) {
		mDetails = details;
	}
	
	/**
	 * @return Empty string when there are no details, details otherwise.
	 */
	public String getDetails() {
		if (mDetails == null) return "";
		return mDetails;
	}
	
	@Override
	public abstract void onDrawSelf(Resources res, Canvas canvas);

	@Override
	public abstract Rect getBounds(Resources res, Canvas canvas);
	
	/**
	 * Used to sort Purchase Details by lowest cost.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	private static class LowCostComparator implements Comparator<PurchaseDetails> {

		@Override
		public int compare(PurchaseDetails lhs, PurchaseDetails rhs) {
			float leftCost = lhs.getCost();
			float rightCost = rhs.getCost();
			if (Math.abs(leftCost-rightCost) < .001) return 0;
			else if (leftCost < rightCost) return -1;
			else return 1;
		}
	} 
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!getClass().equals(o.getClass())) return false;
		Item i = (Item) o;
		return mName.equals(i.mName) && mImages.equals(i.mImages) 
				&& mPurchaseDetails.equals(i.mPurchaseDetails);
	}
	
	@Override
	public int hashCode() {
		return 41 * mName.hashCode() + 3 * mImages.hashCode() + 11 * mPurchaseDetails.hashCode();
	}
	
}
