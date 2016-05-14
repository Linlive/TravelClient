package com.tl.pro.travelkit.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/5/1.
 */
public class DabaseConfigure {
	SQLiteDatabase db = openDatabase("test.db");
	private static final String DEFAULT_DATABASE = "test.db";

	public static SQLiteDatabase getDatabase(){
		return openDatabase(DEFAULT_DATABASE);
	}

	public static SQLiteDatabase getDatabase(String name){
		return openDatabase(name);
	}


	private static SQLiteDatabase openDatabase(String databaseName) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseName, null);
		return null;
	}

}
