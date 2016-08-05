package it.sella.sellaassist.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import it.sella.sellaassist.data.SellaAssistContract.BiometricEntry;
import it.sella.sellaassist.data.SellaAssistContract.BiometricInfoEntry;
import it.sella.sellaassist.data.SellaAssistContract.EventEntry;
import it.sella.sellaassist.data.SellaAssistContract.FeedEntry;
import it.sella.sellaassist.data.SellaAssistContract.UserEntry;

/**
 * Created by GodwinRoseSamuel on 01-Aug-16.
 */
public class SellaAssistProvider extends ContentProvider {
    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private SellaAssistDBHelper sellaAssistDBHelper;

    static final int FEEDS = 100;
    static final int FEED_WITH_ID = 101;

    static final int USERS = 200;
    static final int USER_WITH_ID = 201;

    static final int BIOMETRIC = 300;
    static final int BIOMETRIC_WITH_ID = 301;

    static final int BIOMETRIC_INFO = 400;
    static final int BIOMETRIC_INFO_WITH_ID = 401;

    static final int EVENT = 500;
    static final int EVENT_WITH_ID = 501;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SellaAssistContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SellaAssistContract.PATH_FEED, FEEDS);
        matcher.addURI(authority, SellaAssistContract.PATH_FEED + "/#", FEED_WITH_ID);

        matcher.addURI(authority, SellaAssistContract.PATH_USER, USERS);
        matcher.addURI(authority, SellaAssistContract.PATH_USER + "/#", USER_WITH_ID);

        matcher.addURI(authority, SellaAssistContract.PATH_BIOMETRIC, BIOMETRIC);
        matcher.addURI(authority, SellaAssistContract.PATH_BIOMETRIC + "/#", BIOMETRIC_WITH_ID);

        matcher.addURI(authority, SellaAssistContract.PATH_BIOMETRIC_INFO, BIOMETRIC_INFO);
        matcher.addURI(authority, SellaAssistContract.PATH_BIOMETRIC_INFO + "/#", BIOMETRIC_INFO_WITH_ID);

        matcher.addURI(authority, SellaAssistContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, SellaAssistContract.PATH_EVENT + "/#", EVENT_WITH_ID);

        return matcher;
    }

    private static final SQLiteQueryBuilder feedQueryBuilder;
    private static final SQLiteQueryBuilder userQueryBuilder;
    private static final SQLiteQueryBuilder eventQueryBuilder;

    static {
        feedQueryBuilder = new SQLiteQueryBuilder();
        userQueryBuilder = new SQLiteQueryBuilder();
        eventQueryBuilder = new SQLiteQueryBuilder();

        userQueryBuilder.setTables(UserEntry.TABLE_NAME);
        feedQueryBuilder.setTables(
                FeedEntry.TABLE_NAME +
                        " INNER JOIN " + UserEntry.TABLE_NAME +
                        " ON " + FeedEntry.TABLE_NAME +
                        "." + FeedEntry.COLUMN_GBS_ID +
                        " = " + UserEntry.TABLE_NAME +
                        "." + UserEntry.COLUMN_GBS_ID);

        eventQueryBuilder.setTables(EventEntry.TABLE_NAME);
    }

    private static final String feedWithIdSelection =
            FeedEntry.TABLE_NAME + "." + FeedEntry.COLUMN_FEED_ID + " = ? ";

    private Cursor getFeedWithID(Uri uri, String[] projection, String sortOrder) {
        long id = FeedEntry.getFeedIdFromUri(uri);

        return feedQueryBuilder.query(sellaAssistDBHelper.getReadableDatabase(),
                projection,
                feedWithIdSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public static final String userLoggedInSelection =
            UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_LOGGED_IN + " = ? ";

    public static final String userWithIdSelection =
            UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_GBS_ID + " = ? ";

    private Cursor getUserWithID(Uri uri, String[] projection, String sortOrder) {
        long id = UserEntry.getUserIdFromUri(uri);

        return userQueryBuilder.query(sellaAssistDBHelper.getReadableDatabase(),
                projection,
                userWithIdSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public static final String previousEventSelection =
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_START_TIMESTAMP + " < ? ";

    public static final String upcomingEventSelection =
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_START_TIMESTAMP + " >= ? ";

    public static final String eventWithIdSelection =
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_ID + " = ? ";

    private Cursor getEventWithID(Uri uri, String[] projection, String sortOrder) {
        long id = EventEntry.getEventIdFromUri(uri);

        return userQueryBuilder.query(sellaAssistDBHelper.getReadableDatabase(),
                projection,
                eventWithIdSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        sellaAssistDBHelper = new SellaAssistDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case USER_WITH_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            case USERS:
                return UserEntry.CONTENT_TYPE;
            case FEED_WITH_ID:
                return FeedEntry.CONTENT_ITEM_TYPE;
            case FEEDS:
                return FeedEntry.CONTENT_TYPE;
            case BIOMETRIC_WITH_ID:
                return BiometricEntry.CONTENT_ITEM_TYPE;
            case BIOMETRIC:
                return BiometricEntry.CONTENT_TYPE;
            case BIOMETRIC_INFO_WITH_ID:
                return BiometricInfoEntry.CONTENT_ITEM_TYPE;
            case BIOMETRIC_INFO:
                return BiometricInfoEntry.CONTENT_TYPE;
            case EVENT:
                return EventEntry.CONTENT_TYPE;
            case EVENT_WITH_ID:
                return EventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {

            case USER_WITH_ID: {
                retCursor = getUserWithID(uri, projection, sortOrder);
                break;
            }

            case USERS: {
                retCursor = sellaAssistDBHelper.getReadableDatabase().query(
                        UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case FEED_WITH_ID: {
                retCursor = getFeedWithID(uri, projection, sortOrder);
                break;
            }

            case FEEDS: {
                retCursor = sellaAssistDBHelper.getReadableDatabase().query(
                        FeedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case BIOMETRIC: {
                retCursor = sellaAssistDBHelper.getReadableDatabase().query(
                        BiometricEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case BIOMETRIC_INFO: {
                retCursor = sellaAssistDBHelper.getReadableDatabase().query(
                        BiometricInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case EVENT_WITH_ID: {
                retCursor = getEventWithID(uri, projection, sortOrder);
                break;
            }

            case EVENT: {
                retCursor = sellaAssistDBHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = sellaAssistDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case USERS: {
                long _id = db.insert(UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FEEDS: {
                long _id = db.insert(FeedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = FeedEntry.buildFeedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BIOMETRIC: {
                long _id = db.insert(BiometricEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BiometricEntry.buildBiometricUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BIOMETRIC_INFO: {
                long _id = db.insert(BiometricInfoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BiometricInfoEntry.buildBiometricInfoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case EVENT: {
                long _id = db.insert(EventEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = EventEntry.buildEventUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = sellaAssistDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case USERS:
                rowsDeleted = db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FEEDS:
                rowsDeleted = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BIOMETRIC:
                rowsDeleted = db.delete(BiometricEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BIOMETRIC_INFO:
                rowsDeleted = db.delete(BiometricInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EVENT:
                rowsDeleted = db.delete(EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = sellaAssistDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;

        switch (match) {
            case USERS:
                rowsUpdated = db.update(UserEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FEEDS:
                rowsUpdated = db.update(FeedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BIOMETRIC:
                rowsUpdated = db.update(BiometricEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BIOMETRIC_INFO:
                rowsUpdated = db.update(BiometricInfoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case EVENT:
                rowsUpdated = db.update(EventEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = sellaAssistDBHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case FEEDS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FeedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case EVENT: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(EventEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        sellaAssistDBHelper.close();
        super.shutdown();
    }
}
