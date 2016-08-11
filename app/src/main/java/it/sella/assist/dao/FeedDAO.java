package it.sella.assist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import it.sella.assist.data.SellaAssistContract.FeedEntry;
import it.sella.assist.model.Feed;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class FeedDAO {

    private static final String TAG = FeedDAO.class.getSimpleName();
    private Context context;

    public FeedDAO(Context context) {
        this.context = context;
    }

    private ContentValues createFeedContentValues(Feed feed) {
        final ContentValues feedValues = new ContentValues();
        feedValues.put(FeedEntry.COLUMN_FEED_ID, feed.getId());
        feedValues.put(FeedEntry.COLUMN_GBS_ID, feed.getGbsId());
        feedValues.put(FeedEntry.COLUMN_CREATED_BY_NAME, feed.getCreatedByName());
        feedValues.put(FeedEntry.COLUMN_PROFILE_PIC, feed.getProfileImage());
        feedValues.put(FeedEntry.COLUMN_START_TIMESTAMP, feed.getTimestamp());
        feedValues.put(FeedEntry.COLUMN_IT_AREA, feed.getItArea());
        feedValues.put(FeedEntry.COLUMN_IS_IMPORTANT, String.valueOf(feed.isImportant()));
        feedValues.put(FeedEntry.COLUMN_MESSAGE, feed.getMessage());
        feedValues.put(FeedEntry.COLUMN_IMAGE, feed.getImage());
        feedValues.put(FeedEntry.COLUMN_URL, feed.getUrl());

        return feedValues;
    }

    public void addFeed(Feed feed) {
        Log.v(TAG, "<-----Adding Feed---->" + feed);
        context.getContentResolver().insert(FeedEntry.CONTENT_URI, createFeedContentValues(feed));
    }


    public void addAllFeeds(List<Feed> feeds) {
        Log.v(TAG, "<-----Adding all Feeds---->" + feeds);
        Vector<ContentValues> feedVector = new Vector<>();
        for (Feed feed : feeds) {
            feedVector.add(createFeedContentValues(feed));
        }
        if (feedVector.size() > 0) {
            ContentValues[] feedArray = new ContentValues[feedVector.size()];
            feedVector.toArray(feedArray);
            context.getContentResolver().bulkInsert(FeedEntry.CONTENT_URI, feedArray);
            Log.v(TAG, "<-----" + feedVector.size() + " feeds inserted------->");
        }
    }

    public void notifyChange() {
        context.getContentResolver().notifyChange(FeedEntry.CONTENT_URI, null, false);
    }
}
