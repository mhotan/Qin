package org.risa.android.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.risa.android.data.DemoTarget;
import org.risa.android.data.Item;
import org.risa.android.data.ItemPoint;
import org.risa.android.data.PurchaseDetails;
import org.risa.android.data.RectangularDimension;
import org.risa.android.data.Vendor;

import android.content.Context;

import com.aqt.qin.R;

/**
 * Class that handles the storage of demo targets.  Statically add more target images in here 
 * if you want to add more to the demo.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DemoTargetManager {

	/**
	 * Complete collection of targets.
	 */
	private final Set<DemoTarget> mTargets;

	private static DemoTargetManager mInstance;

	private static List<ImageSource> mPenguinImages;

	private DemoTargetManager(Context ctx) {
		mTargets = new HashSet<DemoTarget>();

		// Add the Versace advertisement.
		String versace_uid = "versace_ad_1";
		
		// TODO: Add content to info
		DemoTarget t = new DemoTarget("Versace", versace_uid, ctx.getResources(), R.raw.versace_ad);
		Item item = getItem("Dress", t.getDimensions(), 400, 372, 49.99f);
		addPenguinImages(item);
		t.addItem(item);
		mTargets.add(t);
		
		// TODO add more targets.

	}

	public static DemoTargetManager getInstance(Context ctx) {
		if (mInstance == null)
			mInstance = new DemoTargetManager(ctx);
		return mInstance;
	} 

	/**
	 * Attempts to get a preregistered DemoTarget by the unique name
	 * 
	 * @param uniqueId Unique ID to look for
	 * @return null if id is not found, DemoTarget with same unique name otherwise.
	 */
	public DemoTarget getDemoTarget(String uniqueId) {
		for (DemoTarget target : mTargets)
			if (target.getUID().equals(uniqueId))
				return target;
		return null;
	}
	
	
	private static Item getItem(String itemName, 
			RectangularDimension targetDimension, 
			int x, int y,
			float lowestCost) {
		ItemPoint item = new ItemPoint(itemName, targetDimension, x, y);
		float[] prices = new float[3];
		for (int i = 0; i < prices.length; ++i) {
			prices[i] = (float) (lowestCost + Math.random() * 20.0f);
		}
		
		item.addPurchaseDetails(new PurchaseDetails(Vendor.MACYS, prices[0], Vendor.MACYS.getURL()));
		item.addPurchaseDetails(new PurchaseDetails(Vendor.NORDSTROMS, prices[1], Vendor.NORDSTROMS.getURL()));
		item.addPurchaseDetails(new PurchaseDetails(Vendor.AMAZON, prices[2], Vendor.AMAZON.getURL()));
		
		return item;
	}
	
	private static void addPenguinImages(Item item) {
		if (mPenguinImages == null) {
			mPenguinImages = new ArrayList<ImageSource>();
			mPenguinImages.add(new ResourceImageLoader(R.raw.penguin1));
			mPenguinImages.add(new ResourceImageLoader(R.raw.penguin2));
			mPenguinImages.add(new ResourceImageLoader(R.raw.penguin3));
			mPenguinImages.add(new ResourceImageLoader(R.raw.penguin4));
		}
		for (ImageSource s: mPenguinImages) 
			item.addImage(s);
	}

}
