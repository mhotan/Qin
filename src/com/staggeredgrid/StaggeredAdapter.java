package com.staggeredgrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
			convertView = layoutInflator.inflate(R.layout.row_staggered_view,
					null);
			holder = new ViewHolder();
			
			holder.imageView = (ScaleImageView) convertView.findViewById(R.id.image);
			holder.captionView = (TextView) convertView.findViewById(R.id.caption);
			holder.posterView = (TextView) convertView.findViewById(R.id.poster);
						
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		int imgId = getImageId(position);
		int profileImageId = getProfileImageId(position);
		CharSequence profileName = getProfileName(position);
		CharSequence caption = getCaption(position);
		
		holder.imageView.setImageResource(imgId);
		holder.imageView.setBackgroundResource(R.drawable.selector);

		holder.captionView.setText(caption);
		holder.posterView.setText(profileName);
		
		// Scale Profile Picture Correctly
		Drawable dr = getContext().getResources().getDrawable(profileImageId);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		Drawable d = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, 60, 60, true));		
		holder.posterView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		
		return convertView;
	}

	private CharSequence getCaption(int position) {
		CharSequence caption;
		switch (position) {
		case 0:
			caption = "loving the fall trends";
			break;
		case 1:
			caption = "Just won this!";
			break;
		case 2:
			caption = "#iloveshopping";
			break;
		case 3:
			caption = "one of my favorite models...";
			break;
		case 4:
			caption = "wouldn't you love to be in hat?";
			break;
		case 5:
			caption = "looking suave";
			break;
		case 6:
			caption = "I want this scarf for xmas";
			break;
		case 7:
			caption = "bling bling";
			break;
		case 8:
			caption = "#hot";
			break;
		default:
			caption = "liking this look right now";
		}
		
		return caption;
	}

	private int getProfileImageId(int position) {
		int imgId;
		switch (position) {
		case 0:
			imgId = R.drawable.profile1;
			break;
		case 1:
			imgId = R.drawable.profile2;
			break;
		case 2:
			imgId = R.drawable.profile3;
			break;
		case 3:
			imgId = R.drawable.profile4;
			break;
		case 4:
			imgId = R.drawable.profile5;
			break;
		case 5:
			imgId = R.drawable.profile6;
			break;
		case 6:
			imgId = R.drawable.profile7;
			break;
		case 7:
			imgId = R.drawable.profile8;
			break;
		case 8:
			imgId = R.drawable.profile9;
			break;
		default:
			imgId = R.drawable.profile10;
		}
		
		return imgId;
	}

	private CharSequence getProfileName(int position) {
		CharSequence name;
		switch (position) {
		case 0:
			name = "Jessica Walden";
			break;
		case 1:
			name = "Takis Sotiriou";
			break;
		case 2:
			name = "Anna Rauh";
			break;
		case 3:
			name = "Joseph Angel";
			break;
		case 4:
			name = "Lora Jamison";
			break;
		case 5:
			name = "Michele Davis";
			break;
		case 6:
			name = "Teale Leonard";
			break;
		case 7:
			name = "Ernst Blofeld";
			break;
		case 8:
			name = "Adam Banzai";
			break;
		default:
			name = "Joe Everett";
		}
		
		return name;
	}

	private int getImageId(int position) {
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
		
		return imgId;
	}

	static class ViewHolder {
		ScaleImageView imageView;
		TextView captionView;
		TextView posterView;
	}
}
