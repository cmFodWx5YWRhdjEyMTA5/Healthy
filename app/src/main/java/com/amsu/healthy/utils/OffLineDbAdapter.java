package com.amsu.healthy.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.database.MySqliteOpenHelper;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库相关操作，用于存取轨迹记录
 * 
 */
public class OffLineDbAdapter {
	private static final String RECORD_TABLE = "uploadreport";
	private static final String TAG = "AppAbortDbAdapter";

	/*public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG,"onCreate");
			db.execSQL(RECORD_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG,"onUpgrade");
		}
	}*/

	private SQLiteDatabase db;
	private final MySqliteOpenHelper mySqliteOpenHelper;

	// constructor
	public OffLineDbAdapter(Context ctx) {
		Log.i(TAG,"AppAbortDbAdapter");
		mySqliteOpenHelper = new MySqliteOpenHelper(ctx,1);
	}

	public OffLineDbAdapter open() throws SQLException {
		Log.i(TAG,"open()");
		db = mySqliteOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mySqliteOpenHelper.close();
	}

	public Cursor getall() {
		return db.rawQuery("SELECT * FROM record", null);
	}

	// remove an entry
	public boolean delete(long rowId) {

		return db.delete(RECORD_TABLE, "id=" + rowId, null) > 0;
	}

	public void addColumnToTable(String columnName,String columnType){
        db.execSQL("alter table "+RECORD_TABLE+" add "+columnName+" "+columnType);
    }

	//数据库存入一条记录
	public long createUploadReport(String FI, String ES, String PI, String CC, String HRVr, String HRVs, String AHR, String maxHR, String minHR, String HRr, String HRs,
								   String EC, String ECr, String ECs, String RA, String timestamp, String datatime, String HR, String AE, String distance, String time,
								   String cadence, String calorie, String state, String zaobo, String loubo, String latitude_longitude, String uploadState,String serveId) {
		ContentValues args = new ContentValues();
        args.put("id", System.currentTimeMillis()+"");
		args.put("serveId", serveId);
		args.put("FI", FI);
		args.put("ES", ES);
		args.put("PI", PI);
		args.put("CC", CC);
		args.put("HRVr", HRVr);
		args.put("HRVs", HRVs);
		args.put("AHR", AHR);
		args.put("MaxHR", maxHR);
		args.put("MinHR", minHR);
		args.put("HRr", HRr);
		args.put("HRs", HRs);
		args.put("EC", EC);
		args.put("ECr", ECr);
		args.put("ECs", ECs);
		args.put("RA", RA);
		args.put("HR", HR);
		args.put("AE", AE);
		args.put("distance", distance);
		args.put("time", time);
		args.put("cadence", cadence);
		args.put("calorie", calorie);
		args.put("state", state);
		args.put("zaobo", zaobo);
		args.put("loubo", loubo);
		args.put("latitude_longitude", latitude_longitude);
		args.put("timestamp", timestamp);
		args.put("datatime", datatime);
		args.put("uploadState", uploadState);
		return db.insert(RECORD_TABLE, null, args);
	}

	//数据库存入一条记录
	public long createOrUpdateUploadReportObject(UploadRecord uploadRecord) {
		ContentValues args = new ContentValues();
        if (!MyUtil.isEmpty(uploadRecord.id)){
            args.put("id", uploadRecord.id);
        }else {
            args.put("id", System.currentTimeMillis()+"");
        }
		args.put("serveId", uploadRecord.serveId);
		args.put("FI", uploadRecord.FI);
		args.put("ES", uploadRecord.ES);
		args.put("PI", uploadRecord.PI);
		args.put("CC", uploadRecord.CC);
		args.put("HRVr", uploadRecord.HRVr);
		args.put("HRVs", uploadRecord.HRVs);
		args.put("AHR", uploadRecord.AHR);
		args.put("MaxHR", uploadRecord.MaxHR);
		args.put("MinHR", uploadRecord.MinHR);
		args.put("HRr", uploadRecord.HRr);
		args.put("HRs", uploadRecord.HRs);
		args.put("EC", uploadRecord.EC);
		args.put("ECr", uploadRecord.ECr);
		args.put("ECs", uploadRecord.ECs);
		args.put("RA", uploadRecord.RA);
		args.put("HR", uploadRecord.HR);
		args.put("AE", uploadRecord.AE);
		args.put("distance", uploadRecord.distance);
		args.put("time", uploadRecord.time);
		args.put("cadence", uploadRecord.cadence);
		args.put("calorie", uploadRecord.calorie);
		args.put("state", uploadRecord.state);
		args.put("zaobo", uploadRecord.zaobo);
		args.put("loubo", uploadRecord.loubo);
		args.put("latitude_longitude", uploadRecord.latitude_longitude);
		args.put("timestamp", uploadRecord.timestamp);
		args.put("datatime", uploadRecord.datatime);
		args.put("uploadState", uploadRecord.uploadState);

		//return db.insert(RECORD_TABLE, null, args);
		return db.replace(RECORD_TABLE, null, args);
	}

	public boolean updateLocalRecordUploadState(String serveId,String localId){
		ContentValues values = new ContentValues();
		values.put("uploadState", "0");
		values.put("serveId", serveId);
		String where = "id" + "=?";
		int update = db.update(RECORD_TABLE, values, where, new String[]{localId});
		if (update>0)return true;
		return false;
	}

