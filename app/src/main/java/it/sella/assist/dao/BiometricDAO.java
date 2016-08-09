package it.sella.assist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.data.SellaAssistContract.BiometricEntry;
import it.sella.assist.data.SellaAssistContract.BiometricInfoEntry;
import it.sella.assist.data.SellaAssistProvider;
import it.sella.assist.model.Biometric;
import it.sella.assist.model.BiometricInfo;
import it.sella.assist.model.User;

/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class BiometricDAO {

    private static final String TAG = BiometricDAO.class.getSimpleName();
    private Context context;

    public BiometricDAO(Context context) {
        this.context = context;
    }

    private ContentValues createBiometricContentValues(Biometric biometric) {
        final ContentValues biometricValues = new ContentValues();
        biometricValues.put(BiometricEntry._ID, biometric.getDate());
        biometricValues.put(BiometricEntry.COLUMN_DATE, biometric.getDate());

        return biometricValues;
    }

    private ContentValues createBiometricInfoContentValues(BiometricInfo biometricInfo) {
        final ContentValues biometricInfoValues = new ContentValues();
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_DATE, biometricInfo.getDate());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP, biometricInfo.getTimestamp());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_LOCATION, biometricInfo.getLocation());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_SENT, String.valueOf(biometricInfo.isSent()));

        return biometricInfoValues;
    }

    public void createBiometric(Biometric biometric, BiometricInfo biometricInfo) {
        Log.v(TAG, "<-----Creating Biometric Entry---->");
        if (!isBiometricExist(biometric.getDate())) {
            addBiometric(biometric);
        }
        addBiometricInfo(biometricInfo);
        notifyChange();
    }

    public boolean isBiometricExist(String date) {
        Cursor biometricCursor = context.getContentResolver().query(
                BiometricEntry.CONTENT_URI,
                BiometricEntry.BIOMETRIC_PROJECTION,
                SellaAssistProvider.biometricWithDateSelection,
                new String[]{date},
                null);
        if (biometricCursor != null && biometricCursor.getCount() > 0) {
            biometricCursor.close();
            Log.v(TAG, "<-----Biometric date already exist---->");
            return true;
        }
        return false;
    }

    public void addBiometric(Biometric biometric) {
        Log.v(TAG, "<-----Adding Biometric---->" + biometric);
        context.getContentResolver().insert(BiometricEntry.CONTENT_URI, createBiometricContentValues(biometric));
    }

    public void addBiometricInfo(BiometricInfo biometricInfo) {
        Log.v(TAG, "<-----Adding BiometricInfo---->" + biometricInfo);
        context.getContentResolver().insert(BiometricInfoEntry.CONTENT_URI, createBiometricInfoContentValues(biometricInfo));
    }

    public void updateBiometricInfo(BiometricInfo biometricInfo) {
        Log.v(TAG, "<-----Updating BiometricInfo---->" + biometricInfo);
        Uri biometricInfoUri = BiometricInfoEntry.buildBiometricInfoByTimestamp(biometricInfo.getTimestamp());
        Log.v(TAG, "<-----biometricInfoUri---->" + biometricInfoUri);

        context.getContentResolver().update(biometricInfoUri, createBiometricInfoContentValues(biometricInfo), null, null);
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

    public void notifyChange() {
        context.getContentResolver().notifyChange(BiometricEntry.CONTENT_URI, null, false);
        context.getContentResolver().notifyChange(BiometricInfoEntry.CONTENT_URI, null, false);
    }
}
