package mobiric.fhbsc.weather;

import lib.debug.Dbug;
import mobiric.fhbsc.weather.model.WeatherReading;
import mobiric.fhbsc.weather.tasks.BaseWebService;
import mobiric.fhbsc.weather.tasks.BaseWebService.OnBaseWebServiceResponseListener;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser.OnWeatherReadingParsedListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity implements OnBaseWebServiceResponseListener,
		OnWeatherReadingParsedListener
{
	private static final String BASE_URL = "http://www.fhbsc.co.za/fhbsc/weather/smartphone/";
	private static final String HOME_PAGE = BASE_URL + "index.html";
	WebView webView;

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
		webView = (WebView) findViewById(R.id.webView1);

		// this site uses javascript to format the page
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				// load inside the webview
				view.loadUrl(url);
				return true;
			}

		});

		doRefresh();
	}


	/**
	 * Allow back button to handle navigation.
	 */
	@Override
	public void onBackPressed()
	{
		if (webView.canGoBack())
		{
			webView.goBack();
		}
		else
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

		// display in webview
		webView.loadDataWithBaseURL(HOME_PAGE, resultWithoutTitle, "text/html", "utf-8", HOME_PAGE);

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
