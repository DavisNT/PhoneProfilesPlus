package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

public class Profile {
	
	public long _id;
	public String _name;
	public String _icon;
	public boolean _checked;
	public int _porder;
	public int _duration;
	public int _afterDurationDo;
	public int _volumeRingerMode;
	public int _volumeZenMode;
	public String _volumeRingtone;
	public String _volumeNotification;
	public String _volumeMedia;
	public String _volumeAlarm;
	public String _volumeSystem;
	public String _volumeVoice;
	public int _soundRingtoneChange;
	public String _soundRingtone;
	public int _soundNotificationChange;
	public String _soundNotification;
	public int _soundAlarmChange;
	public String _soundAlarm;
	public int _deviceAirplaneMode;
	public int _deviceMobileData;
	public int _deviceMobileDataPrefs;
	public int _deviceWiFi;
	public int _deviceBluetooth;
	public int _deviceGPS;
	public int _deviceLocationServicePrefs;
	public int _deviceScreenTimeout;
	public String _deviceBrightness;
	public int _deviceWallpaperChange;
	public String _deviceWallpaper;
	public int _deviceRunApplicationChange;
	public String _deviceRunApplicationPackageName;
	public int _deviceAutosync;
	public boolean _showInActivator;
	public int _deviceAutoRotate;
	public int _volumeSpeakerPhone;
	public int _deviceNFC;
	public int _deviceKeyguard;
    public int _vibrationOnTouch;
	
	public Bitmap _iconBitmap;
	public Bitmap _preferencesIndicator;
	
	public static final int AFTERDURATIONDO_NOTHING = 0; 
	public static final int AFTERDURATIONDO_UNDOPROFILE = 1;
	public static final int AFTERDURATIONDO_BACKGROUNPROFILE = 2;
	public static final int AFTERDURATIONDO_RESTARTEVENTS = 3;
	
	public static final int BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET = -99;
	
	
	// Empty constructorn
	public Profile(){
		
	}
	
	// constructor
	public Profile(long id, 
			       String name, 
			       String icon, 
			       Boolean checked, 
			       int porder,
 			   	   int volumeRingerMode,
			   	   String volumeRingtone,
			   	   String volumeNotification,
			   	   String volumeMedia,
			   	   String volumeAlarm,
			   	   String volumeSystem,
			   	   String volumeVoice,
			   	   int soundRingtoneChange,
			   	   String soundRingtone,
			   	   int soundNotificationChange,
			   	   String soundNotification,
			   	   int soundAlarmChange,
			   	   String soundAlarm,
			   	   int deviceAirplaneMode,
			   	   int deviceWiFi,
			   	   int deviceBluetooth,
			   	   int deviceScreenTimeout,
			   	   String deviceBrightness,
			   	   int deviceWallpaperChange,
			   	   String deviceWallpaper,
			   	   int deviceMobileData,
			   	   int deviceMobileDataPrefs,
			   	   int deviceGPS,
			   	   int deviceRunApplicationChange,
			   	   String deviceRunApplicationPackageName,
			   	   int deviceAutosync,
			   	   boolean showInActivator,
			   	   int deviceAutoRotate,
			   	   int deviceLocationServicePrefs,
			   	   int volumeSpeakerPhone,
			   	   int deviceNFC,
			   	   int duration,
			   	   int afterDurationDo,
 			   	   int volumeZenMode,
 			   	   int deviceKeyguard,
                   int vibrationOnTouch)
	{
		this._id = id;
		this._name = name;
		this._icon = icon;
		this._checked = checked; 
		this._porder = porder;
		this._volumeRingerMode = volumeRingerMode;
		this._volumeZenMode = volumeZenMode;
		this._volumeRingtone = volumeRingtone;
		this._volumeNotification = volumeNotification;
		this._volumeMedia = volumeMedia;
		this._volumeAlarm = volumeAlarm;
		this._volumeSystem = volumeSystem;
		this._volumeVoice = volumeVoice;
		this._soundRingtoneChange = soundRingtoneChange;
		this._soundRingtone = soundRingtone;
		this._soundNotificationChange = soundNotificationChange;
		this._soundNotification = soundNotification;
		this._soundAlarmChange = soundAlarmChange;
		this._soundAlarm = soundAlarm;
		this._deviceAirplaneMode = deviceAirplaneMode;
		this._deviceMobileData = deviceMobileData;
		this._deviceMobileDataPrefs = deviceMobileDataPrefs;
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceGPS = deviceGPS;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
		this._deviceRunApplicationChange = deviceRunApplicationChange;
		this._deviceRunApplicationPackageName = deviceRunApplicationPackageName;
		this._deviceAutosync = deviceAutosync;
		this._showInActivator = showInActivator;
		this._deviceAutoRotate = deviceAutoRotate;
		this._deviceLocationServicePrefs = deviceLocationServicePrefs;
		this._volumeSpeakerPhone = volumeSpeakerPhone;
		this._deviceNFC = deviceNFC;
		this._duration = duration;
		this._afterDurationDo = afterDurationDo;
		this._deviceKeyguard = deviceKeyguard;
        this._deviceKeyguard = deviceKeyguard;
        this._vibrationOnTouch = vibrationOnTouch;

		this._iconBitmap = null;
		this._preferencesIndicator = null;
	}
	
