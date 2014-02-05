package mobiric.fhbsc.weather.adapters;

import mobiric.fhbsc.weather.fragments.WebWeatherFragment;
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

	public ScreenSwipeAdapter(Context context, FragmentManager fm)
	{
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position)
	{
		switch (position)
		{
			case 0:
			default:
			{
				return new WebWeatherFragment();
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

	@Override
	public int getCount()
	{
		// Show 3 total pages.
		return 1;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		// Locale l = Locale.getDefault();
		// switch (position)
		// {
		// case 0:
		// return context.getString(R.string.title_section1).toUpperCase(l);
		// case 1:
		// return context.getString(R.string.title_section2).toUpperCase(l);
		// case 2:
		// return context.getString(R.string.title_section3).toUpperCase(l);
		// }
		// return null;
		return "Live Feed";
	}

}
