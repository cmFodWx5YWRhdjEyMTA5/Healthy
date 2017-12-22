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
    static final String DATABASE_NAME = DATABASE_PATH + "/" + "db20170918_2.db";
    static final String tableName = "uploadreport";
    private static final String TAG = "MySqliteOpenHelper";

    //历史记录本地缓存
    private final String OFFLINE_RECORD_CREATE = getCreateTableSql(tableName);

    public MySqliteOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OFFLINE_RECORD_CREATE);
        Log.i(TAG, "onCreate is calles");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"onUpgrade is calles");

        if(newVersion>oldVersion) {
            //db.execSQL("ALTER TABLE uploadreport ADD lf1 TEXT;");
        }
    }

    private String getCreateTableSql(String tableName){
        String OFFLINE_RECORD_CREATE = "create table if not exists "+tableName+"("
                + "id STRING primary key,"
                + "localEcgFileName STRING,"
                + "fi STRING,"
                + "es STRING,"
                + "pi STRING,"
                + "cc STRING,"
                + "hrvr STRING,"
                + "hrvs STRING,"
                + "ahr STRING,"
                + "maxhr STRING,"
                + "minhr STRING,"
                + "hrr STRING,"
                + "hrs STRING,"
                + "ec STRING,"
                + "ecr STRING,"
                + "ecs STRING,"
                + "ra STRING,"
                + "hr STRING,"
                + "ae STRING,"
                + "distance STRING,"
                + "time STRING,"
                + "cadence STRING,"
                + "calorie STRING,"
                + "state STRING,"
                + "zaobo STRING,"
                + "loubo STRING,"
                + "latitude_longitude TEXT,"
                + "timestamp STRING,"
                + "datatime STRING,"
                + "inuse STRING,"
                + "lf1 STRING,"
                + "lf2 STRING,"
                + "hf1 STRING,"
                + "hf2 STRING,"
                + "hf STRING,"
                + "lf STRING,"
                + "chaosPlotPoint STRING,"
                + "frequencyDomainDiagramPoint STRING,"
                + "chaosPlotMajorAxis STRING,"
                + "chaosPlotMinorAxis STRING,"
                + "sdnn1 STRING,"
                + "sdnn2 STRING,"
                + "uploadState STRING" + ");";

        return OFFLINE_RECORD_CREATE;
    }


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

