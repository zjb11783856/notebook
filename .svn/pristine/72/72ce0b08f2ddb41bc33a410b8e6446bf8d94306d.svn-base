package com.boge.android_0618_notebook.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
	private MyOpenHelper myHelper;
	private SQLiteDatabase db;
	private Context context;
	private static DatabaseUtils dbUtils;

	public DatabaseUtils(Context context) {
		super();
		this.context = context;
		myHelper = new MyOpenHelper(context, "notedb", null, 1);
	}

	public static DatabaseUtils getInstance(Context context) {
		if (dbUtils == null) {

			dbUtils = new DatabaseUtils(context);
		}

		return dbUtils;
	}

	/**
	 * É¾³ýÊý¾Ý
	 * 
	 * @param id
	 */
	public void delete(int id) {
		db = myHelper.getWritableDatabase();
		String whereClause = "id=?";
		String[] whereArgs = { String.valueOf(id) };
		db.delete("note", whereClause, whereArgs);
		db.close();
	}

}
