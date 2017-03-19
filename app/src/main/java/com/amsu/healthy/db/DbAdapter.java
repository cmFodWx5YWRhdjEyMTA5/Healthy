package com.amsu.healthy.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.amsu.healthy.bean.RateRecord;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 数据库相关操作，用于存取分析记录
 * 
 */
public class DbAdapter {
	public static final String KEY_ROWID = "id";
	public static final String KEY_TIME = "time";
	public static final String KEY_LFHF = "lfhf";
	public static final String KEY_SDNN = "sdnn";
	public static final String KEY_RATELIST = "ratelist";
	public static final String KEY_RATEFILEPATH = "ratefilepath";
	public static final String KEY_HRR = "hrr";
	public static final String KEY_RATENORMALCOUNT = "ratenormalcount";
	public static final String KEY_RATEBADCOUNT = "ratebadcount";
	public static final String KEY_MISSBEAT = "missbeat";
	public static final String KEY_PREMATUREBEAT = "prematurebeat";
	private final static String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/raterecordPath";
	static final String DATABASE_NAME = DATABASE_PATH + "/" + "record.db";
	private static final int DATABASE_VERSION = 1;
	private static final String RECORD_TABLE = "raterecord";
	private static final String RECORD_CREATE = "create table if not exists raterecord("
			+ KEY_ROWID
			+ " integer primary key autoincrement,"
			+ "time STRING,"
			+ "lfhf integer,"
			+ "sdnn integer,"
			+ "ratelist STRING,"
			+ "ratefilepath STRING,"
			+ "hrr integer,"
			+ "ratenormalcount integer,"
			+ "ratebadcount integer,"
			+ "missbeat integer,"
			+ "prematurebeat integer"+ ");";

	public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(RECORD_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	private Context mCtx = null;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	// constructor
	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
		dbHelper = new DatabaseHelper(mCtx);
	}

	public DbAdapter open() throws SQLException {

		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getall() {
		return db.rawQuery("SELECT * FROM record", null);
	}

	// remove an entry
	public boolean delete(long rowId) {

		return db.delete(RECORD_TABLE, "id=" + rowId, null) > 0;
	}

	/**
	 * 数据库存入一条轨迹
	 */
	public long createRecord(String time, int lfhf, int sdnn, String ratelist, String ratefilepath, int hrr, int ratenormalcount, int ratebadcount,int missbeat, int prematurebeat ) {
		ContentValues args = new ContentValues();
		args.put("time", time);
		args.put("lfhf", lfhf);
		args.put("sdnn", sdnn);
		args.put("ratelist", ratelist);
		args.put("ratefilepath", ratefilepath);
		args.put("hrr", hrr);
		args.put("ratenormalcount", ratenormalcount);
		args.put("ratebadcount", ratebadcount);
		args.put("missbeat", missbeat);
		args.put("prematurebeat", prematurebeat);
		return db.insert(RECORD_TABLE, null, args);
	}

	/**
	 * 查询所有轨迹记录
	 * 
	 * @return
	 */
	public List<RateRecord> queryRecordAll() {
		List<RateRecord> allRecord = new ArrayList<>();
		Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), null, null, null, null, null);
		while (allRecordCursor.moveToNext()) {
			RateRecord record = new RateRecord();
			record.setId(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_ROWID)));
			record.setTime(allRecordCursor.getString(allRecordCursor.getColumnIndex(DbAdapter.KEY_TIME)));
			record.setLfhf(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_LFHF)));
			record.setSdnn(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_SDNN)));
			String points = allRecordCursor.getString(allRecordCursor.getColumnIndex(DbAdapter.KEY_RATELIST));
			record.setRatelist(parsePoints(points));
			record.setRatefilepath(allRecordCursor.getString(allRecordCursor.getColumnIndex(DbAdapter.KEY_RATEFILEPATH)));
			record.setHrr(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_HRR)));
			record.setRatenormalcount(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_RATENORMALCOUNT)));
			record.setRatebadcount(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_RATEBADCOUNT)));
			record.setMissbeat(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_MISSBEAT)));
			record.setPrematurebeat(allRecordCursor.getInt(allRecordCursor.getColumnIndex(DbAdapter.KEY_PREMATUREBEAT)));
			allRecord.add(record);
		}
		Collections.reverse(allRecord);
		return allRecord;
	}

	private List<Integer> parsePoints(String points) {
		List<Integer> pointList = new ArrayList<>();
		if (!MyUtil.isEmpty(points)){
			String[] split = points.split(",");
			for (int i=0;i<split.length;i++){
				pointList.add(Integer.parseInt(split[i]));
			}
		}
		return pointList;
	}

	/**
	 * 按照id查询
	 * 
	 * @param mRecordItemId
	 * @return
	 */
	public RateRecord queryRecordById(int mRecordItemId) {
		String where = KEY_ROWID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(mRecordItemId) };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
		RateRecord record = new RateRecord();
		if (cursor.moveToNext()) {
			record.setId(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)));
			record.setTime(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TIME)));
			record.setLfhf(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_LFHF)));
			record.setSdnn(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SDNN)));
			String points = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_RATELIST));
			record.setRatelist(parsePoints(points));
			record.setRatefilepath(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_RATEFILEPATH)));
			record.setHrr(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_HRR)));
			record.setRatenormalcount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_RATENORMALCOUNT)));
			record.setRatebadcount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_RATEBADCOUNT)));
			record.setMissbeat(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_MISSBEAT)));
			record.setPrematurebeat(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_PREMATUREBEAT)));
		}
		return record;
	}

	/**
	 * 按照时间查询
	 *
	 * @param time
	 * @return
	 */
	public RateRecord queryRecordByTime(String time) {
		String where = KEY_TIME + "=?";
		String[] selectionArgs = new String[] { time };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where, selectionArgs, null, null, null);
		RateRecord record = new RateRecord();
		if (cursor.moveToNext()) {
			record.setId(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)));
			record.setTime(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TIME)));
			record.setLfhf(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_LFHF)));
			record.setSdnn(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SDNN)));
			String points = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_RATELIST));
			record.setRatelist(parsePoints(points));
			record.setRatefilepath(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_RATEFILEPATH)));
			record.setHrr(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_HRR)));
			record.setRatenormalcount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_RATENORMALCOUNT)));
			record.setRatebadcount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_RATEBADCOUNT)));
			record.setMissbeat(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_MISSBEAT)));
			record.setPrematurebeat(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_PREMATUREBEAT)));
		}
		return record;
	}

	private String[] getColumns() {
		return new String[] { KEY_ROWID, KEY_TIME, KEY_LFHF, KEY_SDNN,
				KEY_RATELIST, KEY_RATEFILEPATH, KEY_HRR, KEY_RATENORMALCOUNT,KEY_RATEBADCOUNT, KEY_MISSBEAT,KEY_PREMATUREBEAT};
	}
}
