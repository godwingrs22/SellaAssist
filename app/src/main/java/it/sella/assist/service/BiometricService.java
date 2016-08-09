package it.sella.assist.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import it.sella.assist.core.BiometricManager;
import it.sella.assist.dao.BiometricDAO;
import it.sella.assist.model.BiometricInfo;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class BiometricService extends IntentService {

    private static final String TAG = BiometricService.class.getSimpleName();

    public BiometricService() {
        super("BiometricService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "<-------Initializing biometric synchronization--------->");

        try {
            final String gbsId = intent.getStringExtra(Utility.USER_GBS_ID_KEY);
            final BiometricManager biometricManager = new BiometricManager();
            final BiometricDAO biometricDAO = new BiometricDAO(this);

            final List<BiometricInfo> biometricInfos = biometricDAO.getAllBiometricsTobeUpdated(this);
            Log.v(TAG, "<-------Biometric to be updated in server------->" + biometricInfos);

            if (!biometricInfos.isEmpty() && biometricManager.updateBiometricToServer(biometricInfos, gbsId)) {
                for (BiometricInfo biometricInfo : biometricInfos) {
                    biometricInfo.setSent(true);
                    biometricDAO.updateBiometricInfo(biometricInfo);
                }
                Log.v(TAG, "<-------Biometric updated successfully------->");
            } else {
                Log.e(TAG, "<-------Biometric Not updated------->");
            }
            biometricDAO.notifyChange();
        } catch (Exception e) {
            Log.e(TAG, "<--Exception---->" + e.getMessage());
        }
    }
}
