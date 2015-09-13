package com.intel.samples.callsensing;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class CallSampleApplication extends Application {
    private Sensing mSensing;
    private static CallSampleApplication mInstance;
    private ContextTypeListener mCallsListener;
    
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
        mCallsListener = new CallsListener();
    }
    
    public static CallSampleApplication getInstance() {
        return mInstance;
    }
    
    public Sensing getSensing() {
        return mSensing;
    }
    
    public ContextTypeListener getCallsListener() {
        return mCallsListener;
    }

    private class MySensingListener implements SensingStatusListener {        
        
        private final String LOG_TAG = MySensingListener.class.getName();

        MySensingListener() {}
        
        @Override
        public void onEvent(SensingEvent event) {
            Toast.makeText(getApplicationContext(), "Event: " + event.getDescription(), Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Event: " + event.getDescription());
        }

        @Override
        public void onFail(ContextError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Context Sensing Error: " + error.getMessage());            
        }
    }

    private class CallsListener implements ContextTypeListener {

        private final String TAG = CallsListener.class.getName();
        
        public void onReceive(Item state) {
            Log.d(TAG, "New State: " + state.getContextType());
            Toast.makeText(getApplicationContext(),
                    "New Call State!", Toast.LENGTH_LONG).show();
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Listener Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: " + error.getMessage());
        }
    }

}
