package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class BluetoothScanAlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "bluetoothScanAlarm";
    public static final String EXTRA_ONESHOT = "oneshot";

    public static BluetoothAdapter bluetooth = null;

    private static List<BluetoothDeviceData> tmpScanLEResults = null;


    public void onReceive(Context context, Intent intent) {

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        PPApplication.logE("##### BluetoothScanAlarmBroadcastReceiver.onReceive", "xxx");

        PPApplication.loadPreferences(context);

        setAlarm(context, false, false);

        if (PPApplication.isEventPreferenceAllowed(EventPreferencesBluetooth.PREF_EVENT_BLUETOOTH_ENABLED, context) !=
                PPApplication.PREFERENCE_ALLOWED) {
            removeAlarm(context);
            return;
        }

        if (bluetooth == null)
            bluetooth = getBluetoothAdapter(context);

        if (PPApplication.getGlobalEventsRuning(context))
        {
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive", "xxx");

            startScanner(context);
        }

    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothAdapter adapter;
        if (android.os.Build.VERSION.SDK_INT < 18)
            adapter = BluetoothAdapter.getDefaultAdapter();
        else {
            BluetoothManager bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }

    public static void initialize(Context context)
    {
        setScanRequest(context, false);
        setLEScanRequest(context, false);
        setWaitForResults(context, false);
        setWaitForLEResults(context, false);
        setBluetoothEnabledForScan(context, false);

        if (PPApplication.isEventPreferenceAllowed(EventPreferencesBluetooth.PREF_EVENT_BLUETOOTH_ENABLED, context) !=
                PPApplication.PREFERENCE_ALLOWED)
            return;

        if (bluetooth == null)
            bluetooth = getBluetoothAdapter(context);
        if (bluetooth == null)
            return;

        unlock();

        clearScanResults(context);

        /*SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(PPApplication.PREF_EVENT_BLUETOOTH_LAST_STATE, -1);
        editor.commit();*/

        if (bluetooth.isEnabled())
        {
            fillBoundedDevicesList(context);
        }

    }

    @SuppressLint("NewApi")
    public static void setAlarm(Context context, boolean shortInterval, boolean forScreenOn)
    {
        //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm", "oneshot=" + oneshot);

        if (PPApplication.isEventPreferenceAllowed(EventPreferencesBluetooth.PREF_EVENT_BLUETOOTH_ENABLED, context)
                == PPApplication.PREFERENCE_ALLOWED)
        {
            PPApplication.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=true");

            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);

            removeAlarm(context);

            Calendar calendar = Calendar.getInstance();

            //SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
            //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));

            if (shortInterval) {
                if (forScreenOn)
                    calendar.add(Calendar.SECOND, 5);
                else
                    calendar.add(Calendar.SECOND, 5);
            }
            else {
                int interval = PPApplication.applicationEventBluetoothScanInterval;
                boolean isPowerSaveMode = DataWrapper.isPowerSaveMode();
                if (isPowerSaveMode && PPApplication.applicationEventBluetoothScanInPowerSaveMode.equals("1"))
                    interval = 2 * interval;
                calendar.add(Calendar.MINUTE, interval);
            }
            long alarmTime = calendar.getTimeInMillis();

            intent.putExtra(EXTRA_ONESHOT, 0);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            if (PPApplication.exactAlarms && (android.os.Build.VERSION.SDK_INT >= 23))
                //alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
                alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            else if (PPApplication.exactAlarms && (android.os.Build.VERSION.SDK_INT >= 19))
                //alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            else
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);

            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm", "alarm is set");

        }
        else
            PPApplication.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=false");
    }

    public static void removeAlarm(Context context/*, boolean oneshot*/)
    {
        //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm", "oneshot=" + oneshot);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent;
        //if (oneshot)
        //    pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
        //else
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
            //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm found");
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","alarm found");

            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        else
            //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm not found");
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","alarm not found");
    }

    public static boolean isAlarmSet(Context context/*, boolean oneshot*/)
    {
        Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent;
        //if (oneshot)
        //    pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
        //else
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
            //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","oneshot="+oneshot+"; alarm found");
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","alarm found");
        else
            //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet", "oneshot=" + oneshot + "; alarm not found");
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet", "alarm not found");

        return (pendingIntent != null);
    }

    public static void lock(Context context)
    {
         // initialise the locks - moved to ScannerService
        /*if (wakeLock == null)
            wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BluetoothScanWakeLock");

        try {
            if (!wakeLock.isHeld())
                wakeLock.acquire();
        //	PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.lock","xxx");
        } catch(Exception e) {
            Log.e("BluetoothScanAlarmBroadcastReceiver.lock", "Error getting Lock: "+e.getMessage());
        }*/
    }
 
    public static void unlock()
    {
        /*if ((wakeLock != null) && (wakeLock.isHeld()))
            wakeLock.release();*/
        //PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.unlock","xxx");
    }
    
    public static void sendBroadcast(Context context)
    {
        Intent broadcastIntent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
        context.sendBroadcast(broadcastIntent);
    }
    
    static public boolean getScanRequest(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PPApplication.PREF_EVENT_BLUETOOTH_SCAN_REQUEST, false);
    }

    static public void setScanRequest(Context context, boolean startScan)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(PPApplication.PREF_EVENT_BLUETOOTH_SCAN_REQUEST, startScan);
        editor.commit();
    }

    static public boolean getLEScanRequest(Context context)
    {
        if (ScannerService.bluetoothLESupported(context)) {
            SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
            return preferences.getBoolean(PPApplication.PREF_EVENT_BLUETOOTH_LE_SCAN_REQUEST, false);
        }
        else
            return false;
    }

    static public void setLEScanRequest(Context context, boolean startScan)
    {
        if (ScannerService.bluetoothLESupported(context)) {
            SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            editor.putBoolean(PPApplication.PREF_EVENT_BLUETOOTH_LE_SCAN_REQUEST, startScan);
            editor.commit();
        }
    }

    static public boolean getWaitForResults(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PPApplication.PREF_EVENT_BLUETOOTH_WAIT_FOR_RESULTS, false);
    }

    static public void setWaitForResults(Context context, boolean startScan)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(PPApplication.PREF_EVENT_BLUETOOTH_WAIT_FOR_RESULTS, startScan);
        editor.commit();
    }

    static public boolean getWaitForLEResults(Context context)
    {
        if (ScannerService.bluetoothLESupported(context)) {
            SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
            return preferences.getBoolean(PPApplication.PREF_EVENT_BLUETOOTH_WAIT_FOR_LE_RESULTS, false);
        }
        else
            return false;
    }

    static public void setWaitForLEResults(Context context, boolean startScan)
    {
        if (ScannerService.bluetoothLESupported(context)) {
            SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            editor.putBoolean(PPApplication.PREF_EVENT_BLUETOOTH_WAIT_FOR_LE_RESULTS, startScan);
            editor.commit();
        }
    }

    static public void startCLScan(Context context)
    {
        if (bluetooth == null)
            bluetooth = getBluetoothAdapter(context);

        if (bluetooth.isDiscovering())
            bluetooth.cancelDiscovery();

        BluetoothScanBroadcastReceiver.discoveryStarted = false;

        if (Permissions.checkLocation(context)) {
            lock(context); // lock wakeLock, then scan.
            // unlock() is then called at the end of the scan from ScannerService

            boolean startScan = bluetooth.startDiscovery();
            PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.startScan", "scanStarted=" + startScan);

            if (!startScan) {
                unlock();
                if (getBluetoothEnabledForScan(context)) {
                    PPApplication.logE("@@@ BluetoothScanAlarmBroadcastReceiver.startScan", "disable bluetooth");
                    bluetooth.disable();
                }
            }
            setWaitForResults(context, startScan);
        }
        setScanRequest(context, false);
    }

    static public void stopCLScan(Context context) {
        if (bluetooth == null)
            bluetooth = getBluetoothAdapter(context);
        if (bluetooth.isDiscovering())
            bluetooth.cancelDiscovery();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    static public void startLEScan(Context context)
    {
        if (ScannerService.bluetoothLESupported(context)) {

            //BluetoothScanBroadcastReceiver.initTmpScanResults();

            if (bluetooth == null)
                bluetooth = getBluetoothAdapter(context);

            if (Permissions.checkLocation(context)) {
                if ((android.os.Build.VERSION.SDK_INT >= 21)) {
                    if (ScannerService.leScanner == null)
                        ScannerService.leScanner = bluetooth.getBluetoothLeScanner();
                    if (ScannerService.leScanCallback21 == null)
                        ScannerService.leScanCallback21 = new BluetoothLEScanCallback21(context);

                    //ScannerService.leScanner.stopScan(ScannerService.leScanCallback21);

                    lock(context); // lock wakeLock, then scan.
                    // unlock() is then called at the end of the scan from ScannerService

                    ScanSettings.Builder builder = new ScanSettings.Builder();

                    int forceScan = PPApplication.getForceOneBluetoothScan(context);
                    if (forceScan == PPApplication.FORCE_ONE_SCAN_FROM_PREF_DIALOG)
                        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
                    else
                        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

                    if (bluetooth.isOffloadedScanBatchingSupported())
                        builder.setReportDelay(PPApplication.applicationEventBluetoothLEScanDuration * 1000);
                    ScanSettings settings = builder.build();

                    List<ScanFilter> filters = new ArrayList<>();
                    ScannerService.leScanner.startScan(filters, settings, ScannerService.leScanCallback21);
                }
                else {
                    if (ScannerService.leScanCallback18 == null)
                        ScannerService.leScanCallback18 = new BluetoothLEScanCallback18(context);

                    //bluetooth.stopLeScan(ScannerService.leScanCallback18);

                    lock(context); // lock wakeLock, then scan.
                    // unlock() is then called at the end of the scan from ScannerService

                    boolean startScan = bluetooth.startLeScan(ScannerService.leScanCallback18);

                    if (!startScan) {
                        unlock();
                        if (getBluetoothEnabledForScan(context)) {
                            bluetooth.disable();
                        }
                    }
                }

                setWaitForLEResults(context, true); //startScan);
            }
            setLEScanRequest(context, false);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    static public void stopLEScan(Context context) {
        if (ScannerService.bluetoothLESupported(context)) {
            if (bluetooth == null)
                bluetooth = getBluetoothAdapter(context);

            if (bluetooth.getState() == BluetoothAdapter.STATE_ON) {
                if ((android.os.Build.VERSION.SDK_INT >= 21)) {
                    if (ScannerService.leScanner == null)
                        ScannerService.leScanner = bluetooth.getBluetoothLeScanner();
                    if (ScannerService.leScanCallback21 == null)
                        ScannerService.leScanCallback21 = new BluetoothLEScanCallback21(context);
                    ScannerService.leScanner.stopScan(ScannerService.leScanCallback21);
                } else {
                    if (ScannerService.leScanCallback18 == null)
                        ScannerService.leScanCallback18 = new BluetoothLEScanCallback18(context);
                    bluetooth.stopLeScan(ScannerService.leScanCallback18);
                }
            }
        }
    }

    static public void finishLEScan(Context context) {
        PPApplication.logE("BluetoothScanBroadcastReceiver.finishLEScan","xxx");

        if (tmpScanLEResults != null) {

            List<BluetoothDeviceData> scanResults = new ArrayList<>();

            for (BluetoothDeviceData device : tmpScanLEResults) {
                scanResults.add(new BluetoothDeviceData(device.getName(), device.address, device.type, false));
            }
            tmpScanLEResults = null;

            /*
            if (BluetoothScanAlarmBroadcastReceiver.scanResults != null)
            {
                for (BluetoothDevice device : BluetoothScanAlarmBroadcastReceiver.scanResults)
                {
                    PPApplication.logE("BluetoothScanBroadcastReceiver.onReceive","device.name="+device.getName());
                }
            }
            */

            BluetoothScanAlarmBroadcastReceiver.saveScanResults(context, scanResults);
        }
    }

    static public void startScanner(Context context)
    {
        Intent scanServiceIntent = new Intent(context, ScannerService.class);
        scanServiceIntent.putExtra(PPApplication.EXTRA_SCANNER_TYPE, PPApplication.SCANNER_TYPE_BLUETOOTH);
        context.startService(scanServiceIntent);
    }

    /*
    static public void stopScan(Context context)
    {
        unlock();
        if (getBluetoothEnabledForScan(context))
            bluetooth.disable();
        setBluetoothEnabledForScan(context, false);
        setScanRequest(context, false);
        setWaitForResults(context, false);
        PPApplication.setForceOneBluetoothScan(context, false);
    }
    */

    static public boolean getBluetoothEnabledForScan(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PPApplication.PREF_EVENT_BLUETOOTH_ENABLED_FOR_SCAN, false);
    }

    static public void setBluetoothEnabledForScan(Context context, boolean setEnabled)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(PPApplication.PREF_EVENT_BLUETOOTH_ENABLED_FOR_SCAN, setEnabled);
        editor.commit();
    }

    static public int getBluetoothType(BluetoothDevice device) {
        if (android.os.Build.VERSION.SDK_INT >= 18)
            return device.getType();
        else
            return 1; // BluetoothDevice.DEVICE_TYPE_CLASSIC
    }

    static public void fillBoundedDevicesList(Context context)
    {
        //if (boundedDevicesList == null)
        //    boundedDevicesList = new ArrayList<BluetoothDeviceData>();

        List<BluetoothDeviceData> boundedDevicesList  = new ArrayList<>();

        if (bluetooth == null)
            bluetooth = getBluetoothAdapter(context);

        Set<BluetoothDevice> boundedDevices = BluetoothScanAlarmBroadcastReceiver.bluetooth.getBondedDevices();
        boundedDevicesList.clear();
        for (BluetoothDevice device : boundedDevices)
        {
            boundedDevicesList.add(new BluetoothDeviceData(device.getName(), device.getAddress(),
                    BluetoothScanAlarmBroadcastReceiver.getBluetoothType(device), false));
        }
        saveBoundedDevicesList(context, boundedDevicesList);
    }

    private static final String SCAN_RESULT_COUNT_PREF = "count";
    private static final String SCAN_RESULT_DEVICE_PREF = "device";

    //public static void getBoundedDevicesList(Context context)
    public static List<BluetoothDeviceData> getBoundedDevicesList(Context context)
    {
        //if (boundedDevicesList == null)
        //    boundedDevicesList = new ArrayList<BluetoothDeviceData>();

        //boundedDevicesList.clear();

        List<BluetoothDeviceData> boundedDevicesList  = new ArrayList<>();

        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_BOUNDED_DEVICES_LIST_PREFS_NAME, Context.MODE_PRIVATE);

        int count = preferences.getInt(SCAN_RESULT_COUNT_PREF, 0);

        Gson gson = new Gson();

        for (int i = 0; i < count; i++)
        {
            String json = preferences.getString(SCAN_RESULT_DEVICE_PREF + i, "");
            if (!json.isEmpty()) {
                BluetoothDeviceData device = gson.fromJson(json, BluetoothDeviceData.class);
                boundedDevicesList.add(device);
            }
        }

        return boundedDevicesList;
    }

    private static void saveBoundedDevicesList(Context context, List<BluetoothDeviceData> boundedDevicesList)
    {
        //if (boundedDevicesList == null)
        //    boundedDevicesList = new ArrayList<BluetoothDeviceData>();

        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_BOUNDED_DEVICES_LIST_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putInt(SCAN_RESULT_COUNT_PREF, boundedDevicesList.size());

        Gson gson = new Gson();

        for (int i = 0; i < boundedDevicesList.size(); i++)
        {
            String json = gson.toJson(boundedDevicesList.get(i));
            editor.putString(SCAN_RESULT_DEVICE_PREF+i, json);
        }

        editor.commit();
    }

    //public static void getScanResults(Context context)
    public static List<BluetoothDeviceData> getScanResults(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);
        int count = preferences.getInt(SCAN_RESULT_COUNT_PREF, -1);

        if (count >= 0) {
            List<BluetoothDeviceData> scanResults = new ArrayList<>();

            Gson gson = new Gson();

            for (int i = 0; i < count; i++) {
                String json = preferences.getString(SCAN_RESULT_DEVICE_PREF + i, "");
                if (!json.isEmpty()) {
                    BluetoothDeviceData device = gson.fromJson(json, BluetoothDeviceData.class);
                    scanResults.add(device);
                }
            }

            return scanResults;
        }
        else
            return null;
    }

    public static void clearScanResults(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.putInt(SCAN_RESULT_COUNT_PREF, -1);

        editor.commit();
    }

    public static void saveScanResults(Context context, List<BluetoothDeviceData> scanResults)
    {
        List<BluetoothDeviceData> savedScanResults = getScanResults(context);
        if (savedScanResults == null)
            savedScanResults = new ArrayList<>();

        for (BluetoothDeviceData device : scanResults) {
            boolean found = false;
            for (BluetoothDeviceData _device : savedScanResults) {

                if (_device.address.equals(device.address)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                savedScanResults.add(new BluetoothDeviceData(device.name, device.address, device.type, false));
            }
        }

        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putInt(SCAN_RESULT_COUNT_PREF, savedScanResults.size());

        Gson gson = new Gson();
        for (int i = 0; i < savedScanResults.size(); i++)
        {
            String json = gson.toJson(savedScanResults.get(i));
            editor.putString(SCAN_RESULT_DEVICE_PREF+i, json);
        }

        editor.commit();
    }

    /*
    public static void addScanResult(Context context, BluetoothDeviceData device) {
        List<BluetoothDeviceData> savedScanResults = getScanResults(context);
        if (savedScanResults == null)
            savedScanResults = new ArrayList<>();

        boolean found = false;
        for (BluetoothDeviceData _device : savedScanResults) {

            if (_device.address.equals(device.address)) {
                found = true;
                break;
            }
        }
        if (!found) {
            savedScanResults.add(new BluetoothDeviceData(device.name, device.address, device.type, false));
        }

        SharedPreferences preferences = context.getSharedPreferences(PPApplication.BLUETOOTH_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putInt(SCAN_RESULT_COUNT_PREF, savedScanResults.size());

        Gson gson = new Gson();
        for (int i = 0; i < savedScanResults.size(); i++)
        {
            String json = gson.toJson(savedScanResults.get(i));
            editor.putString(SCAN_RESULT_DEVICE_PREF+i, json);
        }

        editor.commit();
    }
    */

    public static void addScanResult(Context context, BluetoothDeviceData device) {
        if (tmpScanLEResults == null)
            tmpScanLEResults = new ArrayList<>();

        boolean found = false;
        for (BluetoothDeviceData _device : tmpScanLEResults) {

            if (_device.address.equals(device.address)) {
                found = true;
                break;
            }
        }
        if (!found) {
            tmpScanLEResults.add(new BluetoothDeviceData(device.name, device.address, device.type, false));
        }
    }

}
