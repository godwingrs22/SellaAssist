package it.sella.sellaassist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.sella.sellaassist.data.SellaAssistContract.BiometricEntry;
import it.sella.sellaassist.data.SellaAssistContract.BiometricInfoEntry;
import it.sella.sellaassist.data.SellaAssistContract.EventEntry;
import it.sella.sellaassist.data.SellaAssistContract.FeedEntry;
import it.sella.sellaassist.data.SellaAssistContract.UserEntry;

public class SellaAssistDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 31;
    static final String DATABASE_NAME = "sellaassist.db";

    public SellaAssistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserEntry.COLUMN_GBS_ID + " TEXT NOT NULL,  " +
                UserEntry.COLUMN_NAME + " TEXT NOT NULL,  " +
                UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                UserEntry.COLUMN_PROFILE_PIC + " TEXT NOT NULL, " +
                UserEntry.COLUMN_DEVICEID + " TEXT NOT NULL, " +
                UserEntry.COLUMN_LOGGED_IN + " TEXT NOT NULL, " +
                UserEntry.COLUMN_BUSINESS_UNIT + " TEXT NOT NULL, " +
                " UNIQUE (" + UserEntry.COLUMN_GBS_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FEED_TABLE = "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FeedEntry.COLUMN_FEED_ID + " INTEGER NOT NULL,  " +
                FeedEntry.COLUMN_CREATED_BY_NAME + " TEXT NOT NULL,  " +
                FeedEntry.COLUMN_PROFILE_PIC + " TEXT NOT NULL, " +
                FeedEntry.COLUMN_START_TIMESTAMP + " TEXT NOT NULL, " +
                FeedEntry.COLUMN_IT_AREA + " TEXT, " +
                FeedEntry.COLUMN_IS_IMPORTANT + " TEXT, " +
                FeedEntry.COLUMN_MESSAGE + " TEXT, " +
                FeedEntry.COLUMN_IMAGE + " TEXT, " +
                FeedEntry.COLUMN_URL + " TEXT, " +
                " UNIQUE (" + FeedEntry.COLUMN_FEED_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_BIOMETRIC_TABLE = "CREATE TABLE " + BiometricEntry.TABLE_NAME + " (" +
                BiometricEntry._ID + " TEXT PRIMARY KEY, " +
                BiometricEntry.COLUMN_DATE + " TEXT NOT NULL  " +
                ");";

        final String SQL_CREATE_BIOMETRIC_INFO_TABLE = "CREATE TABLE " + BiometricInfoEntry.TABLE_NAME + " (" +
                BiometricInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BiometricInfoEntry.COLUMN_DATE + " TEXT NOT NULL,  " +
                BiometricInfoEntry.COLUMN_TIMESTAMP + " INTEGER UNIQUE NOT NULL,  " +
                BiometricInfoEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                BiometricInfoEntry.COLUMN_SENT + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + BiometricInfoEntry.COLUMN_DATE + ") REFERENCES " +
                BiometricEntry.TABLE_NAME + " (" + BiometricEntry._ID + "))";

        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventEntry.COLUMN_ID + " INTEGER NOT NULL,  " +
                EventEntry.COLUMN_TITLE + " TEXT NOT NULL,  " +
                EventEntry.COLUMN_START_TIMESTAMP + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_END_TIMESTAMP + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_BANNER_IMAGE + " TEXT NULL, " +
                EventEntry.COLUMN_CREATED_BY_NAME + " TEXT NOT NULL, " +
                EventEntry.COLUMN_CREATED_BY_PROFILE_IMAGE + " TEXT NOT NULL, " +
                EventEntry.COLUMN_CREATED_BY_TIMESTAMP + " INTEGER NULL, " +
                EventEntry.COLUMN_ADDRESS + " TEXT NULL, " +
                EventEntry.COLUMN_INTERESTED + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_REMAINDER + " TEXT NOT NULL, " +
                EventEntry.COLUMN_DESCRIPTION + " TEXT NULL, " +
                " UNIQUE (" + EventEntry.COLUMN_ID + ") ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BIOMETRIC_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BIOMETRIC_INFO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BiometricEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BiometricInfoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
