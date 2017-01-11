package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WifiConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "wifiConnection";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        GlobalData.logE("##### WifiConnectionBroadcastReceiver.onReceive", "xxx");

        if (!GlobalData.getApplicationStarted(context, true))
            // application is not started
            return;

        GlobalData.loadPreferences(context);

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (info != null)
        {

            if (GlobalData.getGlobalEventsRuning(context))
            {
                GlobalData.logE("$$$ WifiConnectionBroadcastReceiver.onReceive","state="+info.getState());

                if ((info.getState() == NetworkInfo.State.CONNECTED) ||
                    (info.getState() == NetworkInfo.State.DISCONNECTED))
                {
                    //boolean isWifiAPEnabled = WifiApManager.isWifiAPEnabled(context);
                    //GlobalData.logE("$$$ WifiAP", "WifiConnectionBroadcastReceiver.onReceive-isWifiAPEnabled="+isWifiAPEnabled);

                    //if (!isWifiAPEnabled) {
                        if (!((WifiScanAlarmBroadcastReceiver.getScanRequest(context)) ||
                                (WifiScanAlarmBroadcastReceiver.getWaitForResults(context)) ||
                                (WifiScanAlarmBroadcastReceiver.getWifiEnabledForScan(context)))) {
                            // wifi is not scanned

                            GlobalData.logE("$$$ WifiConnectionBroadcastReceiver.onReceive", "wifi is not scanned");

                            /*DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
                            boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFICONNECTED) > 0;
                            dataWrapper.invalidateDataWrapper();

                            if (wifiEventsExists) {
                                GlobalData.logE("@@@ WifiConnectionBroadcastReceiver.onReceive", "wifiEventsExists=" + wifiEventsExists);
                            */
                                // start service
                                Intent eventsServiceIntent = new Intent(context, EventsService.class);
                                eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
                                startWakefulService(context, eventsServiceIntent);
                            //}
                        } else
                            GlobalData.logE("$$$ WifiConnectionBroadcastReceiver.onReceive", "wifi is scanned");
                    //}

                }
            }

        }
    }
}
