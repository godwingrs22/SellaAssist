package it.sella.assist.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.sella.assist.R;
import it.sella.assist.core.CalendarManager;
import it.sella.assist.core.EventManager;
import it.sella.assist.dao.EventDAO;
import it.sella.assist.model.Event;
import it.sella.assist.ui.DetailEventActivity;
import it.sella.assist.ui.EventsFragment;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 27-Jul-16.
 */
public class EventsAdapter extends RecyclerViewCursorAdapter<EventsAdapter.EventViewHolder> implements View.OnClickListener {
    private static final String TAG = EventsAdapter.class.getSimpleName();

    private Context context;
    private OnItemClickListener onItemClickListener;
    private int eventType;

    public EventsAdapter(Context context, int type) {
        super();
        this.context = context;
        eventType = type;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, Cursor cursor) {
        holder.bindData(context, cursor);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_events, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onClick(final View view) {
        if (this.onItemClickListener != null) {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Cursor cursor);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public final ImageView bannerImage;
        public final ImageView profileImage;
        public final TextView createdByName;
        public final TextView createdByStartTimestamp;
        public final TextView eventDay;
        public final TextView eventMonth;
        public final TextView title;
        public final TextView eventTime;
        public final TextView address;
        public final TextView interested;
        public final TextView remainder;
        private final LinearLayout actionRequiredLayout;
        private final LinearLayout detailLayout;
        private final LinearLayout createdLayout;

        public EventViewHolder(View view) {
            super(view);
            bannerImage = (ImageView) view.findViewById(R.id.list_event_banner);
            profileImage = (ImageView) view.findViewById(R.id.list_event_profileImage);
            createdByName = (TextView) view.findViewById(R.id.list_event_createdByName);
            createdByStartTimestamp = (TextView) view.findViewById(R.id.list_event_createdBy_startTimestamp);
            eventDay = (TextView) view.findViewById(R.id.list_event_day);
            eventMonth = (TextView) view.findViewById(R.id.list_event_month);
            title = (TextView) view.findViewById(R.id.list_event_title);
            eventTime = (TextView) view.findViewById(R.id.list_event_time);
            address = (TextView) view.findViewById(R.id.list_event_address);
            interested = (TextView) view.findViewById(R.id.list_event_attend_interested);
            remainder = (TextView) view.findViewById(R.id.list_event_attend_remainder);
            actionRequiredLayout = (LinearLayout) view.findViewById(R.id.list_events_action_required_layout);
            detailLayout = (LinearLayout) view.findViewById(R.id.list_events_detail_layout);
            createdLayout = (LinearLayout) view.findViewById(R.id.list_events_created_layout);
        }

        public void bindData(final Context context, final Cursor cursor) {

            final EventDAO eventDAO = new EventDAO(context);
            final Event event = eventDAO.getEvent(cursor);

            Glide.with(context)
                    .load(Uri.parse(event.getBannerImage()))
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.event_placeholder)
                    .crossFade()
                    .into(bannerImage);
            bannerImage.setAdjustViewBounds(true);

            Glide.with(context)
                    .load(Uri.parse(event.getCreatedByProfileImage()))
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .crossFade()
                    .into(profileImage);

            createdByName.setText(event.getCreatedByName());
            createdByStartTimestamp.setText(Utility.getFeedTimeStamp(event.getCreatedByTimestamp()));
            eventDay.setText(Utility.getEventsDay(event.getStartTimestamp()));
            eventMonth.setText(Utility.getEventsMonth(event.getStartTimestamp()));
            title.setText(event.getTitle());
            eventTime.setText(Utility.getFormattedTime(event.getStartTimestamp()) + " - " + Utility.getFormattedTime(event.getEndTimestamp()));
            address.setText(event.getAddress());

            if (EventsFragment.EVENT_TYPE_UPCOMING == eventType) {
                actionRequiredLayout.setVisibility(View.VISIBLE);

                if (EventsFragment.EVENT_INTERESTED_YES == event.getInterested()) {
                    interested.setText("YES");
                    interested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp, 0, 0, 0);
                    interested.setTextColor(ContextCompat.getColor(context, R.color.md_green_500));
                } else if (EventsFragment.EVENT_INTERESTED_NO == event.getInterested()) {
                    interested.setText("NO");
                    interested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_down_black_24dp, 0, 0, 0);
                    interested.setTextColor(ContextCompat.getColor(context, R.color.md_red_500));
                } else {
                    interested.setText("INTERESTED");
                    interested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up_down_black_24dp, 0, 0, 0);
                    interested.setTextColor(ContextCompat.getColor(context, R.color.md_pink_200));
                }
                interested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        PopupMenu popup = new PopupMenu(context, interested);
                        popup.getMenuInflater().inflate(R.menu.event_interested_popup_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.interested_yes) {
                                    event.setInterested(EventsFragment.EVENT_INTERESTED_YES);
                                    UpdateEventTask updateEventTask = new UpdateEventTask(event, v);
                                    updateEventTask.execute();
                                    Log.v(TAG, "<----Event Interested------>" + event.getInterested());

                                    interested.setText("YES");
                                    interested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp, 0, 0, 0);
                                    interested.setTextColor(ContextCompat.getColor(context, R.color.md_green_500));
                                } else if (id == R.id.interested_no) {
                                    event.setInterested(EventsFragment.EVENT_INTERESTED_NO);
                                    final EventDAO eventDAO = new EventDAO(context);
                                    eventDAO.updateEvent(event);
                                    Log.v(TAG, "<----Event Interested------>" + event.getInterested());

                                    interested.setText("NO");
                                    interested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_down_black_24dp, 0, 0, 0);
                                    interested.setTextColor(ContextCompat.getColor(context, R.color.md_red_500));
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                });

                remainder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        PopupMenu popup = new PopupMenu(context, remainder);
                        popup.getMenuInflater().inflate(R.menu.event_add_remainder_popup_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.add_remainder) {
                                    CalendarManager calendarManager = new CalendarManager(context);
                                    calendarManager.addCalendarEvent(event);
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                });
            } else if (EventsFragment.EVENT_TYPE_PREVIOUS == eventType) {
                actionRequiredLayout.setVisibility(View.GONE);
            }

            detailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailEventActivity.class).putExtra(Utility.EVENT_KEY, event);
                    context.startActivity(intent);
                }
            });
            createdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailEventActivity.class).putExtra(Utility.EVENT_KEY, event);
                    context.startActivity(intent);
                }
            });
            bannerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailEventActivity.class).putExtra(Utility.EVENT_KEY, event);
                    context.startActivity(intent);
                }
            });
        }
    }

    public class UpdateEventTask extends AsyncTask<String, Void, Boolean> {

        private Event event;
        private View view;
        private ProgressDialog progressDialog;

        UpdateEventTask(Event event, View view) {
            this.event = event;
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage("Sending request...!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Thread.sleep(1000);
                String gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", context);
                EventManager eventManager = new EventManager(context);
                if (eventManager.doInterestedEvent(gbsId, event.getId())) {
                    final EventDAO eventDAO = new EventDAO(context);
                    eventDAO.updateEvent(event);
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            progressDialog.dismiss();
            if (isSuccess) {
                try {
                    Snackbar.make(view, "Request Sent Successful", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TextView interestedView = (TextView) view.findViewById(R.id.list_event_attend_interested);
                interestedView.setText("INTERESTED");
                interestedView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up_down_black_24dp, 0, 0, 0);
                interestedView.setTextColor(ContextCompat.getColor(context, R.color.md_pink_200));
                try {
                    Snackbar.make(view, "Unable to sent request at the moment", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
