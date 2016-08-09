package it.sella.assist.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import java.util.Calendar;

import it.sella.assist.R;
import it.sella.assist.adapter.BiometricAdapter;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.service.BiometricService;
import it.sella.assist.service.FeedService;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class BiometricFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = BiometricFragment.class.getSimpleName();
    public static final int BIOMETRIC_ADD_MANUAL = 112;
    public static final String BIOMETRIC_ADD_KEY = "biometric";

    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int BIOMETRIC_LOADER = 1;

    private BiometricAdapter biometricAdapter;
    private ExpandableListView biometricListView;
    private SwipeRefreshLayout biometricSwipeRefreshLayout;
    private EditText fromDateText;
    private EditText toDateText;
    private Button filterButton;
    private FloatingActionButton addManual;
    private ViewSwitcher viewSwitcher;
    private String gbsId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_biometric, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        TabLayout tabLayout = (TabLayout) appCompatActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        fromDateText = (EditText) rootView.findViewById(R.id.biometric_from_date_text);
        toDateText = (EditText) rootView.findViewById(R.id.biometric_to_date_text);
        filterButton = (Button) rootView.findViewById(R.id.biomteric_filter);
        addManual = (FloatingActionButton) rootView.findViewById(R.id.biomteric_add_manual);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.biometric_switcher);
        biometricSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.biometric_swipe_refresh);
        biometricListView = (ExpandableListView) rootView.findViewById(R.id.biometric_item_list);

        biometricAdapter = new BiometricAdapter(null, getContext());
        biometricListView.setAdapter(biometricAdapter);

        biometricSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateBiometric();
            }
        });

        biometricSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light);

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        fromDateText.setText(Utility.getFormattedDate(c.getTimeInMillis()));
                    }
                };

                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        if (view.isShown()) {
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day);
                            toDateText.setText(Utility.getFormattedDate(c.getTimeInMillis()));
                        }
                    }
                };
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        addManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BiometricAddActivity.class);
                startActivityForResult(intent, BIOMETRIC_ADD_MANUAL);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDateText.getText().toString().equals("") || toDateText.getText().toString().equals("")) {
                    Snackbar.make(getView(), "Please specify from and to date", Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    biometricSwipeRefreshLayout.setRefreshing(true);
                    biometricSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putString("fromDate", fromDateText.getText().toString());
                            bundle.putString("toDate", toDateText.getText().toString());
                            getLoaderManager().restartLoader(BIOMETRIC_LOADER, bundle, BiometricFragment.this);
                        }
                    }, 1000);
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BIOMETRIC_ADD_MANUAL && data != null) {
            if (data.getBooleanExtra(BIOMETRIC_ADD_KEY, false)) {
                biometricSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLoaderManager().restartLoader(BIOMETRIC_LOADER, null, BiometricFragment.this);
                    }
                }, 500);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        biometricSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBiometric();
            }
        }, 500);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(BIOMETRIC_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "<-------onCreateLoader--------->");
        CursorLoader cursorLoader;
        if (args != null) {
            String fromDate = args.getString("fromDate");
            String toDate = args.getString("toDate");

            cursorLoader = new CursorLoader(getActivity(),
                    SellaAssistContract.BiometricEntry.CONTENT_URI,
                    SellaAssistContract.BiometricEntry.BIOMETRIC_PROJECTION,
                    SellaAssistContract.BiometricEntry.COLUMN_DATE + " BETWEEN ? AND ?",
                    new String[]{fromDate, toDate},
                    SellaAssistContract.BiometricEntry._ID + " DESC");
        } else {
            cursorLoader = new CursorLoader(getActivity(),
                    SellaAssistContract.BiometricEntry.CONTENT_URI,
                    SellaAssistContract.BiometricEntry.BIOMETRIC_PROJECTION,
                    null,
                    null,
                    SellaAssistContract.BiometricEntry._ID + " DESC");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "<-------onLoaderFinished--------->");
        biometricSwipeRefreshLayout.setRefreshing(false);
        checkForBiometric(cursor);
        checkNetwork();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "<-------onLoaderReset--------->");
        int id = loader.getId();
        if (id != BIOMETRIC_LOADER) {
            try {
                biometricAdapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
                Log.w("TAG", "Adapter expired, try again on the next query: " + e.getMessage());
            }
        } else {
            biometricAdapter.setGroupCursor(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    void checkForBiometric(Cursor cursor) {
        if (cursor.getCount() > 0) {
            biometricAdapter.setGroupCursor(cursor);
            if (mPosition != ListView.INVALID_POSITION) {
                biometricListView.smoothScrollToPosition(mPosition);
            }
            if (R.id.biometric_item_list == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.biometric_error == viewSwitcher.getNextView().getId()) {
            viewSwitcher.showNext();
        }
    }

    private void updateBiometric() {
        biometricSwipeRefreshLayout.setRefreshing(true);
//        getLoaderManager().restartLoader(BIOMETRIC_LOADER, null, BiometricFragment.this);
        Intent biometricServiceIntent = new Intent(getActivity(), BiometricService.class);
        biometricServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
        getActivity().startService(biometricServiceIntent);
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
