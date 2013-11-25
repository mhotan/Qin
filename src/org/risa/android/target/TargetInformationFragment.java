package org.risa.android.target;

import java.util.ArrayList;
import java.util.List;

import org.risa.android.data.Interactable;
import org.risa.android.data.Target;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aqt.qin.R;

/**
 * This class shows the details of ex
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class TargetInformationFragment extends Fragment {

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
		List<Interactable> inters = new ArrayList<Interactable>(mTarget.getInteractables());
		InteractablePagerAdapter adapter = new InteractablePagerAdapter(inters, getChildFragmentManager());
		mPager.setAdapter(adapter);
	}
	
	/**
	 * Sets the interactable item to display for this particular Target.  If Target
	 * does not contain this interactable item then no action will occur.
	 * 
	 * @param interactable The interactable to focus on 
	 */
	public void setInteractableToDisplay(Interactable interactable) {
		// TODO
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
        
    	private final List<Interactable> mInteractables;
    	
    	/**
    	 * @param fm Fragment manager that handles fragment positioning
    	 */
    	public InteractablePagerAdapter(List<Interactable> interactables, FragmentManager fm) {
            super(fm);
            mInteractables = interactables;
        }

        @Override
        public Fragment getItem(int position) {
        	Interactable i = mInteractables.get(position);
        	// TODO Pass the interactable content to this fragment
        	ItemInformationFragment frag =  new ItemInformationFragment();
        	Bundle args = new Bundle();
//        	args.putParcelable(ItemInformationFragment.KEY_INTERACTABLE, );
//        	frag.setArguments(args)
        	
        	return frag;
        }

        @Override
        public int getCount() {
            return mInteractables.size();
        }
    }
	
}
