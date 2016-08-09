package it.sella.assist.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by GodwinRoseSamuel on 02-Aug-16.
 */
public class SellaAssistSyncService extends Service {
    private static final String TAG = SellaAssistSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static SellaAssistSyncAdapter sellaAssistSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "<------SellaAssistSyncService----------->");
        synchronized (sSyncAdapterLock) {
            if (sellaAssistSyncAdapter == null) {
                sellaAssistSyncAdapter = new SellaAssistSyncAdapter(getApplicationContext(), true);
            }
        }
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sellaAssistSyncAdapter.getSyncAdapterBinder();
    }
}
