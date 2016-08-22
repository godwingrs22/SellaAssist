package it.sella.assist.core;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;

import it.sella.assist.R;
import it.sella.assist.dao.UserDAO;
import it.sella.assist.model.User;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 23-Aug-16.
 */
public class SOSManager {
    private static String[] sosNos;

    public static Boolean requestHelp(Context context) {
        try {
            sosNos = context.getResources().getStringArray(R.array.sos_phoneno);
            LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            LocationListener mlocListener = new MyLocationListener();
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

            Location location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                final SmsManager smsManager = SmsManager.getDefault();
                for (String sosNo : sosNos)
                    smsManager.sendTextMessage(sosNo, null, getMessage(context, location), null, null);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private static String getMessage(Context context, Location location) {
        final String gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", context);
        String message = null;
        if (!"".equals(gbsId)) {
            final UserDAO userDAO = new UserDAO(context);
            final User user = userDAO.getUser(gbsId);
            message = user.getName() + " is on emergency and is requesting help. His/Her location: https://www.google.com/maps/@" + location.getLatitude() + "," + location.getLongitude();
        }
        return message;
    }
}
