package it.sella.assist.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import it.sella.assist.core.FeedsManager;
import it.sella.assist.dao.FeedDAO;
import it.sella.assist.model.Feed;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class FeedService extends IntentService {

    private static final String TAG = FeedService.class.getSimpleName();

    public FeedService() {
        super("FeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "<-------Fetching Feeds--------->");

        try {
            final String gbsId = intent.getStringExtra(Utility.USER_GBS_ID_KEY);
            final int beaconId = intent.getIntExtra(Utility.BEACON_ID_KEY,0);
            final FeedsManager feedsManager = new FeedsManager(this);
            final FeedDAO feedDAO = new FeedDAO(this);
            List<Feed> feeds = feedsManager.loadFeeds(gbsId, beaconId);
            if (!feeds.isEmpty())
                feedDAO.addAllFeeds(feeds);
            else
                Log.e(TAG, "<-----No new feed---->");
            feedDAO.notifyChange();
        } catch (Exception e) {
            Log.e(TAG, "<--Exception---->" + e.getMessage());
        }
    }
}
