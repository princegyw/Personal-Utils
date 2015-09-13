package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.AppsRunning;
import com.intel.context.item.Item;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class AppsListener implements IApplicationListener {

    private final String LOG_TAG = AppsListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public AppsListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof AppsRunning) {
            AppsRunning apps = (AppsRunning) state;
            mLastKnownItem = apps;
            StringBuilder sb = new StringBuilder();
            sb.append("Current app: " + apps.getCurrentApplication().getApplicationName());
            sb.append("\n" + apps.getDateTime());
            Log.d(LOG_TAG, "New Apps State: " + sb.toString());
            final String appsText = sb.toString();
            ContextSensingApiFlowSampleActivity.mAppsText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mAppsText.setText(appsText);
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
        } else if (item instanceof AppsRunning) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "AppsListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

