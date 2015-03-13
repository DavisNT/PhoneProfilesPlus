package sk.henrichg.phoneprofilesplus;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothStateChangedBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "bluetoothState";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### BluetoothStateChangedBroadcastReceiver.onReceive","xxx");
	
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;

		GlobalData.loadPreferences(context);
		
		int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

		if (GlobalData.getGlobalEventsRuning(context))
		{
    		GlobalData.logE("@@@ BluetoothStateChangedBroadcastReceiver.onReceive","state="+bluetoothState);

            if ((bluetoothState == BluetoothAdapter.STATE_ON) || (bluetoothState == BluetoothAdapter.STATE_OFF)) {

                DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);

                if (bluetoothState == BluetoothAdapter.STATE_ON)
                {
                    if ((!dataWrapper.getIsManualProfileActivation()) || GlobalData.getForceOneBluetoothScan(context))
                    {
                        if (BluetoothScanAlarmBroadcastReceiver.getScanRequest(context))
                        {
                            GlobalData.logE("@@@ BluetoothStateChangedBroadcastReceiver.onReceive", "startScan");
                            BluetoothScanAlarmBroadcastReceiver.startScan(context.getApplicationContext());
                        }
                        else
                        if (!BluetoothScanAlarmBroadcastReceiver.getWaitForResults(context))
                        {
                            // refresh bounded devices
                            BluetoothScanAlarmBroadcastReceiver.fillBoundedDevicesList(context);
                        }
                    }
                }

                boolean bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHCONNECTED) > 0;
                dataWrapper.invalidateDataWrapper();

                if (bluetoothEventsExists)
                {
                    GlobalData.logE("@@@ BluetoothStateChangedBroadcastReceiver.onReceive","bluetoothEventsExists="+bluetoothEventsExists);

                    // start service
                    Intent eventsServiceIntent = new Intent(context, EventsService.class);
                    eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
                    startWakefulService(context, eventsServiceIntent);
                }

            }
        }

        /*
		if (bluetoothState == BluetoothAdapter.STATE_OFF)
		{
			BluetoothScanAlarmBroadcastReceiver.stopScan(context);
		}
		*/
		
	}
}
