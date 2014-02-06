package mobiric.fhbsc.weather.fragments;

import lib.debug.Dbug;
import lib.io.IOUtils;
import lib.widget.LoaderImageView;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
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
	ImageView ivDayWind;
	ImageView ivDayWindDir;

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
		ivDayWind = (ImageView) rootView.findViewById(R.id.ivDayWind);
		ivDayWindDir = (ImageView) rootView.findViewById(R.id.ivDayWindDir);

		updateData();
		initImages();

		return rootView;
	}

	void updateData()
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
	}

	void initImages()
	{
		String filesDir = appContext.getFilesDir().getAbsolutePath();
		ivDayWind.setImageDrawable(Drawable.createFromPath(filesDir + "/daywind.png"));
		ivDayWindDir.setImageDrawable(Drawable.createFromPath(filesDir + "/daywinddir.png"));
	}

	@Override
	IntentFilter getRefreshIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Actions.REFRESH_WEATHER);
		filter.addAction(Actions.REFRESH_IMAGE);
		return filter;
	}

	@Override
	void onRefreshIntentReceived(Intent intent)
	{
		if (Actions.REFRESH_WEATHER.equals(intent.getAction()))
		{
			updateData();
		}
		else if (Actions.REFRESH_IMAGE.equals(intent.getAction()))
		{
			String filesDir = appContext.getFilesDir().getAbsolutePath();
			String imageName = bundle.getString(Extras.IMG_NAME);
			if ("daywind.png".equals(imageName))
			{
				ivDayWind.setImageDrawable(Drawable.createFromPath(filesDir + "/daywind.png"));
			}
			else if ("daywinddir.png".equals(imageName))
			{
				ivDayWindDir
						.setImageDrawable(Drawable.createFromPath(filesDir + "/daywinddir.png"));
			}

			Dbug.log("Updating image [", imageName, "]");
		}
	}

}
