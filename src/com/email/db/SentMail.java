package com.email.db;

import com.email.utils.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SentMail extends SQLiteOpenHelper{

	public SentMail(Context context) {
		super(context, Constant.SQL.DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table "+Constant.SQL.DB_TABLE
				+"(id INTEGER PRIMARY KEY AUTOINCREMENT,mailfrom varchar(20)," +
				"mailto varchar(20),subject varchar(20),snedtime varchar(50)," +
				"content text)");
		db.execSQL("create table "+Constant.SQL.DB_TABLE_DELETE
				+"(id INTEGER PRIMARY KEY AUTOINCREMENT,messageId text(200))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
