package it.sella.sellaassist.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import java.util.List;

import it.sella.sellaassist.R;
import it.sella.sellaassist.core.BiometricManager;
import it.sella.sellaassist.core.EventManager;
import it.sella.sellaassist.core.FeedsManager;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.data.SellaAssistProvider;
import it.sella.sellaassist.model.BiometricInfo;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.SellaCache;
import it.sella.sellaassist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 02-Aug-16.
 */
public class SellaAssistSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = SellaAssistSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private User user = null;

    public SellaAssistSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v(TAG, "<-------Starting sync------>");

        try {
            Cursor cursor = getContext().getContentResolver().query(SellaAssistContract.UserEntry.CONTENT_URI, SellaAssistContract.UserEntry.USER_PROJECTION, SellaAssistProvider.userWithIdSelection, new String[]{SellaCache.getCache(Utility.USER_GBS_ID_KEY, "", getContext())}, null);


            if (cursor.moveToFirst()) {
                user = new User(Parcel.obtain());
                user.setGbsID(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_GBS_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_PASSWORD)));
                user.setProfilePic(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_PROFILE_PIC)));
                user.setDeviceId(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_DEVICEID)));
                user.setLoggedIn(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(SellaAssistContract.UserEntry.COLUMN_LOGGED_IN))));
            }

            final FeedsManager feedsManager = new FeedsManager(getContext());

            boolean isFeedsUpdated = feedsManager.loadFeeds(user.getGbsID(), "9262");
            Log.v(TAG, "<------isFeedsUpdated------>" + isFeedsUpdated);

            final EventManager eventManager = new EventManager(getContext());

            boolean isEventsUpdated = eventManager.loadEvents();
            Log.v(TAG, "<------isEventsUpdated------>" + isEventsUpdated);

            final BiometricManager biometricManager = new BiometricManager();

            List<BiometricInfo> biometricInfos = biometricManager.getAllBiometricsTobeUpdated(getContext());
            Log.v(TAG, "<-------Biometric to be updated in server------->" + biometricInfos);
            if (!biometricInfos.isEmpty() && biometricManager.updateBiometricToServer(biometricInfos, user)) {
                for (BiometricInfo biometricInfo : biometricInfos) {
                    biometricInfo.setSent(true);

                    ContentValues biometricInfoValues = new ContentValues();
                    biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_SENT, String.valueOf(biometricInfo.isSent()));
                    getContext().getContentResolver().update(SellaAssistContract.BiometricInfoEntry.CONTENT_URI, biometricInfoValues,
                            SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP + " = ?", new String[]{Long.toString(biometricInfo.getTimestamp())});
                }
                Log.v(TAG, "<-------Biometric updated------->");
            } else {
                Log.e(TAG, "<-------Biometric Not updated------->");
            }

        } catch (Exception e) {
            Log.e(TAG, "<--Exception---->" + e.getMessage());
        } finally {
            getContext().getContentResolver().notifyChange(SellaAssistContract.FeedEntry.CONTENT_URI, null, false);
            getContext().getContentResolver().notifyChange(SellaAssistContract.BiometricEntry.CONTENT_URI, null, false);
            getContext().getContentResolver().notifyChange(SellaAssistContract.BiometricInfoEntry.CONTENT_URI, null, false);
            getContext().getContentResolver().notifyChange(SellaAssistContract.EventEntry.CONTENT_URI, null, false);
        }
        Log.v(TAG, "<------Sync Completed------>");
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, SellaAssistContract.CONTENT_AUTHORITY).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, SellaAssistContract.CONTENT_AUTHORITY, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), SellaAssistContract.CONTENT_AUTHORITY, bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        SellaAssistSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, SellaAssistContract.CONTENT_AUTHORITY, true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
