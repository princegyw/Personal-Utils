/*
  
  #Current version:2.0  
  #version 1.0    @20140705
  	>>Enable Time Util functions
  #version 2.0    @20150815
  	>>Remove tic and toc function to TimerUtil
 	
  Written by:GuYiwei 
  Email:Yiwei.gu09@gmail.com
  
  IN ACCORDANCE WITH GPL LICENECE
*/

package com.example.handlerdemo;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeUtil {
		
	public static Calendar getTimeAfterInSecs(int secs)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, secs);
		return cal;
	}
	
	public static Calendar getCurrentTime()
	{
		Calendar today = Calendar.getInstance();
		return today;
	}
	
	public static Calendar getTodayAt(int hours, int mins, int secs)
	{
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONDAY);
		int day = today.get(Calendar.DATE);
		
		cal.set(year, month, day, hours, mins, secs);
		return cal;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getDateTimeString(Calendar cal)
	{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");
		df.setLenient(false);
		return df.format(cal.getTime());
	}
	
}
