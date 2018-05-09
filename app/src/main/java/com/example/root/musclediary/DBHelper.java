package com.example.root.musclediary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 4/10/18.
 */

public class DBHelper extends SQLiteOpenHelper {
    /*
    * This helper is not used, but it is intended to
    * use for the saving of data to access through History.
     */

    private static DBHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "musclediary.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TRAINING = "training";

    // training Table Columns
    private static final String KEY_TRA_ID = "idtraining";
    private static final String KEY_TRA_DATE = "datetr";
    private static final String KEY_TRA_AT = "activetraining";
    private static final String KEY_TRA_PV = "peekvalue";
    private static final String KEY_TRA_AV = "averagevalue";

    public static synchronized DBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TRA_TABLE = "CREATE TABLE " + TABLE_TRAINING +
                    "(" +
                    KEY_TRA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                    KEY_TRA_DATE + " datetime default current_timestamp," + // Define a primary key
                    KEY_TRA_AT + " REAL ," + // Define a foreign key
                    KEY_TRA_PV + " REAL ," + // Define a foreign key
                    KEY_TRA_AV + " REAL" +
                    ")";
            db.execSQL(CREATE_TRA_TABLE);
            System.out.println("ManualDeb: created table");
        } catch (Exception e) {
            System.out.println("ManualDeb: Error while creating table "+e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS TABLE_TRAINING");
        onCreate(db);
    }

    public void insertContent (String at, double pv, double av) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TRA_AT, at);
            values.put(KEY_TRA_PV, pv);
            values.put(KEY_TRA_AV, av);

            db.insertOrThrow(TABLE_TRAINING, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("ManualDeb: Error while trying to add post to database "+e.getMessage());
        } finally {
            db.endTransaction();
        }

    }

    public void getAllPosts() {
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_TRAINING);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    System.out.println("ManualDeb: Data "+cursor.getString(cursor.getColumnIndex(KEY_TRA_ID))+
                            cursor.getString(cursor.getColumnIndex(KEY_TRA_DATE))+" "
                            +cursor.getString(cursor.getColumnIndex(KEY_TRA_AT))+" "+
                            cursor.getString(cursor.getColumnIndex(KEY_TRA_PV))+" "+
                            cursor.getString(cursor.getColumnIndex(KEY_TRA_AV)));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            System.out.println("ManualDeb: Error while quering "+e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

}
