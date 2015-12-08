package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fnp.materialpreferences.PreferenceFragment;

import java.util.List;
 
public class EventPreferencesFragment extends PreferenceFragment
                                        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private DataWrapper dataWrapper;
    private Event event;
    //private boolean first_start_activity;
    private int new_event_mode;
    public static int startupSource;
    private PreferenceManager prefMng;
    private SharedPreferences preferences;
    private Context context;
    private ActionMode actionMode;
    private Callback actionModeCallback;

    private static Activity preferencesActivity = null;

    static final String PREFS_NAME_ACTIVITY = "event_preferences_activity";
    static final String PREFS_NAME_FRAGMENT = "event_preferences_fragment";
    private String PREFS_NAME;

    static final String PREF_NOTIFICATION_ACCESS = "eventNotificationNotificationsAccessSettings";
    static final int RESULT_NOTIFICATION_ACCESS_SETTINGS = 1981;
    static final String PREF_WIFI_SCANNING_SYSTEM_SETTINGS = "eventWiFiScanningSystemSettings";
    static final String PREF_WIFI_SCANNING_APP_SETTINGS = "eventEnableWiFiScaningAppSettings";
    static final String PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS = "eventBluetoothScanningSystemSettings";
    static final String PREF_BLUETOOTH_SCANNING_APP_SETTINGS = "eventEnableBluetoothScaningAppSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // must by false to avoid FC when rotation changes and preference dialogs are shown
        setRetainInstance(false);

        preferencesActivity = getActivity();
        context = getActivity().getBaseContext();

        dataWrapper = new DataWrapper(context.getApplicationContext(), true, false, 0);

        long event_id = 0;

        // getting attached fragment data
        if (getArguments().containsKey(GlobalData.EXTRA_NEW_EVENT_MODE))
            new_event_mode = getArguments().getInt(GlobalData.EXTRA_NEW_EVENT_MODE);
        if (getArguments().containsKey(GlobalData.EXTRA_EVENT_ID))
            event_id = getArguments().getLong(GlobalData.EXTRA_EVENT_ID);

        event = EventPreferencesFragmentActivity.createEvent(context.getApplicationContext(), event_id, new_event_mode, true);

        preferences = prefMng.getSharedPreferences();
        
        //if (savedInstanceState == null)
        //    loadPreferences();

        updateSharedPreference();

    }

    @Override
    public void addPreferencesFromResource(int preferenceResId) {
        if (startupSource == GlobalData.PREFERENCES_STARTUP_SOURCE_ACTIVITY)
            PREFS_NAME = PREFS_NAME_ACTIVITY;
        else
        if (startupSource == GlobalData.PREFERENCES_STARTUP_SOURCE_FRAGMENT)
            PREFS_NAME = PREFS_NAME_FRAGMENT;
        else
            PREFS_NAME = PREFS_NAME_FRAGMENT;

        prefMng = getPreferenceManager();
        prefMng.setSharedPreferencesName(PREFS_NAME);
        prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);

        super.addPreferencesFromResource(preferenceResId);
    }

    @Override
    public int addPreferencesFromResource() {
        return R.xml.event_preferences;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferences.registerOnSharedPreferenceChangeListener(this);

        RingtonePreference notificationSoundPreference = (RingtonePreference)prefMng.findPreference(Event.PREF_EVENT_NOTIFICATION_SOUND);
        notificationSoundPreference.setEnabled(GlobalData.notificationStatusBar);

        event._eventPreferencesTime.checkPreferences(prefMng, context);
        event._eventPreferencesBattery.checkPreferences(prefMng, context);
        event._eventPreferencesCall.checkPreferences(prefMng, context);
        event._eventPreferencesCalendar.checkPreferences(prefMng, context);
        event._eventPreferencesPeripherals.checkPreferences(prefMng, context);
        event._eventPreferencesWifi.checkPreferences(prefMng, context);
        event._eventPreferencesScreen.checkPreferences(prefMng, context);
        event._eventPreferencesBluetooth.checkPreferences(prefMng, context);
        event._eventPreferencesSMS.checkPreferences(prefMng, context);
        event._eventPreferencesNotification.checkPreferences(prefMng, context);

        Preference notificationAccessPreference = prefMng.findPreference(PREF_NOTIFICATION_ACCESS);
        //notificationAccessPreference.setWidgetLayoutResource(R.layout.start_activity_preference);
        notificationAccessPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent, RESULT_NOTIFICATION_ACCESS_SETTINGS);
                return false;
            }
        });
        Preference preference = findPreference(PREF_WIFI_SCANNING_APP_SETTINGS);
        //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        preference = findPreference(PREF_BLUETOOTH_SCANNING_APP_SETTINGS);
        //preference.setWidgetLayoutResource(R.layout.start_activity_preference);

        if (Build.VERSION.SDK_INT >= 23) {

            int locationMode = Settings.Secure.getInt(preferencesActivity.getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

            if (WifiScanAlarmBroadcastReceiver.wifi == null)
                WifiScanAlarmBroadcastReceiver.wifi = (WifiManager) preferencesActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            preference = findPreference(PREF_WIFI_SCANNING_SYSTEM_SETTINGS);
            if ((locationMode != Settings.Secure.LOCATION_MODE_OFF) && WifiScanAlarmBroadcastReceiver.wifi.isScanAlwaysAvailable()) {
                PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("eventWifiCategory");
                if (preference != null)
                    preferenceCategory.removePreference(preference);
            }
            //else {
            //    preference.setWidgetLayoutResource(R.layout.start_activity_preference);
            //}

            preference = findPreference(PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS);
            if (locationMode != Settings.Secure.LOCATION_MODE_OFF) {
                PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("eventBluetoothCategory");
                if (preference != null)
                    preferenceCategory.removePreference(preference);
            }
            //else {
            //    preference.setWidgetLayoutResource(R.layout.start_activity_preference);
            //}
        }
        else {
            PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("eventWifiCategory");
            preference = findPreference(PREF_WIFI_SCANNING_SYSTEM_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);

            preferenceCategory = (PreferenceScreen) findPreference("eventBluetoothCategory");
            preference = findPreference(PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        /*
        if (actionMode != null)
        {
            restart = false; // nerestartovat fragment
            actionMode.finish();
        }
        */

    }

    @Override
    public void onDestroy()
    {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        event = null;

        if (dataWrapper != null)
            dataWrapper.invalidateDataWrapper();
        dataWrapper = null;
        
        super.onDestroy();
    }

    public void doOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_NOTIFICATION_ACCESS_SETTINGS) {
            event._eventPreferencesNotification.checkPreferences(prefMng, context);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        doOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateSharedPreference()
    {
        if (event != null) 
        {	

            // updating activity with selected event preferences

            event.setAllSummary(prefMng, context);

        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {

        //eventTypeChanged = false;

        event.setSummary(prefMng, key, sharedPreferences, context);

        //Activity activity = getActivity();
        //boolean canShow = (EditorProfilesActivity.mTwoPane) && (activity instanceof EditorProfilesActivity);
        //canShow = canShow || ((!EditorProfilesActivity.mTwoPane) && (activity instanceof EventPreferencesFragmentActivity));
        //if (canShow)
        //    showActionMode();
        EventPreferencesFragmentActivity activity = (EventPreferencesFragmentActivity)getActivity();
        EventPreferencesFragmentActivity.showSaveMenu = true;
        activity.invalidateOptionsMenu();
    }

    static public Activity getPreferencesActivity()
    {
        return preferencesActivity;
    }

}
