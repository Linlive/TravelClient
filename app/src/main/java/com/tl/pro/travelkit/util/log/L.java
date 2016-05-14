package com.tl.pro.travelkit.util.log;

import android.util.Log;

/**
 * 日志记录输出，供打包的时候清楚日志
 * Created by Administrator on 2016/5/10.
 */
public class L {

	public static boolean debug = true;
	public static void d(String TAG, String message){
		if(debug){
			Log.d(TAG, message);
		}
	}
	public static void i(String TAG, String message){
		if(debug){
			Log.i(TAG, message);
		}
	}
	public static void w(String TAG, String message){
		if(debug){
			Log.w(TAG, message);
		}
	}
	public static void e(String TAG, String message){
		if(debug){
			Log.e(TAG, message);
		}
	}
}
