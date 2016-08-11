package it.sella.assist.ui;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import it.sella.assist.AppController;
import it.sella.assist.R;
import it.sella.assist.adapter.FeedsAdapter;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.data.SellaAssistContract.FeedEntry;
import it.sella.assist.model.User;
import it.sella.assist.service.FeedService;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class FeedsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FeedsFragment.class.getSimpleName();
    private FeedsAdapter feedsAdapter;
    private RecyclerView feedRecylerView;
    private SwipeRefreshLayout feedSwipeRefreshLayout;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int FEED_LOADER = 0;
    private ContentObserver mObserver;
    private ViewSwitcher viewSwitcher;
    private String gbsId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", getContext());
       /* mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                Log.v(TAG, "<-------onChange--------->");
            }
        };
        getContext().getContentResolver().registerContentObserver(FeedEntry.CONTENT_URI, false, mObserver);*/
    }

    /*@Override
    public void onDestroy() {
        getContext().getContentResolver().unregisterContentObserver(mObserver);
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feeds, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        TabLayout tabLayout = (TabLayout) appCompatActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        feedSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_swipe_refresh);
        feedRecylerView = (RecyclerView) rootView.findViewById(R.id.feeds_item_list);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.feeds_switcher);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        feedRecylerView.setLayoutManager(layoutManager);

        feedsAdapter = new FeedsAdapter(getContext());
        feedRecylerView.setAdapter(feedsAdapter);

        feedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });

        feedSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FEED_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "<-------onCreateLoader--------->");

        return new CursorLoader(getActivity(),
                FeedEntry.CONTENT_URI,
                FeedEntry.FEED_PROJECTIONS,
                SellaAssistContract.UserEntry.COLUMN_GBS_ID + " =?",
                new String[]{gbsId},
                FeedEntry.COLUMN_FEED_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(TAG, "<-------onLoaderFinished--------->");
        feedSwipeRefreshLayout.setRefreshing(false);
        checkForFeed(cursor);
        checkNetwork();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.v(TAG, "<-------onLoaderReset--------->");
        feedsAdapter.swapCursor(null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateFeed();
            }
        }, 500);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    void checkForFeed(Cursor cursor) {
        if (cursor.getCount() > 0) {
            feedsAdapter.swapCursor(cursor);
            if (mPosition != ListView.INVALID_POSITION) {
                feedRecylerView.smoothScrollToPosition(mPosition);
            }
            if (R.id.feeds_item_list == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.feed_error == viewSwitcher.getNextView().getId()) {
            viewSwitcher.showNext();
        }
    }

    private void updateFeed() {
        feedSwipeRefreshLayout.setRefreshing(true);
        Intent feedServiceIntent = new Intent(getActivity(), FeedService.class);
        feedServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
        feedServiceIntent.putExtra(Utility.BEACON_ID_KEY, AppController.DEFAULT_BEACON_REGION.getMinor());
        getActivity().startService(feedServiceIntent);
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
