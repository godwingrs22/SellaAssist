package it.sella.sellaassist.ui;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.sella.sellaassist.R;
import it.sella.sellaassist.core.CalendarManager;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.data.SellaAssistProvider;
import it.sella.sellaassist.model.Event;
import it.sella.sellaassist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 28-Jul-16.
 */
public class DetailEventActivity extends AppCompatActivity {
    private static final String TAG = DetailEventActivity.class.getSimpleName();
    private Event event;
    private RadioGroup interestedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = getIntent().getParcelableExtra(EventsFragment.EVENT_KEY);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(event.getTitle());
//        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.md_blue_500));

        ImageView eventBannerImage = (ImageView) findViewById(R.id.detail_event_banner_image);
        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.detail_event_add);
        TextView description = (TextView) findViewById(R.id.detail_event_description);
        TextView createdByName = (TextView) findViewById(R.id.detail_event_createdByName);
        TextView createdByStartTimestamp = (TextView) findViewById(R.id.detail_event_createdBy_startTimestamp);
        ImageView profileImage = (ImageView) findViewById(R.id.detail_event_profileImage);
        TextView date = (TextView) findViewById(R.id.detail_event_date);
        TextView timeStamp = (TextView) findViewById(R.id.detail_event_time);
        TextView address = (TextView) findViewById(R.id.detail_event_address);
        RadioGroup interestedGroup = (RadioGroup) findViewById(R.id.detail_event_interested);

        description.setText(event.getDescription());
        createdByName.setText(event.getCreatedByName());
        createdByStartTimestamp.setText(Utility.getFeedTimeStamp(String.valueOf(event.getCreatedByTimestamp())));
        date.setText(Utility.getFormattedDate1(event.getStartTimestamp()));
        timeStamp.setText(Utility.getFormattedTime(event.getStartTimestamp()) + " - " + Utility.getFormattedTime(event.getEndTimestamp()));
        address.setText(event.getAddress());

        if (event.getInterested() == EventsFragment.EVENT_INTERESTED_YES) {
            interestedGroup.check(R.id.detail_event_interested_yes);
        } else if (event.getInterested() == EventsFragment.EVENT_INTERESTED_NO) {
            interestedGroup.check(R.id.detail_event_interested_no);
        } else {
            interestedGroup.clearCheck();
        }

        interestedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.detail_event_interested_yes:
                        Log.v(TAG, "<----Event Interested------>");
                        event.setInterested(EventsFragment.EVENT_INTERESTED_YES);
                        break;
                    case R.id.detail_event_interested_no:
                        Log.v(TAG, "<----Event Not Interested------>");
                        event.setInterested(EventsFragment.EVENT_INTERESTED_NO);
                        break;
                }
                updateEvent(event);
            }
        });

        Picasso.with(this)
                .load(Uri.parse(event.getCreatedByProfileImage()))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(profileImage);

        Picasso.with(this)
                .load(Uri.parse(event.getBannerImage()))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.event_placeholder)
                .into(eventBannerImage);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarManager calendarManager = new CalendarManager(getApplicationContext());
                calendarManager.addCalendarEvent(event);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateEvent(Event event) {
        ContentValues eventValues = new ContentValues();
        eventValues.put(SellaAssistContract.EventEntry.COLUMN_INTERESTED, event.getInterested());
        getContentResolver().update(SellaAssistContract.EventEntry.CONTENT_URI, eventValues, SellaAssistProvider.eventWithIdSelection, new String[]{String.valueOf(event.getId())});
    }

    private void checkNetwork() {
        if (!Utility.isInternetConnected(this)) {
            Snackbar.make(interestedGroup, getString(R.string.error_network_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkNetwork();
    }
}
