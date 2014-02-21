package mobiric.fhbsc.weather.adapters;

import mobiric.fhbsc.weather.fragments.BarometerFragment;
import mobiric.fhbsc.weather.fragments.TemperatureFragment;
import mobiric.fhbsc.weather.fragments.WebWeatherFragment;
import mobiric.fhbsc.weather.fragments.WindFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
 * sections/tabs/pages.
 */
public class ScreenSwipeAdapter extends FragmentPagerAdapter
{
	Context context;
	WebWeatherFragment webWeatherFragment;

	public ScreenSwipeAdapter(Context context, FragmentManager fm)
	{
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position)
	{
		// TODO don't create a new fragment each time this is called
		switch (position)
		{
			case 0:
			{
				return getWebWeatherFragment();
			}
			case 1:
			{
				return new WindFragment();
			}
			case 2:
			{
				return new TemperatureFragment();
			}
			case 3:
			default:
			{
				return new BarometerFragment();
			}
			// case 1:
			// case 2:
			// {
			// // getItem is called to instantiate the fragment for the given page.
			// // Return a DummySectionFragment (defined as a static inner class
			// // below) with the page number as its lone argument.
			// Fragment fragment = new DummySectionFragment();
			// Bundle args = new Bundle();
			// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			// fragment.setArguments(args);
			// return fragment;
			// }
		}
	}

	/**
	 * Lazy initialiser for the Web view.
	 */
	private WebWeatherFragment getWebWeatherFragment()
	{
		if (webWeatherFragment == null)
		{
			webWeatherFragment = new WebWeatherFragment();
		}
		return webWeatherFragment;
	}

	@Override
	public int getCount()
	{
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		// Locale l = Locale.getDefault();
		switch (position)
		{
			case 0:
				// return context.getString(R.string.title_section1).toUpperCase(l);
				return "Live Feed";
			case 1:
				return "Wind";
			case 2:
				return "Temperature";
			case 3:
				return "Barometer";
		}
		return null;
	}

}
