package mobiric.fhbsc.weather;

import mobiric.fhbsc.weather.tasks.BaseWebService;
import mobiric.fhbsc.weather.tasks.BaseWebService.OnBaseWebServiceResponseListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity implements OnBaseWebServiceResponseListener
{
	private static final String BASE_URL = "http://www.fhbsc.co.za/fhbsc/weather/smartphone/";
	private static final String HOME_PAGE = BASE_URL + "index.html";
	WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

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
	public void onResult(String result)
	{
		// remove title bar from html
		String resultWithoutTitle =
				result.replace(
						"<div data-role=\"header\">      <h1>Fish Hoek Beach Sailing Club, Cape Town</h1>    </div>",
						"");
		webView.loadDataWithBaseURL(HOME_PAGE, resultWithoutTitle, "text/html", "utf-8", HOME_PAGE);
	}


	@Override
	public void onError(String error)
	{
		onResult(error);
	}
}
