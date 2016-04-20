package sk.henrichg.phoneprofilesplus;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;


public class PhoneProfilesService extends Service
                                    implements SensorEventListener
{

    private final BatteryEventBroadcastReceiver batteryEventReceiver = new BatteryEventBroadcastReceiver();
    private final HeadsetConnectionBroadcastReceiver headsetPlugReceiver = new HeadsetConnectionBroadcastReceiver();
    //private final RestartEventsBroadcastReceiver restartEventsReceiver = new RestartEventsBroadcastReceiver();
    //private final WifiStateChangedBroadcastReceiver wifiStateChangedReceiver = new WifiStateChangedBroadcastReceiver();
    //private final WifiConnectionBroadcastReceiver wifiConnectionReceiver = new WifiConnectionBroadcastReceiver();
    private final ScreenOnOffBroadcastReceiver screenOnOffReceiver = new ScreenOnOffBroadcastReceiver();
    //private final BluetoothStateChangedBroadcastReceiver bluetoothStateChangedReceiver = new BluetoothStateChangedBroadcastReceiver();
    private DeviceIdleModeBroadcastReceiver deviceIdleModeReceiver = null;
    private PowerSaveModeBroadcastReceiver powerSaveModeReceiver = null;

    private static SettingsContentObserver settingsContentObserver = null;

    public static SensorManager mSensorManager = null;
    public static boolean mStarted = false;

    //private float mGZ = 0; //gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 7;

    private final float alpha = (float) 0.8;
    private float mGravity[] = new float[3];
    private float mGeomagnetic[] = new float[3];
    private float mProximity = -100;
    private float mMaxProximityDistance;

    public static final int DEVICE_ORIENTATION_UNKNOWN = 0;
    public static final int DEVICE_ORIENTATION_DISPLAY_UP = 1;
    public static final int DEVICE_ORIENTATION_DISPLAY_DOWN = 2;
    public static final int DEVICE_ORIENTATION_RIGHT_SIDE_UP = 3;
    public static final int DEVICE_ORIENTATION_LEFT_SIDE_UP = 4;
    public static final int DEVICE_ORIENTATION_UP_SIDE_UP = 5;
    public static final int DEVICE_ORIENTATION_DOWN_SIDE_UP = 6;

    public static final int DEVICE_ORIENTATION_DEVICE_IS_NEAR = 7;
    public static final int DEVICE_ORIENTATION_DEVICE_IS_FAR = 8;

    private static int mDisplayUp = DEVICE_ORIENTATION_UNKNOWN;
    private static int mSideUp = DEVICE_ORIENTATION_UNKNOWN;
    private static int mDeviceDistance = DEVICE_ORIENTATION_UNKNOWN;

    private static int mTmpSideUp = DEVICE_ORIENTATION_UNKNOWN;

    @Override
    public void onCreate()
    {
        GlobalData.logE("$$$ PhoneProfilesService.onCreate", "xxxxx");

        GlobalData.phoneProfilesService = this;

        // start service for first start
        Intent eventsServiceIntent = new Intent(getApplicationContext(), FirstStartService.class);
        getApplicationContext().startService(eventsServiceIntent);
        
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Intent.ACTION_BATTERY_CHANGED);
        getApplicationContext().registerReceiver(batteryEventReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter();
        for (String action: HeadsetConnectionBroadcastReceiver.HEADPHONE_ACTIONS) {
            intentFilter2.addAction(action);
        }
        getApplicationContext().registerReceiver(headsetPlugReceiver, intentFilter2);

        /*
        IntentFilter intentFilter7 = new IntentFilter();
        intentFilter7.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateChangedReceiver, intentFilter7);

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionReceiver, intentFilter3);
        */

        IntentFilter intentFilter5 = new IntentFilter();
        intentFilter5.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter5.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter5.addAction(Intent.ACTION_USER_PRESENT);
        getApplicationContext().registerReceiver(screenOnOffReceiver, intentFilter5);

        /*
        IntentFilter intentFilter8 = new IntentFilter();
        intentFilter8.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateChangedReceiver, intentFilter8);
        */

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            deviceIdleModeReceiver = new DeviceIdleModeBroadcastReceiver();
            IntentFilter intentFilter9 = new IntentFilter();
            intentFilter9.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
            getApplicationContext().registerReceiver(deviceIdleModeReceiver, intentFilter9);
        }

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            powerSaveModeReceiver = new PowerSaveModeBroadcastReceiver();
            IntentFilter intentFilter10 = new IntentFilter();
            intentFilter10.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
            getApplicationContext().registerReceiver(powerSaveModeReceiver, intentFilter10);
        }

        /*
        // receivers for system date and time change
        // events must by restarted
        IntentFilter intentFilter99 = new IntentFilter();
        intentFilter99.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter99.addAction(Intent.ACTION_TIME_CHANGED);
        getApplicationContext().registerReceiver(restartEventsReceiver, intentFilter99);
        */

        //SMSBroadcastReceiver.registerSMSContentObserver(this);
        //SMSBroadcastReceiver.registerMMSContentObserver(this);

        if (settingsContentObserver != null)
            getContentResolver().unregisterContentObserver(settingsContentObserver);
        settingsContentObserver = new SettingsContentObserver(this, new Handler(getMainLooper()));
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);

        GlobalData.startGeofenceScanner(getApplicationContext());
        GlobalData.startOrientationScanner(getApplicationContext());
    }

    @Override
    public void onDestroy()
    {
        GlobalData.logE("PhoneProfilesService.onDestroy", "xxxxx");

        getApplicationContext().unregisterReceiver(batteryEventReceiver);
        getApplicationContext().unregisterReceiver(headsetPlugReceiver);
        //unregisterReceiver(wifiStateChangedReceiver);
        //unregisterReceiver(wifiConnectionReceiver);
        getApplicationContext().unregisterReceiver(screenOnOffReceiver);
        //unregisterReceiver(bluetoothStateChangedReceiver);
        //getApplicationContext().unregisterReceiver(restartEventsReceiver);
        if (android.os.Build.VERSION.SDK_INT >= 23)
            getApplicationContext().unregisterReceiver(deviceIdleModeReceiver);
        if (android.os.Build.VERSION.SDK_INT >= 21)
            getApplicationContext().unregisterReceiver(powerSaveModeReceiver);

        //SMSBroadcastReceiver.unregisterSMSContentObserver(this);
        //SMSBroadcastReceiver.unregisterMMSContentObserver(this);

        if (settingsContentObserver != null)
            getContentResolver().unregisterContentObserver(settingsContentObserver);

        GlobalData.stopGeofenceScanner();
        GlobalData.phoneProfilesService = null;

    }

    public void startListeningSensors() {
        if (mSensorManager == null)
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) &&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {

            if (!mStarted) {
                mSensorManager.registerListener(this,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        1000000);//SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        1000000);//SensorManager.SENSOR_DELAY_NORMAL);

                mStarted = true;
            }
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)) {
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mMaxProximityDistance = sensor.getMaximumRange();
            mSensorManager.registerListener(this, sensor,
                    1000000);//SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopListeningSensors() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
        mStarted = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        GlobalData.logE("$$$ PhoneProfilesService.onStartCommand", "xxxxx");

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            GlobalData.logE("PhoneProfilesService.onSensorChanged", "proximity value="+event.values[0]);
            if ((event.values[0] == 0) || (event.values[0] == mMaxProximityDistance)) {
                if (event.values[0] != mProximity) {
                    mProximity = event.values[0];
                    if (mProximity == 0) {
                        mDeviceDistance = DEVICE_ORIENTATION_DEVICE_IS_NEAR;
                        GlobalData.logE("PhoneProfilesService.onSensorChanged", "now device is near.");
                    } else {
                        mDeviceDistance = DEVICE_ORIENTATION_DEVICE_IS_FAR;
                        GlobalData.logE("PhoneProfilesService.onSensorChanged", "now device is far");
                    }
                    Intent broadcastIntent = new Intent(this, DeviceOrientationBroadcastReceiver.class);
                    sendBroadcast(broadcastIntent);
                }
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate the force of gravity with the low-pass filter.
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic[0] = event.values[0];
            mGeomagnetic[1] = event.values[1];
            mGeomagnetic[2] = event.values[2];
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                //orientation[0]: azimuth, rotation around the -Z axis, i.e. the opposite direction of Z axis.
                //orientation[1]: pitch, rotation around the -X axis, i.e the opposite direction of X axis.
                //orientation[2]: roll, rotation around the Y axis.

                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, I);
                SensorManager.getOrientation(I, orientation);

                //float azimuth = (float)Math.toDegrees(orientation[0]);
                float pitch = (float)Math.toDegrees(orientation[1]);
                float roll = (float)Math.toDegrees(orientation[2]);

                //GlobalData.logE("PhoneProfilesService.onSensorChanged", "pitch=" + pitch);
                //GlobalData.logE("PhoneProfilesService.onSensorChanged", "roll=" + roll);

                int side = DEVICE_ORIENTATION_UNKNOWN;
                if (pitch > -30 && pitch < 30) {
                    if (roll > -60 && roll < 60)
                        side = DEVICE_ORIENTATION_DISPLAY_UP;
                    if (roll > 150 && roll < 180)
                        side = DEVICE_ORIENTATION_DISPLAY_DOWN;
                    if (roll > -180 && roll < -150)
                        side = DEVICE_ORIENTATION_DISPLAY_DOWN;
                    if (roll > 65 && roll < 115)
                        side = DEVICE_ORIENTATION_UP_SIDE_UP;
                    if (roll > -115 && roll < -65)
                        side = DEVICE_ORIENTATION_DOWN_SIDE_UP;
                }
                if (pitch > 30 && pitch < 90) {
                    side = DEVICE_ORIENTATION_LEFT_SIDE_UP;
                }
                if (pitch > -90 && pitch < -30) {
                    side = DEVICE_ORIENTATION_RIGHT_SIDE_UP;
                }

                if ((mTmpSideUp == DEVICE_ORIENTATION_UNKNOWN) || (/*(side != DEVICE_ORIENTATION_UNKNOWN) &&*/ (side != mTmpSideUp))) {
                    mEventCountSinceGZChanged = 0;

                    //GlobalData.logE("PhoneProfilesService.onSensorChanged", "azimuth="+azimuth);
                    //GlobalData.logE("PhoneProfilesService.onSensorChanged", "pitch=" + pitch);
                    //GlobalData.logE("PhoneProfilesService.onSensorChanged", "roll=" + roll);

                    mTmpSideUp = side;
                }
                else {
                    ++mEventCountSinceGZChanged;
                    if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {

                        if (mTmpSideUp != mSideUp) {

                            mSideUp = mTmpSideUp;

                            if ((mSideUp == DEVICE_ORIENTATION_DISPLAY_UP) || (mSideUp == DEVICE_ORIENTATION_DISPLAY_DOWN))
                                mDisplayUp = mSideUp;

                            if (mDisplayUp == DEVICE_ORIENTATION_DISPLAY_UP)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now screen is facing up.");
                            if (mDisplayUp == DEVICE_ORIENTATION_DISPLAY_DOWN)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now screen is facing down.");

                            if (mSideUp == DEVICE_ORIENTATION_UP_SIDE_UP)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now up side is facing up.");
                            if (mSideUp == DEVICE_ORIENTATION_DOWN_SIDE_UP)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now down side is facing up.");
                            if (mSideUp == DEVICE_ORIENTATION_RIGHT_SIDE_UP)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now right side is facing up.");
                            if (mSideUp == DEVICE_ORIENTATION_LEFT_SIDE_UP)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "now left side is facing up.");
                            if (mSideUp == DEVICE_ORIENTATION_UNKNOWN)
                                GlobalData.logE("PhoneProfilesService.onSensorChanged", "unknown side.");

                            Intent broadcastIntent = new Intent(this, DeviceOrientationBroadcastReceiver.class);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        GlobalData.logE("$$$ PhoneProfilesService.onTaskRemoved", "xxxxx");

        ActivateProfileHelper.screenTimeoutUnlock(getApplicationContext());
        super.onTaskRemoved(rootIntent);
    }

}
