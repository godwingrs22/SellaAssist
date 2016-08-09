package it.sella.assist.core;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;

import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.sella.assist.AppController;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.http.HttpClient;
import it.sella.assist.model.BiometricInfo;
import it.sella.assist.model.User;
import it.sella.assist.util.ServerUtils;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class BiometricManager {
    private static final String TAG = BiometricManager.class.getSimpleName();
    private final HttpClient httpClient = AppController.getInstance().getHttpClient();
    private static final String SUCCESS_CODE = "BIOK";
    private static final String FAILURE_CODE = "BIKO";

    public boolean updateBiometricToServer(List<BiometricInfo> biometrics, String gbsId) {
        boolean isSuccess;
        try {
            URL url = new URL(ServerUtils.getBiometricURL());
            String input = buildBiometricJSONInput(biometrics, gbsId);
            String response = httpClient.getResponse(url, HttpClient.HTTP_POST, input, HttpClient.TIMEOUT);
            isSuccess = isUpdateSuccessful(response);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    private String getBeaconId(String location) {
        Map<String, Integer> beaconList = new HashMap<>();

        for (Region region : AppController.SELLA_BEACON_REGION)
            beaconList.put(region.getIdentifier(), region.getMinor());

        return String.valueOf(beaconList.get(location));
    }

    private String buildBiometricJSONInput(List<BiometricInfo> biometrics, String gbsId) {
        final String BIOMETRIC_USER = "user";
        final String BIOMETRIC_USER_CODE = "code";
        final String BIOMETRIC_LIST = "biometrics";
        final String BIOMETRIC_TIMESTAMP = "timestamp";
        final String BIOMETRIC_LOCATION = "location";

        JSONObject requestJSON = new JSONObject();
        try {
            JSONObject userJSON = new JSONObject();
            userJSON.put(BIOMETRIC_USER_CODE, gbsId);

            JSONArray biometricListJSON = new JSONArray();
            for (BiometricInfo biometric : biometrics) {
                JSONObject biometricJSON = new JSONObject();
                biometricJSON.put(BIOMETRIC_TIMESTAMP, biometric.getTimestamp());
                biometricJSON.put(BIOMETRIC_LOCATION, getBeaconId(biometric.getLocation()));
                biometricListJSON.put(biometricJSON);
            }

            requestJSON.put(BIOMETRIC_USER, userJSON);
            requestJSON.put(BIOMETRIC_LIST, biometricListJSON);
        } catch (JSONException e) {
            Log.e(TAG, "Exception ", e);
        }

        return requestJSON.toString();
    }

    private boolean isUpdateSuccessful(String response) {
        final String BIOMETRIC_STATUS = "status";
        final String BIOMETRIC_CODE = "code";
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(BIOMETRIC_STATUS);
            String code = statusJSON.getString(BIOMETRIC_CODE);
            if (SUCCESS_CODE.equals(code))
                return true;
            else if (FAILURE_CODE.equals(code))
                return false;
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            return false;
        }
        return false;
    }
}
