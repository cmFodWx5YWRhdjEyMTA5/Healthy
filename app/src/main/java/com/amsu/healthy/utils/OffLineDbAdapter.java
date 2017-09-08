package com.amsu.healthy.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amsu.healthy.bean.HealthyPlan;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.database.MySqliteOpenHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库相关操作，用于存取轨迹记录
 * 
 */
public class OffLineDbAdapter {
	private static final String RECORD_TABLE = "uploadreport";
	private static final String TAG = "AppAbortDbAdapterUtil";

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
		Log.i(TAG,"AppAbortDbAdapterUtil");
		mySqliteOpenHelper = new MySqliteOpenHelper(ctx,2);
	}

	public OffLineDbAdapter open() throws SQLException {
		Log.i(TAG,"open()");
		try {
			db = mySqliteOpenHelper.getWritableDatabase();
		}catch (SQLException e){
			Log.i(TAG,"e:"+e);
		}
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
								   String cadence, String calorie, String state, String zaobo, String loubo, String latitude_longitude, String uploadState,String localEcgFileName) {
		ContentValues args = new ContentValues();
        args.put("id", System.currentTimeMillis()+"");
		args.put("localEcgFileName", localEcgFileName);
		args.put("fi", FI);
		args.put("es", ES);
		args.put("pi", PI);
		args.put("cc", CC);
		args.put("hrvr", HRVr);
		args.put("hrvs", HRVs);
		args.put("ahr", AHR);
		args.put("maxhr", maxHR);
		args.put("minhr", minHR);
		args.put("hrr", HRr);
		args.put("hrs", HRs);
		args.put("ec", EC);
		args.put("ecr", ECr);
		args.put("ecs", ECs);
		args.put("ra", RA);
		args.put("hr", HR);
		args.put("ae", AE);
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
	public long createOrUpdateUploadReportObject(UploadRecord uploadRecordCopy) {
        try {
            UploadRecord uploadRecord = (UploadRecord) uploadRecordCopy.clone();
			uploadRecord.ec = "";
			Log.i(TAG,"uploadRecord:"+uploadRecord);

            ContentValues args = new ContentValues();
            if (uploadRecord.id>0){
                args.put("id", uploadRecord.id+"");
            }else {
                args.put("id", System.currentTimeMillis()+"");
            }
            args.put("localEcgFileName", uploadRecord.localEcgFileName);
            args.put("fi", uploadRecord.fi+"");
            args.put("es", uploadRecord.es+"");
            args.put("pi", uploadRecord.pi+"");
            args.put("cc", uploadRecord.cc);
            args.put("hrvr", uploadRecord.hrvr+"");
            args.put("hrvs", uploadRecord.hrvs+"");
            args.put("ahr", uploadRecord.ahr+"");
            args.put("maxhr", uploadRecord.maxhr+"");
            args.put("minhr", uploadRecord.minhr+"");
            args.put("hrr", uploadRecord.hrr+"");
            args.put("hrs", uploadRecord.hrs+"");
            args.put("ec", uploadRecord.ec+"");
            args.put("ecr", uploadRecord.ecr+"");
            args.put("ecs", uploadRecord.ecs+"");
            args.put("ra", uploadRecord.ra+"");
			if (uploadRecord.hr!=null){
				args.put("hr", uploadRecord.hr.toString());
			}
			if (uploadRecord.ae!=null){
				args.put("ae", uploadRecord.ae.toString());
			}
			if (uploadRecord.cadence!=null){
				args.put("cadence", uploadRecord.cadence.toString());
			}
			if (uploadRecord.calorie!=null){
				args.put("calorie", uploadRecord.calorie.toString());
			}
			if (uploadRecord.latitudeLongitude!=null){
				args.put("latitude_longitude", uploadRecord.latitudeLongitude.toString());
			}
            args.put("distance", uploadRecord.distance+"");
            args.put("time", uploadRecord.time+"");
            args.put("state", uploadRecord.state+"");
            args.put("zaobo", uploadRecord.zaobo+"");
            args.put("loubo", uploadRecord.loubo+"");
            args.put("timestamp", uploadRecord.timestamp+"");
            args.put("datatime", uploadRecord.datatime+"");
            args.put("uploadState", uploadRecord.uploadState+"");

            //return db.insert(RECORD_TABLE, null, args);
            return db.replace(RECORD_TABLE, null, args);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return -1;
        }
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
			Log.i(TAG,"uploadRecordByCursor:"+uploadRecordByCursor);
			if (uploadRecordByCursor!=null){
				allRecord.add(uploadRecordByCursor);
			}
		}
		if (allRecord.size()>1){
			Collections.reverse(allRecord);
		}
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

	//按照timestamp查询
	public UploadRecord queryRecordByTimestamp(String timestamp) {
		String where = "timestamp" + "=?";
		String[] selectionArgs = new String[] { timestamp };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
		if (cursor.moveToNext()) {
			return getUploadRecordByCursor(cursor);
		}
		return null;
	}

	public UploadRecord getUploadRecordByCursor(Cursor cursor){
		Log.i(TAG,"cursor: "+cursor.getCount());
		Log.i(TAG,"cursor: "+cursor.getColumnNames().length);
		for (String s:cursor.getColumnNames()){
			Log.i(TAG,"s: "+s);
		}

		UploadRecord uploadRecord = null;
		try {
			uploadRecord = new UploadRecord();

			String id = cursor.getString(cursor.getColumnIndex("id"));
			String fi = cursor.getString(cursor.getColumnIndex("fi"));
			String es = cursor.getString(cursor.getColumnIndex("es"));
			String pi = cursor.getString(cursor.getColumnIndex("pi"));
			String cc = cursor.getString(cursor.getColumnIndex("cc"));
			String hrvr = cursor.getString(cursor.getColumnIndex("hrvr"));
			String hrvs = cursor.getString(cursor.getColumnIndex("hrvs"));
			String ahr = cursor.getString(cursor.getColumnIndex("ahr"));
			String maxhr = cursor.getString(cursor.getColumnIndex("maxhr"));
			String minhr = cursor.getString(cursor.getColumnIndex("minhr"));
			String hrr = cursor.getString(cursor.getColumnIndex("hrr"));
			String hrs = cursor.getString(cursor.getColumnIndex("hrs"));
			String ec = cursor.getString(cursor.getColumnIndex("ec"));
			String ecr = cursor.getString(cursor.getColumnIndex("ecr"));
			String ecs = cursor.getString(cursor.getColumnIndex("ecs"));
			String ra = cursor.getString(cursor.getColumnIndex("ra"));
			String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
			String datatime = cursor.getString(cursor.getColumnIndex("datatime"));
			String hr = cursor.getString(cursor.getColumnIndex("hr"));
			String ae = cursor.getString(cursor.getColumnIndex("ae"));
			String distance = cursor.getString(cursor.getColumnIndex("distance"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String cadence = cursor.getString(cursor.getColumnIndex("cadence"));
			String calorie = cursor.getString(cursor.getColumnIndex("calorie"));
			String state = cursor.getString(cursor.getColumnIndex("state"));
			String zaobo = cursor.getString(cursor.getColumnIndex("zaobo"));
			String loubo = cursor.getString(cursor.getColumnIndex("loubo"));
			String latitudeLongitude = cursor.getString(cursor.getColumnIndex("latitude_longitude"));
			String uploadState = cursor.getString(cursor.getColumnIndex("uploadState"));
			String localEcgFileName = cursor.getString(cursor.getColumnIndex("localEcgFileName"));

			Log.i(TAG,"ae:"+ae);
			Log.i(TAG,"hr:"+hr);
			Log.i(TAG,"cadence:"+cadence);

			Gson gson = new Gson();

			uploadRecord.id = Long.parseLong(id);
			uploadRecord.fi = Integer.parseInt(fi);
			uploadRecord.es = Integer.parseInt(es);
			uploadRecord.pi = Integer.parseInt(pi);
			uploadRecord.cc = Integer.parseInt(cc);
			uploadRecord.hrvr = hrvr;
			uploadRecord.hrvs = hrvs;
			uploadRecord.ahr = Integer.parseInt(ahr);
			uploadRecord.maxhr = Integer.parseInt(maxhr);
			uploadRecord.minhr = Integer.parseInt(minhr);
			uploadRecord.hrr = hrr;
			uploadRecord.hrs = hrs;
			uploadRecord.ec =ec;
			uploadRecord.ecr =Integer.parseInt(ecr);
			uploadRecord.ecs =ecs;
			uploadRecord.ra =Integer.parseInt(ra);
			uploadRecord.timestamp =Long.parseLong(timestamp);
			uploadRecord.datatime =datatime;
			if (!MyUtil.isEmpty(hr)){
				uploadRecord.hr = gson.fromJson(hr,new TypeToken<List<Integer>>() {}.getType());
			}
			if (!MyUtil.isEmpty(ae)){
				uploadRecord.ae =gson.fromJson(ae,new TypeToken<List<Integer>>() {}.getType());
			}
			if (!MyUtil.isEmpty(cadence)){
				uploadRecord.cadence = gson.fromJson(cadence,new TypeToken<List<Integer>>() {}.getType());
			}
			if (!MyUtil.isEmpty(calorie)){
				uploadRecord.calorie =gson.fromJson(calorie,new TypeToken<List<String>>() {}.getType());
			}
			if (!MyUtil.isEmpty(latitudeLongitude)){
				uploadRecord.latitudeLongitude = gson.fromJson(latitudeLongitude,new TypeToken<List<ParcelableDoubleList>>() {}.getType());
			}

			uploadRecord.distance =Double.parseDouble(distance);
			uploadRecord.time = Long.parseLong(time);
			uploadRecord.state =Integer.parseInt(state);
			uploadRecord.zaobo =Integer.parseInt(zaobo);
			uploadRecord.loubo =Integer.parseInt(loubo);
			uploadRecord.uploadState =Integer.parseInt(uploadState);
			uploadRecord.localEcgFileName = localEcgFileName;

			File file = new File(uploadRecord.localEcgFileName);
			//如果本地缓冲文件已经被删除了，需要从网络上获取
			if (uploadRecord.hr.size()==0 || file.exists()){  //运动数据   心电文件不存在
				return uploadRecord;
			}
			else {
				return null;
			}
		}catch (IllegalStateException e){
			e.printStackTrace();
			Log.e(TAG,"抛出异常: "+e);
		}
		Log.i(TAG,"uploadRecord:"+uploadRecord);
		return null;
	}

	private String[] getColumns() {
		/*+ "id integer primary key autoincrement,"
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
			+ "latitude_longitude STRING,"
			+ "timestamp STRING,"
			+ "datatime STRING,"
			+ "uploadState STRING" + ");";*/
		return new String[] { "id", "localEcgFileName","fi", "es","pi","cc","hrvr","hrvs","ahr","maxhr","minhr","hrr","hrs","ec","ecr","ecs","ra",
				"hr","ae","distance","time","cadence","calorie","state","zaobo","loubo","latitude_longitude","timestamp","datatime","uploadState"};
	}
}
