package mobiric.fhbsc.weather.intents;

/**
 * Declares constants used for Intents. Includes Actions, keys in the Extras, common values.
 */
public class IntentConstants
{
	/** Declares intent actions. */
	public static class Actions
	{
		public static final String REFRESH_WEB_WEATHER = "REFRESH_WEB_WEATHER";
	}

	/** Declares constants used when passing arguments in Bundles. */
	public static class Extras
	{
		/** Fragment argument for the base URL to when loading the data. */
		public static final String BASE_URL = "BASE_URL";
		/** Fragment argument for the HTML data to load. */
		public static final String HTML_DATA = "HTML_DATA";
	}
}