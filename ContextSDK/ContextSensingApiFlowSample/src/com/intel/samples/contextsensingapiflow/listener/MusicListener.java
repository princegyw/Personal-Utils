package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.Music;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class MusicListener implements IApplicationListener {

    private final String LOG_TAG = MusicListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public MusicListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Music) {
            Music music = (Music) state;
            mLastKnownItem = music;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Title: " + music.getTitle());
            sb.append("\nAuthor: " + music.getAuthor());
            sb.append("\nAlbum: " + music.getAlbum());
            sb.append("\n" + music.getDateTime());
            Log.d(LOG_TAG, "New Music State: " + sb.toString());
            final String musicText = sb.toString();
            ContextSensingApiFlowSampleActivity.mMusicText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mMusicText.setText(musicText);
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
        } else if (item instanceof Music) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "MusicListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

