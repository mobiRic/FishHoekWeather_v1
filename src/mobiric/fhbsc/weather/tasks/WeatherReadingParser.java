package mobiric.fhbsc.weather.tasks;

import lib.gson.MyGson;
import mobiric.fhbsc.weather.model.WeatherReading;
import android.os.AsyncTask;

/**
 * Task to parse the actual data from the web page in the background. Caller is notified of results
 * through the {@link WeatherReadingParser.OnWeatherReadingParsedListener} interface callbacks.
 */
public class WeatherReadingParser extends AsyncTask<String, Void, WeatherReading>
{

	/**
	 * Listener interface to be implemented by any class interested in calling this
	 * {@link WeatherReadingParser}.
	 */
	public static interface OnWeatherReadingParsedListener
	{
		/**
		 * Called when the response has been successfully parsed.
		 * 
		 * @return the response
		 */
		public void onWeatherReadingParseResult(WeatherReading result);

		/**
		 * Called when there was an error parsing the response.
		 * 
		 * @return error string
		 */
		public void onWeatherReadingParseError(String error);
	}

	OnWeatherReadingParsedListener listener;

	public WeatherReadingParser(OnWeatherReadingParsedListener listener)
	{
		super();
		this.listener = listener;
	}


	/**
	 * Parses the provided string to extract a {@link WeatherReading} data point.
	 * 
	 * @param Web
	 *            page must be passed as the first string parameter
	 * @return {@link WeatherReading} populated from the provided input
	 */
	@Override
	protected WeatherReading doInBackground(String... params)
	{
		// empty check
		if ((params == null) || (params.length < 1))
		{
			if (listener != null)
			{
				listener.onWeatherReadingParseError("Error: No data provided.");
			}
			return null;
		}

		try
		{
			return parseData(params[0]);
		}
		catch (Exception e)
		{
			if (listener != null)
			{
				listener.onWeatherReadingParseError("Error: " + e.getLocalizedMessage());
			}
			return null;
		}
	}

	@Override
	protected void onPostExecute(WeatherReading result)
	{
		super.onPostExecute(result);

		if (listener != null)
		{
			listener.onWeatherReadingParseResult(result);
		}
	}

	/**
	 * Parses the data to extract the weather.
	 * 
	 * @param data
	 *            web page containing the required data
	 * @return {@link WeatherReading} extracted from the provided web page
	 */
	WeatherReading parseData(String data)
	{
		// extract JSON string
		WeatherReading reading = parseWeather(data);

		// time is not part of the JSON string
		reading.time = parseTime(data);

		return reading;
	}

	/**
	 * Helper method to parse the time out of the HTML since it is not included in the JSON string.
	 */
	String parseTime(String data)
	{
		// start pos
		final String strBefore = "<li data-role=\"list-divider\">As at: ";
		int i = data.indexOf(strBefore) + strBefore.length();
		// end pos
		final String strAfter = "</li>";
		int j = data.indexOf(strAfter, i);

		String time = data.substring(i, j);
		return time;
	}

	/**
	 * Helper method to parse the weather reading out of the HTML.
	 */
	WeatherReading parseWeather(String data)
	{
		// start pos
		final String strBefore = "<-- FHBSC-JSON ";
		int i = data.indexOf(strBefore) + strBefore.length();
		// end pos
		final String strAfter = " -->";
		int j = data.indexOf(strAfter, i);

		String json = data.substring(i, j);
		WeatherReading reading = MyGson.PARSER.fromJson(json, WeatherReading.class);
		return reading;
	}

}
