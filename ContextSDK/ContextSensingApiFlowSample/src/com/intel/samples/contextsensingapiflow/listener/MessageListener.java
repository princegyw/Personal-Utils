package com.intel.samples.contextsensingapiflow.listener;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.Message;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleActivity;

public class MessageListener implements IApplicationListener {

    private final String LOG_TAG = MessageListener.class.getName();
    private Context mContext;
    private Item mLastKnownItem;
    
    public MessageListener(Context context) {
        mContext = context;
    }
    
    public void onReceive(Item state) {
        if (state instanceof Message) {
            Message message = (Message) state;
            mLastKnownItem = message;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Phone number: " + message.getPhoneNumber());
            sb.append("\nUnread messages: " + message.getTotalMessagesUnread());
            sb.append("\nSent messages: " + message.getTotalMessagesSent());
            sb.append("\n" + message.getDateTime());
            Log.d(LOG_TAG, "New Message State: " + sb.toString());
            final String messageText = sb.toString();
            ContextSensingApiFlowSampleActivity.mMessageText.post(new Runnable() {
                
                @Override
                public void run() {
                    ContextSensingApiFlowSampleActivity.mMessageText.setText(messageText);
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
        } else if (item instanceof Message) {
            mLastKnownItem = item;
        }
    }

    public void onError(ContextError error) {
        Toast.makeText(mContext,
                "MessageListener error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }
}

