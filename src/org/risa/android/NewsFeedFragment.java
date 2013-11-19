package org.risa.android;

import com.staggeredgrid.StaggeredAdapter;
import com.aqt.qin.R;
import com.origamilabs.library.views.StaggeredGridView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment View used to display news feed items to the user
 * 
 * @author Brendan Lee
 */
public class NewsFeedFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//TextView collectionView = new TextView(getActivity());
		//collectionView.setGravity(Gravity.CENTER);
		//collectionView.setText("List View containing news feed");				 
		
		StaggeredGridView gridView = new StaggeredGridView(getActivity());
		
		int margin = getResources().getDimensionPixelSize(R.dimen.activity_newsfeed_margin);
		
		gridView.setItemMargin(margin); // set the GridView margin
		gridView.setColumnCount(2);
		gridView.setDrawSelectorOnTop(true);
		gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
		
		String[] imgNames = new String[10]; // Size should match the number of test images
		for (int i = 0; i < imgNames.length; i++)
			imgNames[i] = "misc";
		
		StaggeredAdapter adapter = new StaggeredAdapter(getActivity(), R.id.imageView1, imgNames);
		
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		return gridView;
	}
}
