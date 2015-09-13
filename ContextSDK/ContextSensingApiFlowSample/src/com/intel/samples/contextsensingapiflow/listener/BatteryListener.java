package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Battery;
import com.intel.context.item.Item;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class BatteryListener implements IApplicationListener {

    private final String LOG_TAG = BatteryListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public BatteryListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Battery) {
            Battery battery = (Battery) state;
            mLastKnownItem = battery;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Level: " + battery.getLevel());
            sb.append("\nStatus: " + battery.getStatus());
            sb.append("\nTemperature: " + battery.getTemperature());
            sb.append("\n" + battery.getDateTime());
            Log.d(LOG_TAG, "New Battery State: " + sb.toString());
            final String batteryText = sb.toString();
            ContextSensingApiFlowSampleActivity.mBatteryText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mBatteryText.setText(batteryText);
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
        } else if (item instanceof Battery) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "BatteryListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

