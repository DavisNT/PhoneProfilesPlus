package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class EventTimeBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "eventTimeStart";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        PPApplication.logE("##### EventTimeBroadcastReceiver.onReceive", "xxx");

        if (!PPApplication.getApplicationStarted(context, true))
            // application is not started
            return;

        PPApplication.loadPreferences(context);

        if (PPApplication.getGlobalEventsRuning(context))
        {
            PPApplication.logE("@@@ EventTimeBroadcastReceiver.onReceive","xxx");

            /*boolean timeEventsExists = false;

            DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
            timeEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_TIME) > 0;
            PPApplication.logE("EventTimeBroadcastReceiver.onReceive","timeEventsExists="+timeEventsExists);
            dataWrapper.invalidateDataWrapper();

            if (timeEventsExists)
            {*/
                // start service
                Intent eventsServiceIntent = new Intent(context, EventsService.class);
                eventsServiceIntent.putExtra(EventsService.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
                startWakefulService(context, eventsServiceIntent);
            //}

        }

    }

}
