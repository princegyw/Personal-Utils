package com.intel.samples.customstatesensingapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.InitCallback;
import com.intel.context.exception.ContextProviderException;

public class CustomStateSensingSampleActivity extends Activity {
	private final static String LOG_TAG = CustomStateSensingSampleActivity.class.getName();
	private Sensing mySensing;
	private ContextTypeListener myListener;
    private Button startDaemonButton;
	private Button startSensingButton;
	private Button addListenerButton;
	private Button stopSensingButton;
    private Button stopDaemonButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mySensing = CustomStateSensingSampleApplication.getInstance().getSensing();
		myListener = CustomStateSensingSampleApplication.getInstance().getCustomStateListener();
		configureUI();
	}

	public void onDestroy() {
	    super.onDestroy();
	}
	
	private void configureUI(){
        startDaemonButton = (Button) findViewById(R.id.startDaemonButton);
        startDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                startDaemon();
            }});

        startSensingButton = (Button) findViewById(R.id.startSensingButton);
		startSensingButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startSensing();
			}});
		
		addListenerButton = (Button) findViewById(R.id.addListenerButton);
		addListenerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                addListener();
            }});

		stopSensingButton = (Button) findViewById(R.id.stopSensingButton);
		stopSensingButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				stopSensing();
			}});
		
		stopDaemonButton = (Button) findViewById(R.id.stopDaemonButton);
		stopDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                stopDaemon();
            }});
	}

    private void startDaemon() {
        /*
         * Start Context Sensing Daemon 
         */
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
            /*
             * Enable the sensing of this specific custom state, using the
             * custom provider URN identifier.
             * Settings are not required for this custom state provider.
             * (You can alternatively enable the sensing of this custom state
             * by using the alias name).
             */
            mySensing.enableSensing("urn:mycompany:context:device:ringer", bundle);
            //mySensing.enableSensing("ringer", bundle);

            Toast.makeText(getApplicationContext(),
                    "Custom State Sensing Enabled" , Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
	
	private void addListener() {
        try {
            /*
             * Add a listener for the enabled context state.
             */
            mySensing.addContextTypeListener("urn:mycompany:context:device:ringer", myListener);

            Toast.makeText(getApplicationContext(),
                    "Add Listener Success", Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
	
	private void stopSensing(){
        try {
            /*
             * Disable the sensing of the custom state using the URN identifier.
             */
            mySensing.disableSensing("urn:mycompany:context:device:ringer");

            Toast.makeText(getApplicationContext(),
                    "Custom State Sensing Disabled" , Toast.LENGTH_SHORT).show();
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }
	
    private void stopDaemon() {
        /*
         * Stop Context Sensing Daemon 
         */
        try {
            mySensing.stop();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }
}
