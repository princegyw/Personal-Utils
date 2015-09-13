package com.intel.samples.locationsensing;

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

public class LocationSampleActivity extends Activity {
    private final static String LOG_TAG = LocationSampleActivity.class.getName();    
    private Button authorizeButton;
    private Button startSensingButton;
    private Button stopSensingButton;
    private Button startDaemonButton;
    private Button stopDaemonButton;
    private Auth auth;
    private Sensing mySensing;
    private ContextTypeListener mLocationListener;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        auth = Auth.getInstance(this);
        mySensing = LocationSampleApplication.getInstance().getSensing();
        mLocationListener = LocationSampleApplication.getInstance().getLocationListener();
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
        	auth.init(Settings.API_KEY, Settings.SECRET, Settings.REDIRECT_URI, "user:details context:location:detailed context:post:location:detailed context:post:device:information", new MyAuthCallback());
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
    	Bundle settings = null;
        try {
            mySensing.enableSensing(ContextType.LOCATION, settings);
            mySensing.addContextTypeListener(ContextType.LOCATION, mLocationListener);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error enabling / adding listener to provider: " + e.getMessage(), Toast.LENGTH_LONG).show();                    
            Log.e(LOG_TAG, "Error enabling provider");
        }
    }
    
    private void stopSensing(){
        try {
            mySensing.removeContextTypeListener(mLocationListener);   
            mySensing.disableSensing(ContextType.LOCATION);
                Toast.makeText(getApplicationContext(), "Location State Sensing Disabled" , Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error disabling provider: " + e.getMessage() , Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error disabling provider: " + e.getMessage());
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Context Sensing not started", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Context Sensing not started");
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
