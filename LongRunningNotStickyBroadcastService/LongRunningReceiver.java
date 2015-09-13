package com.example.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class LongRunningReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent original_intent) {
		LightedGreenRoom.InitWithLightsOn(context);
		Intent delegateIntent = new Intent(context, getLongRunningServiceClass());
		delegateIntent.putExtra("original_intent", original_intent);
		context.startService(delegateIntent);
	}
	
	public abstract Class<?> getLongRunningServiceClass();
	
}
