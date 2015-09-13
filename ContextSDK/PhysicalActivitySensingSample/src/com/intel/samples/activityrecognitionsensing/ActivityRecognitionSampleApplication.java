package com.intel.samples.activityrecognitionsensing;

import java.util.List;
import java.util.Set;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.item.ActivityRecognition;
import com.intel.context.item.Item;
import com.intel.context.item.activityrecognition.PhysicalActivity;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class ActivityRecognitionSampleApplication extends Application {
    private Sensing mSensing;
    private static ActivityRecognitionSampleApplication mInstance;
    private ContextTypeListener mActivityRecognitionListener;

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
        mActivityRecognitionListener = new ActivityRecognitionListener();      
    }
    
    public static ActivityRecognitionSampleApplication getInstance() {
        return mInstance;
    }

    public Sensing getSensing() {
        return mSensing;
    }

    public ContextTypeListener getActivityRecognitionListener() {
        return mActivityRecognitionListener;
    }

    private class MySensingListener implements SensingStatusListener {        
        
        private final String LOG_TAG = MySensingListener.class.getName();

        MySensingListener() {}

        public void onEvent(SensingEvent event) {
            Toast.makeText(getApplicationContext(),
            		"Event: " + event.getDescription(), Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Event: " + event.getDescription());
        }

        public void onFail(ContextError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Context Sensing error: " + error.getMessage());            
        }
    }

    private class ActivityRecognitionListener implements ContextTypeListener {

        private final String LOG_TAG = ActivityRecognitionListener.class.getName();
        
        public void onReceive(Item state) {
            if (state instanceof ActivityRecognition) {
            	// Cast the incoming context state to an ActivityRecognition Item.
            	ActivityRecognition activityRecognitionState = (ActivityRecognition) state;
            	// Obtain the list of recognized physical activities.
            	List<PhysicalActivity> physicalActivities = activityRecognitionState.getActivities();
            	
            	StringBuilder sb = new StringBuilder();
            	for (PhysicalActivity activity: physicalActivities) {
            		// Obtain the probability of each recognized activity.
            		int activityProbability = activity.getProbability();
            		sb.append("\n - " + activity.getActivity() + ", " + activityProbability + "% chance.");
            	}

	            Toast.makeText(getApplicationContext(), 
	            		"New Activity Recognition State! \n" + sb.toString(),
	            		Toast.LENGTH_LONG).show();

	            Log.d(LOG_TAG, "New Activity Recognition State: " + sb.toString());
            } else {
	            Log.d(LOG_TAG, "Invalid state type: " + state.getContextType());
            }
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
            		"Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + error.getMessage());
        }
    }

}
