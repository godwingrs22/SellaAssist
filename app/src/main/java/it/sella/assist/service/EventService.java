package it.sella.assist.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import it.sella.assist.core.EventManager;
import it.sella.assist.dao.EventDAO;
import it.sella.assist.model.Event;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class EventService extends IntentService {

    private static final String TAG = EventService.class.getSimpleName();

    public EventService() {
        super("EventService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "<-------Fetching Events--------->");

        try {
            final String gbsId = intent.getStringExtra(Utility.USER_GBS_ID_KEY);
            final EventManager eventManager = new EventManager(this);
            final EventDAO eventDAO = new EventDAO(this);
            List<Event> events = eventManager.loadEvents();
            if (!events.isEmpty())
                eventDAO.addAllEvents(events);
            else
                Log.e(TAG, "<-----No new Events---->");
            eventDAO.notifyChange();
        } catch (Exception e) {
            Log.e(TAG, "<--Exception---->" + e.getMessage());
        }
    }
}
