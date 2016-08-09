package it.sella.assist.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import it.sella.assist.R;
import it.sella.assist.core.AuthenticationManager;
import it.sella.assist.dao.UserDAO;
import it.sella.assist.model.User;
import it.sella.assist.util.Utility;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private AutoCompleteTextView gbsIdView;
    private EditText passwordView;
    private Button registerButton;
    private AuthenticationManager authenticationManager;
    protected ProgressDialog progressDialog;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(this);

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
            userDAO.updateUser(user);
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

        User user = userDAO.getUser(gbsID);

        if (user != null) {
            Log.v(TAG, "<-----User found in db---->");
            if (gbsID.equalsIgnoreCase(user.getGbsID()) && password.equals(user.getPassword())) {
                Log.v(TAG, "<-----User valid---->");
                user.setLoggedIn(true);
                userDAO.updateUser(user);
                Log.v(TAG, "<-----Login Successful---->");
                return user;
            }
        } else {
            Log.e(TAG, "<-----User not found in db---->");
            user = authenticationManager.doLogin(gbsID, password);
            if (user != null) {
                user.setPassword(password);
                user.setLoggedIn(true);
                userDAO.addUser(user);
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

