package com.intel.samples.contextsensingapiflow.listener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.Network;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class NetworkListener implements IApplicationListener {

    private final String LOG_TAG = NetworkListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public NetworkListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Network) {
            Network network = (Network) state;
            mLastKnownItem = network;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Type: " + network.getNetworkType());
            sb.append("\nSSID: " + network.getSsid());
            sb.append("\nSignal: " + network.getSignalStrength());
            sb.append("\nTraffic sent: " + network.getTrafficSent());
            sb.append("\n" + network.getDateTime());
            Log.d(LOG_TAG, "New Network State: " + sb.toString());
            final String networkText = sb.toString();
            ContextSensingApiFlowSampleActivity.mNetworkText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mNetworkText.setText(networkText);
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
        } else if (item instanceof Network) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "NetworkListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

