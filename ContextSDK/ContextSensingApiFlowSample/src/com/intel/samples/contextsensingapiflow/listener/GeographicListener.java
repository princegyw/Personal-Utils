package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Geographic;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class GeographicListener implements IApplicationListener {

    private final String LOG_TAG = GeographicListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public GeographicListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Geographic) {
            Geographic geographic = (Geographic) state;
            mLastKnownItem = geographic;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Continent: " + geographic.getContinent());
            sb.append("\nCountry: " + geographic.getCountry());
            sb.append("\nCity: " + geographic.getCity());
            sb.append("\n" + geographic.getDateTime());
            Log.d(LOG_TAG, "New Geographic State: " + sb.toString());
            final String geographicText = sb.toString();
            ContextSensingApiFlowSampleActivity.mGeographicText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mGeographicText.setText(geographicText);
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
        } else if (item instanceof Geographic) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "GeographicListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

