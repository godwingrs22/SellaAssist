package it.sella.sellaassist.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Calendar;

import it.sella.sellaassist.AppController;
import it.sella.sellaassist.R;
import it.sella.sellaassist.data.SellaAssistContract;
import it.sella.sellaassist.model.Biometric;
import it.sella.sellaassist.model.BiometricInfo;
import it.sella.sellaassist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 30-Jul-16.
 */
public class BiometricAddActivity extends AppCompatActivity {
    private static final String TAG = BiometricAddActivity.class.getSimpleName();
    private Spinner locationChooser;
    private TimePicker timePicker;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_add);

        locationChooser = (Spinner) findViewById(R.id.biomteric_add_location_chooser);
        timePicker = (TimePicker) findViewById(R.id.biomteric_add_timepicker);
        addButton = (Button) findViewById(R.id.biomteric_add);

        ArrayList<String> locationList = new ArrayList<>();

        for (Region region : AppController.SELLA_BEACON_REGION)
            locationList.add(region.getIdentifier());

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationChooser.setAdapter(locationAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= 23) {
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                }

                long timestamp = calendar.getTimeInMillis();
                Biometric biometric = new Biometric(Parcel.obtain());
                biometric.setDate(Utility.getFormattedDate(timestamp));

                BiometricInfo biometricInfo = new BiometricInfo(Parcel.obtain());
                biometricInfo.setTimestamp(timestamp);
                biometricInfo.setLocation(String.valueOf(locationChooser.getSelectedItem()));
                biometricInfo.setDate(Utility.getFormattedDate(timestamp));
                biometricInfo.setSent(false);

                addBiometric(biometric, biometricInfo);

                Log.v(TAG, "<------biometricInfo------>" + biometricInfo);

                Intent intent = new Intent();
                intent.putExtra(BiometricFragment.BIOMETRIC_ADD_KEY, true);
                setResult(BiometricFragment.BIOMETRIC_ADD_MANUAL, intent);
                finish();
            }
        });
    }

    private void addBiometric(Biometric biometric, BiometricInfo biometricInfo) {

        String date;

        Cursor biometricCursor = getContentResolver().query(
                SellaAssistContract.BiometricEntry.CONTENT_URI,
                new String[]{SellaAssistContract.BiometricEntry._ID},
                SellaAssistContract.BiometricEntry._ID + " = ?",
                new String[]{biometric.getDate()},
                null);

        if (biometricCursor.moveToFirst()) {
            date = biometricCursor.getString(biometricCursor.getColumnIndex(SellaAssistContract.BiometricEntry._ID));
        } else {
            ContentValues biometricValues = new ContentValues();
            biometricValues.put(SellaAssistContract.BiometricEntry._ID, biometric.getDate());
            biometricValues.put(SellaAssistContract.BiometricEntry.COLUMN_DATE, biometric.getDate());

            date = biometric.getDate();
            getContentResolver().insert(SellaAssistContract.BiometricEntry.CONTENT_URI, biometricValues);
        }

        ContentValues biometricInfoValues = new ContentValues();
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_DATE, date);
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP, biometricInfo.getTimestamp());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_LOCATION, biometricInfo.getLocation());
        biometricInfoValues.put(SellaAssistContract.BiometricInfoEntry.COLUMN_SENT, String.valueOf(biometricInfo.isSent()));

        getContentResolver().insert(SellaAssistContract.BiometricInfoEntry.CONTENT_URI, biometricInfoValues);

//        SellaAssistSyncAdapter.syncImmediately(this);
    }
}
