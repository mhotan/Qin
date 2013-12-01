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
import org.risa.android.data.Target;
import org.risa.android.data.Vendor;

import android.content.Context;
import android.content.res.Resources;

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
	private final Set<Target> mTargets;

	private static DemoTargetManager mInstance;

	private static List<ImageSource> mPenguinImages;


	private static DemoTarget SCARLLET, LEBRON;

	private DemoTargetManager(Context ctx) {
		mTargets = new HashSet<Target>();
		mTargets.add(getVersace(ctx.getResources()));
		mTargets.add(getScarlett(ctx.getResources()));
		mTargets.add(getLebron(ctx.getResources()));
	}

	private static Target getVersace(Resources res) {
		// Add the Versace advertisement.
		String versace_uid = "versace_ad_1";

		// TODO: Add content to info
		DemoTarget t = new DemoTarget("Versace", versace_uid, res, R.raw.versace_ad);

		// Add the Versace Dress
		Item item = getItem("Dress", t.getDimensions(), 400, 372, 149.99f);
		addPenguinImages(item);
		t.addItem(item);

		item = getItem("Tie", t.getDimensions(), 243, 253, 49.99f);
		addPenguinImages(item);
		t.addItem(item);

		item = getItem("Suit", t.getDimensions(), 156, 264, 349.99f);
		addPenguinImages(item);
		t.addItem(item);

		item = getItem("Belt", t.getDimensions(), 289, 454, 49.99f);
		addPenguinImages(item);
		t.addItem(item);

		return t;
	}

	/**
	 * @return Returns Scarllet Johansen advertisement.
	 */
	private static Target getScarlett(Resources res) {
		if (SCARLLET != null) return SCARLLET;

		// Create the initial target
		SCARLLET = new DemoTarget("Scarllet wearing Victoria Beckham", "scarllet", res, R.raw.scarllet);

		// Add the Dress 
		Item item = getItem("Victoria Beckham Classic White Dress", SCARLLET.getDimensions(), 295, 242, 499.99f);
		item.addImage(new ResourceImageLoader(R.raw.dress1));
		item.addImage(new ResourceImageLoader(R.raw.dress2));
		item.setDetails("Scarllet showing off Victoria Beckham's sexy white dress in Febuary 2013 UK Elle magazine");
		SCARLLET.addItem(item);

		// Add the ring 337 358
		item = getItem("Diamond Stackable Jagged Knuckle Ring", SCARLLET.getDimensions(), 337, 358, 1199.99f);
		item.addImage(new ResourceImageLoader(R.raw.ring1));
		item.addImage(new ResourceImageLoader(R.raw.ring2));
		item.addImage(new ResourceImageLoader(R.raw.ring3));
		item.addImage(new ResourceImageLoader(R.raw.ring4));
		item.setDetails("Fancy ring");
		SCARLLET.addItem(item);

		// Add the shoes 264 700
		item = getItem("Lace up White Boots", SCARLLET.getDimensions(), 264, 700, 299.99f);
		item.addImage(new ResourceImageLoader(R.raw.whiteshoe1));
		item.setDetails("White lace up open toe boots");
		SCARLLET.addItem(item);

		return SCARLLET;
	}

	private static Target getLebron(Resources res) {
		if (LEBRON != null) return LEBRON;
		LEBRON = new DemoTarget("The King's new kicks", "lebron", res, R.raw.lebron);

		// 313 300  Add the basketball shoes
		Item item = getItem("Lebron 11", LEBRON.getDimensions(), 313, 300, 129.99f);
		item.addImage(new ResourceImageLoader(R.raw.lebron11_1));
		item.addImage(new ResourceImageLoader(R.raw.lebron11_2));
		item.addImage(new ResourceImageLoader(R.raw.lebron11_3));
		item.setDetails("Lebron James newest basketball shoes.  looks like a pair of shoes for some colorful robots");
		LEBRON.addItem(item);

		// 323 29 Add the headband
		item = getItem("The King headband", LEBRON.getDimensions(), 323, 29, 19.99f);
		item.addImage(new ResourceImageLoader(R.raw.headband1));
		item.addImage(new ResourceImageLoader(R.raw.headband2));
		item.addImage(new ResourceImageLoader(R.raw.headband3));
		item.addImage(new ResourceImageLoader(R.raw.headband4));
		item.setDetails("Expensive piece of cloth for wiping sweat from your head.");
		LEBRON.addItem(item);
		
		return LEBRON;
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
	public Target getDemoTarget(String uniqueId) {
		for (Target target : mTargets)
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
