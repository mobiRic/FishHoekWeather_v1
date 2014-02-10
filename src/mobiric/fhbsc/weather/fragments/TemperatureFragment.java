package mobiric.fhbsc.weather.fragments;

import java.util.Random;

import lib.debug.Dbug;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Fragment that displays the temperature data.
 */
public class TemperatureFragment extends ARefreshableFragment
{
	/** Assumed maximum temperature the thermometer will show. */
	public static final int MAX_TEMP_RANGE = 40;
	/** Assumed minimum temperature the thermometer will show. */
	public static final int MIN_TEMP_RANGE = -15;

	TextView tvOutTemp;
	ImageView ivDayTempDew;
	ImageView ivWeekTempDew;
	ImageView ivThermometer;
	View vThermometerRed;

	float tempDegrees = MIN_TEMP_RANGE;
	float oldTempDegrees = MIN_TEMP_RANGE;

	public TemperatureFragment()
	{
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
		tvOutTemp = (TextView) rootView.findViewById(R.id.tvOutTemp);
		ivDayTempDew = (ImageView) rootView.findViewById(R.id.ivDayTempDew);
		ivWeekTempDew = (ImageView) rootView.findViewById(R.id.ivWeekTempDew);

		ivThermometer = (ImageView) rootView.findViewById(R.id.ivThermometer);
		vThermometerRed = rootView.findViewById(R.id.vThermometerRed);

		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		updateData();
		initImages();
	}

	void updateData()
	{
		// get data from the extras
		String outTemp = bundle.getString(Extras.OUT_TEMP);
		tvOutTemp.setText(outTemp);

		if (outTemp != null)
		{
			setTempDegrees(outTemp);
			setThermometerHeight();
		}
	}

	void initImages()
	{
		updateImage(ivDayTempDew, "daytempdew.png");
		updateImage(ivWeekTempDew, "weektempdew.png");
	}

	void setThermometerHeight()
	{
		int oldTopMargin = calcOffsetForDegrees(oldTempDegrees);
		int topMargin = calcOffsetForDegrees(tempDegrees);

		TranslateAnimation translate = new TranslateAnimation(0, 0, oldTopMargin, topMargin);
		translate.setFillAfter(true);
		translate.setDuration(700);
		vThermometerRed.startAnimation(translate);
	}

	/**
	 * Calculates the offset of the red thermometer background view, based on a given temperature.
	 * 
	 * @param degrees
	 *            temperature
	 * @return offset for the view
	 */
	int calcOffsetForDegrees(float degrees)
	{
		float heightRed =
				(degrees - MIN_TEMP_RANGE) * ivThermometer.getHeight()
						/ (MAX_TEMP_RANGE - MIN_TEMP_RANGE);
		int topOffset = (int) (ivThermometer.getHeight() - heightRed);
		return topOffset;
	}

	public void setTempDegrees(String temp)
	{
		float degrees;
		try
		{
			String strDegrees = temp.substring(0, temp.length() - 2);
			degrees = Float.parseFloat(strDegrees);
		}
		catch (NumberFormatException e)
		{
			degrees = 20;
		}

		// range check
		if (degrees >= MAX_TEMP_RANGE)
		{
			degrees = MAX_TEMP_RANGE;
		}
		else if (degrees < MIN_TEMP_RANGE)
		{
			degrees = MIN_TEMP_RANGE;
		}

		oldTempDegrees = tempDegrees;
		tempDegrees = degrees;
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
			String imageName = bundle.getString(Extras.IMG_NAME);
			if ("daytempdew.png".equals(imageName))
			{
				updateImage(ivDayTempDew, "daytempdew.png");
			}
			else if ("weektempdew.png".equals(imageName))
			{
				updateImage(ivWeekTempDew, "weektempdew.png");
			}

			Dbug.log("Updating image [", imageName, "]");
		}
	}

}
