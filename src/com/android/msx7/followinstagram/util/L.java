package com.android.msx7.followinstagram.util;

import android.util.Log;

/**
 * Log统一管理
 * @author way
 * 
 */
public class L {
	
	private static final String TAG = "debug";
	
	public static boolean isDebug = true;
	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}
	//下面是传入类名打印log
	public static void i(Class<?> _class,String msg){
		if (isDebug)
			Log.i(_class.getName(), msg);
	}
	public static void d(Class<?> _class,String msg){
		if (isDebug)
			Log.i(_class.getName(), msg);
	}
	public static void e(Class<?> _class,String msg){
		if (isDebug)
			Log.i(_class.getName(), msg);
	}
	public static void v(Class<?> _class,String msg){
		if (isDebug)
			Log.i(_class.getName(), msg);
	}
	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}
}
