package mobiric.fhbsc.weather;

import lib.debug.Dbug;
import mobiric.fhbsc.weather.adapters.ScreenSwipeAdapter;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import mobiric.fhbsc.weather.model.WeatherReading;
import mobiric.fhbsc.weather.tasks.BaseWebService;
import mobiric.fhbsc.weather.tasks.BaseWebService.OnBaseWebServiceResponseListener;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser.OnWeatherReadingParsedListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements OnBaseWebServiceResponseListener,
		OnWeatherReadingParsedListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to
	 * switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	ScreenSwipeAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;


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

		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new ScreenSwipeAdapter(this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		doRefresh();
	}


	/**
	 * Allow back button to handle navigation.
	 */
	@Override
	public void onBackPressed()
	{
		// if (webView.canGoBack())
		// {
		// webView.goBack();
		// }
		// else
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
		// webView.reload();
		new BaseWebService(this).execute(HOME_PAGE);
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


	@Override
	public void onWeatherReadingParseResult(WeatherReading result)
	{
		Dbug.log(result.toString());
		reading = result;

		// cache reading
		myApp.setCachedWeatherReading(reading);
	}


	@Override
	public void onWeatherReadingParseError(String error)
	{
		// ignore errors
	}
}
