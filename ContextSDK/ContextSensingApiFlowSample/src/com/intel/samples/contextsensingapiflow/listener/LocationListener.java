package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class LocationListener implements IApplicationListener {

    private final String LOG_TAG = LocationListener.class.getName();
    private Item mLastKnownItem;
    private Context mContext;
    
    public LocationListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof LocationCurrent) {
            LocationCurrent mLocation = (LocationCurrent) state;
            mLastKnownItem = mLocation;
            
            StringBuilder sb = new StringBuilder();
            sb.append(mLocation.getActivity());
            sb.append("\n Latitude: " + mLocation.getLocation().getLatitude());
            sb.append("\n Longitude: " + mLocation.getLocation().getLongitude());
            sb.append("\n" + mLocation.getDateTime());
            
            Log.d(LOG_TAG, "New Location State: " + sb.toString());
            final String locationText = sb.toString();
            ContextSensingApiFlowSampleActivity.mLocationText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mLocationText.setText(locationText);
                }
            });
        }
    }
    
    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "LocationListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

    public Item getLastKnownItem() {
        return mLastKnownItem;
    }
    
    public void setLastKnownItem(Item item) {
        if (item == null ) {
            mLastKnownItem = null;
        } else if (item instanceof LocationCurrent) {
            mLastKnownItem = item;
        }
    }
}