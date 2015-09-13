package com.example.alarmdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeUtil {
	private static long tic_millis = 0;
	
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
	
	public static String getDateTimeString(Calendar cal)
	{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");
		df.setLenient(false);
		return df.format(cal.getTime());
	}

	//tic 
	public void tic()
	{
		tic_millis = getCurrentTime().getTimeInMillis();
	}
	
	public long toc()
	{
		long toc_millis = getCurrentTime().getTimeInMillis();
		return toc_millis - tic_millis;
	}
}
