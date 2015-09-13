package com.intel.samples.locationsensing;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class LocationSampleApplication extends Application {
    //TODO: private Auth mAuth;
    private Sensing mSensing;
    private static LocationSampleApplication mInstance;
    private ContextTypeListener mLocationListener;

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        /*TODO:
        mAuth = new Auth(getApplicationContext(),
                Settings.API_KEY, Settings.SECRET,
                Environment.PROD);*/
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
        mLocationListener = new LocationListener();      
    }
    
    public static LocationSampleApplication getInstance() {
        return mInstance;
    }

    public Sensing getSensing() {
        return mSensing;
    }
    /*TODO:
    public Auth getAuth(){
        return mAuth;
    }*/

    public ContextTypeListener getLocationListener() {
        return mLocationListener;
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
            Log.e(LOG_TAG, "Context Sensing error: " + error.getMessage());            
        }
    }

    private class LocationListener implements ContextTypeListener {

        private final String LOG_TAG = LocationListener.class.getName();
        
        public void onReceive(Item state) {
            if (state instanceof LocationCurrent) {
                LocationCurrent locationState = (LocationCurrent) state;
	            String stateValue = locationState.getContextType() + " - " + locationState.getLocation().toString();
	            Log.d(LOG_TAG, "New Location State: " + stateValue);
	            Toast.makeText(getApplicationContext(), "New Location State!", Toast.LENGTH_LONG).show();
            } else {
	            Log.d(LOG_TAG, "Invalid state type: " + state.getContextType());
            }
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(), "Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + error.getMessage());
        }
    }

}
