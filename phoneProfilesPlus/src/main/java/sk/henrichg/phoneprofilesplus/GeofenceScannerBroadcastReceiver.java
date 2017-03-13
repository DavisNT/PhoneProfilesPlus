package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GeofenceScannerBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "geofenceScanner";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        PPApplication.logE("##### GeofenceScannerBroadcastReceiver.onReceive", "xxx");

        if (!PPApplication.getApplicationStarted(context, true))
            // application is not started
            return;

        //PPApplication.loadPreferences(context);

        if (Event.getGlobalEventsRuning(context))
        {
            PPApplication.logE("@@@ GeofenceScannerBroadcastReceiver.onReceive", "xxx");

            if ((PhoneProfilesService.instance != null) && PhoneProfilesService.isGeofenceScannerStarted())
                PhoneProfilesService.geofencesScanner.updateGeofencesInDB();

            // start service
            Intent eventsServiceIntent = new Intent(context, EventsService.class);
            eventsServiceIntent.putExtra(EventsService.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
            startWakefulService(context, eventsServiceIntent);
        }

    }

}
