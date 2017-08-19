package com.amsu.healthy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.database.MySqliteOpenHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by HP on 2017/6/8.
 */

public class AppAbortDbAdapter {
    private static final String TAG = "AppAbortDbAdapter";


    /*private static final String RECORD_TABLE = "appabort";
    private SQLiteDatabase db;
    private final MySqliteOpenHelper mySqliteOpenHelper;

    // constructor
    public AppAbortDbAdapter(Context ctx) {
        Log.i(TAG,"AppAbortDbAdapter");
        mySqliteOpenHelper = new MySqliteOpenHelper(ctx,1);
    }

    public AppAbortDbAdapter open() throws SQLException {
        Log.i(TAG,"open()");
        db = mySqliteOpenHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mySqliteOpenHelper.close();
    }

    //数据库存入一条记录
    public long createOrUpdateUploadReportObject(AppAbortDataSave appAbortDataSave) {
        ContentValues args = new ContentValues();
        if (!MyUtil.isEmpty(appAbortDataSave.id)){
            args.put("id", appAbortDataSave.id);
        }else {
            args.put("id", System.currentTimeMillis()+"");
        }

        args.put("startTimeMillis", appAbortDataSave.startTimeMillis);
        args.put("ecgFileName", appAbortDataSave.ecgFileName);
        args.put("accFileName", appAbortDataSave.accFileName);
        args.put("mapTrackID", appAbortDataSave.mapTrackID);
        args.put("state", appAbortDataSave.state);

        Gson gson = new Gson();
        String  speedStringList = gson.toJson(appAbortDataSave.speedStringList);
        args.put("speedStringList", speedStringList);

        //return db.insert(RECORD_TABLE, null, args);
        return db.replace(RECORD_TABLE, null, args);
    }

    // 查询所有记录
    public List<AppAbortDataSave> queryRecordAll() {
        List<AppAbortDataSave> allAppAbortDataSave = new ArrayList<>();
        Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), null, null, null, null, null);
        while (allRecordCursor.moveToNext()) {
            AppAbortDataSave appAbortDataSave = getAppAbortDataSaveByCursor(allRecordCursor);
            allAppAbortDataSave.add(appAbortDataSave);
        }
        Collections.reverse(allAppAbortDataSave);
        return allAppAbortDataSave;
    }

    private String[] getColumns() {
        return new String[] { "id", "startTimeMillis","ecgFileName", "accFileName","mapTrackID","state","speedStringList"};
    }

    public AppAbortDataSave getAppAbortDataSaveByCursor(Cursor cursor){
        AppAbortDataSave appAbortDataSave = new AppAbortDataSave();
        appAbortDataSave.id = cursor.getString(cursor.getColumnIndex("id"));
        appAbortDataSave.startTimeMillis = cursor.getLong(cursor.getColumnIndex("startTimeMillis"));
        appAbortDataSave.ecgFileName = cursor.getString(cursor.getColumnIndex("ecgFileName"));
        appAbortDataSave.accFileName = cursor.getString(cursor.getColumnIndex("accFileName"));
        appAbortDataSave.mapTrackID = cursor.getLong(cursor.getColumnIndex("mapTrackID"));
        appAbortDataSave.state = cursor.getInt(cursor.getColumnIndex("state"));

        Gson gson = new Gson();
        ArrayList<Integer> integerList = new ArrayList<>();
        String speedString = cursor.getString(cursor.getColumnIndex("speedStringList"));
        if (!MyUtil.isEmpty(speedString)){
            ArrayList<Integer> abortDatas = gson.fromJson(speedString, new TypeToken<List<Integer>>() {
            }.getType());
            if (abortDatas!=null){
                integerList = abortDatas;
            }
        }
        appAbortDataSave.speedStringList = integerList;
        return appAbortDataSave;
    }*/

    static boolean isPut;

    public static synchronized List<AppAbortDataSave> getAbortDataListFromSP(){
        if (isPut) return new CopyOnWriteArrayList<>();
        String stringValueFromSP = MyUtil.getStringValueFromSP("abortDatas");
        Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);

        List<AppAbortDataSave> abortDatas = new CopyOnWriteArrayList<>();
        Gson gson = new Gson();
        if (!MyUtil.isEmpty(stringValueFromSP)){
            List<AppAbortDataSave> abortDatasTemp =  gson.fromJson(stringValueFromSP, new TypeToken<List<AppAbortDataSave>>() {
            }.getType());
            if (abortDatasTemp!=null){
                abortDatas.addAll(abortDatasTemp);
                return abortDatas;
            }
            else {
                return new CopyOnWriteArrayList<>();
            }
        }
        return new CopyOnWriteArrayList<>();
    }

    public static synchronized void putAbortDataListToSP(List<AppAbortDataSave> abortDatas){
        isPut = true;
        List<AppAbortDataSave> abortDatasCopy = new CopyOnWriteArrayList<>();
        abortDatasCopy.addAll(abortDatas);

        Gson gson = new Gson();
        String  listString = gson.toJson(abortDatasCopy);
        Log.i(TAG,"listString:"+listString);
        MyUtil.putStringValueFromSP("abortDatas",listString);
        isPut = false;
    }

    public static synchronized void putAbortDataToSP(AppAbortDataSave abortData){
        Gson gson = new Gson();
        String  listString = gson.toJson(abortData);
        Log.i(TAG,"listString:"+listString);
        MyUtil.putStringValueFromSP("abortDatas",listString);
    }

    public static synchronized AppAbortDataSave getAbortDataFromSP(){
        String stringValueFromSP = MyUtil.getStringValueFromSP("abortDatas");
        Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);
        Gson gson = new Gson();
        if (!MyUtil.isEmpty(stringValueFromSP)){
            AppAbortDataSave abortDatasTemp =  gson.fromJson(stringValueFromSP, new TypeToken<AppAbortDataSave>() {
            }.getType());
            if (abortDatasTemp!=null){
                return abortDatasTemp;
            }
        }
        return null;
    }

    public static void saveOrUpdateAbortDataRecordToDataBase(){

    }

    public static void deleteAbortDataRecordToDataBase(){

    }

}
