package com.intel.samples.contextsensingapiflow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.context.Historical;
import com.intel.context.Sensing;
import com.intel.context.auth.AuthCallback;
import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.sensing.InitCallback;
import com.intel.context.Auth;
import com.intel.context.exception.ContextException;
import com.intel.context.exception.ContextProviderException;
import com.intel.samples.contextsensingapiflow.listener.ActivityRecognitionListener;
import com.intel.samples.contextsensingapiflow.listener.AppsListener;
import com.intel.samples.contextsensingapiflow.listener.AudioClassificationListener;
import com.intel.samples.contextsensingapiflow.listener.BatteryListener;
import com.intel.samples.contextsensingapiflow.listener.CallListener;
import com.intel.samples.contextsensingapiflow.listener.DateListener;
import com.intel.samples.contextsensingapiflow.listener.IApplicationListener;
import com.intel.samples.contextsensingapiflow.listener.NetworkListener;
import com.intel.samples.contextsensingapiflow.listener.GeographicListener;
import com.intel.samples.contextsensingapiflow.listener.LocationListener;
import com.intel.samples.contextsensingapiflow.listener.MessageListener;
import com.intel.samples.contextsensingapiflow.listener.MusicListener;
import com.intel.samples.contextsensingapiflow.listener.PedometerListener;
import com.intel.samples.contextsensingapiflow.listener.PlaceListener;
import com.intel.samples.contextsensingapiflow.listener.TerminalContextListener;
import com.intel.samples.contextsensingapiflow.listener.WeatherListener;

public class ContextSensingApiFlowSampleActivity extends Activity {
    private final static String LOG_TAG = ContextSensingApiFlowSampleActivity.class.getName();    
    
    private Button startButton;
    private Button stopButton;
    
    private CheckBox enableLocation;
    private CheckBox enablePlace;
    private CheckBox enablePedometer;
    private CheckBox enableTerminalContext;
    private CheckBox enableActivity;
    private CheckBox enableAudio;
    private CheckBox enableApps;
    private CheckBox enableBattery;
    private CheckBox enableWeather;
    private CheckBox enableDate;
    private CheckBox enableGeographic;
    private CheckBox enableCall;
    private CheckBox enableMessage;
    private CheckBox enableMusic;
    private CheckBox enableNetwork;
    
    public static TextView mLocationText;
    public static TextView mPlaceText;
    public static TextView mPedometerText;
    public static TextView mTerminalContextText;
    public static TextView mActivityText;
    public static TextView mAudioText;
    public static TextView mAppsText;
    public static TextView mBatteryText;
    public static TextView mWeatherText;
    public static TextView mDateText;
    public static TextView mGeographicText;
    public static TextView mCallText;
    public static TextView mMessageText;
    public static TextView mMusicText;
    public static TextView mNetworkText;
    
    private Auth auth;
    private Sensing mySensing;
    
    private ActivityRecognitionListener mActivityRecognitionListener;
    private LocationListener mLocationListener;
    private PedometerListener mPedometerListener;
    private TerminalContextListener mTerminalContextListener;
    private PlaceListener mPlaceListener;
    private AudioClassificationListener mAudioListener;
    private AppsListener mAppsListener;
    private BatteryListener mBatteryListener;
    private WeatherListener mWeatherListener;
    private DateListener mDateListener;
    private GeographicListener mGeographicListener;
    private CallListener mCallListener;
    private MessageListener mMessageListener;
    private MusicListener mMusicListener;
    private NetworkListener mNetworkListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
        auth = Auth.getInstance(this);
        mySensing = ContextSensingApiFlowSampleApplication.getInstance().getSensing();
        
