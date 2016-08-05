package it.sella.sellaassist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by GodwinRoseSamuel on 21-Jul-16.
 */

public class SellaAssistContract {

    public static final String CONTENT_AUTHORITY = "it.sella.sellaassist.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";
    public static final String PATH_FEED = "feed";
    public static final String PATH_BIOMETRIC = "biometric";
    public static final String PATH_BIOMETRIC_INFO = "biometric_info";
    public static final String PATH_EVENT = "event";

    public static class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String TABLE_NAME = "user";
        public static final String COLUMN_GBS_ID = "gbsID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_PROFILE_PIC = "profilePic";
        public static final String COLUMN_DEVICEID = "deviceID";
        public static final String COLUMN_LOGGED_IN = "loginStatus";
        public static final String COLUMN_BUSINESS_UNIT_NAME = "businessUnitName";

        public static final String[] USER_PROJECTION = {
                UserEntry._ID,
                UserEntry.COLUMN_GBS_ID,
                UserEntry.COLUMN_NAME,
                UserEntry.COLUMN_PASSWORD,
                UserEntry.COLUMN_PROFILE_PIC,
                UserEntry.COLUMN_DEVICEID,
                UserEntry.COLUMN_LOGGED_IN,
                UserEntry.COLUMN_BUSINESS_UNIT_NAME
        };

        public static final int USER_ID = 0;
        public static final int USER_GBS_ID = 1;
        public static final int USER_NAME = 2;
        public static final int USER_PASSWORD = 3;
        public static final int USER_PROFILE_PIC = 4;
        public static final int USER_DEVICEID = 5;
        public static final int USER_LOGGED_IN = 6;
        public static final int USER_BUSINESS_UNIT_NAME = 7;

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUserID(long userId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(userId)).build();
        }

        public static long getUserIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class FeedEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEED).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;

        public static final String TABLE_NAME = "feed";
        public static final String COLUMN_FEED_ID = "feed_id";
        public static final String COLUMN_GBS_ID = "gbsID";
        public static final String COLUMN_CREATED_BY_NAME = "createdByName";
        public static final String COLUMN_PROFILE_PIC = "profileImage";
        public static final String COLUMN_START_TIMESTAMP = "startTimestamp";
        public static final String COLUMN_IT_AREA = "itArea";
        public static final String COLUMN_IS_IMPORTANT = "isImportant";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_URL = "url";

        public static final String[] FEED_PROJECTIONS = {
                FeedEntry._ID,
                FeedEntry.COLUMN_FEED_ID,
                FeedEntry.COLUMN_GBS_ID,
                FeedEntry.COLUMN_CREATED_BY_NAME,
                FeedEntry.COLUMN_PROFILE_PIC,
                FeedEntry.COLUMN_START_TIMESTAMP,
                FeedEntry.COLUMN_IT_AREA,
                FeedEntry.COLUMN_IS_IMPORTANT,
                FeedEntry.COLUMN_MESSAGE,
                FeedEntry.COLUMN_IMAGE,
                FeedEntry.COLUMN_URL
        };

        public static final int FEED_ID = 0;
        public static final int FEED_FEED_ID = 1;
        public static final int FEED_GBS_ID = 2;
        public static final int FEED_CREATED_BY_NAME = 3;
        public static final int FEED_PROFILE_PIC = 4;
        public static final int FEED_START_TIMESTAMP = 5;
        public static final int FEED_IT_AREA = 6;
        public static final int FEED_IS_IMPORTANT = 7;
        public static final int FEED_MESSAGE =8;
        public static final int FEED_IMAGE = 9;
        public static final int FEED_URL = 10;


        public static Uri buildFeedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFeedID(long feedId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(feedId)).build();
        }

        public static long getFeedIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class BiometricEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIOMETRIC).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIOMETRIC;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIOMETRIC;

        public static final String TABLE_NAME = "biometric";
        public static final String COLUMN_DATE = "date";

        public static final String[] BIOMETRIC_PROJECTION = {
                BiometricEntry._ID,
                BiometricEntry.COLUMN_DATE
        };

        public static final int BIOMETRIC_ID = 0;
        public static final int BIOMETRIC_DATE = 1;

        public static Uri buildBiometricUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class BiometricInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIOMETRIC_INFO).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIOMETRIC_INFO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIOMETRIC_INFO;

        public static final String TABLE_NAME = "biometric_info";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_SENT = "sent";

        public static final String[] BIOMETRIC_INFO_PROJECTION = {
                BiometricInfoEntry._ID,
                BiometricInfoEntry.COLUMN_DATE,
                BiometricInfoEntry.COLUMN_TIMESTAMP,
                BiometricInfoEntry.COLUMN_LOCATION,
                BiometricInfoEntry.COLUMN_SENT
        };

        public static final int BIOMETRIC_INFO_ID = 0;
        public static final int BIOMETRIC_INFO_DATE = 1;
        public static final int BIOMETRIC_INFO_TIMESTAMP = 2;
        public static final int BIOMETRIC_INFO_LOCATION = 3;
        public static final int BIOMETRIC_INFO_SENT = 4;

        public static Uri buildBiometricInfoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "event";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_START_TIMESTAMP = "startTimestamp";
        public static final String COLUMN_END_TIMESTAMP = "endTimestamp";
        public static final String COLUMN_BANNER_IMAGE = "bannerImage";
        public static final String COLUMN_CREATED_BY_NAME = "createdByName";
        public static final String COLUMN_CREATED_BY_PROFILE_IMAGE = "createdByProfileImage";
        public static final String COLUMN_CREATED_BY_TIMESTAMP = "createdByTimestamp";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_INTERESTED = "interested";
        public static final String COLUMN_REMAINDER = "remainder";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final String[] EVENT_PROJECTION = {
                EventEntry._ID,
                EventEntry.COLUMN_ID,
                EventEntry.COLUMN_TITLE,
                EventEntry.COLUMN_START_TIMESTAMP,
                EventEntry.COLUMN_END_TIMESTAMP,
                EventEntry.COLUMN_BANNER_IMAGE,
                EventEntry.COLUMN_CREATED_BY_NAME,
                EventEntry.COLUMN_CREATED_BY_PROFILE_IMAGE,
                EventEntry.COLUMN_CREATED_BY_TIMESTAMP,
                EventEntry.COLUMN_ADDRESS,
                EventEntry.COLUMN_INTERESTED,
                EventEntry.COLUMN_REMAINDER,
                EventEntry.COLUMN_DESCRIPTION
        };

        public static final int EVENT_ID = 0;
        public static final int EVENT_EVENT_ID = 1;
        public static final int EVENT_TITLE = 2;
        public static final int EVENT_START_TIMESTAMP = 3;
        public static final int EVENT_END_TIMESTAMP = 4;
        public static final int EVENT_BANNER_IMAGE = 5;
        public static final int EVENT_CREATED_BY_NAME = 6;
        public static final int EVENT_CREATED_BY_PROFILE_IMAGE = 7;
        public static final int EVENT_CREATED_BY_TIMESTAMP = 8;
        public static final int EVENT_ADDRESS = 9;
        public static final int EVENT_INTERESTED = 10;
        public static final int EVENT_REMAINDER = 11;
        public static final int EVENT_DESCRIPTION = 12;

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventID(long eventId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(eventId)).build();
        }

        public static long getEventIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
