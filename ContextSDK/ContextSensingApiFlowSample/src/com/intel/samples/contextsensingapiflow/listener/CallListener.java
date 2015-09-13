package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Call;
import com.intel.context.item.Item;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class CallListener implements IApplicationListener {

    private final String LOG_TAG = CallListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public CallListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Call) {
            Call call = (Call) state;
            mLastKnownItem = call;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Phone number: " + call.getCaller());
            sb.append("\nMissed qty.: " + call.getMissedQuantity());
            sb.append("\nRing qty.: " + call.getRingQuantity());
            sb.append("\nNotif. type: " + call.getNotificationType());
            sb.append("\n" + call.getDateTime());
            Log.d(LOG_TAG, "New Call State: " + sb.toString());
            final String callText = sb.toString();
            ContextSensingApiFlowSampleActivity.mCallText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mCallText.setText(callText);
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
        } else if (item instanceof Call) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "CallListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

