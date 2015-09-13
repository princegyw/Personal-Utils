package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Date;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class DateListener implements IApplicationListener {

    private final String LOG_TAG = DateListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public DateListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Date) {
            Date date = (Date) state;
            mLastKnownItem = date;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Part Of Week: " + date.getPartOfWeek());
            sb.append("\nDay: " + date.getDay());
            sb.append("\nSeason: " + date.getSeason());
            sb.append("\n" + date.getDateTime());
            Log.d(LOG_TAG, "New Date State: " + sb.toString());
            final String dateText = sb.toString();
            ContextSensingApiFlowSampleActivity.mDateText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mDateText.setText(dateText);
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
        } else if (item instanceof Date) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "DateListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

