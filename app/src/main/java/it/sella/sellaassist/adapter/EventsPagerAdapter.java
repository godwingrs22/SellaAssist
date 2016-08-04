package it.sella.sellaassist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.sella.sellaassist.ui.PreviousEventsFragment;
import it.sella.sellaassist.ui.UpcomingEventsFragment;

/**
 * Created by GodwinRoseSamuel on 28-Jul-16.
 */
public class EventsPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String titles[] = new String[]{"UPCOMING", "PREVIOUS"};

    public EventsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                UpcomingEventsFragment upcomingEventsFragment = new UpcomingEventsFragment();
                return upcomingEventsFragment;
            case 1:
                PreviousEventsFragment previousEventsFragment = new PreviousEventsFragment();
                return previousEventsFragment;
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
