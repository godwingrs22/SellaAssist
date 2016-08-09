package it.sella.assist.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.sella.assist.R;
import it.sella.assist.dao.UserDAO;
import it.sella.assist.model.User;
import it.sella.assist.util.SharedPreferenceManager;
import it.sella.assist.util.Utility;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userDAO = new UserDAO(this);

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

            String gbsId = SharedPreferenceManager.getCache(Utility.USER_GBS_ID_KEY, "", getApplicationContext());
            if (!"".equals(gbsId)) {
                return userDAO.getUser(gbsId);
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null && user.isLoggedIn()) {
                Log.v(TAG, "<----User Already Logged In---->" + user);
                Intent i = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra(Utility.USER_KEY, user);
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
