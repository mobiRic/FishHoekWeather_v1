package lib.debug;

import android.util.Log;

public class Dbug
{
	/** Tag for logcat output. */
	public static final String TAG = "FH_WEATHER";

	/** Global static to define that debug code must be turned on. */
	public static final boolean DEBUG_ON = true;

	@SuppressWarnings("unused")
	private static final boolean LOGGING_ON = DEBUG_ON && true;

	/** <code>true</code> to make random changes to the data on each update. */
	public static final boolean RANDOM_DATA = DEBUG_ON && true;


	/**
	 * Convenient debug log method. </p>
	 * 
	 * @param text
	 *            Array of Objects that create the debug message. This is preferable to
	 *            concatenating many strings into a single parameter as that cannot be optimised
	 *            away by ProGuard.
	 * @see https://blogs.oracle.com/binublog/entry/debug_print_and_varargs
	 */
	public static void log(Object... text)
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
				sb.append(object.toString());
			}
		}

		Log.d(TAG, sb.toString());
	}

}
