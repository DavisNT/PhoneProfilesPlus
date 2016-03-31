package sk.henrichg.phoneprofilesplus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.fnp.materialpreferences.PreferenceFragment;

public class PhoneProfilesPreferencesNestedFragment extends PreferenceFragment
                                              implements SharedPreferences.OnSharedPreferenceChangeListener
{

    protected PreferenceManager prefMng;
    protected SharedPreferences preferences;

    static final String PREF_APPLICATION_PERMISSIONS = "prf_pref_applicationPermissions";
    static final int RESULT_APPLICATION_PERMISSIONS = 1990;
    static final String PREF_WRITE_SYSTEM_SETTINGS_PERMISSIONS = "prf_pref_writeSystemSettingsPermissions";
    static final int RESULT_WRITE_SYSTEM_SETTINGS_PERMISSIONS = 1991;
    static final String PREF_WIFI_SCANNING_SYSTEM_SETTINGS = "applicationEventWiFiScanningSystemSettings";
    static final String PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS = "applicationEventBluetoothScanningSystemSettings";
    static final int RESULT_SCANNING_SYSTEM_SETTINGS = 1992;
    static final String PREF_POWER_SAVE_MODE_SETTINGS = "applicationPowerSaveMode";
    static final int RESULT_POWER_SAVE_MODE_SETTINGS = 1993;
    static final String PREF_POWER_SAVE_MODE_INTERNAL = "applicationPowerSaveModeInternal";
    static final String PREF_LOCATION_SYSTEM_SETTINGS = "applicationEventLocationSystemSettings";
    static final int RESULT_LOCATION_SYSTEM_SETTINGS = 1994;
    static final String PREF_LOCATION_EDITOR = "applicationEventLocationsEditor";
    static final String PREF_BATTERY_OPTIMIZATION_SYSTEM_SETTINGS = "applicationBatteryOptimization";
    static final int RESULT_BATTERY_OPTIMIZATION_SYSTEM_SETTINGS = 1995;

    @Override
    public int addPreferencesFromResource() {
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefMng = getPreferenceManager();
        prefMng.setSharedPreferencesName(GlobalData.APPLICATION_PREFS_NAME);
        prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);

        preferences = prefMng.getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);


        /*
        PreferenceScreen _preference = (PreferenceScreen) findPreference("applicationInterfaceCategory");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryAplicationStart");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categorySystem");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("prf_pref_permissionsCategory");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryNotifications");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("profileActivationCategory");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("wifiScanningCategory");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("bluetoothScanninCategory");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryActivator");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryEditor");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryWidgetList");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        _preference = (PreferenceScreen) findPreference("categoryWidgetIcon");
        if (_preference != null) _preference.setWidgetLayoutResource(R.layout.start_activity_preference);
        */

        if (Build.VERSION.SDK_INT >= 21) {
            //PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("categorySystem");
            //Preference preference = findPreference(PREF_POWER_SAVE_MODE_INTERNAL);
            //if (preference != null)
            //    preferenceCategory.removePreference(preference);

            Preference preference = prefMng.findPreference(PREF_POWER_SAVE_MODE_SETTINGS);
            if (preference != null) {
                //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent;
                        if (Build.VERSION.SDK_INT == 21) {
                            intent = new Intent();
                            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$BatterySaverSettingsActivity"));
                        } else
                            intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
                        //intent.addCategory(Intent.CATEGORY_DEFAULT);
                        try {
                            startActivityForResult(intent, RESULT_POWER_SAVE_MODE_SETTINGS);
                        } catch (Exception e) {
                            if (Build.VERSION.SDK_INT > 21) {
                                intent = new Intent();
                                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$BatterySaverSettingsActivity"));
                                try {
                                    startActivityForResult(intent, RESULT_POWER_SAVE_MODE_SETTINGS);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } else
                                e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        } else {
            PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("categorySystem");
            Preference preference = findPreference(PREF_POWER_SAVE_MODE_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            Preference preference = prefMng.findPreference(PREF_APPLICATION_PERMISSIONS);
            if (preference != null) {
                //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:sk.henrichg.phoneprofilesplus"));
                        startActivityForResult(intent, RESULT_APPLICATION_PERMISSIONS);
                        return false;
                    }
                });
            }
            preference = prefMng.findPreference(PREF_WRITE_SYSTEM_SETTINGS_PERMISSIONS);
            if (preference != null) {
                //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(intent, RESULT_WRITE_SYSTEM_SETTINGS_PERMISSIONS);
                        return false;
                    }
                });
            }

            int locationMode = Settings.Secure.getInt(PhoneProfilesPreferencesFragment.preferencesActivity.getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

            if (WifiScanAlarmBroadcastReceiver.wifi == null)
                WifiScanAlarmBroadcastReceiver.wifi = (WifiManager) PhoneProfilesPreferencesFragment.preferencesActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if ((locationMode == Settings.Secure.LOCATION_MODE_OFF) || (!WifiScanAlarmBroadcastReceiver.wifi.isScanAlwaysAvailable())) {
                preference = prefMng.findPreference(PREF_WIFI_SCANNING_SYSTEM_SETTINGS);
                if (preference != null) {
                    //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            //Intent intent = new Intent(WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE);
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivityForResult(intent, RESULT_SCANNING_SYSTEM_SETTINGS);
                            return false;
                        }
                    });
                }
            }
            else {
                PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("wifiScanningCategory");
                preference = findPreference(PREF_WIFI_SCANNING_SYSTEM_SETTINGS);
                if (preference != null)
                    preferenceCategory.removePreference(preference);
            }

            if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
                preference = prefMng.findPreference(PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS);
                if (preference != null) {
                    //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivityForResult(intent, RESULT_SCANNING_SYSTEM_SETTINGS);
                            return false;
                        }
                    });
                }
            }
            else {
                PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("bluetoothScanninCategory");
                preference = findPreference(PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS);
                if (preference != null)
                    preferenceCategory.removePreference(preference);
            }

            preference = prefMng.findPreference(PREF_LOCATION_SYSTEM_SETTINGS);
            if (preference != null) {
                //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(intent, RESULT_LOCATION_SYSTEM_SETTINGS);
                        return false;
                    }
                });
            }

            preference = prefMng.findPreference(PREF_BATTERY_OPTIMIZATION_SYSTEM_SETTINGS);
            if (preference != null) {
                //preference.setWidgetLayoutResource(R.layout.start_activity_preference);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(intent, RESULT_BATTERY_OPTIMIZATION_SYSTEM_SETTINGS);
                        return false;
                    }
                });
            }

        }
        else {
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("rootScreen");
            PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("prf_pref_permissionsCategory");
            if (preferenceCategory != null)
                preferenceScreen.removePreference(preferenceCategory);

            preferenceCategory = (PreferenceScreen) findPreference("wifiScanningCategory");
            Preference preference = findPreference(PREF_WIFI_SCANNING_SYSTEM_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);

            preferenceCategory = (PreferenceScreen) findPreference("bluetoothScanninCategory");
            preference = findPreference(PREF_BLUETOOTH_SCANNING_SYSTEM_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);

            preferenceCategory = (PreferenceScreen) findPreference("categorySystem");
            preference = findPreference(PREF_BATTERY_OPTIMIZATION_SYSTEM_SETTINGS);
            if (preference != null)
                preferenceCategory.removePreference(preference);

        }
        if (!ScannerService.bluetoothLESupported(PhoneProfilesPreferencesFragment.preferencesActivity.getApplicationContext())) {
            PreferenceScreen preferenceCategory = (PreferenceScreen) findPreference("bluetoothScanninCategory");
            Preference preference = findPreference(GlobalData.PREF_APPLICATION_EVENT_BLUETOOTH_LE_SCAN_DURATION);
            if (preference != null)
                preferenceCategory.removePreference(preference);
        }
    }

    private void setTitleStyle(Preference preference, boolean bold, boolean underline)
    {
        CharSequence title = preference.getTitle();
        Spannable sbt = new SpannableString(title);
        Object spansToRemove[] = sbt.getSpans(0, title.length(), Object.class);
        for(Object span: spansToRemove){
            if(span instanceof CharacterStyle)
                sbt.removeSpan(span);
        }
        if (bold || underline)
        {
            if (bold)
                sbt.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (underline)
                sbt.setSpan(new UnderlineSpan(), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            preference.setTitle(sbt);
        }
        else
        {
            preference.setTitle(sbt);
        }
    }

    public void setSummary(String key)
    {

        Preference preference = prefMng.findPreference(key);

        if (preference == null)
            return;

        // Do not bind toggles.
        if (preference instanceof CheckBoxPreference
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                    && preference instanceof TwoStatePreference)) {
            return;
        }

        String stringValue = preferences.getString(key, "");

        if (key.equals(GlobalData.PREF_APPLICATION_BACKGROUND_PROFILE))
        {
            String sProfileId = stringValue;
            long lProfileId;
            try {
                lProfileId = Long.parseLong(sProfileId);
            } catch (Exception e) {
                lProfileId = 0;
            }
            ProfilePreference profilePreference = (ProfilePreference)preference;
            profilePreference.setSummary(lProfileId);
        }
        else
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            // **** Heno changes ** support for "%" in list items
            CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
            if (summary != null)
            {
                String sSummary = summary.toString();
                sSummary = sSummary.replace("%", "%%");
                preference.setSummary(sSummary);
            }
            else
                preference.setSummary(summary);

            if (key.equals(GlobalData.PREF_APPLICATION_LANGUAGE))
                setTitleStyle(preference, true, false);
        }
        /*else if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.ringtone_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone
                            .getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }

        }*/
         else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            //preference.setSummary(preference.toString());
             preference.setSummary(stringValue);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        setSummary(key);
    }


    @Override
    public void onDestroy()
    {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void doOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode == RESULT_APPLICATION_PERMISSIONS) ||
            (requestCode == RESULT_WRITE_SYSTEM_SETTINGS_PERMISSIONS)) {

            Context context = PhoneProfilesPreferencesFragment.preferencesActivity.getApplicationContext();
            DataWrapper dataWrapper = new DataWrapper(context, true, false, 0);

            ActivateProfileHelper activateProfileHelper = dataWrapper.getActivateProfileHelper();
            activateProfileHelper.initialize(dataWrapper, null, context);

            Profile activatedProfile = dataWrapper.getActivatedProfile();
            dataWrapper.refreshProfileIcon(activatedProfile, false, 0);
            activateProfileHelper.showNotification(activatedProfile, "");
            activateProfileHelper.updateWidget();

            /*
            Intent intent5 = new Intent();
            intent5.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
            intent5.putExtra(RefreshGUIBroadcastReceiver.EXTRA_REFRESH_ICONS, true);
            context.sendBroadcast(intent5);
            */

            PhoneProfilesPreferencesFragment.preferencesActivity.finishAffinity();
        }

        if (requestCode == RESULT_LOCATION_SYSTEM_SETTINGS) {
            final boolean enabled = GlobalData.isLocationEnabled(PhoneProfilesPreferencesFragment.preferencesActivity.getApplicationContext());
            Preference preference = prefMng.findPreference(PREF_LOCATION_EDITOR);
            preference.setEnabled(enabled);
        }

        if (requestCode == LocationGeofencePreference.RESULT_GEOFENCE_EDITOR) {
            //Log.d("EventPreferencesFragment.doOnActivityResult", "xxx");
            if (PhoneProfilesPreferencesFragment.changedLocationGeofencePreference != null) {
                if(resultCode == Activity.RESULT_OK){
                    long geofenceId = data.getLongExtra(LocationGeofencePreference.EXTRA_GEOFENCE_ID, 0);
                    PhoneProfilesPreferencesFragment.changedLocationGeofencePreference.setGeofenceFromEditor(geofenceId);
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        doOnActivityResult(requestCode, resultCode, data);
    }

}
