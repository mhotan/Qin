package org.risa.android;

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
public class CollectionsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView collectionView = new TextView(getActivity());
		collectionView.setGravity(Gravity.CENTER);
		collectionView.setText("List View containing collections of actions");				
		return collectionView;
	}
}
