package com.example.Utils;

import android.app.IntentService;
import android.content.Intent;

public abstract class LongRunningNotStickyBroadcastService extends IntentService {

	public LongRunningNotStickyBroadcastService(String name) {
		super(name);
	}
	
	public LongRunningNotStickyBroadcastService() {
		super("LongRunningNotStickyBroadcastService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();	
		//setup the green room in case for Android restarts due to pendingIntents
		LightedGreenRoom.InitWithLightsOn(getApplicationContext());
		LightedGreenRoom.getInstance().registerClient();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		LightedGreenRoom.getInstance().enter();
		return START_NOT_STICKY;
	}

	@Override
	protected void onHandleIntent(Intent intent) { //worker thread
		Intent originalIntent = intent.getExtras().getParcelable("original_intent");
		onHandleBroadcastIntent(originalIntent);
		
		LightedGreenRoom.getInstance().leave(); //works done
	}
	
	@Override
	public void onDestroy() {
		LightedGreenRoom.getInstance().unRegisterClient();
		super.onDestroy();
	}
	
	//stub
	public abstract void onHandleBroadcastIntent(Intent intent);

}
