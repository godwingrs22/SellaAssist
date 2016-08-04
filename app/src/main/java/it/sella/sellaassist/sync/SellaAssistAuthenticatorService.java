package it.sella.sellaassist.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by GodwinRoseSamuel on 02-Aug-16.
 */
public class SellaAssistAuthenticatorService extends Service {

    private SellaAssistAuthenticator sellaAssistAuthenticator;

    @Override
    public void onCreate() {
        sellaAssistAuthenticator = new SellaAssistAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sellaAssistAuthenticator.getIBinder();
    }
}
