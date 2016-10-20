package sk.henrichg.phoneprofilesplus;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission;

public class Permissions {

    public static final int PERMISSION_PROFILE_VOLUME_PREFERENCES = 1;
    public static final int PERMISSION_PROFILE_VIBRATION_ON_TOUCH = 2;
    public static final int PERMISSION_PROFILE_RINGTONES = 3;
    public static final int PERMISSION_PROFILE_SCREEN_TIMEOUT = 4;
    public static final int PERMISSION_PROFILE_SCREEN_BRIGHTNESS = 5;
    public static final int PERMISSION_PROFILE_AUTOROTATION = 6;
    public static final int PERMISSION_PROFILE_WALLPAPER = 7;
    public static final int PERMISSION_PROFILE_RADIO_PREFERENCES = 8;
    public static final int PERMISSION_PROFILE_PHONE_BROADCAST = 9;
    public static final int PERMISSION_PROFILE_CUSTOM_PROFILE_ICON = 10;
    public static final int PERMISSION_INSTALL_TONE = 11;
    public static final int PERMISSION_EXPORT = 12;
    public static final int PERMISSION_IMPORT = 13;
    public static final int PERMISSION_EVENT_CALENDAR_PREFERENCES = 15;
    public static final int PERMISSION_EVENT_CALL_PREFERENCES = 16;
    public static final int PERMISSION_EVENT_SMS_PREFERENCES = 17;
    public static final int PERMISSION_EVENT_LOCATION_PREFERENCES = 18;
    public static final int PERMISSION_EVENT_CONTACTS = 19;
    public static final int PERMISSION_PROFILE_NOTIFICATION_LED = 20;
    public static final int PERMISSION_PROFILE_VIBRATE_WHEN_RINGING = 21;
    public static final int PERMISSION_PLAY_RINGTONE_NOTIFICATION = 22;
    public static final int PERMISSION_PROFILE_ACCESS_NOTIFICATION_POLICY = 23;

    public static final int GRANT_TYPE_PROFILE = 1;
    public static final int GRANT_TYPE_INSTALL_TONE = 2;
    public static final int GRANT_TYPE_WALLPAPER = 3;
    public static final int GRANT_TYPE_CUSTOM_PROFILE_ICON = 4;
    public static final int GRANT_TYPE_EXPORT = 5;
    public static final int GRANT_TYPE_IMPORT = 6;
    public static final int GRANT_TYPE_EVENT = 8;
    public static final int GRANT_TYPE_WIFI_BT_SCAN_DIALOG = 9;
    public static final int GRANT_TYPE_CALENDAR_DIALOG = 10;
    public static final int GRANT_TYPE_CONTACT_DIALOG = 11;
    public static final int GRANT_TYPE_LOCATION_GEOFENCE_EDITOR_ACTIVITY = 12;
    public static final int GRANT_TYPE_BRIGHTNESS_DIALOG = 13;
    public static final int GRANT_TYPE_PLAY_RINGTONE_NOTIFICATION = 14;
    public static final int GRANT_TYPE_MOBILE_CELLS_SCAN_DIALOG = 9;

    public static final String EXTRA_GRANT_TYPE = "grant_type";
    public static final String EXTRA_MERGED_PROFILE = "merged_profile";
    public static final String EXTRA_PERMISSION_TYPES = "permission_types";
    public static final String EXTRA_ONLY_NOTIFICATION = "only_notification";
    public static final String EXTRA_FOR_GUI = "for_gui";
    public static final String EXTRA_MONOCHROME = "monochrome";
    public static final String EXTRA_MONOCHROME_VALUE = "monochrome_value";
    public static final String EXTRA_INTERACTIVE = "interactive";
    public static final String EXTRA_LOG = "log";
    public static final String EXTRA_APPLICATION_DATA_PATH = "application_data_path";
    public static final String EXTRA_ACTIVATE_PROFILE = "activate_profile";

