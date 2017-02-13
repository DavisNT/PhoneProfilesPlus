package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
import java.util.TimeZone;

public class RadioSwitchBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "radioSwitch";

    //private static ContentObserver smsObserver;
    //private static ContentObserver mmsObserver;
    //private static int mmsCount;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        PPApplication.logE("##### RadioSwitchBroadcastReceiver.onReceive", "xxx");

        int radioSwitchType = intent.getIntExtra(PPApplication.EXTRA_EVENT_RADIO_SWITCH_TYPE, 0);

        Calendar now = Calendar.getInstance();
        int gmtOffset = TimeZone.getDefault().getRawOffset();
        long time = now.getTimeInMillis() + gmtOffset;

        startService(context, radioSwitchType, time);
    }

    private static void startService(Context context, int radioSwitchType, long time)
    {
        if (!PPApplication.getApplicationStarted(context, true))
            // application is not started
            return;

        PPApplication.loadPreferences(context);

        if (PPApplication.getGlobalEventsRuning(context))
        {
            PPApplication.logE("@@@ RadioSwitchBroadcastReceiver.startService","xxx");

            /*boolean smsEventsExists = false;

            DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
            smsEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_SMS) > 0;
            PPApplication.logE("SMSBroadcastReceiver.onReceive","smsEventsExists="+smsEventsExists);
            dataWrapper.invalidateDataWrapper();

            if (smsEventsExists)
            {*/
                // start service
                Intent eventsServiceIntent = new Intent(context, EventsService.class);
                eventsServiceIntent.putExtra(PPApplication.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
                eventsServiceIntent.putExtra(PPApplication.EXTRA_EVENT_RADIO_SWITCH_TYPE, radioSwitchType);
                eventsServiceIntent.putExtra(PPApplication.EXTRA_EVENT_RADIO_SWITCH_DATE, time);
                startWakefulService(context, eventsServiceIntent);
            //}
        }
    }

}
