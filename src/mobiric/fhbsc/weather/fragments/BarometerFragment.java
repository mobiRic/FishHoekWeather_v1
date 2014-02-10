package mobiric.fhbsc.weather.fragments;

import java.util.Random;

import lib.debug.Dbug;
import mobiric.fhbsc.weather.R;
import mobiric.fhbsc.weather.WeatherApp;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import mobiric.fhbsc.weather.model.WeatherReading;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Fragment that displays the barometer data.
 */
public class BarometerFragment extends ARefreshableFragment
{
	/** Assumed maximum pressure the barometer will show. */
	public static final int MAX_PRESSURE_MBARS = 1050;
	/** Assumed minimum pressure the barometer will show. */
	public static final int MIN_PRESSURE_MBARS = 950;

	TextView tvBarometerPressure;
	ImageView ivDayBarometer;
	ImageView ivWeekBarometer;
	ImageView ivArrowBarometer;

	float pressureMBars = 1013.25f;
	float oldPressureMBars = 1013.25f;

	public BarometerFragment()
	{
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_barometer, container, false);
		tvBarometerPressure = (TextView) rootView.findViewById(R.id.tvBarometerPressure);
		ivDayBarometer = (ImageView) rootView.findViewById(R.id.ivDayBarometer);
		ivWeekBarometer = (ImageView) rootView.findViewById(R.id.ivWeekBarometer);

		ivArrowBarometer = (ImageView) rootView.findViewById(R.id.ivArrowBarometer);

		return rootView;
	}

	/**
	 * Updates the data displayed by this fragment. Called when data is refreshed, or when fragment
	 * is created or resumed. </p>
	 * 
	 * Data is fetched from the {@link WeatherApp} instance. Updating via {@link Intent} extras does
	 * not allow fragments to receive the update when paused.
	 * 
	 * @param animate
	 *            <code>true</code> to animate the changes (on a refresh); <code>false</code>
	 *            otherwise (on resume)
	 */
	void updateData(boolean animate)
	{
		// get data from the application cache
		WeatherReading reading = myApp.getCachedWeatherReading();

		String barometerPressure = reading.barometer;
		tvBarometerPressure.setText(barometerPressure);

		if (barometerPressure != null)
		{
			setPressureMBars(barometerPressure);
			rotateArrow(animate);
		}
	}

	void initImages()
	{
		updateImage(ivDayBarometer, "daybarometer.png");
		updateImage(ivWeekBarometer, "weekbarometer.png");
	}

	void rotateArrow(boolean animate)
	{
		float to = calcDegreesForPressure(pressureMBars);
		float from;
		if (animate)
		{
			from = calcDegreesForPressure(oldPressureMBars);
		}
		else
		{
			from = to;
		}

		RotateAnimation rotate =
				new RotateAnimation(from, to, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate.setFillAfter(true);
		rotate.setDuration(700);
		ivArrowBarometer.startAnimation(rotate);
	}

	/**
	 * Calculates the angle of the barometer arrow for a given pressure.
	 * 
	 * @param mBars
	 *            barometer pressure
	 * @return angle for the arrow
	 */
	float calcDegreesForPressure(float mBars)
	{

		float degrees =
				(mBars - MIN_PRESSURE_MBARS) * 360 / (MAX_PRESSURE_MBARS - MIN_PRESSURE_MBARS);
		return degrees;
	}

	public void setPressureMBars(String pressure)
	{
		float mBars;
		try
		{
			String strPressure = pressure.substring(0, pressure.length() - 1);
			mBars = Integer.parseInt(strPressure);
		}
		catch (NumberFormatException e)
		{
			mBars = 1013.25f;
		}

		// random data fluctuations for UI debugging
		if (Dbug.RANDOM_DATA)
		{
			mBars += new Random().nextInt(20) - 10;
		}

		// range check
		if (mBars >= MAX_PRESSURE_MBARS)
		{
			mBars = MAX_PRESSURE_MBARS;
		}
		else if (mBars < MIN_PRESSURE_MBARS)
		{
			mBars = MIN_PRESSURE_MBARS;
		}

		oldPressureMBars = pressureMBars;
		pressureMBars = mBars;
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
			updateData(true);
		}
		else if (Actions.REFRESH_IMAGE.equals(intent.getAction()))
		{
			String imageName = bundle.getString(Extras.IMG_NAME);
			if ("daybarometer.png".equals(imageName))
			{
				updateImage(ivDayBarometer, "daybarometer.png");
			}
			else if ("weekbarometer.png".equals(imageName))
			{
				updateImage(ivWeekBarometer, "weekbarometer.png");
			}

			Dbug.log("Updating image [", imageName, "]");
		}
	}

	@Override
	void refreshOnResume()
	{
		updateData(false);
		initImages();
	}

}
