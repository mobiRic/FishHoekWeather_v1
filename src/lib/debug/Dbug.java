package lib.debug;

import android.annotation.SuppressLint;
import android.util.Log;

public class Dbug
{
	/** Tag for logcat output. */
	public static final String TAG = "FH_WEATHER";

	/** Global static to define that debug code must be turned on. */
	public static final boolean DEBUG_ON = false;

	public static final boolean FLURRY_ON = !DEBUG_ON;
	public static final boolean CRASHLYTICS_ON = !DEBUG_ON;

	private static final boolean LOGGING_ON = DEBUG_ON && true;

	/** <code>true</code> to make random changes to the data on each update. */
	public static final boolean RANDOM_DATA = DEBUG_ON && true;

	/**
	 * Convenient debug log method that uses the application's default log tag. </p>
	 * 
	 * @param text
	 *            Array of Objects that create the debug message. This is preferable to
	 *            concatenating many strings into a single parameter as that cannot be optimised
	 *            away by ProGuard.
	 * @see https://blogs.oracle.com/binublog/entry/debug_print_and_varargs
	 */
	@SuppressLint("SimpleDateFormat")
	public static void log(Object... text)
	{
		logWithTag(TAG, text);
	}

	/**
	 * Convenient debug log method that allows for custom log tags. </p>
	 * 
	 * @param tag
	 *            Custom tag to appear in logcat
	 * @param text
	 *            Array of Objects that create the debug message. This is preferable to
	 *            concatenating many strings into a single parameter as that cannot be optimised
	 *            away by ProGuard.
	 * @see #log(Object...)
	 */
	@SuppressLint("SimpleDateFormat")
	public static void logWithTag(String tag, Object... text)
	{
		// release mode
		if (!LOGGING_ON)
		{
			return;
		}

		// manually concatenate the various arguments
		StringBuilder sb = new StringBuilder();
		if (text != null)
		{
			for (Object object : text)
			{
				if (object != null)
				{
					sb.append(object.toString());
				}
				else
				{
					sb.append("<null>");
				}
			}
		}

		Log.d(tag, sb.toString());
	}

}
