package org.risa.android;

import java.util.Locale;

import org.risa.android.RecognitionFragment.FoundTargetListener;
import org.risa.android.data.DemoTarget;
import org.risa.android.target.TargetActivity;
import org.risa.android.util.Constants;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.aqt.qin.R;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Scanner;
import com.moodstocks.android.Scanner.SyncListener;

public class LaunchActivity extends FragmentActivity implements
ActionBar.TabListener, SyncListener, FoundTargetListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 * A global flag that tracks if the users device is compatible with moodstocks
	 * api for interpretting images
	 */
	private boolean isMoodstockCompatible = false;

	/**
	 * MoodStock core data structure that can sync with a server. Syncing
	 * pulls all the images locally, so they can be identified.
	 */
	private Scanner mScanner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize MoodStocks Api features (if possible)
		isMoodstockCompatible = Scanner.isCompatible();
		if (isMoodstockCompatible) {
			try {
				this.mScanner = Scanner.get();
				mScanner.open(this, Constants.MOODSTOCKS_API_KEY, Constants.MOODSTOCKS_API_SECRET);
				mScanner.sync(this);
			} catch (MoodstocksError e) {
				e.log();
			}
		} else {
			Log.w(getClass().getSimpleName(), "Unable to intiialize Moodstock scanners");
			// TODO: Notify users that there device does not support image recognition
			// Requirements -
			//   Android 2.3+
			//   Arm or x86 CPU
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		// Each Fragment represents the view for the three application tabs
		private final Fragment mRecFrag = new RecognitionFragment();
		private final Fragment mNewsFeedFrag = new NewsFeedFragment();
		private final Fragment mCollectionsFrag = new CollectionsFragment();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			// Handle special fragment for showing interaction capabilities.
			switch (position)
			{
				case 0:
					return mRecFrag;
				case 1:
					return mNewsFeedFrag;
				default:
					return mCollectionsFrag;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(Locale.getDefault());
			case 1:
				return getString(R.string.title_section2).toUpperCase(Locale.getDefault());
			case 2:
				return getString(R.string.title_section3).toUpperCase(Locale.getDefault());
			}
			return null;
		}
	}


	/////////////////////////////////////////////////////////
	/////  SyncListener 
	/////  Pulling image data from Moodstocks
	/////////////////////////////////////////////////////////


	@Override
	public void onSyncStart() {
		Log.d("Moodstocks SDK", "Sync will start.");
	}

	@Override
	public void onSyncComplete() {
		try {
			Log.d("Moodstocks SDK", String.format("Sync succeeded (%d image(s))", mScanner.count()));
		} catch (MoodstocksError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSyncFailed(MoodstocksError e) {
		Log.d("Moodstocks SDK", "Sync error: " + e.getErrorCode());
	}

	@Override
	public void onSyncProgress(int total, int current) {
		int percent = (int) ((float) current / (float) total * 100);
		Log.d("Moodstocks SDK", String.format("Sync progressing: %d%%", percent));
	}

	@Override
	public void onFoundDemoTarget(DemoTarget target) {
		// Launch the found activity once we identified the target.
		if (target != null) {
			Intent i = new Intent(this, TargetActivity.class);
			i.putExtra(TargetActivity.ARG_IMAGE_NAME, target.getUID());
			startActivity(i);
		}
	}
}
