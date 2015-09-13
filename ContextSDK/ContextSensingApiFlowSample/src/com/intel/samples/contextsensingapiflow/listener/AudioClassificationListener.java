package com.intel.samples.contextsensingapiflow.listener;


import java.util.List;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.AudioClassification;
import com.intel.context.item.Item;
import com.intel.context.item.audioclassification.Audio;
import com.intel.context.item.audioclassification.AudioType;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class AudioClassificationListener implements IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public AudioClassificationListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof AudioClassification) {
            // Cast the incoming context state to an ActivityRecognition Item.
            AudioClassification audioRecognitionState = (AudioClassification) state;
            mLastKnownItem = audioRecognitionState;
            
            StringBuilder sb = new StringBuilder();
            List<Audio> audioList = audioRecognitionState.getAudio();
            for (Audio a: audioList) {
                if (a.getName().equals(AudioType.SPEECH)) {
                    sb.append("Speech: " + a.getProbability() + "% chance\n");
                } 
                if (a.getName().equals(AudioType.CROWD_CHATTER)) {
                    sb.append("CrowdChatter: " + a.getProbability() + "% chance\n");
                }
                if (a.getName().equals(AudioType.MUSIC)) {
                    sb.append("Music: " + a.getProbability() + "% chance\n");
                }
                if (a.getName().equals(AudioType.MOTION)) {
                    sb.append("Motion: " + a.getProbability() + "% chance\n");
                }
                if (a.getName().equals(AudioType.MECHANICAL)) {
                    sb.append("Mechanical: " + a.getProbability() + "% chance\n");
                }
            }    
            sb.append(audioRecognitionState.getDateTime());
            Log.d(LOG_TAG, "New Audio State: " + sb.toString());
            final String audioText = sb.toString();
            ContextSensingApiFlowSampleActivity.mAudioText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mAudioText.setText(audioText);
                    
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
        } else if (item instanceof AudioClassification) {
            mLastKnownItem = item;
        }
    }

    public Item getLastKnownItem() {
        return mLastKnownItem;
    }
}
