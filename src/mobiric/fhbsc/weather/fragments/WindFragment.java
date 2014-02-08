package mobiric.fhbsc.weather.fragments;

import java.util.Random;

import lib.debug.Dbug;
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
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
	ImageView ivArrowWindDir;

	int windDirDegrees = 180;
	int oldWindDirDegrees = 180;

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

		ivArrowWindDir = (ImageView) rootView.findViewById(R.id.ivArrowWindDir);

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

		if (windDir != null)
		{
			setWindDirDegrees(windDir);
			rotateArrow();
		}
	}

	void initImages()
	{
		String filesDir = appContext.getFilesDir().getAbsolutePath();
		updateImage(ivDayWind, filesDir + "/daywind.png");
		updateImage(ivDayWindDir, filesDir + "/daywinddir.png");
	}

	void rotateArrow()
	{
		RotateAnimation rotate =
				new RotateAnimation(oldWindDirDegrees, windDirDegrees,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF,
						0.5f);
		rotate.setFillAfter(true);
		rotate.setDuration(700);
		ivArrowWindDir.startAnimation(rotate);
	}

	public void setWindDirDegrees(String windDir)
	{
		int degrees;
		try
		{
			String strDegrees = windDir.substring(0, windDir.length() - 1);
			degrees = Integer.parseInt(strDegrees);
		}
		catch (NumberFormatException e)
		{
			degrees = 180;
		}

		// range check
		if (degrees >= 360)
		{
			degrees -= 360;
		}
		else if (degrees < 0)
		{
			degrees += 360;
		}

		oldWindDirDegrees = windDirDegrees;
		windDirDegrees = degrees;
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
				updateImage(ivDayWind, filesDir + "/daywind.png");
			}
			else if ("daywinddir.png".equals(imageName))
			{
				updateImage(ivDayWindDir, filesDir + "/daywinddir.png");
			}

			Dbug.log("Updating image [", imageName, "]");
		}
	}

	/**
	 * Updates the given view with an image file. Also resizes the view to fit the width of the
	 * screen.
	 * 
	 * @param view
	 *            {@link ImageView} to update
	 * @param imagePath
	 *            path to the image
	 */
	public void updateImage(ImageView view, String imagePath)
	{
		view.setImageDrawable(Drawable.createFromPath(imagePath));
		int width = view.getWidth();

		// get new height based on image size plus 1 pixel for rounding error
		int height = (width * 180 / 300) / 4 * 3 + 1;
		RelativeLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.height = height;
	}

}
