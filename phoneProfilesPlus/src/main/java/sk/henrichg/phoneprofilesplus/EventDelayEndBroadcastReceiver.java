package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class EventDelayEndBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "eventDelayEnd";

    @Override
    public void onReceive(Context context, Intent intent) {

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        GlobalData.logE("##### EventDelayEndBroadcastReceiver.onReceive", "xxx");

        if (!GlobalData.getApplicationStarted(context, true))
            // application is not started
            return;

        GlobalData.loadPreferences(context);

        if (GlobalData.getGlobalEventsRuning(context))
        {
            GlobalData.logE("@@@ EventDelayEndBroadcastReceiver.onReceive","xxx");

            // start service
            Intent eventsServiceIntent = new Intent(context, EventsService.class);
            eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
            startWakefulService(context, eventsServiceIntent);
        }

    }

}
