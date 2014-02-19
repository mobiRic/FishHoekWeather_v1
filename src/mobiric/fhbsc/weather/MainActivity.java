package mobiric.fhbsc.weather;

import lib.debug.Dbug;
import lib.view.ViewPagerParallax;
import mobiric.fhbsc.weather.adapters.ScreenSwipeAdapter;
import mobiric.fhbsc.weather.fragments.ARefreshableFragment;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import mobiric.fhbsc.weather.model.WeatherReading;
import mobiric.fhbsc.weather.tasks.BaseWebService;
import mobiric.fhbsc.weather.tasks.BaseWebService.OnBaseWebServiceResponseListener;
import mobiric.fhbsc.weather.tasks.ImageDownloader;
import mobiric.fhbsc.weather.tasks.ImageDownloader.OnImageDownloadedListener;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser.OnWeatherReadingParsedListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements OnBaseWebServiceResponseListener,
		OnWeatherReadingParsedListener, OnImageDownloadedListener
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


	public static final String BASE_URL = "http://www.fhbsc.co.za/fhbsc/weather/smartphone/";
	public static final String HOME_PAGE = BASE_URL + "index.html";

	/**
	 * Handle to {@link WeatherApp} instance for caching data.
	 */
	WeatherApp myApp;

	/**
	 * Data extracted from the web service.
	 */
	WeatherReading reading;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get cached weather reading
		myApp = (WeatherApp) getApplication();
		reading = myApp.getCachedWeatherReading();

		setUpdateTime();

		screenSwipeAdapter = new ScreenSwipeAdapter(this, getSupportFragmentManager());

		setContentView(R.layout.activity_main);

		viewPager = (ViewPagerParallax) findViewById(R.id.pager);
		viewPager.set_max_pages(4);
		viewPager.setBackgroundAsset(R.raw.false_bay);
		viewPager.setAdapter(screenSwipeAdapter);
		viewPager.setCurrentItem(3);

		doRefresh();
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setUpdateTime()
	{
		if (reading != null)
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			{
				setTitle(getResources().getString(R.string.app_name) + " - " + reading.time);
			}
			else
			{
				ActionBar actionBar = getActionBar();
				if (actionBar != null)
				{
					actionBar.setSubtitle("updated: " + reading.time);
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// manual refresh
			case R.id.action_refresh:
			{
				doRefresh();

				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refreshes the site.
	 */
	void doRefresh()
	{
		new BaseWebService(this).execute(HOME_PAGE);

		// wind graphs
		new ImageDownloader(this, this).execute("http://www.fhbsc.co.za/fhbsc/weather/daywind.png",
				"daywind.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daywinddir.png", "daywinddir.png");


		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekwind.png", "weekwind.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekwinddir.png", "weekwinddir.png");

		// temperature graphs
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daytempdew.png", "daytempdew.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weektempdew.png", "weektempdew.png");

		// barometer graphs
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daybarometer.png", "daybarometer.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekbarometer.png", "weekbarometer.png");
	}


	@Override
	public void onBaseWebServiceResult(String result)
	{
		// remove title bar from html
		String resultWithoutTitle =
				result.replace(
						"<div data-role=\"header\">      <h1>Fish Hoek Beach Sailing Club, Cape Town</h1>    </div>",
						"");

		// update webview
		Intent refresh = new Intent(Actions.REFRESH_WEB_WEATHER);
		refresh.putExtra(Extras.BASE_URL, BASE_URL);
		refresh.putExtra(Extras.HTML_DATA, resultWithoutTitle);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);

		// parse data
		new WeatherReadingParser(this).execute(result);
	}


	@Override
	public void onBaseWebServiceError(String error)
	{
		onBaseWebServiceResult(error);
	}


	@SuppressLint("NewApi")
	@Override
	public void onWeatherReadingParseResult(WeatherReading result)
	{
		if (result == null)
		{
			Intent refresh = new Intent(Actions.REFRESH_WEATHER);
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);
			return;
		}

		Dbug.log(result.toString());
		reading = result;

		setUpdateTime();

		// update ui
		Intent refresh = new Intent(Actions.REFRESH_WEATHER);

		// wind
		refresh.putExtra(Extras.WIND_SPEED, result.windSpeed);
		refresh.putExtra(Extras.WIND_DIR, result.windDir);
		refresh.putExtra(Extras.WIND_GUST, result.windGust);
		refresh.putExtra(Extras.WIND_GUST_DIR, result.windGustDir);

		// temperature
		refresh.putExtra(Extras.OUT_TEMP, result.outTemp);
		refresh.putExtra(Extras.OUT_TEMP_MIN, result.outTempMin);
		refresh.putExtra(Extras.OUT_TEMP_MAX, result.outTempMax);

		// barometer
		refresh.putExtra(Extras.BAROMETER, result.barometer);

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);

		// cache reading
		myApp.setCachedWeatherReading(reading);
	}


	@Override
	public void onWeatherReadingParseError(String error)
	{
		// ignore errors
		Dbug.log("Error downloading weather page [", error, "]");
	}


	@Override
	public void onImageDownloadSuccess(String filename)
	{
		Dbug.log("Image downloaded [", filename, "]");

		// update image
		Intent refresh = new Intent(Actions.REFRESH_IMAGE);
		refresh.putExtra(Extras.IMG_NAME, filename);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);
	}


	@Override
	public void onImageDownloadError(String error)
	{
		// ignore errors
		Dbug.log("Error downloading image [", error, "]");
	}
}