	// constructor
	public Profile(String name, 
			       String icon, 
			       Boolean checked, 
			       int porder,
 			   	   int volumeRingerMode,
			   	   String volumeRingtone,
			   	   String volumeNotification,
			   	   String volumeMedia,
			   	   String volumeAlarm,
			   	   String volumeSystem,
			   	   String volumeVoice,
			   	   int soundRingtoneChange,
			   	   String soundRingtone,
			   	   int soundNotificationChange,
			   	   String soundNotification,
			   	   int soundAlarmChange,
			   	   String soundAlarm,
			   	   int deviceAirplaneMode,
			   	   int deviceWiFi,
			   	   int deviceBluetooth,
			   	   int deviceScreenTimeout,
			   	   String deviceBrightness,
			   	   int deviceWallpaperChange,
			   	   String deviceWallpaper,
			   	   int deviceMobileData,
			   	   int deviceMobileDataPrefs,
			   	   int deviceGPS,
			   	   int deviceRunApplicationChange,
			   	   String deviceRunApplicationPackageName,
			   	   int deviceAutosync,
			   	   boolean showInActivator,
			   	   int deviceAutoRotate,
			   	   int deviceLocationServicePrefs,
			   	   int volumeSpeakerPhone,
			   	   int deviceNFC,
			   	   int duration,
			   	   int afterDurationDo,
 			   	   int volumeZenMode,
 			   	   int deviceKeyguard,
                   int vibrationOnTouch)
	{
		this._name = name;
		this._icon = icon;
		this._checked = checked; 
		this._porder = porder;
		this._volumeRingerMode = volumeRingerMode;
		this._volumeZenMode = volumeZenMode;
		this._volumeRingtone = volumeRingtone;
		this._volumeNotification = volumeNotification;
		this._volumeMedia = volumeMedia;
		this._volumeAlarm = volumeAlarm;
		this._volumeSystem = volumeSystem;
		this._volumeVoice = volumeVoice;
		this._soundRingtoneChange = soundRingtoneChange;
		this._soundRingtone = soundRingtone;
		this._soundNotificationChange = soundNotificationChange;
		this._soundNotification = soundNotification;
		this._soundAlarmChange = soundAlarmChange;
		this._soundAlarm = soundAlarm;
		this._deviceAirplaneMode = deviceAirplaneMode;
		this._deviceMobileData = deviceMobileData;
		this._deviceMobileDataPrefs = deviceMobileDataPrefs;
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceGPS = deviceGPS;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
		this._deviceRunApplicationChange = deviceRunApplicationChange;
		this._deviceRunApplicationPackageName = deviceRunApplicationPackageName;
		this._deviceAutosync = deviceAutosync;
		this._showInActivator = showInActivator;
		this._deviceAutoRotate = deviceAutoRotate;
		this._deviceLocationServicePrefs = deviceLocationServicePrefs;
		this._volumeSpeakerPhone = volumeSpeakerPhone;
		this._deviceNFC = deviceNFC;
		this._duration = duration;
		this._afterDurationDo = afterDurationDo;
		this._deviceKeyguard = deviceKeyguard;
        this._vibrationOnTouch = vibrationOnTouch;

		this._iconBitmap = null;
		this._preferencesIndicator = null;
	}
	
