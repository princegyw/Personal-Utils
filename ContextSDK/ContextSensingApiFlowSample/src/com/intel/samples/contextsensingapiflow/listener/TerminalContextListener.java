package com.intel.samples.contextsensingapiflow.listener;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.Pedometer;
import com.intel.context.item.TerminalContext;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class TerminalContextListener implements IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public TerminalContextListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof TerminalContext) {
            TerminalContext terminal = (TerminalContext) state;
            mLastKnownItem = terminal;
            
            StringBuilder sb = new StringBuilder();
            sb.append(terminal.getOrientation().toString());
            sb.append("\n" + terminal.getFace().toString());
            sb.append("\n" + terminal.getDateTime());
            Log.d(LOG_TAG, "New Terminal Context State: " + sb.toString());
            final String placeText = sb.toString();
            ContextSensingApiFlowSampleActivity.mTerminalContextText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mTerminalContextText.setText(placeText);
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
                "TerminalContextListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}


