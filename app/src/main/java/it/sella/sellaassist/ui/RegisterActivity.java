package it.sella.sellaassist.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.sella.sellaassist.R;
import it.sella.sellaassist.core.AuthenticationManager;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.model.BusinessUnit;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.util.Utility;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
    private ImageView profilePhotoView;
    private Uri outputFileUri;
    private Uri selectedImageUri;
    private AutoCompleteTextView gbsIdView;
    private AutoCompleteTextView nameView;
    private EditText passwordView;
    private LinearLayout registerActivity;
    private Button registerButton;
    protected ProgressDialog progressDialog;
    private AuthenticationManager authenticationManager;
    private Spinner businessUnitChooser;
    private Handler handler;
    private Map<String, BusinessUnit> businessUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        gbsIdView = (AutoCompleteTextView) findViewById(R.id.register_gbsid_textview);
        nameView = (AutoCompleteTextView) findViewById(R.id.register_name_textview);
        passwordView = (EditText) findViewById(R.id.register_password_textview);
        businessUnitChooser = (Spinner) findViewById(R.id.register_businessunit_chooser);
        registerActivity = (LinearLayout) findViewById(R.id.register_activity);

        authenticationManager = new AuthenticationManager();

        checkNetwork();

        handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    businessUnits = authenticationManager.getBusinessUnit();
                    final List<String> businessUnitNames = new ArrayList<>(businessUnits.keySet());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> businessUnitAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, businessUnitNames);
                            businessUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            businessUnitChooser.setAdapter(businessUnitAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

        gbsIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    boolean isValid = isValidInput();
                    if (isValid) {
                        doRegister();
                    }
                    return true;
                }
                return false;
            }
        });

        profilePhotoView = (ImageView) findViewById(R.id.register_profile_photo);
        profilePhotoView.setTag("profile_avatar");

        profilePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromDevice();
            }
        });

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = isValidInput();
                if (isValid) {
                    doRegister();
                }
            }
        });
    }

    private void checkNetwork() {
        if (!Utility.isInternetConnected(this)) {
            Snackbar.make(registerActivity, getString(R.string.error_network_info), Snackbar.LENGTH_LONG).show();
        }
    }

    public void doRegister() {
        String gbsID = gbsIdView.getText().toString();
        String name = nameView.getText().toString();
        String password = passwordView.getText().toString();
        String businessUnitName = String.valueOf(businessUnitChooser.getSelectedItem());

        if (businessUnitName != null && !"".equalsIgnoreCase(businessUnitName) && !AuthenticationManager.BUSINESS_UNIT.equalsIgnoreCase(businessUnitName)) {
            if (Utility.isInternetConnected(this)) {
                UserRegisterTask userRegisterTask = new UserRegisterTask(gbsID, name, password, selectedImageUri, businessUnits.get(businessUnitName));
                userRegisterTask.execute();
            } else {
                Snackbar.make(registerActivity, getString(R.string.error_network_info), Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(registerActivity, getString(R.string.error_business_unit_failed), Snackbar.LENGTH_LONG).show();
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, User> {

        private final String gbsId;
        private final String name;
        private final String password;
        private final Uri selectedImageUri;
        private final BusinessUnit businessUnit;

        UserRegisterTask(String gbsId, String name, String password, Uri selectedImageUri, BusinessUnit businessUnit) {
            this.gbsId = gbsId;
            this.name = name;
            this.password = password;
            this.selectedImageUri = selectedImageUri;
            this.businessUnit = businessUnit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Registering as a new user..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return registerUser(gbsId, name, password, selectedImageUri, businessUnit);
        }

        @Override
        protected void onPostExecute(User user) {
            progressDialog.dismiss();
            if (user != null && user.isLoggedIn()) {
                registerSuccess(user);
            } else {
                Snackbar.make(registerActivity, getString(R.string.error_register_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private User registerUser(String gbsId, String name, String password, Uri selectedImageUri, BusinessUnit businessUnit) {
        User user = new User(Parcel.obtain());
        user.setGbsID(gbsId);
        user.setName(name);
        user.setPassword(password);
        user.setProfilePic(String.valueOf(selectedImageUri));
        user.setDeviceId(Utility.getDeviceId(RegisterActivity.this));
        user.setBusinessUnitName(businessUnit.getName());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            if (authenticationManager.doRegister(user, businessUnit, bitmap)) {
                Log.v(TAG, "<-----user registered---->" + user);
                user.setLoggedIn(true);
                addUser(user);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "<-----Unable to register---->");
        return null;
    }

    private long addUser(User user) {
        long id;
        Cursor userCursor = getContentResolver().query(
                SellaAssistContract.UserEntry.CONTENT_URI,
                new String[]{SellaAssistContract.UserEntry._ID},
                SellaAssistContract.UserEntry.COLUMN_GBS_ID + " = ?",
                new String[]{user.getGbsID()},
                null);

        if (userCursor.moveToFirst()) {
            int idIndex = userCursor.getColumnIndex(SellaAssistContract.UserEntry._ID);
            id = userCursor.getLong(idIndex);
        } else {
            ContentValues userValues = new ContentValues();
            userValues.put(SellaAssistContract.UserEntry.COLUMN_GBS_ID, user.getGbsID());
            userValues.put(SellaAssistContract.UserEntry.COLUMN_NAME, user.getName());
            userValues.put(SellaAssistContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
            userValues.put(SellaAssistContract.UserEntry.COLUMN_PROFILE_PIC, user.getProfilePic());
            userValues.put(SellaAssistContract.UserEntry.COLUMN_DEVICEID, user.getDeviceId());
            userValues.put(SellaAssistContract.UserEntry.COLUMN_LOGGED_IN, String.valueOf(user.isLoggedIn()));
            userValues.put(SellaAssistContract.UserEntry.COLUMN_BUSINESS_UNIT_NAME, user.getBusinessUnitName());

            Uri insertedUri = getContentResolver().insert(SellaAssistContract.UserEntry.CONTENT_URI, userValues);

            id = ContentUris.parseId(insertedUri);
        }

        Log.v(TAG, "<------user added--------->" + id);

        userCursor.close();

        return id;
    }

    private void registerSuccess(User user) {
        finish();
        Intent i = new Intent(this, MainActivity.class)
                .putExtra(Utility.USER_GBS_ID_KEY, user);
        startActivity(i);
    }

    public boolean isValidInput() {

        if (TextUtils.isEmpty(gbsIdView.getText())) {
            Snackbar.make(registerActivity, getString(R.string.error_gbs_field_required), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gbsIdView.setFocusable(true);
                        }
                    })
                    .show();
            return false;
        } else if (TextUtils.isEmpty(nameView.getText())) {
            Snackbar.make(registerActivity, getString(R.string.error_name_field_required), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nameView.setFocusable(true);
                        }
                    })
                    .show();
            return false;
        } else if (TextUtils.isEmpty(passwordView.getText())) {
            Snackbar.make(registerButton, getString(R.string.error_password_field_required), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            passwordView.setFocusable(true);
                        }
                    })
                    .show();
            return false;
        } else if (profilePhotoView.getTag().equals(getResources().getResourceEntryName(R.drawable.profile_avatar))) {
            Snackbar.make(registerActivity, getString(R.string.error_profile_photo_required), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
            return false;
        } else
            return true;
    }

    private void getImageFromDevice() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "SellaAssit" + File.separator);
        root.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        final File sdImageMainDirectory = new File(root, imageFileName);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    Log.v(TAG, "Image Captured:----->" + selectedImageUri.getPath());
                    profilePhotoView.setTag("");
                    profilePhotoView.setImageURI(selectedImageUri);
                } else {
                    if (data.getData() == null) {
                        selectedImageUri = outputFileUri;
                        Log.v(TAG, "Image Captured:----->" + selectedImageUri.getPath());
                        profilePhotoView.setTag("");
                        profilePhotoView.setImageURI(selectedImageUri);

                    } else {
                        selectedImageUri = data.getData();
                        Log.v(TAG, "Image Taken from Source:----->" + selectedImageUri.getPath());
                        profilePhotoView.setTag("");
                        profilePhotoView.setImageURI(selectedImageUri);
                    }
                }
            }
        }
    }
}

