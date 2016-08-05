package it.sella.sellaassist.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.sella.sellaassist.R;
import it.sella.sellaassist.core.AuthenticationManager;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.data.SellaAssistProvider;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.Utility;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private AutoCompleteTextView gbsIdView;
    private EditText passwordView;
    private Button registerButton;
    private AuthenticationManager authenticationManager;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gbsIdView = (AutoCompleteTextView) findViewById(R.id.login_gbsid_textview);
        passwordView = (EditText) findViewById(R.id.login_password_textview);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    boolean isValid = isValidInput();
                    if (isValid) {
                        doLogin();
                    }
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = isValidInput();
                if (isValid) {
                    doLogin();
                }
            }
        });


        registerButton = (Button) findViewById(R.id.login_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        User user = getIntent().getParcelableExtra(Utility.USER_KEY);
        if (user != null && user.isLoggedIn()) {
            user.setLoggedIn(false);

            ContentValues userValues = new ContentValues();
            userValues.put(SellaAssistContract.UserEntry.COLUMN_LOGGED_IN, String.valueOf(user.isLoggedIn()));

            getContentResolver().update(SellaAssistContract.UserEntry.CONTENT_URI, userValues, SellaAssistProvider.userWithIdSelection, new String[]{user.getGbsID()});
        }
    }

    public void doLogin() {
        String gbsID = gbsIdView.getText().toString();
        String password = passwordView.getText().toString();
        UserLoginTask userLoginTask = new UserLoginTask(gbsID, password);
        userLoginTask.execute();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, User> {

        private final String mGbsID;
        private final String mPassword;

        UserLoginTask(String gbsID, String password) {
            mGbsID = gbsID;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("validating user credentials..");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return validateLogin(mGbsID, mPassword);
        }

        @Override
        protected void onPostExecute(User user) {
            progressDialog.dismiss();
            if (user != null && user.isLoggedIn()) {
                loginSuccess(user);
            } else {
                Snackbar.make(registerButton, getString(R.string.error_incorrect_credentials), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private User validateLogin(String gbsID, String password) {
        authenticationManager = new AuthenticationManager();

        Cursor userCursor = getContentResolver().query(
                SellaAssistContract.UserEntry.CONTENT_URI,
                SellaAssistContract.UserEntry.USER_PROJECTION,
                SellaAssistContract.UserEntry.COLUMN_GBS_ID + " = ?",
                new String[]{gbsID},
                null);

        if (userCursor.moveToFirst()) {
            Log.v(TAG, "<-----User found internal db---->");

            User user = new User(Parcel.obtain());
            user.setGbsID(userCursor.getString(SellaAssistContract.UserEntry.USER_GBS_ID));
            user.setName(userCursor.getString(SellaAssistContract.UserEntry.USER_NAME));
            user.setPassword(userCursor.getString(SellaAssistContract.UserEntry.USER_PASSWORD));
            user.setProfilePic(userCursor.getString(SellaAssistContract.UserEntry.USER_PROFILE_PIC));
            user.setDeviceId(userCursor.getString(SellaAssistContract.UserEntry.USER_DEVICEID));
            user.setLoggedIn(Boolean.parseBoolean(userCursor.getString(SellaAssistContract.UserEntry.USER_LOGGED_IN)));
            user.setBusinessUnitName(userCursor.getString(SellaAssistContract.UserEntry.USER_BUSINESS_UNIT_NAME));

            if (gbsID.equalsIgnoreCase(user.getGbsID()) && password.equals(user.getPassword())) {
                Log.v(TAG, "<-----User valid---->");
                user.setLoggedIn(true);

                ContentValues userValues = new ContentValues();
                userValues.put(SellaAssistContract.UserEntry.COLUMN_LOGGED_IN, String.valueOf(user.isLoggedIn()));
                getContentResolver().update(SellaAssistContract.UserEntry.CONTENT_URI, userValues, SellaAssistProvider.userWithIdSelection, new String[]{user.getGbsID()});

                Log.v(TAG, "<-----Login Successful---->");
                return user;
            }

        } else {
            Log.e(TAG, "<-----User not in internal db---->");
            if (authenticationManager.doLogin(gbsID, password)) {
                User user = new User(Parcel.obtain());
                user.setGbsID(gbsID);
                user.setPassword(password);
                user.setLoggedIn(true);

                ContentValues userValues = new ContentValues();
                userValues.put(SellaAssistContract.UserEntry.COLUMN_GBS_ID, user.getGbsID());
                userValues.put(SellaAssistContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
                userValues.put(SellaAssistContract.UserEntry.COLUMN_LOGGED_IN, String.valueOf(user.isLoggedIn()));

                getContentResolver().insert(SellaAssistContract.UserEntry.CONTENT_URI, userValues);

                Log.v(TAG, "<-----Login Successful---->");
                return user;
            }
        }

        Log.e(TAG, "<-----Invalid Login---->");
        return null;
    }


    private void loginSuccess(User user) {
        Log.v(TAG, "<----User Logged In---->" + user);
//        sellaAssitProvider.updateUser(user);
        finish();
        Intent i = new Intent(this, MainActivity.class)
                .putExtra(Utility.USER_KEY, user);
        startActivity(i);
    }

    public boolean isValidInput() {

        if (TextUtils.isEmpty(gbsIdView.getText())) {
            Snackbar.make(registerButton, getString(R.string.error_gbs_field_required), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gbsIdView.setFocusable(true);
                        }
                    })
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(passwordView.getText())) {
            Snackbar.make(registerButton, getString(R.string.error_invalid_password), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            passwordView.setFocusable(true);
                        }
                    })
                    .show();
            return false;
        } else
            return true;
    }

    private void checkNetwork() {
        if (!Utility.isInternetConnected(this)) {
            Snackbar.make(registerButton, getString(R.string.error_network_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkNetwork();
    }
}

