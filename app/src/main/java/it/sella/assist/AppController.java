package it.sella.assist;

import android.app.Application;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.sella.assist.dao.BiometricDAO;
import it.sella.assist.http.HttpClient;
import it.sella.assist.model.Biometric;
import it.sella.assist.model.BiometricInfo;
import it.sella.assist.service.FeedService;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;


/**
 * Created by GodwinRoseSamuel on 03-Aug-15.
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

    public static final Region DEFAULT_BEACON_REGION = SELLA_BEACON_REGION[0]; //ENTRANCE BEACON

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
                final String gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", getApplicationContext());
                if (!"".equals(gbsId)) {
                    long timestamp = new Date().getTime();
                    Biometric biometric = new Biometric(Parcel.obtain());
                    biometric.setDate(Utility.getFormattedDate(timestamp));

                    BiometricInfo biometricInfo = new BiometricInfo(Parcel.obtain());
                    biometricInfo.setTimestamp(timestamp);
                    biometricInfo.setLocation(region.getIdentifier());
                    biometricInfo.setDate(Utility.getFormattedDate(timestamp));
                    biometricInfo.setSent(false);

                    final BiometricDAO biometricDAO = new BiometricDAO(getApplicationContext());
                    biometricDAO.createBiometric(biometric, biometricInfo);

                    //Fetch feed for the particular beacon
                    Intent feedServiceIntent = new Intent(getApplicationContext(), FeedService.class);
                    feedServiceIntent.putExtra(Utility.USER_GBS_ID_KEY, gbsId);
                    feedServiceIntent.putExtra(Utility.BEACON_ID_KEY, region.getMinor());
                    getApplicationContext().startService(feedServiceIntent);
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.v(TAG, "<-------onExitedRegion----------->" + region.getIdentifier());
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (Region region : SELLA_BEACON_REGION) {
                    beaconManager.startMonitoring(region);
                }
            }
        });
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
