package it.sella.assist.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import it.sella.assist.AppController;
import it.sella.assist.R;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.service.BiometricService;
import it.sella.assist.service.EventService;
import it.sella.assist.service.FeedService;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 02-Aug-16.
 */
public class SellaAssistSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = SellaAssistSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public SellaAssistSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v(TAG, "<-------Starting sync------>");

        try {
            String gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", getContext());
            Log.v(TAG, "<-------Starting sync------>");
            if (!"".equals(gbsId)) {
                // Syn Feeds
                Intent feedServiceIntent = new Intent(getContext(), FeedService.class);
                feedServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
                feedServiceIntent.putExtra(Utility.BEACON_ID_KEY, AppController.DEFAULT_BEACON_REGION.getMinor());
                getContext().startService(feedServiceIntent);

                // Syn Events
                Intent eventServiceIntent = new Intent(getContext(), EventService.class);
                eventServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
                getContext().startService(eventServiceIntent);

                // Syn Biometric
                Intent biometricServiceIntent = new Intent(getContext(), BiometricService.class);
                biometricServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
                getContext().startService(biometricServiceIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "<--Exception---->" + e.getMessage());
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
