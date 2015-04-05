package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WifiScanAlarmBroadcastReceiver extends BroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiScanAlarm";

	public static WifiManager wifi = null;
	private static WifiLock wifiLock = null;
    private static PowerManager.WakeLock wakeLock = null;

	public static List<WifiSSIDData> scanResults = null;
	public static List<WifiSSIDData> wifiConfigurationList = null;
	
	@SuppressLint("NewApi")
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### WifiScanAlarmBroadcastReceiver.onReceive","xxx");

		if (scanResults == null)
			scanResults = new ArrayList<WifiSSIDData>();

		if (wifiConfigurationList == null)
			wifiConfigurationList = new ArrayList<WifiSSIDData>();

        if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) !=
                GlobalData.HARDWARE_CHECK_ALLOWED) {
            removeAlarm(context, false);
            removeAlarm(context, true);
            return;
        }

        if (wifi == null)
			wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
		// disabled fro firstStartEvents
		//if (!GlobalData.getApplicationStarted(context))
			// application is not started
		//	return;

		GlobalData.loadPreferences(context);

		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","xxx");

			boolean wifiEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);

			if (wifiEventsExists || GlobalData.getForceOneWifiScan(context))
			{
				startScanner(context);
			}
            else {
                removeAlarm(context, false);
                removeAlarm(context, true);
            }
			
			dataWrapper.invalidateDataWrapper();
		}
		
	}
	
	public static void initialize(Context context)
	{
        setScanRequest(context, false);
        setWaitForResults(context, false);
        setWifiEnabledForScan(context, false);

        if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) !=
                GlobalData.HARDWARE_CHECK_ALLOWED)
            return;

		if (wifi == null)
			wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
    	unlock();

		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		if (networkInfo.getState() == NetworkInfo.State.CONNECTED)		
			editor.putInt(GlobalData.PREF_EVENT_WIFI_LAST_STATE, 1);
		else
		if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED)		
			editor.putInt(GlobalData.PREF_EVENT_WIFI_LAST_STATE, 0);
		else
			editor.putInt(GlobalData.PREF_EVENT_WIFI_LAST_STATE, -1);
		editor.commit();
		
		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
		{
			fillWifiConfigurationList(context);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static void setAlarm(Context context, boolean oneshot)
	{
		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot);

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) 
				== GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=true");
	        
	        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	 			
	 		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);

	 		if (oneshot)
	 		{
				removeAlarm(context, true);

				Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.SECOND, 2);
		        long alarmTime = calendar.getTimeInMillis(); 
		        		
			    SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));
		        
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
	 		}
	 		else
	 		{
				removeAlarm(context, false);

				Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.SECOND, 10);
		        long alarmTime = calendar.getTimeInMillis(); 
		        		
			    SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));
				
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
												alarmTime,
												GlobalData.applicationEventWifiScanInterval * 60 * 1000, 
												alarmIntent);
	 		}
			
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarm is set");

		}
		else
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=false");
	}
	
	public static void removeAlarm(Context context, boolean oneshot)
	{
  		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent;
		if (oneshot)
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
		else
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
        else
       		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm not found");
    }
	
	public static boolean isAlarmSet(Context context, boolean oneshot)
	{
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent;
		if (oneshot)
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
		else
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.isAlarmSet","oneshot="+oneshot+"; alarm found");
        else
        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.isAlarmSet","oneshot="+oneshot+"; alarm not found");

        return (pendingIntent != null);
	}

    public static void lock(Context context)
    {
		 // initialise the locks
		if (wifiLock == null)
	        wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "WifiScanWifiLock");
		if (wakeLock == null)
	        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
	                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WifiScanWakeLock");

    	try {
    		if (!wakeLock.isHeld())
    			wakeLock.acquire();
    		if (!wifiLock.isHeld())
    			wifiLock.acquire();
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.lock","xxx");
        } catch(Exception e) {
            Log.e("WifiScanAlarmBroadcastReceiver.lock", "Error getting Lock: "+e.getMessage());
        }
    }
 
    public static void unlock()
    {
        if ((wakeLock != null) && (wakeLock.isHeld()))
            wakeLock.release();
        if ((wifiLock != null) && (wifiLock.isHeld()))
            wifiLock.release();
		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.unlock","xxx");
    }
    
    public static void sendBroadcast(Context context)
    {
		Intent broadcastIntent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
		context.sendBroadcast(broadcastIntent);
    }
    
	static public boolean getScanRequest(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_WIFI_SCAN_REQUEST, false);
	}

	static public void setScanRequest(Context context, boolean scanRequest)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_WIFI_SCAN_REQUEST, scanRequest);
		editor.commit();
      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setScanRequest","scanRequest="+scanRequest);
	}

    static public boolean getWaitForResults(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(GlobalData.PREF_EVENT_WIFI_WAIT_FOR_RESULTS, false);
    }

    static public void setWaitForResults(Context context, boolean waitForResults)
    {
        SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(GlobalData.PREF_EVENT_WIFI_WAIT_FOR_RESULTS, waitForResults);
        editor.commit();
        GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setWaitForResults","waitForResults="+waitForResults);
    }

    static public void startScan(Context context)
	{
		lock(context); // lock wakeLock and wifiLock, then scan.
        			// unlock() is then called at the end of the onReceive function of WifiScanBroadcastReceiver
		boolean startScan = wifi.startScan();
      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.startScan","scanStarted="+startScan);
		if (!startScan)
		{
			if (getWifiEnabledForScan(context))
			{
				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.startScan","disable wifi");
				wifi.setWifiEnabled(false);
			}
            unlock();
		}
        setWaitForResults(context, startScan);
        setScanRequest(context, false);
	}
	
	static public void startScanner(Context context)
	{
		Intent scanServiceIntent = new Intent(context, ScannerService.class);
		scanServiceIntent.putExtra(GlobalData.EXTRA_SCANNER_TYPE, GlobalData.SCANNER_TYPE_WIFI);
		context.startService(scanServiceIntent);
	}

    /*
	static public void stopScan(Context context)
	{
      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.stopScan","xxx");
		unlock();
		if (getWifiEnabledForScan(context))
			wifi.setWifiEnabled(false);
		setWifiEnabledForScan(context, false);
		setScanRequest(context, false);
        setWaitForResults(context, false);
		GlobalData.setForceOneWifiScan(context, false);
	}
	*/
	
	static public boolean getWifiEnabledForScan(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_WIFI_ENABLED_FOR_SCAN, false);
	}

	static public void setWifiEnabledForScan(Context context, boolean setEnabled)
	{
      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan","setEnabled="+setEnabled);
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_WIFI_ENABLED_FOR_SCAN, setEnabled);
		editor.commit();
	}

    static public void fillWifiConfigurationList(Context context)
	{
		if (wifiConfigurationList == null)
			wifiConfigurationList = new ArrayList<WifiSSIDData>();
		
		if (wifi == null)
			wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
		List<WifiConfiguration> _wifiConfigurationList = wifi.getConfiguredNetworks();
		if (_wifiConfigurationList != null)
		{
			wifiConfigurationList.clear();
			for (WifiConfiguration device : _wifiConfigurationList)
			{
				boolean found = false;
				for (WifiSSIDData _device : wifiConfigurationList)
				{
					//if (_device.bssid.equals(device.BSSID))  
					if ((_device.ssid != null) && (_device.ssid.equals(device.SSID)))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					wifiConfigurationList.add(new WifiSSIDData(device.SSID, device.BSSID));
				}
			}
		}
        saveWifiConfigurationList(context);
	}

	static public void fillScanResults(Context context)
	{
		if (scanResults == null)
			scanResults = new ArrayList<WifiSSIDData>();
		
		if (wifi == null)
			wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
		List<ScanResult> _scanResults = wifi.getScanResults();
		if (_scanResults != null)
		{
			scanResults.clear();
			for (ScanResult device : _scanResults)
			{
				boolean found = false;
				for (WifiSSIDData _device : scanResults)
				{
					if (_device.bssid.equals(device.BSSID))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					scanResults.add(new WifiSSIDData(device.SSID, device.BSSID));
				}
			}
		}
        saveScanResults(context);
	}

    private static final String SCAN_RESULT_COUNT_PREF = "count";
    private static final String SCAN_RESULT_DEVICE_PREF = "device";

    public static void getWifiConfigurationList(Context context)
    {
        if (wifiConfigurationList == null)
            wifiConfigurationList = new ArrayList<WifiSSIDData>();

        wifiConfigurationList.clear();

        SharedPreferences preferences = context.getSharedPreferences(GlobalData.WIFI_CONFIGURATION_LIST_PREFS_NAME, Context.MODE_PRIVATE);

        int count = preferences.getInt(SCAN_RESULT_COUNT_PREF, 0);

        Gson gson = new Gson();

        for (int i = 0; i < count; i++)
        {
            String json = preferences.getString(SCAN_RESULT_DEVICE_PREF+i, "");
            if (!json.isEmpty()) {
                WifiSSIDData device = gson.fromJson(json, WifiSSIDData.class);
                wifiConfigurationList.add(device);
            }
        }

    }

    private static void saveWifiConfigurationList(Context context)
    {
        if (wifiConfigurationList == null)
            wifiConfigurationList = new ArrayList<WifiSSIDData>();

        SharedPreferences preferences = context.getSharedPreferences(GlobalData.WIFI_CONFIGURATION_LIST_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putInt(SCAN_RESULT_COUNT_PREF, wifiConfigurationList.size());

        Gson gson = new Gson();

        for (int i = 0; i < wifiConfigurationList.size(); i++)
        {
            String json = gson.toJson(wifiConfigurationList.get(i));
            editor.putString(SCAN_RESULT_DEVICE_PREF+i, json);
        }

        editor.commit();
    }

    public static void getScanResults(Context context)
    {
        if (scanResults == null)
            scanResults = new ArrayList<WifiSSIDData>();

        scanResults.clear();

        SharedPreferences preferences = context.getSharedPreferences(GlobalData.WIFI_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);

        int count = preferences.getInt(SCAN_RESULT_COUNT_PREF, 0);

        Gson gson = new Gson();

        for (int i = 0; i < count; i++)
        {
            String json = preferences.getString(SCAN_RESULT_DEVICE_PREF+i, "");
            if (!json.isEmpty()) {
                WifiSSIDData device = gson.fromJson(json, WifiSSIDData.class);
                scanResults.add(device);
            }
        }

    }

    private static void saveScanResults(Context context)
    {
        if (scanResults == null)
            scanResults = new ArrayList<WifiSSIDData>();

        SharedPreferences preferences = context.getSharedPreferences(GlobalData.WIFI_SCAN_RESULTS_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putInt(SCAN_RESULT_COUNT_PREF, scanResults.size());

        Gson gson = new Gson();

        for (int i = 0; i < scanResults.size(); i++)
        {
            String json = gson.toJson(scanResults.get(i));
            editor.putString(SCAN_RESULT_DEVICE_PREF+i, json);
        }

        editor.commit();
    }

}
