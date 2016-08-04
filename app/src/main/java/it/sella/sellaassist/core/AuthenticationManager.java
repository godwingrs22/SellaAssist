package it.sella.sellaassist.core;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.sella.sellaassist.AppController;
import it.sella.sellaassist.http.HttpClient;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.ServerUtils;
import it.sella.sellaassist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 29-Jul-16.
 */
public class AuthenticationManager {
    private static final String TAG = AuthenticationManager.class.getSimpleName();
    private final HttpClient httpClient = AppController.getInstance().getHttpClient();
    private static final String SUCCESS_CODE = "BIOK";
    private static final String FAILURE_CODE = "BIKO";

    public static final String BUSINESS_UNIT = "Business unit";

    public List<String> getBusinessUnit() {
        String response = null;
        try {
            URL url = new URL(ServerUtils.getBusinessUnitURL());
            response = httpClient.getResponse(url, HttpClient.HTTP_GET, null, HttpClient.TIMEOUT);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return getBusinessUnitFromJson(response);
    }

    private List<String> getBusinessUnitFromJson(String response) {
        final String BUSINESS_UNIT_STATUS = "status";
        final String BUSINESS_UNIT_CODE = "code";
        final String BUSINESS_UNIT_PROFILES = "profiles";
        final String BUSINESS_UNIT_PROFILE_NAME = "name";

        List<String> businessUnitList = new ArrayList<>();
        businessUnitList.add(BUSINESS_UNIT);

        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(BUSINESS_UNIT_STATUS);
            String code = statusJSON.getString(BUSINESS_UNIT_CODE);
            if (SUCCESS_CODE.equals(code)) {
                JSONArray profileList = responseJSON.getJSONArray(BUSINESS_UNIT_PROFILES);
                if (profileList != null) {
                    for (int i = 0; i < profileList.length(); i++) {
                        JSONObject profileJson = (JSONObject) profileList.get(i);
                        businessUnitList.add(profileJson.getString(BUSINESS_UNIT_PROFILE_NAME));
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return businessUnitList;
    }

    public boolean doRegister(User user, Bitmap bitmap) {
      boolean isSuccess = false;
        try {
            URL url = new URL(ServerUtils.getRegisterURL());
            String input = buildRegisterJSONInput(user, bitmap);
            String response = httpClient.getResponse(url, HttpClient.HTTP_POST, input, HttpClient.TIMEOUT);
            isSuccess = isRegisterSuccessful(response);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            isSuccess = false;
        }
        return isSuccess;
//        return true;
    }

    public boolean doLogin(String gbsID, String password) {
        boolean isSuccess;
        try {
            URL url = new URL(ServerUtils.getLoginURL());
            String input = buildLoginJSONInput(gbsID, password);
            String response = httpClient.getResponse(url, HttpClient.HTTP_POST, input, HttpClient.TIMEOUT);
            isSuccess = isLoginSuccessful(response);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    private String buildRegisterJSONInput(User user, Bitmap bitmap) {
        final String REGISTER_USERCODE = "userCode";
        final String REGISTER_DEVICE_ID = "userDeviceId";
        final String REGISTER_BEACON_ID = "deviceId";
        final String REGISTER_USERNAME = "userName";
        final String REGISTER_PASSWORD = "password";
        final String REGISTER_PROFILE_IMAGE = "imgByte64Code";
        final String REGISTER_BUSINESS_UNIT = "businessUnit";

        JSONObject registerJSON = new JSONObject();
        try {
            registerJSON.put(REGISTER_USERCODE, user.getGbsID());
            registerJSON.put(REGISTER_DEVICE_ID, user.getDeviceId());
            registerJSON.put(REGISTER_BEACON_ID, "");
            registerJSON.put(REGISTER_USERNAME, user.getName());
            registerJSON.put(REGISTER_PASSWORD, user.getPassword());
            registerJSON.put(REGISTER_PROFILE_IMAGE, Utility.imageToBase64(bitmap));
            registerJSON.put(REGISTER_BUSINESS_UNIT, user.getBusinessUnit());
        } catch (JSONException e) {
            Log.e(TAG, "Exception ", e);
        }
        return registerJSON.toString();
    }

    private boolean isRegisterSuccessful(String response) {
        final String REGISTER_STATUS = "status";
        final String REGISTER_CODE = "code";
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(REGISTER_STATUS);
            String code = statusJSON.getString(REGISTER_CODE);
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

    private String buildLoginJSONInput(String gbsID, String password) {
        final String LOGIN_USERCODE = "user";
        final String LOGIN_PASSWORD = "password";

        JSONObject loginJSON = new JSONObject();
        try {
            loginJSON.put(LOGIN_USERCODE, gbsID);
            loginJSON.put(LOGIN_PASSWORD, password);
        } catch (JSONException e) {
            Log.e(TAG, "Exception ", e);
        }
        return loginJSON.toString();
    }

    private boolean isLoginSuccessful(String response) {
        final String LOGIN_STATUS = "status";
        final String LOGIN_CODE = "code";
        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(LOGIN_STATUS);
            String code = statusJSON.getString(LOGIN_CODE);
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