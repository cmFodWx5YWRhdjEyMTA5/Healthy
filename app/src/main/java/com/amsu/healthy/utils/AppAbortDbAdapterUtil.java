package com.amsu.healthy.utils;

import android.util.Log;

import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.AppAbortDataSaveInsole;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by HP on 2017/6/8.
 */

public class AppAbortDbAdapterUtil {
    private static final String TAG = "AppAbortDbAdapterUtil";


    /*private static final String RECORD_TABLE = "appabort";
    private SQLiteDatabase db;
    private final MySqliteOpenHelper mySqliteOpenHelper;

    // constructor
    public AppAbortDbAdapterUtil(Context ctx) {
        Log.i(TAG,"AppAbortDbAdapterUtil");
        mySqliteOpenHelper = new MySqliteOpenHelper(ctx,1);
    }

    public AppAbortDbAdapterUtil open() throws SQLException {
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

    public static void putAbortDataToSP(AppAbortDataSave abortData){
        try {
            Gson gson = new Gson();
            String  listString = gson.toJson(abortData.clone());
            Log.i(TAG,"listString:"+listString);
            MyUtil.putStringValueFromSP("abortDatas",Constant.sportType_Cloth+"&&"+listString);
            MyUtil.putIntValueFromSP(Constant.sportRunningType,Constant.sportType_Cloth);
        }catch (Exception e){
            Log.e(TAG,"e:"+e);
        }
    }
    public static void deleteAbortDataRecordFomeSP(){
        MyUtil.putStringValueFromSP("abortDatas","");
        MyUtil.putIntValueFromSP(Constant.sportRunningType,-1);
    }

    public static void putAbortDataToSP(AppAbortDataSaveInsole abortData){
        try {
            Gson gson = new Gson();
            String  listString = gson.toJson(abortData.clone());
            Log.i(TAG,"listString:"+listString);
            MyUtil.putStringValueFromSP("abortDatas",Constant.sportType_Insole+"&&"+listString);
            MyUtil.putIntValueFromSP(Constant.sportRunningType,Constant.sportType_Insole);
        }catch (Exception e){ Log.e(TAG,"e:"+e);}
    }

    public static <T> T getAbortDataFromSP(int sportType){
        String stringValueFromSP = MyUtil.getStringValueFromSP("abortDatas");
        Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);
        if (!MyUtil.isEmpty(stringValueFromSP) ){
            String[] split = stringValueFromSP.split("&&");
            if (split.length==2 && !MyUtil.isEmpty(split[0])){
                Gson gson = new Gson();
                Type type = null;
                if (sportType==Constant.sportType_Cloth){
                    type = new TypeToken<AppAbortDataSave>() {}.getType();
                }
                else if (sportType==Constant.sportType_Insole){
                    type = new TypeToken<AppAbortDataSaveInsole>() {}.getType();
                }

                if (type!=null){
                    try {
                        T abortDatasTemp =  gson.fromJson(split[1], type);
                        if (abortDatasTemp!=null){
                            return abortDatasTemp;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static void saveOrUpdateAbortDataRecordToDataBase(){

    }

    public static void deleteAbortDataRecordToDataBase(){

    }

}
