package com.example.hellojni;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class DebuggerUtil {
	
	private static final int SLEEP_TIME	= 5000;
	
	public static void waitForDebugger(Context context){
		int appFlag = context.getApplicationInfo().flags;
		if ( (appFlag & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ) { //if true
	        try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
