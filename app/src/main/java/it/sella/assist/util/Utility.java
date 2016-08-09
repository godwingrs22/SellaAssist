package it.sella.assist.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.sella.assist.R;
import it.sella.assist.model.Permission;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by GodwinRoseSamuel on 03-Jul-16.
 */
public class Utility {

    public static final String USER_KEY = "it.sella.assist.user";
    public static final String USER_GBS_ID_KEY = "it.sella.assist.user.gbsid";
    public static final String FEED_KEY = "it.sella.assist.feed";
    public static final String EVENT_KEY = "it.sella.assist.event";
    public static final String BEACON_ID_KEY = "it.sella.assist.beacon.id";


    public static String calculateGrossHours(long startTimestamp, long endTimestamp) {
        return getTimeDifference(startTimestamp, endTimestamp);
    }

    public static String getTimeDifference(long startTimestamp, long endTimestamp) {
        long diff = endTimestamp - startTimestamp;

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        DecimalFormat mFormat = new DecimalFormat("00");

        return mFormat.format(Double.valueOf(diffHours)) + ":" + mFormat.format(Double.valueOf(diffMinutes));
    }

    public static String getEventsDay(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date date = new Date();
        date.setTime(timestamp);
        return formatter.format(date);
    }

    public static String getEventsMonth(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM");
        Date date = new Date();
        date.setTime(timestamp);
        return formatter.format(date);
    }


    public static long getDateInTimestamp(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        long timestamp = 0;
        try {
            timestamp = formatter.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static String getFormattedDate1(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        date.setTime(timestamp);
        return formatter.format(date);
    }

    public static String getFormattedDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date.setTime(timestamp);
        return formatter.format(date);
    }

    public static String getFormattedTime(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(timestamp);
        return formatter.format(date);
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static long getDateBefore(int days) {
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date.setTime(c.getTime().getTime());

        return date.getTime();
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getFeedTimeStamp(long timestamp) {
        return (String) DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static Bitmap getResizedBitmap(Uri imageURI, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(imageURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(is, null, options);
        int Height = bm.getHeight();
        int Width = bm.getWidth();
        int newHeight = 250;
        int newWidth = 250;
        float scaleWidth = ((float) newWidth) / Width;
        float scaleHeight = ((float) newHeight) / Height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, Width, Height, matrix, true);

        return resizedBitmap;
    }

    public static List<Permission> addPermissions() {
        List<Permission> permissions = new ArrayList<>();
//        Permission bluetoothPermission = new Permission(BLUETOOTH, R.drawable.ic_bluetooth_white_48dp, "Bluetooth", "Allows SellaAssit to scan and monitor beacons");
        Permission locationPermission = new Permission(ACCESS_COARSE_LOCATION, R.drawable.ic_location_on_white_48dp, "Location", "Allows SellaAssist for accessing beacons in background");
        Permission storagePermission = new Permission(WRITE_EXTERNAL_STORAGE, R.drawable.ic_sd_storage_white_48dp, "Storage", "Allows SellaAssist to store data in your internal storage");
        Permission cameraPermission = new Permission(CAMERA, R.drawable.ic_photo_camera_white_48dp, "Camera", "Allows SellaAssist to take photo while registration");
        Permission smsPermission = new Permission(SEND_SMS, R.drawable.ic_sms_white_48dp, "SMS", "Allows you to apply leave through Sella Assist");
        Permission calendarPermission = new Permission(WRITE_CALENDAR, R.drawable.ic_date_range_white_48dp, "Calendar", "Allows you to set remainder on calendar for events through Sella Assist");

//        permissions.add(bluetoothPermission);
        permissions.add(locationPermission);
        permissions.add(storagePermission);
        permissions.add(cameraPermission);
        permissions.add(smsPermission);
        permissions.add(calendarPermission);

        return permissions;
    }

    public static boolean doesUserHaveAllPermission(Context context) {
        for (Permission permission : addPermissions()) {
            if (context.checkCallingOrSelfPermission(permission.getName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static String imageToBase64(Bitmap bitmap) {
        Log.v("imageToBase64", bitmap.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

}
