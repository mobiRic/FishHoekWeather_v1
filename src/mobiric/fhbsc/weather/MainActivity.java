package mobiric.fhbsc.weather;


import com.crashlytics.android.Crashlytics;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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

		Crashlytics.start(this);
		screenSwipeAdapter = new ScreenSwipeAdapter(this, getSupportFragmentManager());

		setContentView(R.layout.activity_main);

		viewPager = (ViewPagerParallax) findViewById(R.id.pager);
		viewPager.set_max_pages(3);
		viewPager.setBackgroundAsset(R.raw.false_bay);
		viewPager.setAdapter(screenSwipeAdapter);
		viewPager.setCurrentItem(loadLastViewedPageSetting());
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

		saveLastViewedPageSetting(viewPager.getCurrentItem());
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_view_on_web:
			{
				// launch browser
				Intent browserIntent =
						new Intent(Intent.ACTION_VIEW, Uri.parse(WeatherApp.HOME_PAGE));
				startActivity(browserIntent);

				break;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}

		// handled by us
		return true;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void saveLastViewedPageSetting(int position)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor e = prefs.edit();
		e.putInt("LAST_VIEWED_PAGE", position);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			e.apply();
		}
		else
		{
			e.commit();
		}
	}

	private int loadLastViewedPageSetting()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getInt("LAST_VIEWED_PAGE", 0);
	}

}
