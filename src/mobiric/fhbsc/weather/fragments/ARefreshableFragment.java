package mobiric.fhbsc.weather.fragments;


import java.io.IOException;

import lib.debug.Dbug;
import lib.io.IOUtils;
import mobiric.fhbsc.weather.WeatherApp;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Base fragment class that registers for intent updates to receive refreshed information. Only
 * listens for local broadcasts sent by the {@link LocalBroadcastManager}.
 */
public abstract class ARefreshableFragment extends Fragment
{
	/** Application context for this fragment. */
	Context appContext;

	/**
	 * Handle to {@link WeatherApp} instance for caching data.
	 */
	WeatherApp myApp;

	/**
	 * Bundle containing data for this fragment. Initialised with the arguments returned by
	 * {@link #getArguments()}, and updated when {@link #refreshReceiver} gets a new intent.
	 */
	Bundle bundle = null;

	/** Receiver for refresh intents. Passes the intent to the implementing subclass. */
	private BroadcastReceiver refreshReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getExtras() != null)
			{
				bundle.putAll(intent.getExtras());
			}
			onRefreshIntentReceived(intent);
		}
	};

	public ARefreshableFragment()
	{
		super();
	}

	/**
	 * Initialises the application context for this fragment.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		appContext = activity.getApplicationContext();
		myApp = (WeatherApp) activity.getApplication();
	}

	/**
	 * Initialises the {@link #bundle}.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (bundle == null)
		{
			bundle = getArguments();
			if (bundle == null)
			{
				bundle = new Bundle();
			}
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		register();
		refreshOnResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregister();
	}

	/**
	 * Override this method to return the Intent Filter to match refreshed data sent to the
	 * implementing class.
	 * 
	 * @return {@link IntentFilter} that this {@link ARefreshableFragment} is registered to receive.
	 */
	abstract IntentFilter getRefreshIntentFilter();

	/**
	 * Override this method to receive notification of a data refresh. Any data received will
	 * already have been added to {@link #bundle} before this method is called.
	 * 
	 * @param intent
	 *            {@link Intent} received
	 */
	abstract void onRefreshIntentReceived(Intent intent);

	/**
	 * Override this method to do any data refresh that may be required when this
	 * {@link ARefreshableFragment} is created, or resumes from a paused state. This allows the
	 * fragment to display updated data that may have been refreshed while the
	 * {@link #refreshReceiver} was not registered.</p>
	 * 
	 * Strictly speaking this can be accomplished by overriding the {@link #onResume()} method.
	 * Creating this {@link #refreshOnResume()} abstract method means that this update stage cannot
	 * be forgotten.
	 */
	abstract void refreshOnResume();

	private void register()
	{
		LocalBroadcastManager.getInstance(appContext).registerReceiver(refreshReceiver,
				getRefreshIntentFilter());
	}

	private void unregister()
	{
		LocalBroadcastManager.getInstance(appContext).unregisterReceiver(refreshReceiver);
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
	public void updateImage(final ImageView view, String imagePath)
	{
		Drawable drawable = null;
		try
		{
			Bitmap bitmap = IOUtils.getBitmap(appContext, imagePath);
			drawable = new BitmapDrawable(getResources(), bitmap);
		}
		catch (IOException e)
		{
			Dbug.log("Image not updated [", imagePath, "] ", e.getLocalizedMessage());
		}

		view.setImageDrawable(drawable);

		// size view correctly
		view.post(new Runnable()
		{
			public void run()
			{

				int width = view.getWidth();
				// get new height based on image size plus 1 pixel for rounding error
				int height = (width * 180 / 300) / 4 * 3 + 1;
				LinearLayout.LayoutParams params =
						(LinearLayout.LayoutParams) view.getLayoutParams();
				params.height = height;
				view.setLayoutParams(params);
			}
		});
	}

	/**
	 * Override this to allow BACK button processing.
	 * 
	 * @return <code>true</code> if the BACK button has been processed; <code>false</code> to pass
	 *         the event on
	 */
	public boolean onBackPressed()
	{
		return false;
	}
}