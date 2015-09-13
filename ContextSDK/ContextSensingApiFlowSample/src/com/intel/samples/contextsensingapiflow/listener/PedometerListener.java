package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.Pedometer;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class PedometerListener implements IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public PedometerListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Pedometer) {
            Pedometer pedometer = (Pedometer) state;
            mLastKnownItem = pedometer;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Steps: " + pedometer.getSteps());
            sb.append("\n" + pedometer.getDateTime());
            Log.d(LOG_TAG, "New Pedometer State: " + sb.toString());
            final String placeText = sb.toString();
            ContextSensingApiFlowSampleActivity.mPedometerText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mPedometerText.setText(placeText);
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
        } else if (item instanceof Pedometer) {
            mLastKnownItem = item;
        }
    }
    
    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "PedometerListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

