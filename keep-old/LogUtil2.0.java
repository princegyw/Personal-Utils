/*--ver2.0--*/
package com.example.httpdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;
import android.util.Log;

public class LogUtil {
	private static boolean debuggable = true;
	
	public static final String TAG = "gyw";
	
	public static void setDebuggable(boolean inDebuggable)
	{
		debuggable = inDebuggable;
	}
	
	public static void setDebuggableFromApplicationInfo(Context context)
	{
		int appFlag = context.getApplicationInfo().flags;
		if ( (appFlag & ApplicationInfo.FLAG_DEBUGGABLE) == 0 ) {
			debuggable = false;
		}
	}
	
	public static boolean getDebuggable()
	{
		return debuggable;
	}
	
	public static void d(String tag, String msg)
	{
		if (debuggable) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(String msg)
	{
		LogUtil.d(TAG, msg);
	}
	
	public static void i(String tag, String msg)
	{
		if (debuggable) {
			Log.i(tag, msg);
		}
	}
	
	public static void i(String msg)
	{
		LogUtil.i(TAG, msg);
	}
	
	public static void v(String tag, String msg)
	{
		if (debuggable) {
			Log.v(tag, msg);
		}
	}
	
	public static void v(String msg)
	{
		LogUtil.v(TAG, msg);
	}
	
	
	public static void e(String tag, String msg)
	{
		if (debuggable) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(String msg)
	{
		LogUtil.e(TAG, msg);
	}
	
	
	@SuppressLint("NewApi")
	public static void StrictModeInit()
	{
		if (debuggable) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
										.detectDiskReads()
										.detectNetwork()
										.detectDiskWrites()
										.penaltyLog()
										.build());
			
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
									.detectActivityLeaks()
									.detectLeakedSqlLiteObjects()
									.penaltyLog()
									.penaltyDeath()
									.build());
		}
	}
}
