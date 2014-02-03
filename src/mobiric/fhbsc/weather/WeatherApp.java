package mobiric.fhbsc.weather;

import lib.debug.Dbug;
import lib.gson.MyGson;
import mobiric.fhbsc.weather.model.WeatherReading;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;


/**
 * Singleton class containing various global variables for quick access. </p>
 * 
 * {@link WeatherApp} is guaranteed to be in memory (by definition) whenever the background
 * {@link Switcher} service is running, and is accessible from all activities & the service. This is
 * faster than reading from preference files each time a value is needed.
 */
public class WeatherApp extends Application
{

	/**
	 * Cached {@link WeatherReading} for quick loading.
	 */
	WeatherReading reading = null;

	public void onCreate()
	{
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

	/** Saves {@link WeatherReading} data in JSON format. */
	private void saveWeatherReading(WeatherReading reading)
	{
		// save settings
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		String json = MyGson.PARSER.toJson(reading);
		editor.putString("READING", json);
		editor.commit();
	}

	/** Loads {@link WeatherReading} data in JSON format. */
	private WeatherReading loadWeatherReading()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String json = preferences.getString("READING", null);

		WeatherReading reading = MyGson.PARSER.fromJson(json, WeatherReading.class);
		return reading;
	}
}
