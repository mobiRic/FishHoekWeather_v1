package mobiric.fhbsc.weather;


import lib.view.ViewPagerParallax;
import mobiric.fhbsc.weather.adapters.ScreenSwipeAdapter;
import mobiric.fhbsc.weather.fragments.ARefreshableFragment;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.model.WeatherReading;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AutoRefreshActivity
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to
	 * switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	ScreenSwipeAdapter screenSwipeAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPagerParallax viewPager;


	/** Receiver for refresh intents. Passes the intent to the implementing subclass. */
	private BroadcastReceiver refreshReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			/*
			 * Recently came across a post by Dianne Hackborn saying not to pass data inside
			 * Intents. Rather to use globals to share data between Activities.
			 */
			WeatherReading reading = myApp.getCachedWeatherReading();
			setUpdateTime(reading.time);
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		screenSwipeAdapter = new ScreenSwipeAdapter(this, getSupportFragmentManager());

		setContentView(R.layout.activity_main);

		viewPager = (ViewPagerParallax) findViewById(R.id.pager);
		viewPager.set_max_pages(4);
		viewPager.setBackgroundAsset(R.raw.false_bay);
		viewPager.setAdapter(screenSwipeAdapter);
		viewPager.setCurrentItem(3);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		register();
		refreshOnResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregister();
	}

	private void register()
	{
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
				refreshReceiver, new IntentFilter(Actions.REFRESH_UPDATE_TIME));
	}

	private void unregister()
	{
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
				refreshReceiver);
	}

	void refreshOnResume()
	{
		WeatherReading reading = myApp.getCachedWeatherReading();
		setUpdateTime(reading.time);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpdateTime(String time)
	{
		if (time != null)
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			{
				setTitle(getResources().getString(R.string.app_name) + " - " + time);
			}
			else
			{
				ActionBar actionBar = getActionBar();
				if (actionBar != null)
				{
					actionBar.setSubtitle("updated: " + time);
				}
			}
		}
	}


	/**
	 * Allow back button to handle navigation.
	 */
	@Override
	public void onBackPressed()
	{
		ARefreshableFragment fragment =
				(ARefreshableFragment) screenSwipeAdapter.getItem(viewPager.getCurrentItem());
		if (!fragment.onBackPressed())
		{
			super.onBackPressed();
		}
	}

}
