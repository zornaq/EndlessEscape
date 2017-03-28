package com.zorro.game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	// C:\Programs\Android\sdk\platform-tools>adb shell
	// sqlite3 ./data/data/your_package_name/databases/databasename
	// sqlite > select * from database;
	// sqlite > insert into highscores (createdAt,score) values (666111,191919191);

	private static final String TAG = DbHelper.class.getSimpleName();

	// Database name and version (changing version upgrades DB)
	public static final String DB_NAME = "highscore.db";
	public static final int DB_VERSION = 1;

	// Table name, information field names
	public static final String TABLE = "highscores";
	public static final String C_ID = "_id";
	public static final String C_DATE = "createdAt";
	public static final String C_SCORE = "score";

	public DbHelper(Context context) {
		// Context from Android, database name, no factory, version
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// set create parameters in string, make three columns
		// 1. column = ID and set it to primary key + autoincrementing starting
		// from 0
		// 2. column = Date set it to text + null is not accepted
		// 3. column = Score set it to integer + null is not accepted
		String sql = String.format("create table %s (" 
						+ "%s INTEGER PRIMARY KEY AUTOINCREMENT," 
						+ "%s TEXT NOT NULL," 
						+ "%s INT NOT NULL)", TABLE, C_ID, C_DATE, C_SCORE);

		Log.d(TAG, "onCreate sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop table if exists and create new table (database lost)
		db.execSQL("drop table if exists " + TABLE);
		Log.d(TAG, "onUpdate dropped table " + TABLE);
		this.onCreate(db);
	}
}
