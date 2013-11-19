package com.staggeredgrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aqt.qin.R;

/**
 * Currently, this class dummies up the news feed feature of the application,
 * It will just pull static images from resources and display them.  In the future, this 
 * feature will produces a news feed in real time
 * 
 * @author brendanLee
 *
 */
public class StaggeredAdapter extends ArrayAdapter<String> {
	public StaggeredAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			convertView = layoutInflator.inflate(R.layout.row_staggered_demo,
					null);
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		
		int imgId;
		switch (position) {
		case 0:
			imgId = R.drawable.newsfeed1;
			break;
		case 1:
			imgId = R.drawable.newsfeed2;
			break;
		case 2:
			imgId = R.drawable.newsfeed3;
			break;
		case 3:
			imgId = R.drawable.newsfeed4;
			break;
		case 4:
			imgId = R.drawable.newsfeed5;
			break;
		case 5:
			imgId = R.drawable.newsfeed6;
			break;
		case 6:
			imgId = R.drawable.newsfeed7;
			break;
		case 7:
			imgId = R.drawable.newsfeed8;
			break;
		case 8:
			imgId = R.drawable.newsfeed9;
			break;
		default:
			imgId = R.drawable.newsfeed10;
		}
		
		holder.imageView.setImageResource(imgId);
		return convertView;
	}

	static class ViewHolder {
		ScaleImageView imageView;
	}
}
