package mobiric.fhbsc.weather.fragments;

import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Fragment that displays the original web page.
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebWeatherFragment extends ARefreshableFragment
{
	WebView webView;

	public WebWeatherFragment()
	{
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_web_page, container, false);
		webView = (WebView) rootView.findViewById(R.id.webView);

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

		updateWebView();

		return rootView;
	}
	
	void updateWebView()
	{
		// get data from the extras
		String baseUrl = bundle.getString(Extras.BASE_URL);
		String htmlData = bundle.getString(Extras.HTML_DATA);
		webView.loadDataWithBaseURL(baseUrl, htmlData, "text/html", "utf-8", null);
	}

	@Override
	String getRefreshIntentAction()
	{
		return Actions.REFRESH_WEB_WEATHER;
	}

	@Override
	void onRefreshIntentReceived(Intent intent)
	{
		updateWebView();
	}

}
