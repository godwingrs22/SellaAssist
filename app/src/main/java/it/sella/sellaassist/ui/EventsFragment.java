package it.sella.sellaassist.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.sella.sellaassist.R;
import it.sella.sellaassist.adapter.EventsPagerAdapter;
import it.sella.sellaassist.model.User;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class EventsFragment extends Fragment {
    private static final String TAG = EventsFragment.class.getSimpleName();
    private EventsPagerAdapter eventsPagerAdapter;
    private ViewPager mViewPager;
    private User user;
    public static final int EVENT_INTERESTED_NONE = -1;
    public static final int EVENT_INTERESTED_YES = 1;
    public static final int EVENT_INTERESTED_NO = 0;

    public static final int EVENT_TYPE_UPCOMING = 1;
    public static final int EVENT_TYPE_PREVIOUS = 0;

    public static final String EVENT_KEY = "event";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        TabLayout tabLayout = (TabLayout) appCompatActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);

        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();

        eventsPagerAdapter = new EventsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) rootView.findViewById(R.id.events_container);
        mViewPager.setAdapter(eventsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        return rootView;
    }
}
