package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class ExecuteRadioProfilePrefsService extends IntentService 
{
	
	private static final String	PPHELPER_ACTION_SETPROFILEPREFERENCES = "sk.henrichg.phoneprofileshelper.ACTION_SETPROFILEPREFERENCES";

	private static final String PPHELPER_PROCEDURE = "procedure";
	private static final String PPHELPER_PROCEDURE_RADIO_CHANGE = "radioChange";
	private static final String PPHELPER_GPS_CHANGE = "GPSChange";
	private static final String PPHELPER_AIRPLANE_MODE_CHANGE = "airplaneModeChange";
	private static final String PPHELPER_NFC_CHANGE = "NFCChange";
	private static final String PPHELPER_WIFI_CHANGE = "WiFiChange";
	private static final String PPHELPER_BLUETOOTH_CHANGE = "bluetoothChange";
	private static final String PPHELPER_MOBILE_DATA_CHANGE = "mobileDataChange";
	private static final String PPHELPER_WIFI_AP_CHANGE = "WiFiAPChange";

	public ExecuteRadioProfilePrefsService() {
		super("ExecuteRadioProfilePrefsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		GlobalData.logE("ExecuteRadioProfilePrefsService.onHandleIntent","-- START ----------");
		
		Context context = getApplicationContext();
		
		GlobalData.loadPreferences(context);
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		
		long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		boolean merged = intent.getBooleanExtra(GlobalData.EXTRA_MERGED_PROFILE, false);
		Profile profile = dataWrapper.getProfileById(profile_id, merged);
		
		/*
		// synchronization, wait for end of radio state change
		GlobalData.logE("@@@ ActivateProfileHelper.executeForRadios", "start waiting for radio change");
		GlobalData.waitForRadioChangeState(context);
		GlobalData.logE("@@@ ActivateProfileHelper.executeForRadios", "end waiting for radio change");
		
		GlobalData.setRadioChangeState(context, true);
		*/

        GlobalData.logE("$$$ ExecuteRadioProfilePrefsService.onHandleIntent", "before synchronized block");

		synchronized (GlobalData.radioChangeStateMutex) {

            GlobalData.logE("$$$ ExecuteRadioProfilePrefsService.onHandleIntent", "in synchronized block - start");

		if (PhoneProfilesHelper.isPPHelperInstalled(context, 0))
		{
			// broadcast PPHelper
			Intent ppHelperIntent = new Intent();
			ppHelperIntent.setAction(PPHELPER_ACTION_SETPROFILEPREFERENCES);
			ppHelperIntent.putExtra(PPHELPER_PROCEDURE, PPHELPER_PROCEDURE_RADIO_CHANGE);
			ppHelperIntent.putExtra(PPHELPER_GPS_CHANGE, profile._deviceGPS);
			ppHelperIntent.putExtra(PPHELPER_AIRPLANE_MODE_CHANGE, profile._deviceAirplaneMode);
			ppHelperIntent.putExtra(PPHELPER_NFC_CHANGE, profile._deviceNFC);
			ppHelperIntent.putExtra(PPHELPER_WIFI_CHANGE, profile._deviceWiFi);
			ppHelperIntent.putExtra(PPHELPER_BLUETOOTH_CHANGE, profile._deviceBluetooth);
			ppHelperIntent.putExtra(PPHELPER_MOBILE_DATA_CHANGE, profile._deviceMobileData);
			ppHelperIntent.putExtra(PPHELPER_WIFI_AP_CHANGE, profile._deviceWiFiAP);
		    context.sendBroadcast(ppHelperIntent);

            // run execute radios from ActivateProfileHelper - only check
            profile = GlobalData.getMappedProfile(profile, context);
            if (profile != null)
            {
                ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
                aph.initialize(dataWrapper, null, context);
                aph.executeForRadios(profile, true);
            }

            // wait for PPHelper
			try {
	        	Thread.sleep(500);
		    } catch (InterruptedException e) {
		        System.out.println(e);
		    }
		}
		else
		{
			// run execute radios from ActivateProfileHelper
			profile = GlobalData.getMappedProfile(profile, context);
			if (profile != null)
			{
				ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
				aph.initialize(dataWrapper, null, context);
				aph.executeForRadios(profile, false);
			}
		}

            GlobalData.logE("$$$ ExecuteRadioProfilePrefsService.onHandleIntent", "in synchronized block - end");

		}

        GlobalData.logE("$$$ ExecuteRadioProfilePrefsService.onHandleIntent", "after synchronized block");

		//GlobalData.setRadioChangeState(context, false);
		
		dataWrapper.invalidateDataWrapper();

		GlobalData.logE("ExecuteRadioProfilePrefsService.onHandleIntent","-- END ----------");
		
	}
}
