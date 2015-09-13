/*
  
  #Current version:3.1  
  #version 1.0    @20140705
  	>>Enable system log function
  #version 2.0    @20140714
  	>>Enable log with predefined tag
  #version 3.0	  @20140714
  	>>Enable log with user's own file
  #version 3.1    @20140731
  	>>add 	public static void logThreadId(String tag, String annotation)
			public static void logThreadId(String annotation)
			public static void logThreadId()
   	
  Written by:GuYiwei 
  Email:Yiwei.gu09@gmail.com
  
  IN ACCORDANCE WITH GPL LICENECE
*/

package com.example.broadcastreciver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;
import android.util.Log;

public class LogUtil {
	private static boolean debuggable = true;
	
	private static final String TAG = "gyw";
	private static String logFilePath = null;
	
	public static void setDebuggable(boolean inDebuggable)
	{
		debuggable = inDebuggable;
	}
	
	@SuppressLint("NewApi")
	public static void setDebuggableFromApplicationInfo(Context context)
	{
		int appFlag = context.getApplicationInfo().flags;
		if ( (appFlag & ApplicationInfo.FLAG_DEBUGGABLE) == 0 ) {
			debuggable = false;
		}
	}
	
	public static void setLogFilePath(String in_logFilePath)
	{
		logFilePath = in_logFilePath;
	}
	
	public static void removeLogFile(String filePath)
	{
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}	
		}
	}
	
	public static void closeUserLog()
	{
		logFilePath = null;
	}
	
	public static boolean getDebuggable()
	{
		return debuggable;
	}
	
	public static void d(String tag, String msg)
	{
		if (debuggable) {
			Log.d(tag, msg);
			if (logFilePath != null) {
				String logTime = getDateTimeString(getCurrentTime());
				String logType = "d";
				long tid = android.os.Process.myTid();
				log(logTime, logType, tid, tag, msg);
			}
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
			if (logFilePath != null) {
				String logTime = getDateTimeString(getCurrentTime());
				String logType = "i";
				long tid = Thread.currentThread().getId();
				log(logTime, logType, tid, tag, msg);
			}
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
			if (logFilePath != null) {
				String logTime = getDateTimeString(getCurrentTime());
				String logType = "v";
				long tid = Thread.currentThread().getId();
				log(logTime, logType, tid, tag, msg);
			}
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
			if (logFilePath != null) {
				String logTime = getDateTimeString(getCurrentTime());
				String logType = "e";
				long tid = Thread.currentThread().getId();
				log(logTime, logType, tid, tag, msg);
			}
		}
	}
	
	public static void e(String msg)
	{
		LogUtil.e(TAG, msg);
	}
	
	public static void logThreadId(String tag, String annotation)
	{
		long id = Thread.currentThread().getId();
		LogUtil.i(tag, "TID:" + String.valueOf(id) + "; " + annotation);
	}
	
	public static void logThreadId(String annotation)
	{
		LogUtil.logThreadId(TAG, annotation);
	}
	
	public static void logThreadId()
	{
		LogUtil.logThreadId("");
	}
	

	@SuppressLint("NewApi")
	public static void applyStrictPolicy()
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
	
	@SuppressLint("NewApi")
	public static void applyEasyPolicy()
	{
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	}
	
	
	private static void log(String logTime, String logType, long tid, String tag, String text)
	{
		String logItem = logTime + "  " + logType + "  " +
			     String.valueOf(tid) + "  " + tag + "  " + text + "\n";
		try
		{
			FileWriter fw = new FileWriter(logFilePath, true); //the true will append the new data
			fw.write(logItem);//appends the string to the file
			fw.close();
		}
		catch(IOException ioe)
		{

		}
	}
	
	//import some functions from TimeUtil
	private static Calendar getCurrentTime()
	{
		Calendar today = Calendar.getInstance();
		return today;
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getDateTimeString(Calendar cal)
	{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");
		df.setLenient(false);
		return df.format(cal.getTime());
	}
	

	
		
}
