package it.sella.assist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.data.SellaAssistContract.EventEntry;
import it.sella.assist.data.SellaAssistProvider;
import it.sella.assist.model.Event;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class EventDAO {

    private static final String TAG = EventDAO.class.getSimpleName();
    private Context context;

    public EventDAO(Context context) {
        this.context = context;
    }

    private ContentValues createEventContentValues(Event event) {
        final ContentValues eventValues = new ContentValues();
        eventValues.put(EventEntry.COLUMN_ID, event.getId());
        eventValues.put(EventEntry.COLUMN_TITLE, event.getTitle());
        eventValues.put(EventEntry.COLUMN_START_TIMESTAMP, event.getStartTimestamp());
        eventValues.put(EventEntry.COLUMN_END_TIMESTAMP, event.getEndTimestamp());
        eventValues.put(EventEntry.COLUMN_BANNER_IMAGE, event.getBannerImage());
        eventValues.put(EventEntry.COLUMN_CREATED_BY_NAME, event.getCreatedByName());
        eventValues.put(EventEntry.COLUMN_CREATED_BY_PROFILE_IMAGE, event.getCreatedByProfileImage());
        eventValues.put(EventEntry.COLUMN_CREATED_BY_TIMESTAMP, event.getCreatedByTimestamp());
        eventValues.put(EventEntry.COLUMN_ADDRESS, event.getAddress());
        eventValues.put(EventEntry.COLUMN_INTERESTED, event.getInterested());
        eventValues.put(EventEntry.COLUMN_REMAINDER, String.valueOf(event.isRemainder()));
        eventValues.put(EventEntry.COLUMN_DESCRIPTION, event.getDescription());

        return eventValues;
    }

    public void addEvent(Event event) {
        Log.v(TAG, "<-----Adding Event---->" + event);
        context.getContentResolver().insert(EventEntry.CONTENT_URI, createEventContentValues(event));
    }


    public void addAllEvents(List<Event> events) {
        Log.v(TAG, "<-----Adding all Events---->" + events);
        Vector<ContentValues> eventVector = new Vector<>();
        for (Event event : events) {
            eventVector.add(createEventContentValues(event));
        }
        if (eventVector.size() > 0) {
            ContentValues[] eventArray = new ContentValues[eventVector.size()];
            eventVector.toArray(eventArray);
            context.getContentResolver().bulkInsert(EventEntry.CONTENT_URI, eventArray);
            Log.v(TAG, "<-----" + eventVector.size() + " events inserted------->");
        }
    }

    public Event getEvent(long id) {
        Log.v(TAG, "<-----Getting event with id---->" + id);
        Event event = null;
        Uri eventUri = EventEntry.buildEventById(event.getId());
        Log.v(TAG, "<-----eventUri---->" + eventUri);

        Cursor eventCursor = context.getContentResolver().query(
                eventUri,
                EventEntry.EVENT_PROJECTION,
                SellaAssistProvider.eventWithIdSelection,
                new String[]{Long.toString(id)},
                null);

        if (eventCursor != null && eventCursor.moveToFirst()) {
            event = new Event(Parcel.obtain());
            event.setId(eventCursor.getInt(EventEntry.EVENT_EVENT_ID));
            event.setTitle(eventCursor.getString(EventEntry.EVENT_TITLE));
            event.setStartTimestamp(eventCursor.getLong(EventEntry.EVENT_START_TIMESTAMP));
            event.setEndTimestamp(eventCursor.getLong(EventEntry.EVENT_END_TIMESTAMP));
            event.setBannerImage(eventCursor.getString(EventEntry.EVENT_BANNER_IMAGE));
            event.setCreatedByName(eventCursor.getString(EventEntry.EVENT_CREATED_BY_NAME));
            event.setCreatedByProfileImage(eventCursor.getString(EventEntry.EVENT_CREATED_BY_PROFILE_IMAGE));
            event.setCreatedByTimestamp(eventCursor.getLong(EventEntry.EVENT_CREATED_BY_TIMESTAMP));
            event.setAddress(eventCursor.getString(EventEntry.EVENT_ADDRESS));
            event.setInterested(eventCursor.getInt(EventEntry.EVENT_INTERESTED));
            event.setRemainder(Boolean.parseBoolean(eventCursor.getString(EventEntry.EVENT_REMAINDER)));
            event.setDescription(eventCursor.getString(EventEntry.EVENT_DESCRIPTION));
        }

        Log.v(TAG, "<----Event---->" + event);

        return event;
    }

    public Event getEvent(Cursor cursor) {
        Log.v(TAG, "<-----Getting event from cursor---->");
        Event event = new Event(Parcel.obtain());

        event.setId(cursor.getInt(SellaAssistContract.EventEntry.EVENT_EVENT_ID));
        event.setTitle(cursor.getString(SellaAssistContract.EventEntry.EVENT_TITLE));
        event.setStartTimestamp(cursor.getLong(SellaAssistContract.EventEntry.EVENT_START_TIMESTAMP));
        event.setEndTimestamp(cursor.getLong(SellaAssistContract.EventEntry.EVENT_END_TIMESTAMP));
        event.setBannerImage(cursor.getString(SellaAssistContract.EventEntry.EVENT_BANNER_IMAGE));
        event.setCreatedByName(cursor.getString(SellaAssistContract.EventEntry.EVENT_CREATED_BY_NAME));
        event.setCreatedByProfileImage(cursor.getString(SellaAssistContract.EventEntry.EVENT_CREATED_BY_PROFILE_IMAGE));
        event.setCreatedByTimestamp(cursor.getLong(SellaAssistContract.EventEntry.EVENT_CREATED_BY_TIMESTAMP));
        event.setAddress(cursor.getString(SellaAssistContract.EventEntry.EVENT_ADDRESS));
        event.setInterested(cursor.getInt(SellaAssistContract.EventEntry.EVENT_INTERESTED));
        event.setRemainder(Boolean.parseBoolean(cursor.getString(SellaAssistContract.EventEntry.EVENT_REMAINDER)));
        event.setDescription(cursor.getString(SellaAssistContract.EventEntry.EVENT_DESCRIPTION));

        Log.v(TAG, "<----Event---->" + event);

        return event;
    }

    public void updateEvent(Event event) {
        Log.v(TAG, "<-----Updating event---->" + event);
        Uri eventUri = EventEntry.buildEventById(event.getId());
        Log.v(TAG, "<-----eventUri---->" + eventUri);

        context.getContentResolver().update(eventUri, createEventContentValues(event), null, null);
    }

    public void notifyChange() {
        context.getContentResolver().notifyChange(EventEntry.CONTENT_URI, null, false);
    }
}
