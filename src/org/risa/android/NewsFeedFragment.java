package org.risa.android;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

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
		RelativeLayout containerLayout = new RelativeLayout(getActivity());
		
		StaggeredGridView gridView = new StaggeredGridView(getActivity());
		containerLayout.addView(gridView);
		
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
		
		gridView.setDrawSelectorOnTop(true);
		gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
		
		String[] imgNames = new String[10]; // Size should match the number of test images
		for (int i = 0; i < imgNames.length; i++)
			imgNames[i] = "misc";
		
		StaggeredAdapter adapter = new StaggeredAdapter(getActivity(), R.id.imageView1, imgNames);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyle);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		lp.addRule(RelativeLayout.ALIGN_BOTTOM);
		containerLayout.addView(progressBar, lp);
		
		return containerLayout;
	}
}
