package it.sella.assist.core;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import it.sella.assist.http.HttpClient;
import it.sella.assist.model.BusinessUnit;
import it.sella.assist.model.User;
import it.sella.assist.util.ServerUtils;
import it.sella.assist.util.Utility;
import okhttp3.HttpUrl;

/**
 * Created by GodwinRoseSamuel on 29-Jul-16.
 */
public class AuthenticationManager {
    private static final String TAG = AuthenticationManager.class.getSimpleName();
    private static final String SUCCESS_CODE = "BIOK";
    private static final String FAILURE_CODE = "BIKO";

    public static final String BUSINESS_UNIT = "Business unit";

    public Map<String, BusinessUnit> getBusinessUnit() {
        String response = null;
        try {
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.BUSINESS_UNIT_API)
                    .build();
            response = HttpClient.GET(httpUrl);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return getBusinessUnitFromJson(response);
    }

    private Map<String, BusinessUnit> getBusinessUnitFromJson(String response) {
        final String BUSINESS_UNIT_STATUS = "status";
        final String BUSINESS_UNIT_CODE = "code";
        final String BUSINESS_UNIT_PROFILES = "profiles";
        final String BUSINESS_UNIT_PROFILE_ID = "id";
        final String BUSINESS_UNIT_PROFILE_NAME = "name";
        final String BUSINESS_UNIT_PROFILE_OWNER_ID = "owner";

        Map<String, BusinessUnit> businessUnitMap = new LinkedHashMap<>();
        BusinessUnit headerBusinessUnit = new BusinessUnit(Parcel.obtain());
        headerBusinessUnit.setId(0);
        headerBusinessUnit.setName(BUSINESS_UNIT);
        businessUnitMap.put(BUSINESS_UNIT, headerBusinessUnit);

        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject statusJSON = (JSONObject) responseJSON.get(BUSINESS_UNIT_STATUS);
            String code = statusJSON.getString(BUSINESS_UNIT_CODE);
            if (SUCCESS_CODE.equals(code)) {
                JSONArray profileList = responseJSON.getJSONArray(BUSINESS_UNIT_PROFILES);
                if (profileList != null) {
                    for (int i = 0; i < profileList.length(); i++) {
                        JSONObject profileJson = (JSONObject) profileList.get(i);
                        BusinessUnit businessUnit = new BusinessUnit(Parcel.obtain());
                        businessUnit.setId(profileJson.getInt(BUSINESS_UNIT_PROFILE_ID));
                        businessUnit.setName(profileJson.getString(BUSINESS_UNIT_PROFILE_NAME));
                        businessUnit.setOwnerId(profileJson.getString(BUSINESS_UNIT_PROFILE_OWNER_ID));
                        businessUnitMap.put(businessUnit.getName(), businessUnit);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return businessUnitMap;
    }

    public boolean doRegister(User user, BusinessUnit businessUnit, Bitmap bitmap) {
        boolean isSuccess;
        try {
            final HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.REGISTER_API)
                    .build();

            final String input = buildRegisterJSONInput(user, businessUnit, bitmap);
            final String response = HttpClient.POST(httpUrl, input);
            isSuccess = isRegisterSuccessful(response);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
            isSuccess = false;
        }
        return isSuccess;
//        return true;
    }

    private String buildRegisterJSONInput(User user, BusinessUnit businessUnit, Bitmap bitmap) {
        final String REGISTER_USERCODE = "userCode";
        final String REGISTER_DEVICE_ID = "userDeviceId";
        final String REGISTER_BEACON_ID = "deviceId";
        final String REGISTER_USERNAME = "userName";
        final String REGISTER_PASSWORD = "password";
        final String REGISTER_PROFILE_IMAGE = "imgByte64Code";
        final String REGISTER_BUSINESS_UNIT_ID = "businessUnit";

        JSONObject registerJSON = new JSONObject();
        try {
            registerJSON.put(REGISTER_USERCODE, user.getGbsID());
            registerJSON.put(REGISTER_DEVICE_ID, user.getDeviceId());
            registerJSON.put(REGISTER_BEACON_ID, "");
            registerJSON.put(REGISTER_USERNAME, user.getName());
            registerJSON.put(REGISTER_PASSWORD, user.getPassword());
            registerJSON.put(REGISTER_PROFILE_IMAGE, Utility.imageToBase64(bitmap));
            registerJSON.put(REGISTER_BUSINESS_UNIT_ID, businessUnit.getId());
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

    public User doLogin(String gbsID, String password) {
        User user = null;
        try {
            final HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(ServerUtils.HTTP_PROTOCOL)
                    .host(ServerUtils.HOSTNAME)
                    .port(ServerUtils.PORT_NO)
                    .addPathSegments(ServerUtils.LOGIN_API)
                    .build();
            final String input = buildLoginJSONInput(gbsID, password);
            final String response = HttpClient.POST(httpUrl, input);
            user = getUserFromLoginJson(response);
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return user;
    }

    private User getUserFromLoginJson(String response) {
        final String LOGIN_USER = "currentUser";
        final String LOGIN_USER_CODE = "code";
        final String LOGIN_USER_NAME = "name";
        final String LOGIN_USER_IMAGE = "image";

        try {
            JSONObject responseJSON = new JSONObject(response);
            JSONObject userJSON = responseJSON.getJSONObject(LOGIN_USER);
            if (userJSON != null) {
                User user = new User(Parcel.obtain());
                user.setGbsID(userJSON.getString(LOGIN_USER_CODE));
                user.setName(userJSON.getString(LOGIN_USER_NAME));
                user.setProfilePic(ServerUtils.getServerURL() + userJSON.getString(LOGIN_USER_IMAGE));

                return user;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception ", e);
        }
        return null;
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
