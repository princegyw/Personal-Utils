package com.intel.samples.runningappssensing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.intel.context.Auth;
import com.intel.context.Sensing;
import com.intel.context.auth.AuthCallback;
import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.InitCallback;
import com.intel.context.exception.ContextProviderException;

public class RunningAppsSampleActivity extends Activity {
    private final static String LOG_TAG = RunningAppsSampleActivity.class.getName();
    private Sensing mySensing;
    private Auth auth;
    private ContextTypeListener myListener;
    private Button authorizeButton;
    private Button startDaemonButton;
    private Button startSensingButton;
    private Button addListenerButton;
    private Button removeListenerButton;
    private Button stopSensingButton;
    private Button stopDaemonButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mySensing = RunningAppsSampleApplication.getInstance().getSensing();
        auth = Auth.getInstance(this);
        myListener = RunningAppsSampleApplication.getInstance().getAppsListener();
        configureUI();
    }

    public void onDestroy() {
        super.onDestroy();
    }
    
    private void configureUI(){
    	authorizeButton = (Button) findViewById(R.id.authorizeButton);
    	authorizeButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                authorize();
            }});
        
        startDaemonButton = (Button) findViewById(R.id.startSensingDaemonButton);
        startDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                startDaemon();
            }
        });

        startSensingButton = (Button) findViewById(R.id.startSensingButton);
        startSensingButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                startSensing();
            }});
        
        addListenerButton = (Button) findViewById(R.id.addListenerButton);
        addListenerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                addAppsListener();
            }});

        removeListenerButton = (Button) findViewById(R.id.removeListenerButton);
        removeListenerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                removeAppsListener();
            }});

        
        stopSensingButton = (Button) findViewById(R.id.stopSensingButton);
        stopSensingButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                stopSensing();
            }});
        
        stopDaemonButton = (Button) findViewById(R.id.stopSensingDaemonButton);
        stopDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                stopDaemon();
            }
        });
    }
    
    private void authorize() {      
        if (!auth.isInit()) {
        	auth.init(Settings.API_KEY, Settings.SECRET, Settings.REDIRECT_URI, "user:details context:device:applications:running context:post:device:applications:running context:post:device:information", new MyAuthCallback());
        } else {
            Toast.makeText(getApplicationContext(), "Already Authorized", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Already Authorized");
        }
    }
    
    private void startDaemon() {
        mySensing.start(new InitCallback() {
            
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started" , Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void startSensing() {
        Bundle bundle = null;
        try {
            bundle = new Bundle();
            mySensing.enableSensing(ContextType.APPS, bundle);
            Toast.makeText(getApplicationContext(), "Apps State Sensing Enabled" , Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error enabling sensing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void addAppsListener() {
        try {
            mySensing.addContextTypeListener(ContextType.APPS, myListener);
            Toast.makeText(getApplicationContext(), "Add Listener Success", Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Log.e(LOG_TAG, "Add Listener error: " + e.getMessage());
        }
    }
    
    private void removeAppsListener() {
    	try {
			mySensing.removeContextTypeListener(myListener);
			Toast.makeText(getApplicationContext(), "Remove Listener Success", Toast.LENGTH_SHORT).show();
		} catch (ContextProviderException e) {
			Log.e(LOG_TAG, "Remove Listener error: " + e.getMessage());
		}
    }
    
    private void stopSensing(){
        try {
            mySensing.disableSensing(ContextType.APPS);
            Toast.makeText(getApplicationContext(), "Apps State Sensing Disabled" , Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "No Context Sensing instance", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "No Context Sensing instance. " + e.getMessage());
            e.printStackTrace();
        } catch (ContextProviderException e) {
            e.printStackTrace();
        }
    }
    
    private void stopDaemon() {
        try {
            mySensing.stop();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }

    private class MyAuthCallback implements AuthCallback {

        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), "Authorization Success", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Authorization Success");
        }

        @Override
        public void onError(ContextError error) {
            String msg = "Authorization Error: " + error.getMessage();
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, msg);
        }

        @Override
        public void onExpired() {
            // To be implemented in future versions             
        }
    }
}
