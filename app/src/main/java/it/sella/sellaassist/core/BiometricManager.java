package it.sella.sellaassist.core;

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

import it.sella.sellaassist.AppController;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.http.HttpClient;
import it.sella.sellaassist.model.BiometricInfo;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.ServerUtils;

/**
 * Created by GodwinRoseSamuel on 26-Jul-16.
 */
public class BiometricManager {
    private static final String TAG = BiometricManager.class.getSimpleName();
    private final HttpClient httpClient = AppController.getInstance().getHttpClient();
    private static final String SUCCESS_CODE = "BIOK";
    private static final String FAILURE_CODE = "BIKO";

    public boolean updateBiometricToServer(List<BiometricInfo> biometrics, User user) {
        boolean isSuccess;
        try {
            URL url = new URL(ServerUtils.getBiometricURL());
            String input = buildBiometricJSONInput(biometrics, user);
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

    private String buildBiometricJSONInput(List<BiometricInfo> biometrics, User user) {
        final String BIOMETRIC_USER = "user";
        final String BIOMETRIC_USER_CODE = "code";
        final String BIOMETRIC_LIST = "biometrics";
        final String BIOMETRIC_TIMESTAMP = "timestamp";
        final String BIOMETRIC_LOCATION = "location";

        JSONObject requestJSON = new JSONObject();
        try {
            JSONObject userJSON = new JSONObject();
            userJSON.put(BIOMETRIC_USER_CODE, user.getGbsID());

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

    public List<BiometricInfo> getAllBiometricsTobeUpdated(Context context) {
        List<BiometricInfo> biometricInfos = new LinkedList<>();
        Cursor biometricCursor = context.getContentResolver().query(
                SellaAssistContract.BiometricInfoEntry.CONTENT_URI,
                SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_PROJECTION,
                SellaAssistContract.BiometricInfoEntry.COLUMN_SENT + " = ?",
                new String[]{String.valueOf(false)},
                null);
        if (biometricCursor.moveToFirst()) {
            do {
                BiometricInfo biometricInfo = new BiometricInfo(Parcel.obtain());
                biometricInfo.setDate(biometricCursor.getString(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_DATE));
                biometricInfo.setTimestamp(biometricCursor.getLong(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_TIMESTAMP));
                biometricInfo.setLocation(biometricCursor.getString(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_LOCATION));
                biometricInfo.setSent(Boolean.parseBoolean(biometricCursor.getString(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_SENT)));

                biometricInfos.add(biometricInfo);
            } while (biometricCursor.moveToNext());
        }
        return biometricInfos;
    }
}
