package com.intel.samples.contextsensingapiflow;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;
import com.intel.samples.contextsensingapiflow.listener.ActivityRecognitionListener;
import com.intel.samples.contextsensingapiflow.listener.AppsListener;
import com.intel.samples.contextsensingapiflow.listener.AudioClassificationListener;
import com.intel.samples.contextsensingapiflow.listener.BatteryListener;
import com.intel.samples.contextsensingapiflow.listener.CallListener;
import com.intel.samples.contextsensingapiflow.listener.DateListener;
import com.intel.samples.contextsensingapiflow.listener.GeographicListener;
import com.intel.samples.contextsensingapiflow.listener.LocationListener;
import com.intel.samples.contextsensingapiflow.listener.MessageListener;
import com.intel.samples.contextsensingapiflow.listener.MusicListener;
import com.intel.samples.contextsensingapiflow.listener.NetworkListener;
import com.intel.samples.contextsensingapiflow.listener.PedometerListener;
import com.intel.samples.contextsensingapiflow.listener.PlaceListener;
import com.intel.samples.contextsensingapiflow.listener.TerminalContextListener;
import com.intel.samples.contextsensingapiflow.listener.WeatherListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class ContextSensingApiFlowSampleApplication extends Application {
    private Sensing mSensing;
    private static ContextSensingApiFlowSampleApplication mInstance;
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
    
    
    private boolean mServiceStarted = false;
    
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
        mActivityRecognitionListener = new ActivityRecognitionListener(getApplicationContext());
        mLocationListener = new LocationListener(getApplicationContext());
        mPedometerListener = new PedometerListener(getApplicationContext());
        mTerminalContextListener = new TerminalContextListener(getApplicationContext()); 
        mPlaceListener = new PlaceListener(getApplicationContext());   
        mAudioListener = new AudioClassificationListener(getApplicationContext());
        mAppsListener = new AppsListener(getApplicationContext());
        mBatteryListener = new BatteryListener(getApplicationContext());
        mWeatherListener = new WeatherListener(getApplicationContext());
        mDateListener = new DateListener(getApplicationContext());
        mGeographicListener = new GeographicListener(getApplicationContext());
        mCallListener = new CallListener(getApplicationContext());
        mMessageListener = new MessageListener(getApplicationContext());
        mMusicListener = new MusicListener(getApplicationContext());
        mNetworkListener = new NetworkListener(getApplicationContext());
    }
    
    public static ContextSensingApiFlowSampleApplication getInstance() {
        return mInstance;
    }

    public Sensing getSensing() {
        return mSensing;
    }

    public ActivityRecognitionListener getActivityRecognitionListener() {
        return mActivityRecognitionListener;
    }

    public LocationListener getLocationListener() {
        return mLocationListener;
    }

    public PedometerListener getPedometerListener() {
        return mPedometerListener;
    }
    
    public TerminalContextListener getTerminalContextListener() {
        return mTerminalContextListener;
    }

    
    public PlaceListener getPlaceListener() {
        return mPlaceListener;
    }
    
    public AudioClassificationListener getAudioListener() {
        return mAudioListener;
    }

    public AppsListener getAppsListener() {
        return mAppsListener;
    }
    
    public BatteryListener getBatteryListener() {
        return mBatteryListener;
    }
    
    public WeatherListener getWeatherListener() {
        return mWeatherListener;
    }
    
    public DateListener getDateListener() {
        return mDateListener;
    }
    
    public GeographicListener getGeographicListener() {
        return mGeographicListener;
    }
    
    public CallListener getCallListener() {
        return mCallListener;
    }
    
    public MessageListener getMessageListener() {
        return mMessageListener;
    }
    
    public MusicListener getMusicListener() {
        return mMusicListener;
    }
    
    public NetworkListener getNetworkListener() {
        return mNetworkListener;
    }
    
    public void start() {
        mServiceStarted = true;
    }
    
    public void stop() {
        mServiceStarted = false;
    }
    
    public boolean isStarted() {
        return mServiceStarted;
    }
    
    /*
     * Listener to receive updates from Context Sensing Daemon.
     */
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
}
