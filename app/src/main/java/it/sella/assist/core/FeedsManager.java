package it.sella.assist.core;

import android.content.Context;
import android.net.ParseException;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import it.sella.assist.http.HttpClient;
import it.sella.assist.model.Feed;
import it.sella.assist.util.ServerUtils;
import okhttp3.HttpUrl;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class FeedsManager {
    private static final String TAG = FeedsManager.class.getSimpleName();
    private static final String NOTIFICATIONS_PARAM = "notification";
    private Context context;

    public FeedsManager(Context context) {
        this.context = context;
    }

    public List<Feed> loadFeeds(String gbsCode, int beaconId) {
        List<Feed> feeds = new LinkedList<>();
        try {
            final HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.NOTIFICATION_API)
                    .addPathSegment(gbsCode)
                    .addPathSegment(Integer.toString(beaconId))
                    .addPathSegment(NOTIFICATIONS_PARAM)
                    .build();
            final String feedData = HttpClient.GET(httpUrl);
            feeds = getFeedDataFromJson(feedData, gbsCode);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return feeds;
    }

    private List<Feed> getFeedDataFromJson(final String data, String gbsCode) {
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

        List<Feed> feeds = new LinkedList<>();

        try {
            JSONObject FeedDataJson = new JSONObject(data);
            JSONArray feedList = FeedDataJson.getJSONArray(FEED_LIST);

            if (feedList != null) {
                for (int i = 0; i < feedList.length(); i++) {
                    JSONObject feedJson = (JSONObject) feedList.get(i);
                    JSONObject createdBy = (JSONObject) feedJson.get(FEED_CREATED_BY);

                    int id = feedJson.getInt(FEED_ID);
                    String createdByName = createdBy.getString(FEED_CREATED_BY_NAME);
                    String profileImage = ServerUtils.getServerURL() + createdBy.getString(FEED_CREATED_BY_PROFILE_IMAGE);
                    long startTimestamp = feedJson.getLong(FEED_START_TIMESTAMP);
                    String itArea = createdBy.isNull(FEED_CREATED_BY_IT_AREA) ? "IT Racolta" : createdBy.getString(FEED_CREATED_BY_IT_AREA);
                    String url = feedJson.isNull(FEED_MESSAGE_URL) ? null : feedJson.getString(FEED_MESSAGE_URL);

                    Feed feed = new Feed(Parcel.obtain());
                    feed.setId(id);
                    feed.setGbsId(gbsCode);
                    feed.setCreatedByName(createdByName);
                    feed.setProfileImage(profileImage);
                    feed.setTimestamp(startTimestamp);
                    feed.setItArea(itArea);
                    feed.setUrl(url);

                    JSONArray messages = feedJson.getJSONArray(FEED_MESSAGE_LIST);

                    if (messages.length() == 0) {
                        feeds.add(feed);
                    } else {
                        for (int j = 0; j < messages.length(); j++) {
                            JSONObject messagesJSON = (JSONObject) messages.get(j);

                            boolean isImportant = messagesJSON.isNull(FEED_MESSAGE_IS_IMPORTANT) ? true : messagesJSON.getBoolean(FEED_MESSAGE_IS_IMPORTANT);
                            String content = messagesJSON.isNull(FEED_MESSAGE_CONTENT) ? null : messagesJSON.getString(FEED_MESSAGE_CONTENT);
                            String image = messagesJSON.isNull(FEED_MESSAGE_IMAGE) ? null : ServerUtils.getServerURL() + messagesJSON.getString(FEED_MESSAGE_IMAGE);

                            Feed messageFeed = new Feed(Parcel.obtain());
                            messageFeed.setId(id);
                            messageFeed.setGbsId(gbsCode);
                            messageFeed.setCreatedByName(createdByName);
                            messageFeed.setProfileImage(profileImage);
                            messageFeed.setTimestamp(startTimestamp);
                            messageFeed.setItArea(itArea);
                            messageFeed.setImportant(isImportant);
                            messageFeed.setMessage(content);
                            messageFeed.setImage(image);
                            messageFeed.setUrl(url);

                            feeds.add(messageFeed);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Parsing err" + e);
        } catch (ParseException e) {
            Log.e(TAG, "Date Parsing err" + e);
        }
        return feeds;
    }
}
