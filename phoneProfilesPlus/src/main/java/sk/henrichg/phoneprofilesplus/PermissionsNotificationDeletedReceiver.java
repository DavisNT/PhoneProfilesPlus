package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PermissionsNotificationDeletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        GlobalData.logE("##### PermissionsNotificationDeletedReceiver.onReceive", "xxx");

        GlobalData.clearMergedPermissions(context.getApplicationContext());

    }

}
