package com.email.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.utils.Constant;
import com.email.utils.Constant.KEY;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SentDao {
	SentMail mail;

	public SentDao(Context context) {
		mail = new SentMail(context);
	}

	public void onInsert_SentMail(String mailto, String subject,
			String mailfrom, String snedtime, String content) {
		SQLiteDatabase db = mail.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mailto", mailto); // 接收人
		values.put("subject", subject); // 主题
		values.put("mailfrom", mailfrom); // 发送人
		values.put("snedtime", snedtime); // 发送时间
		values.put("content", content); // 内容
		db.insert(Constant.SQL.DB_TABLE, null, values);

	}

	public void onDelete_SentMail(String id) {
		SQLiteDatabase db = mail.getWritableDatabase();
		
		db.delete(Constant.SQL.DB_TABLE, "id=?", new String[]{id});

	}
	
	public List<Email> onSelectAll_SentMail() {

		SQLiteDatabase db = mail.getReadableDatabase();

		Cursor c = db.query(Constant.SQL.DB_TABLE, null, "mailfrom=?", new String[]{MyApplication.info.getUserName()}, null,
				null, null);
		List<Email> list = new ArrayList<Email>();
		if (c != null) {

			while (c.moveToNext()) {
				Email em = new Email();
				em.setContent(c.getString(c.getColumnIndex("content")));
				em.setMessageID(c.getInt(c.getColumnIndex("id")) + "");
				em.setTo(c.getString(c.getColumnIndex("mailto")));
				em.setSubject(c.getString(c.getColumnIndex("subject")));
				em.setFrom(c.getString(c.getColumnIndex("mailfrom")));
				em.setSentTime(c.getString(c.getColumnIndex("snedtime")));
				list.add(em);
			}
			c.close();
		}
		return list;
	}

	public Email onSelect_SentMail(String  messageId) {

		SQLiteDatabase db = mail.getReadableDatabase();
		
		
		Cursor c = db.query(Constant.SQL.DB_TABLE, null, "id=? and mailfrom=?", new String[]{messageId, MyApplication.info.getUserName()}, null,
				null, null);
		if (c != null) {
			while (c.moveToNext()) {
				Email em = new Email();
				em.setContent(c.getString(c.getColumnIndex("content")));
				em.setMessageID(c.getInt(c.getColumnIndex("id")) + "");
				em.setTo(c.getString(c.getColumnIndex("mailto")));
				em.setSubject(c.getString(c.getColumnIndex("subject")));
				em.setFrom(c.getString(c.getColumnIndex("mailfrom")));
				em.setSentTime(c.getString(c.getColumnIndex("snedtime")));
				return em;
			}

			c.close();
		}
		return null;
	}
	
	public void ondelete_receiveEmail(String messageId){
		SQLiteDatabase db = mail.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("messageId", messageId); // 接收人
		
		db.insert(Constant.SQL.DB_TABLE_DELETE, null, values);
	}
	public List<Map<String, String>> ondelete_ReceiveEmail(){
		SQLiteDatabase db = mail.getWritableDatabase();
		
		Cursor c = db.query(Constant.SQL.DB_TABLE_DELETE, null, null, null, null, null, null);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while (c.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", c.getInt(c.getColumnIndex("id"))+"");
			map.put("messageId", c.getString(c.getColumnIndex("messageId")));
			list.add(map);
		}
		return list;
	}
	
}
