package it.sella.assist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import it.sella.assist.R;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 24-Jul-16.
 */
public class BiometricAdapter extends CursorTreeAdapter {
    private static final String TAG = BiometricAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private Context context;

    public BiometricAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        Cursor itemCursor = getGroup(groupCursor.getPosition());

        CursorLoader cursorLoader = new CursorLoader(context, SellaAssistContract.BiometricInfoEntry.CONTENT_URI,
                SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_PROJECTION,
                SellaAssistContract.BiometricInfoEntry.TABLE_NAME + "." + SellaAssistContract.BiometricInfoEntry.COLUMN_DATE + " = ? ",
                new String[]{itemCursor.getString(itemCursor.getColumnIndex(SellaAssistContract.BiometricEntry._ID))},
                SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP + " DESC");

        Cursor childCursor = null;

        try {
            childCursor = cursorLoader.loadInBackground();
            childCursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return childCursor;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.list_biometric_group, parent, false);
        return view;
    }


    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView dateHeader = (TextView) view.findViewById(R.id.list_biometric_date_textview);
        dateHeader.setText(cursor.getString(SellaAssistContract.BiometricEntry.BIOMETRIC_DATE));

        TextView grossHrsHeader = (TextView) view.findViewById(R.id.list_biometric_gross_hrs_textview);
        long fromTimestamp = 0;
        long toTimestamp = 0;

        Cursor biometricInfoCursor = context.getContentResolver().query(
                SellaAssistContract.BiometricInfoEntry.CONTENT_URI,
                SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_PROJECTION,
                SellaAssistContract.BiometricInfoEntry.COLUMN_DATE + " = ?",
                new String[]{cursor.getString(SellaAssistContract.BiometricEntry.BIOMETRIC_ID)},
                SellaAssistContract.BiometricInfoEntry.COLUMN_TIMESTAMP + " DESC");

        if (biometricInfoCursor.moveToFirst())
            toTimestamp = biometricInfoCursor.getLong(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_TIMESTAMP);

        if (biometricInfoCursor.moveToLast())
            fromTimestamp = biometricInfoCursor.getLong(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_TIMESTAMP);

        grossHrsHeader.setText(Utility.calculateGrossHours(fromTimestamp, toTimestamp));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.list_biometric_item, parent, false);
        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        TextView locationTextView = (TextView) view.findViewById(R.id.list_biometric_location_intextview);
        locationTextView.setText(cursor.getString(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_LOCATION));

        TextView timeTextview = (TextView) view.findViewById(R.id.list_biometric_time_textview);
        timeTextview.setText(Utility.getFormattedTime(cursor.getLong(SellaAssistContract.BiometricInfoEntry.BIOMETRIC_INFO_TIMESTAMP)));
    }
}