    public static Activity profileActivationActivity = null;
    public static ImageViewPreference imageViewPreference = null;
    public static ProfileIconPreference profileIconPreference = null;
    public static EditorProfilesActivity editorActivity = null;
    public static WifiSSIDPreference wifiSSIDPreference = null;
    public static BluetoothNamePreference bluetoothNamePreference = null;
    public static CalendarsMultiSelectDialogPreference calendarsMultiSelectDialogPreference = null;
    public static ContactsMultiSelectDialogPreference contactsMultiSelectDialogPreference = null;
    public static ContactGroupsMultiSelectDialogPreference contactGroupsMultiSelectDialogPreference = null;
    public static LocationGeofenceEditorActivity locationGeofenceEditorActivity = null;
    public static BrightnessDialogPreference brightnessDialogPreference = null;
    public static MobileCellsPreference mobileCellsPreference = null;


    public static class PermissionType implements Parcelable {
        public int preference;
        public String permission;

        PermissionType (int preference, String permission) {
            this.preference = preference;
            this.permission = permission;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.preference);
            dest.writeString(this.permission);
        }

        protected PermissionType(Parcel in) {
            this.preference = in.readInt();
            this.permission = in.readString();
        }

        public static final Parcelable.Creator<PermissionType> CREATOR = new Parcelable.Creator<PermissionType>() {
            public PermissionType createFromParcel(Parcel source) {
                return new PermissionType(source);
            }

            public PermissionType[] newArray(int size) {
                return new PermissionType[size];
            }
        };
    }

    public static List<PermissionType> recheckPermissions(Context context, List<PermissionType> _permissions) {
        List<PermissionType>  permissions = new ArrayList<PermissionType>();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            for (PermissionType _permission : _permissions) {
                if (_permission.permission.equals(Manifest.permission.WRITE_SETTINGS)) {
                    if (!Settings.System.canWrite(context))
                        permissions.add(new PermissionType(_permission.preference, _permission.permission));
                } else if (_permission.permission.equals(permission.ACCESS_NOTIFICATION_POLICY)) {
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (!mNotificationManager.isNotificationPolicyAccessGranted())
                        permissions.add(new PermissionType(_permission.preference, _permission.permission));
                } else {
                    if (ContextCompat.checkSelfPermission(context, _permission.permission) != PackageManager.PERMISSION_GRANTED)
                        permissions.add(new PermissionType(_permission.preference, _permission.permission));
                }
            }
        }
        return permissions;
    }

    public static List<PermissionType> checkProfilePermissions(Context context, Profile profile) {
        List<PermissionType>  permissions = new ArrayList<PermissionType>();
        if (profile == null) return permissions;
        //Log.e("Permissions", "checkProfilePermissions - profile.icon="+profile._icon);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!checkProfileVolumePreferences(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_VOLUME_PREFERENCES, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_VOLUME_PREFERENCES");
            }
            if (!checkProfileVibrationOnTouch(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_VIBRATION_ON_TOUCH, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_VIBRATION_ON_TOUCH");
            }
            if (!checkProfileVibrateWhenRinging(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_VIBRATE_WHEN_RINGING, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_VIBRATE_WHEN_RINGING");
            }
            if (!checkProfileRingtones(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_RINGTONES, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_RINGTONES");
            }
            if (!checkProfileScreenTimeout(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_SCREEN_TIMEOUT, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_SCREEN_TIMEOUT");
            }
            if (!checkProfileScreenBrightness(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_SCREEN_BRIGHTNESS, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_SCREEN_BRIGHTNESS");
            }
            if (!checkProfileAutoRotation(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_AUTOROTATION, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_AUTOROTATION");
            }
            if (!checkProfileNotificationLed(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_NOTIFICATION_LED, permission.WRITE_SETTINGS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_NOTIFICATION_LED");
            }
            if (!checkProfileWallpaper(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_WALLPAPER, permission.READ_EXTERNAL_STORAGE));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_WALLPAPER");
            }
            if (!checkProfileRadioPreferences(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_RADIO_PREFERENCES, permission.WRITE_SETTINGS));
                permissions.add(new PermissionType(PERMISSION_PROFILE_RADIO_PREFERENCES, permission.READ_PHONE_STATE));
                //permissions.add(new PermissionType(PERMISSION_PROFILE_RADIO_PREFERENCES, permission.MODIFY_PHONE_STATE));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_RADIO_PREFERENCES");
            }
            if (!checkProfilePhoneBroadcast(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_PHONE_BROADCAST, permission.READ_PHONE_STATE));
                permissions.add(new PermissionType(PERMISSION_PROFILE_PHONE_BROADCAST, permission.PROCESS_OUTGOING_CALLS));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_PHONE_BROADCAST");
            }
            if (!checkCustomProfileIcon(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_CUSTOM_PROFILE_ICON, permission.READ_EXTERNAL_STORAGE));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_CUSTOM_PROFILE_ICON");
            }
            if (!checkProfileAccessNotificationPolicy(context, profile)) {
                permissions.add(new PermissionType(PERMISSION_PROFILE_ACCESS_NOTIFICATION_POLICY, permission.ACCESS_NOTIFICATION_POLICY));
                //Log.d("Permissions.checkProfilePermissions","PERMISSION_PROFILE_ACCESS_NOTIFICATION_POLICY");
            }
            return permissions;
        }
        else
            return permissions;
    }

    public static boolean checkProfileVolumePreferences(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //Settings.System.putInt(context.getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 0); -- NOT WORKING
            if ((profile._volumeRingerMode != 0) ||
                    profile.getVolumeRingtoneChange() ||
                    profile.getVolumeNotificationChange() ||
                    profile.getVolumeMediaChange() ||
                    profile.getVolumeAlarmChange() ||
                    profile.getVolumeSystemChange() ||
                    profile.getVolumeVoiceChange()) {
                //return Settings.System.canWrite(context);
                return true;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkInstallTone(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkPlayRingtoneNotification(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkProfileVibrationOnTouch(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._vibrationOnTouch != 0) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileVibrateWhenRinging(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._vibrateWhenRinging != 0) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileNotificationLed(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._notificationLed != 0) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileRingtones(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if ((profile._soundRingtoneChange != 0) ||
                    (profile._soundNotificationChange != 0) ||
                    (profile._soundAlarmChange != 0)) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileScreenTimeout(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._deviceScreenTimeout != 0) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkScreenBrightness(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = Settings.System.canWrite(context);
            //Log.d("Permissions.checkScreenBrightness", "granted="+granted);
            if (granted)
                GlobalData.setShowRequestWriteSettingsPermission(context, true);
            return granted;
        }
        else
            return true;
    }

    public static boolean checkProfileScreenBrightness(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile.getDeviceBrightnessChange()) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileAutoRotation(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._deviceAutoRotate != 0) {
                boolean granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileWallpaper(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._deviceWallpaperChange != 0) {
                return (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkCustomProfileIcon(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
            Profile _profile = dataWrapper.getDatabaseHandler().getProfile(profile._id, false);
            if (_profile == null) return true;
            if (!_profile.getIsIconResourceID()) {
                return (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkGallery(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkImport(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkExport(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkNFC(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23)
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        else
            return true;
    }

    public static boolean checkProfileRadioPreferences(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = true;
            if ((profile._deviceWiFiAP != 0)) {
                granted = Settings.System.canWrite(context);
                if (granted)
                    GlobalData.setShowRequestWriteSettingsPermission(context, true);
            }
            if ((profile._deviceMobileData != 0) || (profile._deviceNetworkType != 0))
                granted = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
            //if (profile._deviceNFC != 0)
            //    granted = checkNFC(context);
            return granted;
        }
        else
            return true;
    }

    public static boolean checkProfilePhoneBroadcast(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (profile._volumeSpeakerPhone != 0) {
                boolean granted = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
                granted = granted && (ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkProfileAccessNotificationPolicy(Context context, Profile profile) {
        if (profile == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean no60 = !Build.VERSION.RELEASE.equals("6.0");
            if (no60) {
                if ((profile._volumeRingerMode != 0) ||
                        profile.getVolumeRingtoneChange() ||
                        profile.getVolumeNotificationChange() ||
                        profile.getVolumeSystemChange()) {
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    boolean granted = mNotificationManager.isNotificationPolicyAccessGranted();
                    if (granted)
                        GlobalData.setShowRequestAccessNotificationPolicyPermission(context, true);
                    return granted;
                } else
                    return true;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkAccessNotificationPolicy(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean no60 = !Build.VERSION.RELEASE.equals("6.0");
            if (no60) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                boolean granted = mNotificationManager.isNotificationPolicyAccessGranted();
                if (granted)
                    GlobalData.setShowRequestAccessNotificationPolicyPermission(context, true);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static List<PermissionType> checkEventPermissions(Context context, Event event) {
        List<PermissionType>  permissions = new ArrayList<PermissionType>();
        if (event == null) return permissions;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!checkEventCalendar(context, event)) permissions.add(new PermissionType(PERMISSION_EVENT_CALENDAR_PREFERENCES, permission.READ_CALENDAR));
            if (!checkEventCallContacts(context, event)) permissions.add(new PermissionType(PERMISSION_EVENT_CALL_PREFERENCES, permission.READ_CONTACTS));
            if (!checkEventPhoneBroadcast(context, event)) {
                permissions.add(new PermissionType(PERMISSION_EVENT_CALL_PREFERENCES, permission.READ_PHONE_STATE));
                permissions.add(new PermissionType(PERMISSION_EVENT_CALL_PREFERENCES, permission.PROCESS_OUTGOING_CALLS));
            }
            if (!checkEventSMSContacts(context, event)) permissions.add(new PermissionType(PERMISSION_EVENT_SMS_PREFERENCES, permission.READ_CONTACTS));
            if (!checkEventSMSBroadcast(context, event)) {
                permissions.add(new PermissionType(PERMISSION_EVENT_SMS_PREFERENCES, permission.RECEIVE_SMS));
                permissions.add(new PermissionType(PERMISSION_EVENT_SMS_PREFERENCES, permission.READ_SMS));
            }
            if (!checkEventLocation(context, event)) {
                permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_FINE_LOCATION));
                //permissions.add(new PermissionType(PERMISSION_EVENT_SCANNING_PREFERENCES, permission.ACCESS_COARSE_LOCATION));
            }

            return permissions;
        }
        else
            return permissions;
    }


    public static boolean checkContacts(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return (ContextCompat.checkSelfPermission(context, permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
        }
        else
            return true;
    }

    public static boolean checkEventCallContacts(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (event._eventPreferencesCall._enabled)
                return (ContextCompat.checkSelfPermission(context, permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkEventSMSContacts(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (event._eventPreferencesSMS._enabled)
                return (ContextCompat.checkSelfPermission(context, permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkCalendar(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return (ContextCompat.checkSelfPermission(context, permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED);
        }
        else
            return true;
    }

    public static boolean checkEventCalendar(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (event._eventPreferencesCalendar._enabled)
                return (ContextCompat.checkSelfPermission(context, permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED);
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkEventSMSBroadcast(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (event._eventPreferencesSMS._enabled) {
                boolean granted = (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED);
                granted = granted && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkLocation(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            //boolean granted = (ContextCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            return granted;
        }
        else
            return true;
    }

    public static boolean checkEventLocation(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if ((event._eventPreferencesWifi._enabled &&
                    ((event._eventPreferencesWifi._connectionType == EventPreferencesWifi.CTYPE_INFRONT) ||
                     (event._eventPreferencesWifi._connectionType == EventPreferencesWifi.CTYPE_NOTINFRONT)))||
                (event._eventPreferencesBluetooth._enabled &&
                    ((event._eventPreferencesBluetooth._connectionType == EventPreferencesBluetooth.CTYPE_INFRONT) ||
                     (event._eventPreferencesBluetooth._connectionType == EventPreferencesBluetooth.CTYPE_NOTINFRONT))) ||
                (event._eventPreferencesLocation._enabled) ||
                (event._eventPreferencesMobileCells._enabled)) {
                return (ContextCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                //return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean checkEventPhoneBroadcast(Context context, Event event) {
        if (event == null) return true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (event._eventPreferencesCall._enabled || event._eventPreferencesOrientation._enabled) {
                boolean granted = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
                granted = granted && (ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED);
                return granted;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static boolean grantProfilePermissions(Context context, Profile profile, boolean mergedProfile,
                                                  boolean onlyNotification,
                                                  boolean forGUI, boolean monochrome, int monochromeValue,
                                                  int startupSource, boolean interactive, Activity activity,
                                                  boolean log, boolean activateProfile) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            List<PermissionType> permissions = checkProfilePermissions(context, profile);
            GlobalData.logE("Permissions.grantProfilePermissions", "permissions.size()=" + permissions.size());
            GlobalData.logE("Permissions.grantProfilePermissions", "startupSource=" + startupSource);
            if (permissions.size() > 0) {
                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_PROFILE);
                intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
                intent.putExtra(EXTRA_MERGED_PROFILE, mergedProfile);
                if (onlyNotification)
                    GlobalData.addMergedPermissions(context, permissions);
                else
                    intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, onlyNotification);
                intent.putExtra(EXTRA_FOR_GUI, forGUI);
                intent.putExtra(EXTRA_MONOCHROME, monochrome);
                intent.putExtra(EXTRA_MONOCHROME_VALUE, monochromeValue);
                intent.putExtra(GlobalData.EXTRA_STARTUP_SOURCE, startupSource);
                intent.putExtra(EXTRA_INTERACTIVE, interactive);
                intent.putExtra(EXTRA_LOG, log);
                intent.putExtra(EXTRA_ACTIVATE_PROFILE, activateProfile);
                if (!onlyNotification)
                    profileActivationActivity = activity;
                else
                    profileActivationActivity = null;
                context.startActivity(intent);
            }
            return permissions.size() == 0;
        }
        else
            return true;
    }

    public static boolean grantInstallTonePermissions(Context context, boolean onlyNotification) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkInstallTone(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_INSTALL_TONE, permission.WRITE_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_INSTALL_TONE);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, onlyNotification);
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantPlayRingtoneNotificationPermissions(Context context, boolean onlyNotification) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkInstallTone(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_PLAY_RINGTONE_NOTIFICATION, permission.READ_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_PLAY_RINGTONE_NOTIFICATION);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, onlyNotification);
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantWallpaperPermissions(Context context, ImageViewPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkGallery(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_PROFILE_WALLPAPER, permission.READ_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_WALLPAPER);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                imageViewPreference = preference;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantCustomProfileIconPermissions(Context context, ProfileIconPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkGallery(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_PROFILE_CUSTOM_PROFILE_ICON, permission.READ_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_CUSTOM_PROFILE_ICON);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                profileIconPreference = preference;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantBrightnessDialogPermissions(Context context, BrightnessDialogPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkScreenBrightness(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_PROFILE_SCREEN_BRIGHTNESS, permission.WRITE_SETTINGS));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_BRIGHTNESS_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                brightnessDialogPreference = preference;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantExportPermissions(Context context, EditorProfilesActivity editor) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkExport(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EXPORT, permission.WRITE_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_EXPORT);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                editorActivity = editor;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantImportPermissions(Context context, EditorProfilesActivity editor, String applicationDataPath) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkImport(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_IMPORT, permission.READ_EXTERNAL_STORAGE));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_IMPORT);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                intent.putExtra(EXTRA_APPLICATION_DATA_PATH, applicationDataPath);
                editorActivity = editor;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantEventPermissions(Context context, Event event,
                                                  boolean onlyNotification) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            List<PermissionType> permissions = checkEventPermissions(context, event);
            if (permissions.size() > 0) {
                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_EVENT);
                intent.putExtra(GlobalData.EXTRA_EVENT_ID, event._id);
                if (onlyNotification)
                    GlobalData.addMergedPermissions(context, permissions);
                else
                    intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, onlyNotification);
                context.startActivity(intent);
            }
            return permissions.size() == 0;
        }
        else
            return true;
    }

    public static boolean grantWifiScanDialogPermissions(Context context, WifiSSIDPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkLocation(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_FINE_LOCATION));
                //permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_COARSE_LOCATION));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_WIFI_BT_SCAN_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                wifiSSIDPreference = preference;
                bluetoothNamePreference = null;
                locationGeofenceEditorActivity = null;
                mobileCellsPreference = null;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantBluetoothScanDialogPermissions(Context context, BluetoothNamePreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkLocation(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_FINE_LOCATION));
                //permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_COARSE_LOCATION));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_WIFI_BT_SCAN_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                bluetoothNamePreference = preference;
                wifiSSIDPreference = null;
                locationGeofenceEditorActivity = null;
                mobileCellsPreference = null;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantCalendarDialogPermissions(Context context, CalendarsMultiSelectDialogPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkCalendar(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_CALENDAR_PREFERENCES, permission.READ_CALENDAR));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_CALENDAR_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                calendarsMultiSelectDialogPreference = preference;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantContactsDialogPermissions(Context context, ContactsMultiSelectDialogPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkContacts(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_CONTACTS, permission.READ_CONTACTS));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_CONTACT_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                contactsMultiSelectDialogPreference = preference;
                contactGroupsMultiSelectDialogPreference = null;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantContactGroupsDialogPermissions(Context context, ContactGroupsMultiSelectDialogPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkContacts(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_CONTACTS, permission.READ_CONTACTS));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_CONTACT_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                contactGroupsMultiSelectDialogPreference = preference;
                contactsMultiSelectDialogPreference = null;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantLocationGeofenceEditorPermissions(Context context, LocationGeofenceEditorActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkLocation(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_FINE_LOCATION));
                //permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_COARSE_LOCATION));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_LOCATION_GEOFENCE_EDITOR_ACTIVITY);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                bluetoothNamePreference = null;
                wifiSSIDPreference = null;
                locationGeofenceEditorActivity = activity;
                mobileCellsPreference = null;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static boolean grantMobileCellsDialogPermissions(Context context, MobileCellsPreference preference) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            boolean granted = checkLocation(context);
            if (!granted) {
                List<PermissionType> permissions = new ArrayList<PermissionType>();
                permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_FINE_LOCATION));
                //permissions.add(new PermissionType(PERMISSION_EVENT_LOCATION_PREFERENCES, permission.ACCESS_COARSE_LOCATION));

                Intent intent = new Intent(context, GrantPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // this close all activities with same taskAffinity
                intent.putExtra(EXTRA_GRANT_TYPE, GRANT_TYPE_MOBILE_CELLS_SCAN_DIALOG);
                intent.putParcelableArrayListExtra(EXTRA_PERMISSION_TYPES, (ArrayList<PermissionType>) permissions);
                intent.putExtra(EXTRA_ONLY_NOTIFICATION, false);
                wifiSSIDPreference = null;
                bluetoothNamePreference = null;
                locationGeofenceEditorActivity = null;
                mobileCellsPreference = preference;
                context.startActivity(intent);
            }
            return granted;
        }
        else
            return true;
    }

    public static void removeProfileNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GlobalData.GRANT_PROFILE_PERMISSIONS_NOTIFICATION_ID);
    }

    public static void removeInstallToneNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GlobalData.GRANT_INSTALL_TONE_PERMISSIONS_NOTIFICATION_ID);
    }

    public static void removePlayRingtoneNotificationNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GlobalData.GRANT_PLAY_RINGTONE_NOTIFICATION_PERMISSIONS_NOTIFICATION_ID);
    }

    public static void removeEventNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GlobalData.GRANT_EVENT_PERMISSIONS_NOTIFICATION_ID);
    }

    public static void removeNotifications(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GlobalData.GRANT_PROFILE_PERMISSIONS_NOTIFICATION_ID);
        notificationManager.cancel(GlobalData.GRANT_INSTALL_TONE_PERMISSIONS_NOTIFICATION_ID);
        notificationManager.cancel(GlobalData.GRANT_EVENT_PERMISSIONS_NOTIFICATION_ID);
    }

    public static void releaseReferences() {
        profileActivationActivity = null;
        imageViewPreference = null;
        profileIconPreference = null;
        editorActivity = null;
        wifiSSIDPreference = null;
        bluetoothNamePreference = null;
        calendarsMultiSelectDialogPreference = null;
        contactsMultiSelectDialogPreference = null;
        contactGroupsMultiSelectDialogPreference = null;
        mobileCellsPreference = null;
    }
}
