package it.sella.sellaassist.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import java.util.Date;

import it.sella.sellaassist.R;
import it.sella.sellaassist.adapter.EventsAdapter;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.data.SellaAssistProvider;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.sync.SellaAssistSyncAdapter;
import it.sella.sellaassist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class UpcomingEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = UpcomingEventsFragment.class.getSimpleName();
    private EventsAdapter eventsAdapter;
    private SwipeRefreshLayout eventsSwipeRefreshLayout;
    private RecyclerView eventsRecyclerView;
    private ViewSwitcher viewSwitcher;
    private User user;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_upcoming_position";
    private static final int UPCOMING_LOADER = 2;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_upcoming, container, false);

        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();

        eventsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.events_swipe_refresh);
        eventsRecyclerView = (RecyclerView) rootView.findViewById(R.id.events_item_list);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.upcoming_events_switcher);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventsRecyclerView.setLayoutManager(layoutManager);

        eventsAdapter = new EventsAdapter(getContext(), EventsFragment.EVENT_TYPE_UPCOMING);
        eventsRecyclerView.setAdapter(eventsAdapter);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());

       /* eventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Event event = eventsAdapter.getEvent(position);
                Intent intent = new Intent(getActivity(), DetailEventActivity.class).putExtra(EventsFragment.EVENT_KEY, event);
                ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
                getActivity().startActivity(intent, opts.toBundle());
            }
        }));*/


        eventsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEvent();
            }
        });

        eventsSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light);


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateEvent();
            }
        }, 500);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(UPCOMING_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "<-------onCreateLoader--------->");
        Uri eventsUri = SellaAssistContract.EventEntry.CONTENT_URI;
        String sortOrder = SellaAssistContract.EventEntry.COLUMN_START_TIMESTAMP + " ASC";
        String[] selectionArgs = new String[]{String.valueOf(new Date().getTime())};

        return new CursorLoader(getActivity(),
                eventsUri,
                null,
                SellaAssistProvider.upcomingEventSelection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "<-------onLoaderFinished--------->");
        eventsSwipeRefreshLayout.setRefreshing(false);
        checkForEvents(data);
        checkNetwork();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "<-------onLoaderReset--------->");
        eventsAdapter.swapCursor(null);
    }

    void checkForEvents(Cursor cursor) {
        if (cursor.getCount() > 0) {
            eventsAdapter.swapCursor(cursor);
            if (mPosition != ListView.INVALID_POSITION) {
                eventsRecyclerView.smoothScrollToPosition(mPosition);
            }
            if (R.id.events_item_list == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.upcoming_events_error == viewSwitcher.getNextView().getId()) {
            viewSwitcher.showNext();
        }
    }

    private void updateEvent() {
        eventsSwipeRefreshLayout.setRefreshing(true);
        SellaAssistSyncAdapter.syncImmediately(getActivity());
    }

    private void checkNetwork() {
        if (!Utility.isInternetConnected(getContext())) {
            Snackbar.make(viewSwitcher, getString(R.string.error_network_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkNetwork();
    }
}