        mActivityRecognitionListener = ContextSensingApiFlowSampleApplication.getInstance().getActivityRecognitionListener();
        mLocationListener = ContextSensingApiFlowSampleApplication.getInstance().getLocationListener();
        mPedometerListener = ContextSensingApiFlowSampleApplication.getInstance().getPedometerListener();
        mTerminalContextListener = ContextSensingApiFlowSampleApplication.getInstance().getTerminalContextListener();
        mPlaceListener = ContextSensingApiFlowSampleApplication.getInstance().getPlaceListener();
        mAudioListener = ContextSensingApiFlowSampleApplication.getInstance().getAudioListener();
        mAppsListener = ContextSensingApiFlowSampleApplication.getInstance().getAppsListener();
        mBatteryListener = ContextSensingApiFlowSampleApplication.getInstance().getBatteryListener();
        mWeatherListener = ContextSensingApiFlowSampleApplication.getInstance().getWeatherListener();
        mDateListener = ContextSensingApiFlowSampleApplication.getInstance().getDateListener();
        mGeographicListener = ContextSensingApiFlowSampleApplication.getInstance().getGeographicListener();
        mCallListener = ContextSensingApiFlowSampleApplication.getInstance().getCallListener();
        mMessageListener = ContextSensingApiFlowSampleApplication.getInstance().getMessageListener();
        mMusicListener = ContextSensingApiFlowSampleApplication.getInstance().getMusicListener();
        mNetworkListener = ContextSensingApiFlowSampleApplication.getInstance().getNetworkListener();
        
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {                
                authorize();
            }});
        
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                stopDaemon();
                clearCheckBoxes();
            }});

        mLocationText = (TextView) findViewById(R.id.datalocation);
        mPlaceText = (TextView) findViewById(R.id.dataPlace);
        mPedometerText = (TextView) findViewById(R.id.dataPedometer);
        mTerminalContextText = (TextView) findViewById(R.id.dataTerminalContext);
        mActivityText = (TextView) findViewById(R.id.dataActivity);
        mAudioText = (TextView) findViewById(R.id.dataAudio);
        mAppsText =  (TextView) findViewById(R.id.dataApps);
        mBatteryText =  (TextView) findViewById(R.id.dataBattery);
        mWeatherText =  (TextView) findViewById(R.id.dataWeather);
        mDateText =  (TextView) findViewById(R.id.dataDate);
        mGeographicText =  (TextView) findViewById(R.id.dataGeographic);
        mCallText =  (TextView) findViewById(R.id.dataCall);
        mMessageText =  (TextView) findViewById(R.id.dataMessage);
        mMusicText =  (TextView) findViewById(R.id.dataMusic);
        mNetworkText =  (TextView) findViewById(R.id.dataNetwork);

        enableLocation = (CheckBox) findViewById(R.id.enableLocation);
        enableDisableProvider(enableLocation, 
                ContextType.LOCATION, 
                null, 
                ContextSensingApiFlowSampleActivity.mLocationText,
                mLocationListener,
                true);

        enablePedometer = (CheckBox) findViewById(R.id.enablePedomter);
        Bundle pedometerSettings = new Bundle();
        pedometerSettings.putString("MODE", "fast"); //fast, reports every 16 seconds
        enableDisableProvider(enablePedometer, 
                ContextType.PEDOMETER, 
                pedometerSettings, 
                ContextSensingApiFlowSampleActivity.mPedometerText,
                mPedometerListener,
                true);
        
        enableTerminalContext = (CheckBox) findViewById(R.id.enableTerminalContext);
        enableDisableProvider(enableTerminalContext, 
                ContextType.TERMINAL_CONTEXT, 
                null, 
                ContextSensingApiFlowSampleActivity.mTerminalContextText,
                mTerminalContextListener,
                true); 
        
        enableActivity = (CheckBox) findViewById(R.id.enableActivity);
        Bundle activitySettings = new Bundle();
        activitySettings.putString("MODE", "fast"); //fast, reports every 30 seconds
        enableDisableProvider(enableActivity, 
                ContextType.ACTIVITY_RECOGNITION, 
                activitySettings, 
                ContextSensingApiFlowSampleActivity.mActivityText,
                mActivityRecognitionListener,
                true);

        enableAudio = (CheckBox) findViewById(R.id.enableAudio);
        Bundle audioSettings = new Bundle();
        audioSettings.putLong("INTERVAL", 30); 
        enableDisableProvider(enableAudio, 
                ContextType.AUDIO, 
                audioSettings, 
                ContextSensingApiFlowSampleActivity.mAudioText,
                mAudioListener,
                true);
        
        enableApps = (CheckBox) findViewById(R.id.enableApps);
        enableDisableProvider(enableApps, 
                ContextType.APPS, 
                null, 
                ContextSensingApiFlowSampleActivity.mAppsText,
                mAppsListener,
                true);
        
        enableBattery = (CheckBox) findViewById(R.id.enableBattery);
        enableDisableProvider(enableBattery, 
                ContextType.BATTERY, 
                null, 
                ContextSensingApiFlowSampleActivity.mBatteryText,
                mBatteryListener,
                true);
        
        enablePlace = (CheckBox) findViewById(R.id.enablePlace);
        addListener(enablePlace, 
                ContextType.PLACE, 
                ContextSensingApiFlowSampleActivity.mPlaceText,
                mPlaceListener,
                true);
        
        enableWeather = (CheckBox) findViewById(R.id.enableWeather);
        addListener(enableWeather,
                ContextType.WEATHER,
                ContextSensingApiFlowSampleActivity.mWeatherText,
                mWeatherListener,
                true); 
        
        enableDate = (CheckBox) findViewById(R.id.enableDate);
        addListener(enableDate,
                ContextType.DATE,
                ContextSensingApiFlowSampleActivity.mDateText,
                mDateListener,
                true);
        
        enableGeographic = (CheckBox) findViewById(R.id.enableGeographic);
        addListener(enableGeographic,
                ContextType.GEOGRAPHIC,
                ContextSensingApiFlowSampleActivity.mGeographicText,
                mGeographicListener,
                true);
        
        enableCall = (CheckBox) findViewById(R.id.enableCall);
        enableDisableProvider(enableCall, 
                ContextType.CALL, 
                null, 
                ContextSensingApiFlowSampleActivity.mCallText,
                mCallListener,
                true);
        
        enableMessage = (CheckBox) findViewById(R.id.enableMessage);
        enableDisableProvider(enableMessage, 
                ContextType.MESSAGE, 
                null, 
                ContextSensingApiFlowSampleActivity.mMessageText,
                mMessageListener,
                true);
        
        enableMusic = (CheckBox) findViewById(R.id.enableMusic);
        enableDisableProvider(enableMusic, 
                ContextType.MUSIC, 
                null, 
                ContextSensingApiFlowSampleActivity.mMusicText,
                mMusicListener,
                true);

        enableNetwork = (CheckBox) findViewById(R.id.enableNetwork);
        enableDisableProvider(enableNetwork, 
                ContextType.NETWORK, 
                null, 
                ContextSensingApiFlowSampleActivity.mNetworkText,
                mNetworkListener,
                true);
        
        if (ContextSensingApiFlowSampleApplication.getInstance().isStarted()) {
            Log.i(LOG_TAG, "We have sensing started");
        } else {
            Log.i(LOG_TAG, "Clearing check boxes...");
            clearCheckBoxes();
        }
        
        populateStates();
        
        updateCheckBoxPreferences();
    }
    
    /*
     * Enable/Disable the sensing of a specific context type and add a listener
     * to receive state's updates.
     */
    private void enableDisableProvider(final CheckBox box, 
            final ContextType type, 
            final Bundle options, 
            final TextView text, 
            final IApplicationListener listener,
            final boolean needtoEnable) {
        box.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (box.isChecked()) {
                    try {
                        mySensing.addContextTypeListener(type, listener);
                        if (needtoEnable) {
                            mySensing.enableSensing(type, options);
                        }
                        saveCheckBoxPreference(box.getId());
                    } catch (ContextProviderException e) {
                        box.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Error enabling / adding listener to provider: " + e.getMessage(), Toast.LENGTH_LONG).show();                    
                        Log.e(LOG_TAG, "Error enabling provider: " + e.getMessage());
                    }
                } else {
                    removeCheckBoxPreference(box.getId());
                    try {
                        mySensing.removeContextTypeListener(listener);
                        if (needtoEnable) {
                            mySensing.disableSensing(type);
                        }
                        text.setText("None");
                        listener.setLastKnownItem(null);
                    } catch (ContextProviderException e) {
                        Toast.makeText(getApplicationContext(), "Error disabling provider: " + e.getMessage() , Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Error disabling provider: " + e.getMessage());
                    }
                }
            }
        });
    }
    
    /*
     * Just add/remove a context type listener (when enableSensing is not required).
     * Used for cloud based context states such as Weather, Date, Geographic, etc.
     */
    private void addListener(final CheckBox box,
            final ContextType type,
            final TextView text,
            final IApplicationListener listener,
            final boolean needtoEnable) {
        box.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (box.isChecked()) {
                    if (!ContextSensingApiFlowSampleApplication.getInstance().isStarted()) {
                        box.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Error adding listener to provider: Service needs to be started.", Toast.LENGTH_LONG).show();                    
                        Log.e(LOG_TAG, "Error adding listener: Service needs to be started.");
                    }
                    try {
                        if (needtoEnable) {
                            mySensing.addContextTypeListener(type, listener);
                        }
                        saveCheckBoxPreference(box.getId());
                    } catch (ContextProviderException e) {
                        box.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(), Toast.LENGTH_LONG).show();                    
                        Log.e(LOG_TAG, "Error adding listener: " + e.getMessage());
                    }
                } else {
                    removeCheckBoxPreference(box.getId());
                    try {
                        if (needtoEnable) {
                            mySensing.removeContextTypeListener(listener);
                        }
                        text.setText("None");
                        listener.setLastKnownItem(null);
                    } catch (ContextProviderException e) {
                        Toast.makeText(getApplicationContext(), "Error removing listener from provider: " + e.getMessage() , Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Error removing listener: " + e.getMessage());
                    }
                }
                
            }
        });
    }

    private void populateStates() {
        populateState(mLocationListener);
        populateState(mActivityRecognitionListener);
        populateState(mPedometerListener);
        populateState(mAppsListener);
        populateState(mBatteryListener);
        populateState(mCallListener);
        populateState(mMessageListener);
        populateState(mMusicListener);
        populateState(mAudioListener);
        populateState(mNetworkListener);
        populateState(mPlaceListener);
        populateState(mGeographicListener);
        populateState(mDateListener);
        populateState(mWeatherListener);
    }

    private void populateState(IApplicationListener listener) {
        if (listener.getLastKnownItem() != null) {
            listener.onReceive(listener.getLastKnownItem());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.trainHome:
            Toast.makeText(getApplicationContext(), "Training Home", Toast.LENGTH_LONG).show();
            setCurrentLocationByTime(22);
            return true;
        case R.id.trainWork:
            Toast.makeText(getApplicationContext(), "Training Work", Toast.LENGTH_LONG).show();
            setCurrentLocationByTime(9);
            return true;
        default:
          return super.onOptionsItemSelected(item);
        }
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
        
    
    private void authorize() {        
        if (!auth.isInit()) {
            auth.init(Settings.API_KEY, 
                    Settings.SECRET,
                    Settings.REDIRECT_URI,
                    "context:post:device:sensor context:post:location:detailed context:post:device:information context:geolocation:detailed context:time:detailed context:weather context:location:detailed context:post:device:applications:running context:post:device:status:battery context:time:detailed context:post:device:telephony context:post:device:personal context:post:media:consumption", 
                    new MyAuthCallback());
        } else {
            Log.w(LOG_TAG, "You are already authorized");
            startDaemon();
        }
            
    }
    
    private void removeAuthorization() {
        Auth auth = Auth.getInstance(this);
        auth.release();
    }
    
    
    private void startDaemon() {
        mySensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started" , Toast.LENGTH_SHORT).show();
                /* After successfully starting the Context Sensing Daemon, we can enable
                 * the sensing of context states such as activity recognition, location, etc.
                 */
                
                ContextSensingApiFlowSampleApplication.getInstance().start();
            }
            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void stopDaemon() {
        try {            
            mySensing.stop();
            removeAuthorization();
            ContextSensingApiFlowSampleApplication.getInstance().stop();
            
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }
    
    /*
     * This method forces the training of home/work based on the current Location state.
     * Use this method in order to simulate the 14 days required model training.
     */
    private void setCurrentLocationByTime(int hour) {
        if (mLocationListener.getLastKnownItem() == null) {
            Toast.makeText(getApplicationContext(),
                    "Location is null, please enable the sensing of Location before training home/work.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Historical historical = new Historical(getApplicationContext());    
        List<Item> items = new ArrayList<Item>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                hour,
                0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                hour,
                5);
        
        for (int i=0; i < 20; i++) {
            LocationCurrent item = new LocationCurrent();
            LocationCurrent lastKnown = (LocationCurrent) mLocationListener.getLastKnownItem();
            item.setActivity("urn:activity:in");
            item.setAccuracy(lastKnown.getAccuracy());
            item.setLocation(lastKnown.getLocation());
            item.setTimestamp(calendar.getTimeInMillis());
            
            LocationCurrent item2 = new LocationCurrent();
            item2.setActivity("urn:activity:in");
            item2.setAccuracy(lastKnown.getAccuracy());
            item2.setLocation(lastKnown.getLocation());
            item2.setTimestamp(calendar2.getTimeInMillis());
            
            items.add(item);
            items.add(item2);
            calendar.add(Calendar.DATE, -1);
            calendar2.add(Calendar.DATE, -1);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            Log.d(LOG_TAG, "Training: " + item.getActivity() + " Lat " 
                    + item.getLocation().getLatitude() + "Long:  " 
                    + item.getLocation().getLongitude() + " Time: " 
                    + df.format(item.getTimestamp()).concat("Z"));
        }
        try {
            historical.setItem(items);
        } catch (ContextException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }

    private void updateCheckBoxPreferences() {
        GridLayout grid = (GridLayout) findViewById(R.id.checkBoxesLocal_layout);
        for (int i=0; i < grid.getChildCount(); i++) {
            View element = grid.getChildAt(i);
            if (element instanceof CheckBox) {
                CheckBox box = (CheckBox) element;
                if (isCheckBoxPreference(box.getId())) {
                    box.setChecked(true);
                }
            }
        }

        grid = (GridLayout) findViewById(R.id.checkBoxesCloud_layout);
        for (int i=0; i < grid.getChildCount(); i++) {
            View element = grid.getChildAt(i);
            if (element instanceof CheckBox) {
                CheckBox box = (CheckBox) element;
                if (isCheckBoxPreference(box.getId())) {
                    box.setChecked(true);
                }
            }
        }    }
    
    private void removeAllCheckBoxPreferences() {
        GridLayout grid = (GridLayout) findViewById(R.id.checkBoxesLocal_layout);
        for (int i=0; i < grid.getChildCount(); i++) {
            View element = grid.getChildAt(i);
            if (element instanceof CheckBox) {
                CheckBox box = (CheckBox) element;
                removeCheckBoxPreference(box.getId());
            }
        }

        grid = (GridLayout) findViewById(R.id.checkBoxesCloud_layout);
        for (int i=0; i < grid.getChildCount(); i++) {
            View element = grid.getChildAt(i);
            if (element instanceof CheckBox) {
                CheckBox box = (CheckBox) element;
                removeCheckBoxPreference(box.getId());
            }
        }    }
    
    private void saveCheckBoxPreference(int id) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(String.valueOf(id), true);
        editor.commit();
    }
    
    private void removeCheckBoxPreference(int id) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(String.valueOf(id));
        editor.commit();
    }
    
    private void clearCheckBoxes() {
        enableLocation.setChecked(false);
        enablePlace.setChecked(false);
        enablePedometer.setChecked(false);
        enableActivity.setChecked(false);
        enableAudio.setChecked(false);
        enableApps.setChecked(false);
        enableBattery.setChecked(false);
        enableWeather.setChecked(false);
        enableDate.setChecked(false);
        enableGeographic.setChecked(false);
        enableCall.setChecked(false);
        enableMessage.setChecked(false);
        enableMusic.setChecked(false);
        enableNetwork.setChecked(false);
        
        removeAllCheckBoxPreferences();
        
        ContextSensingApiFlowSampleActivity.mLocationText.setText("None");
        ContextSensingApiFlowSampleActivity.mPedometerText.setText("None");
        ContextSensingApiFlowSampleActivity.mPlaceText.setText("None");
        ContextSensingApiFlowSampleActivity.mActivityText.setText("None");
        ContextSensingApiFlowSampleActivity.mAudioText.setText("None");
        ContextSensingApiFlowSampleActivity.mAppsText.setText("None");
        ContextSensingApiFlowSampleActivity.mBatteryText.setText("None");
        ContextSensingApiFlowSampleActivity.mWeatherText.setText("None");
        ContextSensingApiFlowSampleActivity.mDateText.setText("None");
        ContextSensingApiFlowSampleActivity.mGeographicText.setText("None");
        ContextSensingApiFlowSampleActivity.mCallText.setText("None");
        ContextSensingApiFlowSampleActivity.mMessageText.setText("None");
        ContextSensingApiFlowSampleActivity.mMusicText.setText("None");
        ContextSensingApiFlowSampleActivity.mNetworkText.setText("None");

        mLocationListener.setLastKnownItem(null);
        mActivityRecognitionListener.setLastKnownItem(null);
        mPlaceListener.setLastKnownItem(null);
        mPedometerListener.setLastKnownItem(null);
        mAppsListener.setLastKnownItem(null);
        mBatteryListener.setLastKnownItem(null);
        mWeatherListener.setLastKnownItem(null);
        mDateListener.setLastKnownItem(null);
        mGeographicListener.setLastKnownItem(null);
        mCallListener.setLastKnownItem(null);
        mMessageListener.setLastKnownItem(null);
        mMusicListener.setLastKnownItem(null);
        mNetworkListener.setLastKnownItem(null);
    }
    
    private boolean isCheckBoxPreference(int id) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        boolean checkBoxPreference = sharedPref.getBoolean(String.valueOf(id), false);
        return checkBoxPreference;
    }
    
    
    
    
    private class MyAuthCallback implements AuthCallback {

        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), "Init Success", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Init Success");
            // After init, let's start Context Sensing               
            startDaemon();
        }

        @Override
        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(), "Init Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onExpired() {
            // TODO Auto-generated method stub
            
        }
    }
}
