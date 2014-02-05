package mobiric.fhbsc.weather.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Base fragment class that registers for intent updates to receive refreshed information. Only
 * listens for local broadcasts sent by the {@link LocalBroadcastManager}.
 */
public abstract class ARefreshableFragment extends Fragment
{
	/** Application context for this fragment. */
	Context appContext;

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
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregister();
	}

	/**
	 * Override this method to return the Action string used when sending refreshed data to the
	 * implementing class.
	 * 
	 * @return Action that this {@link ARefreshableFragment} is registered to receive.
	 */
	abstract String getRefreshIntentAction();

	/**
	 * Override this method to receive notification of a data refresh. Any data received will
	 * already have been added to {@link #bundle} before this method is called.
	 * 
	 * @param intent
	 *            {@link Intent} received
	 */
	abstract void onRefreshIntentReceived(Intent intent);

	private void register()
	{
		IntentFilter filter = new IntentFilter(getRefreshIntentAction());
		LocalBroadcastManager.getInstance(appContext).registerReceiver(refreshReceiver, filter);
	}

	private void unregister()
	{
		LocalBroadcastManager.getInstance(appContext).unregisterReceiver(refreshReceiver);
	}
}