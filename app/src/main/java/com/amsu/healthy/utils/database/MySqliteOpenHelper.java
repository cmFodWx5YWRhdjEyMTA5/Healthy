package com.amsu.healthy.utils.database;

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

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/recordPath";
    static final String DATABASE_NAME = DATABASE_PATH + "/" + "mydb_1.db";

    public MySqliteOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OFFLINE_RECORD_CREATE);
        Log.i("onCreate", "onCreate is calles");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("onUpgrade","onUpgrade is calles");
    }

    //历史记录本地缓存
    private static final String OFFLINE_RECORD_CREATE = "create table if not exists uploadreport("
            + "id STRING primary key,"
            + "serveId STRING,"
            + "FI STRING,"
            + "ES STRING,"
            + "PI STRING,"
            + "CC STRING,"
            + "HRVr STRING,"
            + "HRVs STRING,"
            + "AHR STRING,"
            + "MaxHR STRING,"
            + "MinHR STRING,"
            + "HRr STRING,"
            + "HRs STRING,"
            + "EC STRING,"
            + "ECr STRING,"
            + "ECs STRING,"
            + "RA STRING,"
            + "HR STRING,"
            + "AE STRING,"
            + "distance STRING,"
            + "time STRING,"
            + "cadence STRING,"
            + "calorie STRING,"
            + "state STRING,"
            + "zaobo STRING,"
            + "loubo STRING,"
            + "latitude_longitude STRING,"
            + "timestamp STRING,"
            + "datatime STRING,"
            + "uploadState STRING" + ");";

    //app异常中断
    private static final String APPABORT_RECORD_CREATE = "create table if not exists appabort("
            + "id STRING primary key,"
            + "startTimeMillis STRING,"
            + "ecgFileName STRING,"
            + "accFileName STRING,"
            + "mapTrackID STRING,"
            + "state STRING,"
            + "speedStringList STRING" + ");";


}

