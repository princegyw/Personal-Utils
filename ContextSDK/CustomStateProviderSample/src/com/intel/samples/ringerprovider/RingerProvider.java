package com.intel.samples.ringerprovider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.intel.context.provider.IProviderPublisher;
import com.intel.context.provider.IStateProvider;
import com.intel.context.exception.ContextProviderException;

/**
 * This class contains the logic of our custom context state
 * provider. In this example, this provider will detect
 * changes on the device's ringer mode. When a new change is
 * detected, a new "Ringer" item will be published through
 * the SDK by using IProviderPublisher.
 * 
 * A custom state provider must implement IStateProvider to
 * be compliant with the SDK.
 */
public class RingerProvider implements IStateProvider {
    private final static String LOG_TAG = RingerProvider.class.getName();
    private IProviderPublisher mPublisher = null;
    private Context mContext = null;
    private BroadcastReceiver ringerModeReceiver = null;

    /*
     * At this method we should initialize and start
     * our provider to start publishing items.
     */
    public void start(Context context, IProviderPublisher publisher,
            Bundle settings) throws ContextProviderException {
        mContext = context;
        mPublisher = publisher;
        ringerModeReceiver = new RingerModeReceiver();
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        context.registerReceiver(ringerModeReceiver, filter);
    }

    /*
     * At this method we should stop the provider's tasks
     * and free any used resources.
     */
    public void stop() {
        mContext.unregisterReceiver(ringerModeReceiver);
        Log.d(LOG_TAG, "Custom Provider Stopped");
    }
    
    private class RingerModeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle received = intent.getExtras();
            
            String stateValue = null;
            int ringerMode = (Integer) received.get(android.media.AudioManager.EXTRA_RINGER_MODE);

            switch (ringerMode){
            case android.media.AudioManager.RINGER_MODE_NORMAL:
                stateValue = "NORMAL";
                break;
            case android.media.AudioManager.RINGER_MODE_SILENT:
                stateValue = "SILENT";
                break;
            case android.media.AudioManager.RINGER_MODE_VIBRATE:
                stateValue = "VIBRATE";
                break;
            }

            /*
             * After collecting the context information, let's create
             * a new "Ringer" Item, fill the values in and publish the
             * item by using the method updateState(item).
             */
            Ringer item = new Ringer();
            item.setRingerMode(stateValue);
            mPublisher.updateState(item);
        }
    }    
}
