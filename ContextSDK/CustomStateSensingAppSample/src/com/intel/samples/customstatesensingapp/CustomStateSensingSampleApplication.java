package com.intel.samples.customstatesensingapp;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;
import com.intel.samples.ringerprovider.Ringer;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * This sample shows you how to enable the context sensing of a custom
 * state by using the context type URN identifier. This scenario is 
 * useful when using third party context state providers that are not
 * built-in with the SDK.
 */
public class CustomStateSensingSampleApplication extends Application {
    private Sensing mSensing;
    private static CustomStateSensingSampleApplication mInstance;
    private ContextTypeListener mCustomStateListener;

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        /*
         * Create a new Sensing instance with a listener to receive
         * sensing status notifications.
         */
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
        mCustomStateListener = new CustomStateListener();      
    }
    
    public static CustomStateSensingSampleApplication getInstance() {
        return mInstance;
    }

    public Sensing getSensing() {
        return mSensing;
    }

    public ContextTypeListener getCustomStateListener() {
        return mCustomStateListener;
    }

    /*
     * Implement the listener to receive status notifications from
     * the Context Sensing daemon.
     */
    private class MySensingListener implements SensingStatusListener {        
        
        private final String TAG = MySensingListener.class.getName();

        MySensingListener() {}
        
        @Override
        public void onEvent(SensingEvent event) {
        	Toast.makeText(getApplicationContext(),
        	        "Event: " + event.getDescription(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Event: " + event.getDescription());
        }

        @Override
        public void onFail(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Context Sensing Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Context Sensing Error: " + error.getMessage());            
        }
    }

    /*
     * Implement the listener to receive new context states published
     * by the custom state provider.
     */
    private class CustomStateListener implements ContextTypeListener {

        private final String LOG_TAG = CustomStateListener.class.getName();

        public void onReceive(Item state) {
        	Log.d(LOG_TAG, "New Custom State: " + state.getContextType());
        	/*
        	 *  Being that this custom state provider publishes "Ringer"
        	 *  items, we can cast from "Item" to a "Ringer" state item.
        	 */
        	Ringer item = (Ringer) state;
            Toast.makeText(getApplicationContext(),
                    "The Ringer Mode is: \n" + item.getRingerMode(), Toast.LENGTH_LONG).show();
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + error.getMessage());
        }
    }
}
