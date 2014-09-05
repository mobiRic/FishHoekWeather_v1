package mobiric.fhbsc.weather;

import lib.debug.Dbug;
import lib.gson.MyGson;
import mobiric.fhbsc.weather.intents.IntentConstants.Actions;
import mobiric.fhbsc.weather.intents.IntentConstants.Extras;
import mobiric.fhbsc.weather.model.WeatherReading;
import mobiric.fhbsc.weather.tasks.BaseWebService;
import mobiric.fhbsc.weather.tasks.BaseWebService.OnBaseWebServiceResponseListener;
import mobiric.fhbsc.weather.tasks.ImageDownloader;
import mobiric.fhbsc.weather.tasks.ImageDownloader.OnImageDownloadedListener;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser;
import mobiric.fhbsc.weather.tasks.WeatherReadingParser.OnWeatherReadingParsedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;


/**
 * Singleton class containing various global variables for quick access. </p>
 * 
 * {@link WeatherApp} is guaranteed to be in memory (by definition) whenever the background
 * {@link Switcher} service is running, and is accessible from all activities & the service. This is
 * faster than reading from preference files each time a value is needed. </p>
 * 
 * {@link WeatherApp} also runs the various {@link AsyncTask} operations as this prevents memory
 * leaks that can be caused when calling such tasks from an {@link Activity}. If the device is
 * rotated, and new Activity is created and the task will get leaked. By moving the tasks to the
 * Application instance, this will not happen.
 */
public class WeatherApp extends Application implements OnBaseWebServiceResponseListener,
		OnWeatherReadingParsedListener, OnImageDownloadedListener
{
	public static final String BASE_URL = "http://www.fhbsc.co.za/fhbsc/weather/smartphone/";
	public static final String HOME_PAGE = BASE_URL + "index.html";

	/** Default weather data to return if no reading has been cached or received from the server. */
	private static final String DEFAULT_WEATHER_JSON =
			"{\"Time\":\"not updated\", \"windSpeed\":\"0 knots\", \"windDir\":\"0°\", \"windGust\":\"0 knots\", \"windGustDir\":\"0°\", \"barometer\":\"1013.0 mbar\", \"outTemp\":\"0°C\", \"outTempMin\":\"-15°C\", \"outTempMax\":\"-15°C\"}";

	/**
	 * Cached {@link WeatherReading} for quick loading.
	 */
	WeatherReading reading = null;

	public void onCreate()
	{
		super.onCreate();

		/* CRASHLYTICS */
		if (Dbug.CRASHLYTICS_ON)
		{
			Crashlytics.start(this);
		}

		/* FLURRY */
		// Flurry.init(this);

		// log version
		Dbug.log("Android version identified: SDK=", Build.VERSION.SDK, " SDK_INT=",
				Build.VERSION.SDK_INT, " RELEASE=", Build.VERSION.RELEASE, " CODENAME=",
				Build.VERSION.CODENAME);

		// initialise previous reading
		reading = loadWeatherReading();
	}


	/**
	 * @return last {@link WeatherReading} received from the server; <code>null</code> if nothing
	 *         cached.
	 */
	public WeatherReading getCachedWeatherReading()
	{
		return this.reading;
	}

	/**
	 * Caches the {@link WeatherReading} in JSON form to the settings.
	 */
	public void setCachedWeatherReading(WeatherReading reading)
	{
		this.reading = reading;

		saveWeatherReading(reading);
	}

	/** Saves {@link WeatherReading} data to {@link SharedPreferences} in JSON format. */
	private void saveWeatherReading(WeatherReading reading)
	{
		// save settings
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		String json = MyGson.PARSER.toJson(reading);
		editor.putString("READING", json);
		editor.commit();
	}

	/** Loads {@link WeatherReading} data from {@link SharedPreferences} in JSON format. */
	private WeatherReading loadWeatherReading()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String json = preferences.getString("READING", DEFAULT_WEATHER_JSON);

		WeatherReading reading = MyGson.PARSER.fromJson(json, WeatherReading.class);
		return reading;
	}

	/**
	 * Refreshes the site.
	 */
	void doRefresh()
	{
		new BaseWebService(this).execute(HOME_PAGE);

		// wind graphs
		new ImageDownloader(this, this).execute("http://www.fhbsc.co.za/fhbsc/weather/daywind.png",
				"daywind.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daywinddir.png", "daywinddir.png");


		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekwind.png", "weekwind.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekwinddir.png", "weekwinddir.png");

		// temperature graphs
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daytempdew.png", "daytempdew.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weektempdew.png", "weektempdew.png");

		// barometer graphs
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/daybarometer.png", "daybarometer.png");
		new ImageDownloader(this, this).execute(
				"http://www.fhbsc.co.za/fhbsc/weather/weekbarometer.png", "weekbarometer.png");
	}


	@Override
	public void onBaseWebServiceResult(String result)
	{
		// parse data
		new WeatherReadingParser(this).execute(result);
	}


	@Override
	public void onBaseWebServiceError(String error)
	{
		onBaseWebServiceResult(error);
	}


	@SuppressLint("NewApi")
	@Override
	public void onWeatherReadingParseResult(WeatherReading result)
	{
		if (result == null)
		{
			Intent refresh = new Intent(Actions.REFRESH_WEATHER);
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);
			return;
		}

		Dbug.log(result.toString());
		reading = result;

		// update time
		Intent refreshUpdateTime = new Intent(Actions.REFRESH_UPDATE_TIME);
		refreshUpdateTime.putExtra(Extras.TIME, result.time);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refreshUpdateTime);

		// update weather
		Intent refreshWeather = new Intent(Actions.REFRESH_WEATHER);

		// wind
		refreshWeather.putExtra(Extras.WIND_SPEED, result.windSpeed);
		refreshWeather.putExtra(Extras.WIND_DIR, result.windDir);
		refreshWeather.putExtra(Extras.WIND_GUST, result.windGust);
		refreshWeather.putExtra(Extras.WIND_GUST_DIR, result.windGustDir);

		// temperature
		refreshWeather.putExtra(Extras.OUT_TEMP, result.outTemp);
		refreshWeather.putExtra(Extras.OUT_TEMP_MIN, result.outTempMin);
		refreshWeather.putExtra(Extras.OUT_TEMP_MAX, result.outTempMax);

		// barometer
		refreshWeather.putExtra(Extras.BAROMETER, result.barometer);

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refreshWeather);

		// cache reading
		setCachedWeatherReading(reading);
	}


	@Override
	public void onWeatherReadingParseError(String error)
	{
		// ignore errors
		Dbug.log("Error downloading weather page [", error, "]");
	}


	@Override
	public void onImageDownloadSuccess(String filename)
	{
		Dbug.log("Image downloaded [", filename, "]");

		// update image
		Intent refresh = new Intent(Actions.REFRESH_IMAGE);
		refresh.putExtra(Extras.IMG_NAME, filename);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refresh);
	}


	@Override
	public void onImageDownloadError(String error)
	{
		// ignore errors
		Dbug.log("Error downloading image [", error, "]");
	}

}
