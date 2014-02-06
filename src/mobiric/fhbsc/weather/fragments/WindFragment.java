package mobiric.fhbsc.weather.fragments;

import lib.widget.LoaderImageView;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Fragment that displays the wind data.
 */
public class WindFragment extends ARefreshableFragment
{
	TextView tvWindSpeed;
	TextView tvWindDir;
	TextView tvWindGustSpeed;
	TextView tvWindGustDir;
	LoaderImageView ivDayWind;
	LoaderImageView ivDayWindDir;

	public WindFragment()
	{
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_wind, container, false);
		tvWindSpeed = (TextView) rootView.findViewById(R.id.tvWindSpeed);
		tvWindDir = (TextView) rootView.findViewById(R.id.tvWindDir);
		tvWindGustSpeed = (TextView) rootView.findViewById(R.id.tvWindGustSpeed);
		tvWindGustDir = (TextView) rootView.findViewById(R.id.tvWindGustDir);
		ivDayWind = (LoaderImageView) rootView.findViewById(R.id.ivDayWind);
		ivDayWindDir = (LoaderImageView) rootView.findViewById(R.id.ivDayWindDir);

		updateUI();

		return rootView;
	}

	void updateUI()
	{
		// get data from the extras
		String windSpeed = bundle.getString(Extras.WIND_SPEED);
		tvWindSpeed.setText(windSpeed);

		String windDir = bundle.getString(Extras.WIND_DIR);
		tvWindDir.setText(windDir);

		String windGustSpeed = bundle.getString(Extras.WIND_GUST);
		tvWindGustSpeed.setText(windGustSpeed);

		String windGustDir = bundle.getString(Extras.WIND_GUST_DIR);
		tvWindGustDir.setText(windGustDir);

		ivDayWind.setImageDrawable("http://www.fhbsc.co.za/fhbsc/weather/daywind.png");
		ivDayWindDir.setImageDrawable("http://www.fhbsc.co.za/fhbsc/weather/daywinddir.png");

		// Picasso.with(appContext).load("http://www.fhbsc.co.za/fhbsc/weather/daywind.png").fetch();
		// Picasso.with(appContext).load("http://www.fhbsc.co.za/fhbsc/weather/daywinddir.png").fetch();
		// Picasso.with(appContext).load("http://www.fhbsc.co.za/fhbsc/weather/daywind.png").into(ivDayWind);
		// Picasso.with(appContext).load("http://www.fhbsc.co.za/fhbsc/weather/daywinddir.png").into(ivDayWindDir);
	}

	@Override
	String getRefreshIntentAction()
	{
		return Actions.REFRESH_WEATHER;
	}

	@Override
	void onRefreshIntentReceived(Intent intent)
	{
		updateUI();
	}

}
