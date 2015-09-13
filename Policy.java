package com.example.wilson.myapplication;

import android.annotation.SuppressLint;
import android.os.StrictMode;

public class Policy {

	@SuppressLint("NewApi")
	public static void applyStrictPolicy()
	{

			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					                .detectAll()
									.penaltyLog()
									.penaltyDeath()
									.build());
	}

	@SuppressLint("NewApi")
	public static void applyEasyPolicy()
	{
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}




}
