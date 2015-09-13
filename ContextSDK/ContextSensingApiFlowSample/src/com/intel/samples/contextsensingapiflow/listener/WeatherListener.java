package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Weather;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class WeatherListener implements IApplicationListener {

    private final String LOG_TAG = WeatherListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public WeatherListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Weather) {
            Weather weather = (Weather) state;
            mLastKnownItem = weather;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Condition: " + weather.getCondition());
            sb.append("\nTemp.: " + weather.getTemperature().getImperial());
            sb.append("\nHumidity: " + weather.getHumidity());
            sb.append("\n" + weather.getDateTime());
            Log.d(LOG_TAG, "New Weather State: " + sb.toString());
            final String weatherText = sb.toString();
            ContextSensingApiFlowSampleActivity.mWeatherText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mWeatherText.setText(weatherText);
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
        } else if (item instanceof Weather) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "WeatherListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

