package org.risa.android.target;

import org.risa.android.data.Interactable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aqt.qin.R;

/**
 * Class that displays the information of a particular item inside a target.
 *  
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ItemInformationFragment extends Fragment {

	public static final String KEY_CONTENT = ItemInformationFragment.class.getName() + "_INTERACTABLE";
	
	@Override 
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_information_fragment,
				container, false);

		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
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
		 * @return Interactable item to display, cannot be null.
		 */
		public Interactable getInteractable();
		
		
		
	}
	
}
