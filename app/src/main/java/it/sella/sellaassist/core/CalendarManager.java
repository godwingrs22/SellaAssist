package it.sella.sellaassist.core;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Calendar;

import it.sella.sellaassist.model.Event;

/**
 * Created by GodwinRoseSamuel on 27-Jul-16.
 */
public class CalendarManager {

    private static final String TAG = CalendarManager.class.getSimpleName();

    private Context context;

    public CalendarManager(Context context) {
        this.context = context;
    }

    public void addCalendarEvent(Event event) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartTimestamp())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndTimestamp())
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, "Sella Assit")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getAddress())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);

        context.startActivity(intent);
    }

    private String getCalendarUriBase(Activity activity) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = activity.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = activity.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

    public void addEvent(Event event) {
//        GregorianCalendar calDate = new GregorianCalendar(this._year, this._month, this._day, this._hour, this._minute);

        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, event.getStartTimestamp());
            values.put(CalendarContract.Events.DTEND, event.getEndTimestamp());
            values.put(CalendarContract.Events.TITLE, event.getTitle());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Log.v(TAG, uri.getLastPathSegment());
            setReminder(cr, event.getId(), 5);
            setReminder(cr, event.getId(), 60);
            setReminder(cr, event.getId(), 1440);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar" + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeEvent(Activity activity, Event event) {
        ContentResolver cr = context.getContentResolver();

        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse(getCalendarUriBase(activity) + "events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, event.getId());
        iNumRowsDeleted = cr.delete(eventUri, null, null);

        Log.i(TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");
    }


    public int updateEvent(Activity activity, Event event) {
        int iNumRowsUpdated = 0;

        ContentValues eventUpdate = new ContentValues();

        eventUpdate.put(CalendarContract.Events.TITLE, event.getTitle());
        eventUpdate.put("hasAlarm", 1); // 0 for false, 1 for true
        eventUpdate.put(CalendarContract.Events.DTSTART, event.getStartTimestamp());
        eventUpdate.put(CalendarContract.Events.DTEND, event.getEndTimestamp());

        Uri eventsUri = Uri.parse(getCalendarUriBase(activity) + "events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, event.getId());

        iNumRowsUpdated = activity.getContentResolver().update(eventUri, eventUpdate, null,
                null);

        Log.i(TAG, "Updated " + iNumRowsUpdated + " calendar entry.");

        return iNumRowsUpdated;
    }
}
