package org.risa.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aqt.qin.R;

/**
 * Fragment View used to display news feed items to the user
 * 
 * @author Brendan Lee
 */
public class CollectionsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_collections, container, false);
	    GridView gridView = (GridView)view.findViewById(R.id.gridview);
	    
		((GridView) gridView).setAdapter(new CustomGridViewAdapter(getActivity()));
		
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
		    // Landscape
			gridView.setNumColumns(3);
		}
		else {
		    // Portrait  
			gridView.setNumColumns(2);
		}
		
	    gridView.setDrawSelectorOnTop(true);
	    gridView.setSelector(R.color.buttonhighlight);
	    //gridView.setSelector(getResources().getDrawable(R.drawable.selector));
	    
	    return view;
	}
	
	private class CustomGridViewAdapter extends BaseAdapter {
	    private List<Item> items = new ArrayList<Item>();
	    private LayoutInflater inflater;

	    public CustomGridViewAdapter(Context context) {
	        inflater = LayoutInflater.from(context);

	        items.add(new Item("Recent Scans", R.drawable.collections1));
	        items.add(new Item("Favorites", R.drawable.collections2));
	        items.add(new Item("cool hats", R.drawable.collections3));
	        items.add(new Item("#falltrends", R.drawable.collections4));
	        items.add(new Item("nordstroms", R.drawable.collections5));
	        items.add(new Item("New Collection", R.drawable.plus_icon));
	    }

	    @Override
	    public int getCount() {
	        return items.size();
	    }

	    @Override
	    public Object getItem(int i) {
	        return items.get(i);
	    }

	    @Override
	    public long getItemId(int i) {
	        return items.get(i).drawableId;
	    }

	    @Override
	    public View getView(int i, View view, ViewGroup viewGroup) {
	        View v = view;
	        ImageView picture;
	        TextView name;
	        
	        if(v == null) {
	            v = inflater.inflate(R.layout.collections_item, viewGroup, false);
	            v.setTag(R.id.picture, v.findViewById(R.id.picture));
	            v.setTag(R.id.text, v.findViewById(R.id.text));
	        }

	        picture = (ImageView)v.getTag(R.id.picture);

	        name = (TextView)v.getTag(R.id.text);

	        Item item = (Item)getItem(i);
	        name.setText(item.name);
	        
	        picture.setImageResource(item.drawableId);	
	        picture.setBackgroundResource(R.drawable.selector);
	        
	        return v;
	    }

	    private class Item {
	        final String name;
	        final int drawableId;

	        Item(String name, int drawableId) {
	            this.name = name;
	            this.drawableId = drawableId;
	        }
	    }
	}
}
