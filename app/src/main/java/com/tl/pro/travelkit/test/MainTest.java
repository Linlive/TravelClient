package com.tl.pro.travelkit.test;

import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/4/24.
 */
public class MainTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
//		SQLiteDatabase database = DabaseConfigure.getDatabase();
//		createTable(database);
//		Tuser t = new Tuser();
//		t.setName("tanlin");
//		t.setAge(22);
//		ContentValues cvs = new ContentValues();
//		cvs.put("name", "tanlin");
//		cvs.put("age", 22);
//		database.insert("usertable", null,cvs);
		//database.insert("")
		String a = new String(Base64.encode("cc".getBytes("utf-8"), 0));
		String b = new String(Base64.decode(a, 0));
		System.out.println(a + "\n" + b);
	}
	private static void createTable(SQLiteDatabase db){
		//创建表SQL语句
		String stu_table="create table usertable(_id integer primary key autoincrement, sname varchar(20),snumber integer(2))";
		//执行SQL语句
		db.execSQL(stu_table);
	}
}
