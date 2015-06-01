package com.email.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.util.Log;

public class Tools {

	public static String getData(String str){
		long millis = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		Date  date = new Date(millis);
		String time = format.format(date);
		Log.e(""+str, time);
		return time;
	}
}
