package it.sella.sellaassist.core;

import android.content.ContentValues;
import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Vector;

import it.sella.sellaassist.AppController;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.http.HttpClient;
import it.sella.sellaassist.util.ServerUtils;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class FeedsManager {
    private static final String TAG = FeedsManager.class.getSimpleName();
    private static final String NOTIFICATIONS_PARAM = "notification";
    private final HttpClient httpClient = AppController.getInstance().getHttpClient();
    private Context context;

    public FeedsManager(Context context) {
        this.context = context;
    }

    public boolean loadFeeds(String gbsCode, String beaconId) {
        Boolean isSuccess = false;
        final Uri builtUri = Uri.parse(ServerUtils.getNotificationURL()).buildUpon()
                .appendPath(gbsCode)
                .appendPath(beaconId)
                .appendPath(NOTIFICATIONS_PARAM)
                .build();
        try {
            URL url = new URL(builtUri.toString());
            final String feedData = httpClient.getResponse(url, HttpClient.HTTP_GET, null, HttpClient.TIMEOUT);
            isSuccess = getFeedDataFromJson(feedData);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return isSuccess;
    }

    private boolean getFeedDataFromJson(final String data) {
        final String FEED_LIST = "announcements";
        final String FEED_ID = "id";
        final String FEED_MESSAGE_LIST = "messages";
        final String FEED_MESSAGE_CONTENT = "content";
        final String FEED_MESSAGE_IMAGE = "image";
        final String FEED_MESSAGE_IS_IMPORTANT = "isImportant";
        final String FEED_MESSAGE_URL = "url";
        final String FEED_CREATED_BY = "createdBy";
        final String FEED_CREATED_BY_NAME = "name";
        final String FEED_CREATED_BY_PROFILE_IMAGE = "image";
        final String FEED_CREATED_BY_IT_AREA = "area";
        final String FEED_START_TIMESTAMP = "startTimeStamp";

        try {
            JSONObject FeedDataJson = new JSONObject(data);
            JSONArray feedList = FeedDataJson.getJSONArray(FEED_LIST);
            Vector<ContentValues> cVVector = new Vector<>();

            if (feedList != null) {
                for (int i = 0; i < feedList.length(); i++) {
                    JSONObject feedJson = (JSONObject) feedList.get(i);
                    JSONObject createdBy = (JSONObject) feedJson.get(FEED_CREATED_BY);

                    int id = feedJson.getInt(FEED_ID);
                    String createdByName = createdBy.getString(FEED_CREATED_BY_NAME);
                    String profileImage = ServerUtils.getServerURL() + createdBy.getString(FEED_CREATED_BY_PROFILE_IMAGE);
                    String startTimestamp = feedJson.getString(FEED_START_TIMESTAMP);
                    String itArea = createdBy.isNull(FEED_CREATED_BY_IT_AREA) ? "IT Racolta" : createdBy.getString(FEED_CREATED_BY_IT_AREA);
                    String url = feedJson.isNull(FEED_MESSAGE_URL) ? null : feedJson.getString(FEED_MESSAGE_URL);

                    ContentValues feedValues = new ContentValues();
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_FEED_ID, id);
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_CREATED_BY_NAME, createdByName);
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_PROFILE_PIC, profileImage);
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_START_TIMESTAMP, startTimestamp);
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_IT_AREA, itArea);
                    feedValues.put(SellaAssistContract.FeedEntry.COLUMN_URL, url);

                    JSONArray messages = feedJson.getJSONArray(FEED_MESSAGE_LIST);

                    if (messages.length() == 0) {
                        cVVector.add(feedValues);
                    } else {
                        for (int j = 0; j < messages.length(); j++) {
                            JSONObject messagesJSON = (JSONObject) messages.get(j);

                            boolean isImportant = messagesJSON.isNull(FEED_MESSAGE_IS_IMPORTANT) ? true : messagesJSON.getBoolean(FEED_MESSAGE_IS_IMPORTANT);
                            String content = messagesJSON.isNull(FEED_MESSAGE_CONTENT) ? null : messagesJSON.getString(FEED_MESSAGE_CONTENT);
                            String image = messagesJSON.isNull(FEED_MESSAGE_IMAGE) ? null : ServerUtils.getServerURL() + messagesJSON.getString(FEED_MESSAGE_IMAGE);


                            ContentValues messageFeedValues = new ContentValues();
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_FEED_ID, id);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_CREATED_BY_NAME, createdByName);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_PROFILE_PIC, profileImage);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_START_TIMESTAMP, startTimestamp);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_IT_AREA, itArea);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_IS_IMPORTANT, String.valueOf(isImportant));
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_MESSAGE, content);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_IMAGE, image);
                            messageFeedValues.put(SellaAssistContract.FeedEntry.COLUMN_URL, url);

                            cVVector.add(messageFeedValues);
                        }
                    }
                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    context.getContentResolver().bulkInsert(SellaAssistContract.FeedEntry.CONTENT_URI, cvArray);

                   /* context.getContentResolver().delete(SellaAssistContract.FeedEntry.CONTENT_URI,
                            SellaAssistContract.FeedEntry.COLUMN_START_TIMESTAMP + " <= ?",
                            new String[]{Long.toString(Utility.getDateBefore(-2))});*/

                    Log.v(TAG, "<-----" + cVVector.size() + " feeds inserted------->");
                    return true;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Parsing err" + e);
        } catch (ParseException e) {
            Log.e(TAG, "Date Parsing err" + e);
        }
        return false;
    }


}
