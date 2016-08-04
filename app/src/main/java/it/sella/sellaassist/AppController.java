package it.sella.sellaassist;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.http.HttpClient;
import it.sella.sellaassist.model.Biometric;
import it.sella.sellaassist.model.BiometricInfo;
import it.sella.sellaassist.util.Utility;


/**
 * Created by GodwinRoseSamuel on 04-Dec-15.
 */

public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController appController;
    private HttpClient httpClient;
    private BeaconManager beaconManager;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

    public static final Region[] SELLA_BEACON_REGION = new Region[]{
            new Region("ENTRANCE", UUID.fromString(ESTIMOTE_PROXIMITY_UUID), 30143, 9262),
            new Region("WEALTH_MANAGMENT", UUID.fromString(ESTIMOTE_PROXIMITY_UUID), 30143, 9263),
            new Region("BRANCH", UUID.fromString(ESTIMOTE_PROXIMITY_UUID), 30143, 9265)
    };

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setBackgroundScanPeriod(1000, 1000);
        beaconManager.setForegroundScanPeriod(1000, 1000);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.v(TAG, "<-------onEnteredRegion----------->" + region.getIdentifier());
                Log.v(TAG, "<-------Adding Biometric----------->");

                long timestamp = new Date().getTime();
                Biometric biometric = new Biometric(Parcel.obtain());
                biometric.setDate(Utility.getFormattedDate(timestamp));

                BiometricInfo biometricInfo = new BiometricInfo(Parcel.obtain());
                biometricInfo.setTimestamp(timestamp);
                biometricInfo.setLocation(region.getIdentifier());
                biometricInfo.setDate(Utility.getFormattedDate(timestamp));
                biometricInfo.setSent(false);
                addBiometric(biometric, biometricInfo);
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.v(TAG, "<-------onExitedRegion----------->" + region.getIdentifier());
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
               /* for (Region region : SELLA_BEACON_REGION) {
                    beaconManager.startMonitoring(region);
                }*/
            }
        });

    }

    private void addBiometric(Biometric biometric, BiometricInfo biometricInfo) {

        String date;

        Cursor biometricCursor = getContentResolver().query(
                SellaAssistContract.BiometricEntry.CONTENT_URI,
                new String[]{SellaAssistContract.BiometricEntry._ID},
                SellaAssistContract.BiometricEntry._ID + " = ?",
                new String[]{biometric.getDate()},
                null);

        if (biometricCursor.moveToFirst()) {
            date = biometricCursor.getString(biometricCursor.getColumnIndex(SellaAssistContract.BiometricEntry._ID));
        } else {
            ContentValues biometricValues = new ContentValues();
            biometricValues.put(SellaAssistContract.BiometricEntry._ID, biometric.getDate());
            biometricValues.put(SellaAssistContract.BiometricEntry.COLUMN_DATE, biometric.getDate());

            date = biometric.getDate();
            getContentResolver().insert(SellaAssistContract.BiometricEntry.CONTENT_URI, biometricValues);
        }

        ContentValues biometricInfoValues = new ContentValues();
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_DATE, date);
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP, biometricInfo.getTimestamp());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_LOCATION, biometricInfo.getLocation());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_SENT, String.valueOf(biometricInfo.isSent()));

        getContentResolver().insert(SellaAssistContract.BiometricInfoEntry.CONTENT_URI, biometricInfoValues);
    }

    public static synchronized AppController getInstance() {
        return appController;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new HttpClient();
        }
        return httpClient;
    }
}
