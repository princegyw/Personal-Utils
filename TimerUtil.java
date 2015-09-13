/*
  
  #Current version:1.0  
  #version 1.0    @20140815
  	>>Enable tic() toc() tic_accurate() toc_accurate() clear() functions
 	
  Written by:GuYiwei 
  Email:Yiwei.gu09@gmail.com
  
  IN ACCORDANCE WITH GPL LICENECE
*/

package com.example.handlerdemo;

import java.util.Calendar;

public class TimerUtil {
	private static long tic_millis = 0;
	private static long tic_nano = 0;
	
	//tic 
	public static void tic() {
		tic_millis = Calendar.getInstance().getTimeInMillis();
	}
	
	public static long toc() {
		return Calendar.getInstance().getTimeInMillis() - tic_millis;
	}
	
	public static void tic_accurate() {
		tic_nano = System.nanoTime();
	}
	
	public static long toc_accurate() {
		return System.nanoTime() - tic_nano;
	}
	
	public static void clear() {
		tic_millis = 0;
		tic_nano = 0;
	}
}
