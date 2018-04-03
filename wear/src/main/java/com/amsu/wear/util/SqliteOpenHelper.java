package com.amsu.wear.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils.database
 * @time 7/5/2017 9:20 AM
 * @describe
 */

public class SqliteOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/amsu/db";
    static final String DATABASE_NAME = DATABASE_PATH + "/" + "data.db";
    private static final String TAG = "MySqliteOpenHelper";



    public SqliteOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate is calles");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"onUpgrade is calles");

        if(newVersion>oldVersion) {
            //db.execSQL("ALTER TABLE uploadreport ADD lf1 TEXT;");
        }
    }



}

