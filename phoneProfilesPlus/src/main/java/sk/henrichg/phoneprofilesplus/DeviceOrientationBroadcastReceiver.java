package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class DeviceOrientationBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "deviceOrientation";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        PPApplication.logE("##### DeviceOrientationBroadcastReceiver.onReceive", "xxx");

        if (!PPApplication.getApplicationStarted(context, true))
            // application is not started
            return;

        //PPApplication.loadPreferences(context);

        if (Event.getGlobalEventsRuning(context))
        {
            PPApplication.logE("@@@ DeviceOrientationBroadcastReceiver.onReceive", "-----------");

            if (PhoneProfilesService.mDeviceDistance == PhoneProfilesService.DEVICE_ORIENTATION_DEVICE_IS_NEAR)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "now device is NEAR.");
            else
            if (PhoneProfilesService.mDeviceDistance == PhoneProfilesService.DEVICE_ORIENTATION_DEVICE_IS_FAR)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "now device is FAR");
            else
            if (PhoneProfilesService.mDeviceDistance == PhoneProfilesService.DEVICE_ORIENTATION_UNKNOWN)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "unknown distance");

            if (PhoneProfilesService.mDisplayUp == PhoneProfilesService.DEVICE_ORIENTATION_DISPLAY_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(D) now screen is facing up.");
            if (PhoneProfilesService.mDisplayUp == PhoneProfilesService.DEVICE_ORIENTATION_DISPLAY_DOWN)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(D) now screen is facing down.");
            if (PhoneProfilesService.mDisplayUp == PhoneProfilesService.DEVICE_ORIENTATION_UNKNOWN)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(D) unknown display orientation.");

            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_DISPLAY_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now screen is facing up.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_DISPLAY_DOWN)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now screen is facing down.");

            if (PhoneProfilesService.mSideUp == PhoneProfilesService.mDisplayUp)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now device is horizontal.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_UP_SIDE_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now up side is facing up.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_DOWN_SIDE_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now down side is facing up.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_RIGHT_SIDE_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now right side is facing up.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_LEFT_SIDE_UP)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) now left side is facing up.");
            if (PhoneProfilesService.mSideUp == PhoneProfilesService.DEVICE_ORIENTATION_UNKNOWN)
                PPApplication.logE("DeviceOrientationBroadcastReceiver.onReceive", "(S) unknown side.");

            PPApplication.logE("@@@ DeviceOrientationBroadcastReceiver.onReceive", "-----------");

            DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
            if (dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_ORIENTATION) == 0) {
                if (PhoneProfilesService.instance != null) {
                    PPApplication.stopOrientationScanner(dataWrapper.context);
                }
                dataWrapper.invalidateDataWrapper();
                return;
            }

            // start service
            Intent eventsServiceIntent = new Intent(context, EventsService.class);
            eventsServiceIntent.putExtra(EventsService.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
            startWakefulService(context, eventsServiceIntent);
        }

    }

}
