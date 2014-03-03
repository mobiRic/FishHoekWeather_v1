package mobiric.fhbsc.weather;


import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import lib.debug.Dbug;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Provides auto refresh capability to the app.
 */
public class AutoRefreshActivity extends FragmentActivity
{
	private static final int MANUAL_REFRESH = -1;

	/**
	 * Handle to {@link WeatherApp} instance for caching data.
	 */
	WeatherApp myApp;

	int[] autoRefreshValues;
	int autoRefreshPeriod;
	Timer autoRefreshTimer = null;

	public AutoRefreshActivity()
	{
		super();
	}

	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);

		myApp = (WeatherApp) getApplication();
		autoRefreshValues = getResources().getIntArray(R.array.auto_refresh_values);
		autoRefreshPeriod = autoRefreshValues[loadAutoRefreshSetting()];
	}


	/**
	 * Cancel auto refresh when paused.
	 */
	@Override
	public void onPause()
	{
		setAutoRefreshTimer(MANUAL_REFRESH);

		super.onPause();
	}

	/**
	 * Restart auto refresh when resumed.
	 */
	@Override
	public void onResume()
	{
		autoRefreshPeriod = autoRefreshValues[loadAutoRefreshSetting()];
		if (MANUAL_REFRESH != autoRefreshPeriod)
		{
			setAutoRefreshTimer(autoRefreshPeriod);
			doRefresh();
		}

		super.onResume();
	}

	/**
	 * Adds the auto-refresh options spinner.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);

		// auto refresh spinner
		final Spinner s = (Spinner) menu.findItem(R.id.menu_spinner).getActionView();
		SpinnerAdapter mSpinnerAdapter =
				ArrayAdapter.createFromResource(this, R.array.auto_refresh_entries,
						android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(mSpinnerAdapter);
		s.setSelection(loadAutoRefreshSetting());
		s.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// set the auto refresh period
				setAutoRefreshPeriod(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// nothing selected
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Sets the auto refresh period in seconds.
	 * 
	 * @param position
	 *            Position in the auto refresh entries & values arrays
	 */
	private void setAutoRefreshPeriod(int position)
	{
		// save setting
		saveAutoRefreshSetting(position);

		// set timer
		autoRefreshPeriod = autoRefreshValues[position];
		setAutoRefreshTimer(autoRefreshPeriod);
	}

	/**
	 * Sets a repeating timer task to refresh the data.
	 * 
	 * @param autoRefreshPeriod
	 *            Time in millis between auto-refresh calls
	 */
	private void setAutoRefreshTimer(int autoRefreshPeriod)
	{
		Dbug.log("auto refresh - ", autoRefreshPeriod);

		// cancel previous tasks
		if (autoRefreshTimer != null)
		{
			autoRefreshTimer.cancel();
			autoRefreshTimer.purge();
			autoRefreshTimer = null;
		}

		if (autoRefreshPeriod != MANUAL_REFRESH)
		{
			autoRefreshTimer = new Timer();
			autoRefreshTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					doRefresh();
				}
			}, autoRefreshPeriod, autoRefreshPeriod);
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void saveAutoRefreshSetting(int position)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor e = prefs.edit();
		e.putInt("AUTO_REFRESH", position);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			e.apply();
		}
		else
		{
			e.commit();
		}
	}

	private int loadAutoRefreshSetting()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getInt("AUTO_REFRESH", 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_refresh:
			{
				// manually set the pull to refresh library
				doRefresh();

				break;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}

		// handled by us
		return true;
	}

	/**
	 * Helper method to start refresh from button or auto refresh.
	 */
	void doRefresh()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				Dbug.log("... refreshing ... ");

				myApp.doRefresh();
			}
		});
	}

}