	// 查询所有记录
	public List<UploadRecord> queryRecordAll() {
		List<UploadRecord> allRecord = new ArrayList<>();
		Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), null, null, null, null, null);
		while (allRecordCursor.moveToNext()) {
			UploadRecord uploadRecordByCursor = getUploadRecordByCursor(allRecordCursor);
			allRecord.add(uploadRecordByCursor);
		}
		Collections.reverse(allRecord);
		return allRecord;
	}

	//按照id查询
	public UploadRecord queryRecordById(String id) {
		String where = "id" + "=?";
		String[] selectionArgs = new String[] { id };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
		if (cursor.moveToNext()) {
			return getUploadRecordByCursor(cursor);
		}
		return null;
	}

	//按照uploadState查询 uploadState:  0未上传  1已上传
	public List<UploadRecord> queryRecordByUploadState(String uploadState) {
		List<UploadRecord> allRecord = new ArrayList<>();
		String where = "uploadState" + "=?";
		String[] selectionArgs = new String[] { uploadState };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);

		while (cursor.moveToNext()) {
			UploadRecord uploadRecordByCursor = getUploadRecordByCursor(cursor);
			allRecord.add(uploadRecordByCursor);
		}
		Collections.reverse(allRecord);
		return allRecord;
	}

	//按照timestamp查询
	public UploadRecord queryRecordByTimestamp(long timestamp) {
		String where = "timestamp" + "=?";
		String[] selectionArgs = new String[] { String.valueOf(timestamp) };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
		if (cursor.moveToNext()) {
			return getUploadRecordByCursor(cursor);
		}
		return null;
	}

    //按照datatime查询
    public UploadRecord queryRecordByDatatime(String datatime) {
        String where = "datatime" + "=?";
        String[] selectionArgs = new String[] { datatime };
        Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            return getUploadRecordByCursor(cursor);
        }
        return null;
    }

	public UploadRecord getUploadRecordByCursor(Cursor cursor){
		UploadRecord uploadRecord = new UploadRecord();

		uploadRecord.setId(cursor.getString(cursor.getColumnIndex("id")));
		uploadRecord.setServeId(cursor.getString(cursor.getColumnIndex("serveId")));
		uploadRecord.setFI(cursor.getString(cursor.getColumnIndex("FI")));
		uploadRecord.setES(cursor.getString(cursor.getColumnIndex("ES")));
		uploadRecord.setPI(cursor.getString(cursor.getColumnIndex("PI")));
		uploadRecord.setCC(cursor.getString(cursor.getColumnIndex("CC")));
		uploadRecord.setHRVr(cursor.getString(cursor.getColumnIndex("HRVr")));
		uploadRecord.setHRVs(cursor.getString(cursor.getColumnIndex("HRVs")));
		uploadRecord.setAHR(cursor.getString(cursor.getColumnIndex("AHR")));
		uploadRecord.setMaxHR(cursor.getString(cursor.getColumnIndex("MaxHR")));
		uploadRecord.setMinHR(cursor.getString(cursor.getColumnIndex("MinHR")));
		uploadRecord.setHRr(cursor.getString(cursor.getColumnIndex("HRr")));
		uploadRecord.setHRs(cursor.getString(cursor.getColumnIndex("HRs")));
		uploadRecord.setEC(cursor.getString(cursor.getColumnIndex("EC")));
		uploadRecord.setECr(cursor.getString(cursor.getColumnIndex("ECr")));
		uploadRecord.setECs(cursor.getString(cursor.getColumnIndex("ECs")));
		uploadRecord.setRA(cursor.getString(cursor.getColumnIndex("RA")));
		uploadRecord.setHR(cursor.getString(cursor.getColumnIndex("HR")));
		uploadRecord.setAE(cursor.getString(cursor.getColumnIndex("AE")));
		uploadRecord.setDistance(cursor.getString(cursor.getColumnIndex("distance")));
		uploadRecord.setTime(cursor.getString(cursor.getColumnIndex("time")));
		uploadRecord.setCadence(cursor.getString(cursor.getColumnIndex("cadence")));
		uploadRecord.setCalorie(cursor.getString(cursor.getColumnIndex("calorie")));
		uploadRecord.setState(cursor.getString(cursor.getColumnIndex("state")));
		uploadRecord.setZaobo(cursor.getString(cursor.getColumnIndex("zaobo")));
		uploadRecord.setLoubo(cursor.getString(cursor.getColumnIndex("loubo")));
		uploadRecord.setLatitude_longitude(cursor.getString(cursor.getColumnIndex("latitude_longitude")));
		uploadRecord.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
		uploadRecord.setDatatime(cursor.getString(cursor.getColumnIndex("datatime")));
		uploadRecord.setUploadState(cursor.getString(cursor.getColumnIndex("uploadState")));
		return uploadRecord;
	}

	private String[] getColumns() {
		/*+ "id integer primary key autoincrement,"
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
			+ "uploadState STRING" + ");";*/
		return new String[] { "id", "serveId","FI", "ES","PI","CC","HRVr","HRVs","AHR","MaxHR","MinHR","HRr","HRs","EC","ECr","ECs","RA",
				"HR","AE","distance","time","cadence","calorie","state","zaobo","loubo","latitude_longitude","timestamp","datatime","uploadState"};
	}
}