	public void copyProfile(Profile profile)
	{
		this._id = profile._id;
		this._name = profile._name;
		this._icon = profile._icon;
		this._checked = profile._checked; 
		this._porder = profile._porder;
		this._volumeRingerMode = profile._volumeRingerMode;
		this._volumeZenMode = profile._volumeZenMode;
		this._volumeRingtone = profile._volumeRingtone;
		this._volumeNotification = profile._volumeNotification;
		this._volumeMedia = profile._volumeMedia;
		this._volumeAlarm = profile._volumeAlarm;
		this._volumeSystem = profile._volumeSystem;
		this._volumeVoice = profile._volumeVoice;
		this._soundRingtoneChange = profile._soundRingtoneChange;
		this._soundRingtone = profile._soundRingtone;
		this._soundNotificationChange = profile._soundNotificationChange;
		this._soundNotification = profile._soundNotification;
		this._soundAlarmChange = profile._soundAlarmChange;
		this._soundAlarm = profile._soundAlarm;
		this._deviceAirplaneMode = profile._deviceAirplaneMode;
		this._deviceMobileData = profile._deviceMobileData;
		this._deviceMobileDataPrefs = profile._deviceMobileDataPrefs;
		this._deviceWiFi = profile._deviceWiFi;
		this._deviceBluetooth = profile._deviceBluetooth;
		this._deviceGPS = profile._deviceGPS;
		this._deviceScreenTimeout = profile._deviceScreenTimeout;
		this._deviceBrightness = profile._deviceBrightness;
		this._deviceWallpaperChange = profile._deviceWallpaperChange;
		this._deviceWallpaper = profile._deviceWallpaper;
		this._deviceRunApplicationChange = profile._deviceRunApplicationChange;
		this._deviceRunApplicationPackageName = profile._deviceRunApplicationPackageName;
		this._deviceAutosync = profile._deviceAutosync;
		this._showInActivator = profile._showInActivator;
		this._deviceAutoRotate = profile._deviceAutoRotate;
		this._deviceLocationServicePrefs = profile._deviceLocationServicePrefs;
		this._volumeSpeakerPhone = profile._volumeSpeakerPhone;
		this._deviceNFC = profile._deviceNFC;
		this._duration = profile._duration;
		this._afterDurationDo = profile._afterDurationDo;
		this._deviceKeyguard = profile._deviceKeyguard;
        this._vibrationOnTouch = profile._vibrationOnTouch;
		
		this._iconBitmap = profile._iconBitmap;
		this._preferencesIndicator = profile._preferencesIndicator;
	}

