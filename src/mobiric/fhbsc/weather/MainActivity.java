package mobiric.fhbsc.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity
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

			@Override
			public void onPageFinished(WebView view, String url)
			{
				// hide title on home page
				// this is a HACK that causes a flicker
				if (HOME_PAGE.equals(url))
				{
					String jsHideTitle =
							"  var allElements = document.getElementsByTagName('*');"
									+ "  for (var i = 0; i < allElements.length; i++)"
									+ "  {"
									+ "    if (allElements[i].getAttribute('data-role')==\"header\")"
									+ "    {" + "      allElements[i].style.display='none';"
									+ "      break;" + "    }" + "  }";
					view.loadUrl("javascript:" + jsHideTitle);
				}

				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(HOME_PAGE);
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
		webView.reload();
	}
}
