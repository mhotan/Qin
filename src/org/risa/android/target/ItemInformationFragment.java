package org.risa.android.target;

import java.util.ArrayList;
import java.util.List;

import org.risa.android.data.Item;
import org.risa.android.data.PurchaseDetails;
import org.risa.android.data.Vendor;
import org.risa.android.util.FX;
import org.risa.android.util.ImageSource;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aqt.qin.R;

/**
 * Class that displays the information of a particular item inside a target.
 *  
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ItemInformationFragment extends Fragment implements OnClickListener {

	public static final String KEY_INDEX = ItemInformationFragment.class.getName() + "_INTERACTABLE";

	//////////////////////////////////////////////////////////////////
	// Static fields used just for the demo.
	//////////////////////////////////////////////////////////////////

	private static final int WINNERS_RANGE = 30;
	private static final int WINNERS_MIN = 10;

	//////////////////////////////////////////////////////////////////
	// UI elements
	//////////////////////////////////////////////////////////////////

	/**
	 * Name of the item.
	 */
	private TextView mNameView, mLowestPriceView, mWinnersView, mDetails;

	/**
	 * Container for tall the items
	 */
	private LinearLayout mContainer;
	
	/**
	 * Button that asks for information about this item.
	 */
	private ImageButton mInfoButton;

	/**
	 * Gallery of images that pertain this specific item.
	 */
	private Gallery mGallery;

	/**
	 * Position of this item within the Target
	 */
	private int mPosition;

	/**
	 * Callback to get item for display.
	 */
	private ItemInteractionListener mCallback;

	@Override 
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_information_fragment,
				container, false);

		mNameView = (TextView) view.findViewById(R.id.name_label);
		mLowestPriceView = (TextView) view.findViewById(R.id.lowest_price_label);
		mWinnersView = (TextView) view.findViewById(R.id.winners_label);
		mInfoButton = (ImageButton) view.findViewById(R.id.info_button);
		mDetails = (TextView) view.findViewById(R.id.item_details);
		mGallery = (Gallery) view.findViewById(R.id.image_gallery);
		mContainer = (LinearLayout) view.findViewById(R.id.scroll_container);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (ItemInteractionListener) getParentFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(mCallback.getClass().getSimpleName() + " must implement " 
					+ getClass().getSimpleName() + "." + ItemInteractionListener.class.getSimpleName());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_INDEX, mPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mPosition = savedInstanceState.getInt(KEY_INDEX, 0);
		} else {
			mPosition = getArguments().getInt(KEY_INDEX, 0);
		}
		Item item = mCallback.getItem(mPosition);
		drawItem(item);
	}

	/**
	 * Builds this view around this item.
	 * 
	 * @param item Item to draw
	 */
	private void drawItem(Item item) {

		// Set the name of the item
		mNameView.setText(item.getName());
		Resources res = getActivity().getResources();
		String cost = item.getLowestCost().getReadableCost();
		mLowestPriceView.setText(String.format(res.getString(R.string.label_lowest_cost), cost));
		int winners = getRandomAmountOfWinners();
		mWinnersView.setText(String.format(res.getString(R.string.label_num_winners), winners));

		// Establish the images for this adapter.
		List<ImageSource> images = item.getImages();
		if (images.isEmpty()) {
			mGallery.setVisibility(View.GONE);
		} else {
			ImageAdapter adapter = new ImageAdapter(getActivity(), item.getImages());
			mGallery.setAdapter(adapter);
		}
		
		// Add the vendors
		List<PurchaseDetails> details = item.getPurchaseDetails();
		for (PurchaseDetails detail: details) {
			View vendorView = getVendorView(getActivity(), detail, this);
			mContainer.addView(vendorView);
		}
		
		// Set the onclick listener for details
		mInfoButton.setOnClickListener(this);
		mDetails.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param activity
	 * @param details
	 * @return A view of the individual vendor
	 */
	private static View getVendorView(Activity activity, PurchaseDetails details, OnClickListener listener) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.vendor_list_item, null);

		// Extract the components
		ImageButton vendorButton = (ImageButton) rowView.findViewById(R.id.vendor_button);
		vendorButton.setOnClickListener(listener);
		TextView price = (TextView) rowView.findViewById(R.id.price_label);

		// Extract teh content to show
		Vendor vendor = details.getVendor();
		ImageSource logo = vendor.getLogo();
		if (logo == null) {
			// TODO Create a drawable to place as the image source. 
		}

		// Set the content with the
		logo.loadImage(activity, vendorButton);
		price.setText(details.getReadableCost());

		return rowView;
	}

	private static int getRandomAmountOfWinners() {
		return (int) (Math.random() * WINNERS_RANGE + WINNERS_MIN);
	}

	/**
	 * Interface for any parent class to implement to allow backwords communication.
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface ItemInteractionListener {

		/**
		 * Returns the interactable item to display within this
		 * fragment.
		 *  
		 * @param pos Position of 
		 * @return Interactable item to display, cannot be null.
		 */
		public Item getItem(int pos);

	}

	private class ImageAdapter extends BaseAdapter {

		private final Context mContext;
		private final List<ImageSource> mImages;

		/**
		 * Creates an image adapter for this
		 * @param ctx Context to load images.
		 * @param images Images to load.
		 */
		public ImageAdapter(Context ctx, List<ImageSource> images) {
			mContext = ctx;
			mImages = new ArrayList<ImageSource>(images);
		}

		@Override
		public int getCount() {
			return mImages.size();
		}

		@Override
		public Object getItem(int pos) {
			return mImages.get(pos);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView view = new ImageView(mContext);
			view.setScaleType(ImageView.ScaleType.FIT_CENTER);
			view.setLayoutParams(new Gallery.LayoutParams(
					android.widget.Gallery.LayoutParams.MATCH_PARENT, 
					android.widget.Gallery.LayoutParams.MATCH_PARENT));
			ImageSource source = mImages.get(position);
			source.loadImage(mContext, view);
			return view;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_button:
			toggleDetails();
			break;
		case R.id.vendor_button:
			
			break;
		}
	}
	
	/**
	 * Toggles the visibility of the details pane.
	 */
	private void toggleDetails() {
		if (mDetails.isShown()) {
			FX.collapseUp(this.getActivity(), mDetails);
			mDetails.setVisibility(View.GONE);
		} else {
			mDetails.setVisibility(View.VISIBLE);
			FX.expandDown(this.getActivity(), mDetails);
		}
	}

}
