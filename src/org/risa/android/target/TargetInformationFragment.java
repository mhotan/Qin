package org.risa.android.target;

import java.util.ArrayList;
import java.util.List;

import org.risa.android.data.Interactable;
import org.risa.android.data.Item;
import org.risa.android.data.Target;
import org.risa.android.target.ItemInformationFragment.ItemInteractionListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aqt.qin.R;

/**
 * This class shows the details of ex
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetInformationFragment extends Fragment implements 
OnClickListener, OnPageChangeListener, ItemInteractionListener {

	/**
	 * Key to place in bundle to show
	 */
	public static final String KEY_ITEM_NAME = TargetInformationFragment.class.getName() + " ITEM_NAME";

	/**
	 * Listening activity
	 */
	private InformationListener mListener;

	/**
	 * The sole interactable item to display to the user.
	 */
	private Target mTarget;

	/**
	 * The pager adapter, which provides the ability to flip to different items.
	 */
	private ViewPager mPager;

	/**
	 * Adapter for items
	 */
	private InteractablePagerAdapter mAdapter;

	private TextView mLabel;
	private ImageButton mPrevious, mNext;

	/*
	 * Fragment Specific Methods
	 */

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Make sure owning activity implements correct interface
		try {
			mListener = (InformationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement " 
					+ getClass().getSimpleName() + ".TargetInformationListener");
		}
	}

	@Override 
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.information_fragment,
				container, false);

		// TODO Populate the view for 
		mPager = (ViewPager) view.findViewById(R.id.pager);

		// Extract the content of the view
		mLabel = (TextView) view.findViewById(R.id.label_title);
		mPrevious = (ImageButton) view.findViewById(R.id.button_previous);
		mNext = (ImageButton) view.findViewById(R.id.button_next);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Build the item to display.
		mTarget = mListener.getTarget();

		// Label the views appropiately
		mLabel.setText(mTarget.getReadableIdentifier());
		mPrevious.setOnClickListener(this);
		mNext.setOnClickListener(this);

		List<Item> items = new ArrayList<Item>(mTarget.getItems());
		mAdapter = new InteractablePagerAdapter(items, getChildFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);
	}

	/**
	 * Sets the interactable item to display for this particular Target.  If Target
	 * does not contain this interactable item then no action will occur.
	 * 
	 * @param item The item to focus on 
	 */
	public void setItemToDisplay(Item item) {
		int index = mAdapter.getItemPosition(item);
		mPager.setCurrentItem(index);

		// Update 
		updateTraversalButtons(index);
	}

	/**
	 * All class that plan to use this fragment must implement this interface.
	 * It allows Activities to listen for fragment interaction events. 
	 * 
	 * @author Michael Hotan, michael.hotan@gmail.com
	 */
	public interface InformationListener {

		/**
		 * @return Target to present within this fragment.
		 */
		public Target getTarget();

		// TODO Add Call backs for interacting with specific content.

	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
	 * sequence.
	 */
	private class InteractablePagerAdapter extends FragmentStatePagerAdapter {

		private final List<Item> mItems;

		/**
		 * @param fm Fragment manager that handles fragment positioning
		 */
		public InteractablePagerAdapter(List<Item> interactables, FragmentManager fm) {
			super(fm);
			mItems = interactables;
		}

		@Override
		public Fragment getItem(int position) {
			Interactable i = mItems.get(position);
			// TODO Pass the interactable content to this fragment
			ItemInformationFragment frag =  new ItemInformationFragment();
			Bundle args = new Bundle();
			args.putInt(ItemInformationFragment.KEY_INDEX, position);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		public int getItemPosition(Item item) {
			return mItems.indexOf(item);
		}
		
		public Item get(int position) {
			return mItems.get(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int position) {
		updateTraversalButtons(position);
	}

	/**
	 * Updates the buttons that handles traversal between different items.
	 * 
	 * @param position Position that defines the apearance of some buttons
	 */
	private void updateTraversalButtons(int position) {
		// Set the visibility of the previous and next button.
		mPrevious.setVisibility(View.VISIBLE);
		mPrevious.setEnabled(true);
		mNext.setVisibility(View.VISIBLE);
		mNext.setEnabled(true);
		
		// Hide buttons appropiately
		if (position == 0) {
			mPrevious.setVisibility(View.INVISIBLE);
			mPrevious.setEnabled(false);
		} 
		if (position == mAdapter.getCount() - 1) {
			mNext.setVisibility(View.INVISIBLE);
			mNext.setEnabled(false);
		}
	}

	@Override
	public void onClick(View view) {
		int itemIndex = mPager.getCurrentItem();
		switch (view.getId()) {
		case R.id.button_previous:
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			break;
		case R.id.button_next:
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
			break;
		default:
		}
	}

	@Override
	public Item getItem(int pos) {
		return mAdapter.get(pos);
	}

}
