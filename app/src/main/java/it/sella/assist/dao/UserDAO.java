package it.sella.assist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import it.sella.assist.data.SellaAssistContract.UserEntry;
import it.sella.assist.data.SellaAssistProvider;
import it.sella.assist.model.User;


/**
 * Created by GodwinRoseSamuel on 08-Aug-16.
 */
public class UserDAO {

    private static final String TAG = UserDAO.class.getSimpleName();
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
    }

    private ContentValues createUserContentValues(User user) {
        final ContentValues userValues = new ContentValues();
        userValues.put(UserEntry.COLUMN_GBS_ID, user.getGbsID());
        userValues.put(UserEntry.COLUMN_NAME, user.getName());
        userValues.put(UserEntry.COLUMN_PASSWORD, user.getPassword());
        userValues.put(UserEntry.COLUMN_PROFILE_PIC, user.getProfilePic());
        userValues.put(UserEntry.COLUMN_DEVICEID, user.getDeviceId());
        userValues.put(UserEntry.COLUMN_LOGGED_IN, String.valueOf(user.isLoggedIn()));
        userValues.put(UserEntry.COLUMN_BUSINESS_UNIT_NAME, user.getBusinessUnitName());

        return userValues;
    }

    public void addUser(User user) {
        Log.v(TAG, "<-----Adding user---->" + user);
        context.getContentResolver().insert(UserEntry.CONTENT_URI, createUserContentValues(user));
    }

    public User getUser(String gbsId) {
        Log.v(TAG, "<-----Getting user with id---->" + gbsId);
        User user = null;
        Uri userUri = UserEntry.buildUserByGbsId(gbsId);
        Log.v(TAG, "<-----userUri---->" + userUri);

        Cursor userCursor = context.getContentResolver().query(
                userUri,
                UserEntry.USER_PROJECTION,
                SellaAssistProvider.userByGbsIdSelection,
                new String[]{gbsId},
                null);

        if (userCursor != null && userCursor.moveToFirst()) {
            user = new User(Parcel.obtain());
            user.setGbsID(userCursor.getString(UserEntry.USER_GBS_ID));
            user.setName(userCursor.getString(UserEntry.USER_NAME));
            user.setPassword(userCursor.getString(UserEntry.USER_PASSWORD));
            user.setProfilePic(userCursor.getString(UserEntry.USER_PROFILE_PIC));
            user.setDeviceId(userCursor.getString(UserEntry.USER_DEVICEID));
            user.setLoggedIn(Boolean.parseBoolean(userCursor.getString(UserEntry.USER_LOGGED_IN)));
            user.setBusinessUnitName(userCursor.getString(UserEntry.USER_BUSINESS_UNIT_NAME));
        }

        Log.v(TAG, "<----user---->" + user);

        return user;
    }

    public boolean isUserExist(String gbsId) {
        Cursor userCursor = context.getContentResolver().query(
                UserEntry.CONTENT_URI,
                UserEntry.USER_PROJECTION,
                SellaAssistProvider.userByGbsIdSelection,
                new String[]{gbsId},
                null);
        if (userCursor != null && userCursor.getCount() > 0) {
            userCursor.close();
            Log.v(TAG, "<-----isUserExist---->" + true);
            return true;
        }
        return false;
    }

    public void updateUser(User user) {
        Log.v(TAG, "<-----Updating user---->" + user);
        Uri userUri = UserEntry.buildUserByGbsId(user.getGbsID());
        Log.v(TAG, "<-----userUri---->" + userUri);

        context.getContentResolver().update(userUri, createUserContentValues(user), null, null);
    }
}
