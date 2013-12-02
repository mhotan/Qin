package org.risa.android;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import com.aqt.qin.R;
import com.origamilabs.library.views.StaggeredGridView;
import com.staggeredgrid.StaggeredAdapter;

/**
 * Fragment View used to display news feed items to the user
 * 
 * @author Brendan Lee
 */
public class NewsFeedFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {			
		StaggeredGridView gridView = new StaggeredGridView(getActivity());
		gridView.setDrawSelectorOnTop(true);
		gridView.setSelector(R.drawable.selector);
		
		int margin = getResources().getDimensionPixelSize(R.dimen.activity_newsfeed_margin);
		gridView.setItemMargin(margin); // set the GridView margin
		
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
		    // Landscape
			gridView.setColumnCount(3);
		}
		else {
		    // Portrait  
			gridView.setColumnCount(2);
		}
		
		gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
		
		String[] imgNames = new String[10]; // Size should match the number of test images
		for (int i = 0; i < imgNames.length; i++)
			imgNames[i] = "misc";
		
		StaggeredAdapter adapter = new StaggeredAdapter(getActivity(), R.id.image, imgNames);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		//gridView.setSelector(getResources().getDrawable(R.drawable.selector));
		gridView.setDrawSelectorOnTop(true);
		
		return gridView;
	}
}
