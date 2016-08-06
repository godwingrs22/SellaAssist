package it.sella.sellaassist.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.sella.sellaassist.R;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.data.SellaAssistProvider;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.Utility;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_MULTIPLE_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Utility.doesUserHaveAllPermission(this)) {
                reviewPermissionDialog("SellaAssit needs certain permissions to work properly",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent i = new Intent(getApplicationContext(), PermissionActivity.class);
                                        startActivity(i);
                                        break;
                                }
                            }
                        });
            } else {
                Log.v(TAG, "<---All permissions are granted---->");
                validateIsLoggedIn();
            }
        } else {
            validateIsLoggedIn();
        }
    }

    private void validateIsLoggedIn() {
        new ValidateIsLoggedInTask().execute();
    }

    public class ValidateIsLoggedInTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }


            Cursor cursor = getContentResolver().query(SellaAssistContract.UserEntry.CONTENT_URI, SellaAssistContract.UserEntry.USER_PROJECTION, SellaAssistProvider.userLoggedInSelection, new String[]{String.valueOf(true)}, null);

            if (cursor.moveToFirst()) {
                User user = new User(Parcel.obtain());
                user.setGbsID(cursor.getString(SellaAssistContract.UserEntry.USER_GBS_ID));
                user.setName(cursor.getString(SellaAssistContract.UserEntry.USER_NAME));
                user.setPassword(cursor.getString(SellaAssistContract.UserEntry.USER_PASSWORD));
                user.setProfilePic(cursor.getString(SellaAssistContract.UserEntry.USER_PROFILE_PIC));
                user.setDeviceId(cursor.getString(SellaAssistContract.UserEntry.USER_DEVICEID));
                user.setLoggedIn(Boolean.parseBoolean(cursor.getString(SellaAssistContract.UserEntry.USER_LOGGED_IN)));
                user.setBusinessUnitName(cursor.getString(SellaAssistContract.UserEntry.USER_BUSINESS_UNIT_NAME));

                Log.v(TAG, "<----user---->" + user);
                return user;
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null && user.isLoggedIn()) {
                Log.v(TAG, "<----User Already Logged In---->" + user);
                Intent i = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra(Utility.USER_GBS_ID_KEY, user);
                startActivity(i);
            } else {
                Log.e(TAG, "<----User Not Logged In---->" + user);
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void reviewPermissionDialog(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("REVIEW PERMISSIONS", okListener)
                .setCancelable(false)
                .create()
                .show();
    }
}
