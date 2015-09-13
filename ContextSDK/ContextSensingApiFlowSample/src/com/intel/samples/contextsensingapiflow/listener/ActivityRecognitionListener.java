package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.ActivityRecognition;
import com.intel.context.item.Item;
import com.intel.context.item.activityrecognition.PhysicalActivity;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class ActivityRecognitionListener implements IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public ActivityRecognitionListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof ActivityRecognition) {
            // Cast the incoming context state to an ActivityRecognition Item.
            ActivityRecognition activityRecognitionState = (ActivityRecognition) state;
            
            mLastKnownItem = activityRecognitionState;
            // Obtain the list of recognized physical activities.
            List<PhysicalActivity> physicalActivities = activityRecognitionState.getActivities();
            
            StringBuilder sb = new StringBuilder();
            for (PhysicalActivity activity: physicalActivities) {
                // Obtain the probability of each recognized activity.
                int activityProbability = activity.getProbability();
                sb.append(activity.getActivity().toString() + " " + activityProbability + "% chance\n");
                
            }
            sb.append(activityRecognitionState.getDateTime());
            Log.d(LOG_TAG, "New Activity Recognition State: " + sb.toString());
            final String activityText = sb.toString();
            ContextSensingApiFlowSampleActivity.mActivityText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mActivityText.setText(activityText);
                }
            });
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

    public void setLastKnownItem(Item item) {
        if (item == null ) {
            mLastKnownItem = null;
        } else if (item instanceof ActivityRecognition) {
            mLastKnownItem = item;
        }
    }

    public Item getLastKnownItem() {
        return mLastKnownItem;
    }
}

