package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Place;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class PlaceListener implements IApplicationListener {

    private final String LOG_TAG = PlaceListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public PlaceListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Place) {
            Place place = (Place) state;
            mLastKnownItem = place;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Type: " + place.getType());
            sb.append("\n" + place.getDateTime());
            Log.d(LOG_TAG, "New Place State: " + sb.toString());
            final String placeText = sb.toString();
            ContextSensingApiFlowSampleActivity.mPlaceText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mPlaceText.setText(placeText);
                }
            });
        }
    }

    public Item getLastKnownItem() {
        return mLastKnownItem;
    }
    
    public void setLastKnownItem(Item item) {
        if (item == null ) {
            mLastKnownItem = null;
        } else if (item instanceof Place) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "PlaceListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

