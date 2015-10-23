package sk.henrichg.phoneprofilesplus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class BluetoothLEScanBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String BROADCAST_RECEIVER_TYPE = "bluetoothScan";

    @Override
    public void onReceive(Context context, Intent intent) {

        //GlobalData.logE("#### BluetoothLEScanBroadcastReceiver.onReceive","xxx");
        GlobalData.logE("@@@ BluetoothLEScanBroadcastReceiver.onReceive", "----- start");

        //if (BluetoothScanAlarmBroadcastReceiver.bluetooth == null)
        //    BluetoothScanAlarmBroadcastReceiver.bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();

        if (!GlobalData.getApplicationStarted(context))
            // application is not started
            return;

        GlobalData.loadPreferences(context);

        if (GlobalData.getGlobalEventsRuning(context))
        {

            boolean scanStarted = (BluetoothScanAlarmBroadcastReceiver.getWaitForLEResults(context));

            if (scanStarted)
            {
                GlobalData.logE("@@@ BluetoothLEScanBroadcastReceiver.onReceive","xxx");

                BluetoothScanAlarmBroadcastReceiver.fillBoundedDevicesList(context);

                BluetoothScanAlarmBroadcastReceiver.setWaitForLEResults(context, false);

                int forceOneScan = GlobalData.getForceOneLEBluetoothScan(context);
                GlobalData.setForceOneLEBluetoothScan(context, GlobalData.FORCE_ONE_SCAN_DISABLED);

                if (forceOneScan != GlobalData.FORCE_ONE_SCAN_ENABLED) // not start service for force scan
                {
                    // start service
                    Intent eventsServiceIntent = new Intent(context, EventsService.class);
                    eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
                    startWakefulService(context, eventsServiceIntent);
                }

            }

        }

        GlobalData.logE("@@@ BluetoothLEScanBroadcastReceiver.onReceive","----- end");

    }

}