    public void mergeProfiles(long withProfileId, DataWrapper dataWrapper)
    {
        Profile withProfile = dataWrapper.getProfileById(withProfileId);

        if (withProfile != null) {
            this._id = withProfile._id;
            this._name = withProfile._name;
            this._icon = withProfile._icon;
            this._iconBitmap = withProfile._iconBitmap;
            this._preferencesIndicator = withProfile._preferencesIndicator;

            if (withProfile._volumeRingerMode != 0)
                this._volumeRingerMode = withProfile._volumeRingerMode;
            if (withProfile._volumeZenMode != 0)
                this._volumeZenMode = withProfile._volumeZenMode;
            if (withProfile.getVolumeRingtoneChange())
                this._volumeRingtone = withProfile._volumeRingtone;
            if (withProfile.getVolumeNotificationChange())
                this._volumeNotification = withProfile._volumeNotification;
            if (withProfile.getVolumeAlarmChange())
                this._volumeAlarm = withProfile._volumeAlarm;
            if (withProfile.getVolumeMediaChange())
                this._volumeMedia = withProfile._volumeMedia;
            if (withProfile.getVolumeSystemChange())
                this._volumeSystem = withProfile._volumeSystem;
            if (withProfile.getVolumeVoiceChange())
                this._volumeVoice = withProfile._volumeVoice;
            if (withProfile._soundRingtoneChange != 0) {
                this._soundRingtoneChange = 1;
                this._soundRingtone = withProfile._soundRingtone;
            }
            if (withProfile._soundNotificationChange != 0) {
                this._soundNotificationChange = 1;
                this._soundNotification = withProfile._soundNotification;
            }
            if (withProfile._soundAlarmChange != 0) {
                this._soundAlarmChange = 1;
                this._soundAlarm = withProfile._soundAlarm;
            }
            if (withProfile._deviceAirplaneMode != 0) {
                if (withProfile._deviceAirplaneMode != 3) // toggle
                    this._deviceAirplaneMode = withProfile._deviceAirplaneMode;
                else {
                    if (this._deviceAirplaneMode == 1)
                        this._deviceAirplaneMode = 2;
                    else if (this._deviceAirplaneMode == 2)
                        this._deviceAirplaneMode = 1;
                }
            }
            if (withProfile._deviceAutosync != 0) {
                if (withProfile._deviceAutosync != 3) // toggle
                    this._deviceAutosync = withProfile._deviceAutosync;
                else {
                    if (this._deviceAutosync == 1)
                        this._deviceAutosync = 2;
                    else if (this._deviceAutosync == 2)
                        this._deviceAutosync = 1;
                }
            }
            if (withProfile._deviceMobileData != 0) {
                if (withProfile._deviceMobileData != 3) // toggle
                    this._deviceMobileData = withProfile._deviceMobileData;
                else {
                    if (this._deviceMobileData == 1)
                        this._deviceMobileData = 2;
                    else if (this._deviceMobileData == 2)
                        this._deviceMobileData = 1;
                }
            }
            if (withProfile._deviceMobileDataPrefs != 0)
                this._deviceMobileDataPrefs = withProfile._deviceMobileDataPrefs;
            if (withProfile._deviceWiFi != 0) {
                if (withProfile._deviceWiFi != 3) // toggle
                    this._deviceWiFi = withProfile._deviceWiFi;
                else {
                    if (this._deviceWiFi == 1)
                        this._deviceWiFi = 2;
                    else if (this._deviceWiFi == 2)
                        this._deviceWiFi = 1;
                }
            }
            if (withProfile._deviceBluetooth != 0) {
                if (withProfile._deviceBluetooth != 3) // toggle
                    this._deviceBluetooth = withProfile._deviceBluetooth;
                else {
                    if (this._deviceBluetooth == 1)
                        this._deviceBluetooth = 2;
                    else if (this._deviceBluetooth == 2)
                        this._deviceBluetooth = 1;
                }
            }
            if (withProfile._deviceGPS != 0) {
                if (withProfile._deviceGPS != 3) // toggle
                    this._deviceGPS = withProfile._deviceGPS;
                else {
                    if (this._deviceGPS == 1)
                        this._deviceGPS = 2;
                    else if (this._deviceGPS == 2)
                        this._deviceGPS = 1;
                }
            }
            if (withProfile._deviceLocationServicePrefs != 0)
                this._deviceLocationServicePrefs = withProfile._deviceLocationServicePrefs;
            if (withProfile._deviceScreenTimeout != 0)
                this._deviceScreenTimeout = withProfile._deviceScreenTimeout;
            if (withProfile.getDeviceBrightnessChange())
                this._deviceBrightness = withProfile._deviceBrightness;
            if (withProfile._deviceAutoRotate != 0)
                this._deviceAutoRotate = withProfile._deviceAutoRotate;
            if (withProfile._deviceRunApplicationChange != 0) {
                this._deviceRunApplicationChange = 1;
                this._deviceRunApplicationPackageName = withProfile._deviceRunApplicationPackageName;
            }
            if (withProfile._deviceWallpaperChange != 0) {
                this._deviceWallpaperChange = 1;
                this._deviceWallpaper = withProfile._deviceWallpaper;
            }
            if (withProfile._volumeSpeakerPhone != 0)
                this._volumeSpeakerPhone = withProfile._volumeSpeakerPhone;
            if (withProfile._deviceNFC != 0) {
                if (withProfile._deviceNFC != 3) // toggle
                    this._deviceNFC = withProfile._deviceNFC;
                else {
                    if (this._deviceNFC == 1)
                        this._deviceNFC = 2;
                    else if (this._deviceNFC == 2)
                        this._deviceNFC = 1;
                }
            }
            if (withProfile._deviceKeyguard != 0)
                this._deviceKeyguard = withProfile._deviceKeyguard;
            if (withProfile._vibrationOnTouch != 0)
                this._vibrationOnTouch = withProfile._vibrationOnTouch;
        }
    }

