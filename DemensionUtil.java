package com.example.wilson.viewpagerdemo;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class DemensionUtil {
	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}
	
	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	public static int getScreenHeight(Context context) {
		//return context.getResources().getDisplayMetrics().heightPixels;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
}
