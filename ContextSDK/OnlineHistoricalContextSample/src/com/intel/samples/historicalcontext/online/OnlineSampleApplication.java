package com.intel.samples.historicalcontext.online;



import android.app.Application;

public class OnlineSampleApplication extends Application {
	//TODO: private Auth mAuth;

	private static OnlineSampleApplication mInstance;

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		//TODO:  mAuth = new Auth(getApplicationContext(), Settings.API_KEY, Settings.SECRET, Environment.PROD);

	}

	public static OnlineSampleApplication getInstance() {
		return mInstance;
	}
	/*TODO:
	public Auth getAuth() {
		return mAuth;
	}*/

}