    // getting icon identifier
	public String getIconIdentifier()
	{
		String value;
		try {
			String[] splits = _icon.split("\\|");
			value = splits[0];
		} catch (Exception e) {
			value = "ic_profile_default";
		}
		return value;
	}
	
	// getting where icon is resource id
	public boolean getIsIconResourceID()
	{
		boolean value;
		try {
			String[] splits = _icon.split("\\|");
			value = (splits[1].equals("1")) ? true : false;

		} catch (Exception e) {
			value = true;
		}
		return value;
	}
	
	public int getVolumeRingtoneValue()
	{
		int value;
		try {
			String[] splits = _volumeRingtone.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public boolean getVolumeRingtoneChange()
	{
		int value;
		try {
			String[] splits = _volumeRingtone.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeRingtoneDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeRingtone.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getVolumeNotificationValue()
	{
		int value;
		try {
			String[] splits = _volumeNotification.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeNotificationChange()
	{
		int value;
		try {
			String[] splits = _volumeNotification.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeNotificationDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeNotification.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getVolumeMediaValue()
	{
		int value;
		try {
			String[] splits = _volumeMedia.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeMediaChange()
	{
		int value;
		try {
			String[] splits = _volumeMedia.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeMediaDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeMedia.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getVolumeAlarmValue()
	{
		int value;
		try {
			String[] splits = _volumeAlarm.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeAlarmChange()
	{
		int value;
		try {
			String[] splits = _volumeAlarm.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeAlarmDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeAlarm.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getVolumeSystemValue()
	{
		int value;
		try {
			String[] splits = _volumeSystem.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeSystemChange()
	{
		int value;
		try {
			String[] splits = _volumeSystem.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeSystemDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeSystem.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getVolumeVoiceValue()
	{
		int value;
		try {
			String[] splits = _volumeVoice.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeVoiceChange()
	{
		int value;
		try {
			String[] splits = _volumeVoice.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public boolean getVolumeVoiceDefaultProfile()
	{
		int value;
		try {
			String[] splits = _volumeVoice.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}
	
	public int getDeviceBrightnessValue()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public boolean getDeviceBrightnessChange()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}

	public boolean getDeviceBrightnessDefaultProfile()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[3]);
		} catch (Exception e) {
			value = 0;
		}
		return (value == 1) ? true : false;
	}

	public boolean getDeviceBrightnessAutomatic()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 1) ? true : false;
	}
	
	public static int convertPercentsToBrightnessManualValue(int perc, Context context)
	{
		int maximumValue = ActivateProfileHelper.getMaximumScreenBrightnessSetting(context);
		int minimumValue = ActivateProfileHelper.getMinimumScreenBrightnessSetting(context);

		int value;

		if (perc == BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
			// brightness is not set, change it to default manual brightness value
			value = Settings.System.getInt(context.getContentResolver(), 
											Settings.System.SCREEN_BRIGHTNESS, 128);
		else
			value = Math.round((float)(maximumValue - minimumValue) / 100 * perc) + minimumValue;
		
		return value;
	}
	
	public int getDeviceBrightnessManualValue(Context context)
	{
		int perc = getDeviceBrightnessValue();
		return convertPercentsToBrightnessManualValue(perc, context);
	}

	public static float convertPercentsToBrightnessAdaptiveValue(int perc, Context context)
	{
		float value;
		
		if (perc == BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
			// brightness is not set, change it to default adaptive brightness value
			value = Settings.System.getFloat(context.getContentResolver(), 
								ActivateProfileHelper.ADAPTIVE_BRIGHTNESS_SETTING_NAME, 0f);
		else
			value = (perc - 50) / 50f;
		
		return value;
	}
	
	public float getDeviceBrightnessAdaptiveValue(Context context)
	{
		int perc = getDeviceBrightnessValue();
		return convertPercentsToBrightnessAdaptiveValue(perc, context);
	}
	
	public static long convertBrightnessToPercents(int value, 
			int maxValue, int minValue, Context context)
	{
		long perc;
		if (value == BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
			perc = value; // keep BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET
		else
			perc = Math.round((float)(value-minValue) / (maxValue - minValue) * 100.0);
		
		return perc;
	}
	
	public void setDeviceBrightnessManualValue(int value, Context context)
	{
		int maxValue = ActivateProfileHelper.getMaximumScreenBrightnessSetting(context);
		int minValue = ActivateProfileHelper.getMinimumScreenBrightnessSetting(context);

		long perc = convertBrightnessToPercents(value, maxValue, minValue, context);
		
		//value|noChange|automatic|defaultProfile
		String[] splits = _deviceBrightness.split("\\|");
		// hm, found brightness values without default profile :-/ 
		if (splits.length == 4)
			_deviceBrightness = String.valueOf(perc)+"|"+splits[1]+"|"+splits[2]+"|"+ splits[3];
		else
			_deviceBrightness = String.valueOf(perc)+"|"+splits[1]+"|"+splits[2]+"|0";
	}
	
	public void setDeviceBrightnessAdaptiveValue(float value)
	{
		long perc;
		if (value == BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
			perc = Math.round(value); // keep BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET
		else
			perc = Math.round(value * 50 + 50);

		//value|noChange|automatic|defaultProfile
		String[] splits = _deviceBrightness.split("\\|");
		// hm, found brightness values without default profile :-/ 
		if (splits.length == 4)
			_deviceBrightness = String.valueOf(perc)+"|"+splits[1]+"|"+splits[2]+"|"+ splits[3];
		else
			_deviceBrightness = String.valueOf(perc)+"|"+splits[1]+"|"+splits[2]+"|0";
	}
	
	// getting wallpaper identifikator
	public String getDeviceWallpaperIdentifier()
	{
		String value;
		try {
			String[] splits = _deviceWallpaper.split("\\|");
			value = splits[0];
		} catch (Exception e) {
			value = "-";
		}
		return value;
	}
	
	
	//----------------------------------
	
	public void generateIconBitmap(Context context, boolean monochrome, int monochromeValue)
	{
        if (!getIsIconResourceID())
        {
        	releaseIconBitmap();
        	
        	Resources resources = context.getResources();
    		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		_iconBitmap = BitmapManipulator.resampleBitmap(getIconIdentifier(), width, height);
    		
    		if (_iconBitmap == null)
    		{
    			// no icon found, set default icon
				_icon = "ic_profile_default|1";
    			if (monochrome)
    			{
    	        	int iconResource = context.getResources().getIdentifier(getIconIdentifier(), "drawable", context.getPackageName());
    	        	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
    	        	_iconBitmap = BitmapManipulator.monochromeBitmap(bitmap, monochromeValue, context);
    	        	// getIsIconResourceID must return false
    	        	_icon = getIconIdentifier() + "|0";
    			}
    		}
    		else
    		if (monochrome)
    			_iconBitmap = BitmapManipulator.grayscaleBitmap(_iconBitmap);
        }
        else
        if (monochrome)
        {
        	int iconResource = context.getResources().getIdentifier(getIconIdentifier(), "drawable", context.getPackageName());
        	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
        	_iconBitmap = BitmapManipulator.monochromeBitmap(bitmap, monochromeValue, context);
        	// getIsIconResourceID must return false
        	_icon = getIconIdentifier() + "|0";
        }
        else
        	_iconBitmap = null;
	}
	
	public void generatePreferencesIndicator(Context context, boolean monochrome, int monochromeValue)
	{
    	releasePreferencesIndicator();

    	_preferencesIndicator = ProfilePreferencesIndicator.paint(this, context);

    	if (monochrome)
    		_preferencesIndicator = BitmapManipulator.monochromeBitmap(_preferencesIndicator, monochromeValue, context);

	}
	
	public void releaseIconBitmap()
	{
    	if (_iconBitmap != null)
    	{
    		_iconBitmap.recycle();
    		_iconBitmap = null;
    	}
	}
	
	public void releasePreferencesIndicator()
	{
    	if (_preferencesIndicator != null)
    	{
    		_preferencesIndicator.recycle();
    		_preferencesIndicator = null;
    	}
	}
	
}
