package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

class MobileDataStateChangedContentObserver extends ContentObserver {

    //public static boolean internalChange = false;

    private static boolean previousState = false;

    Context context;

    MobileDataStateChangedContentObserver(Context c, Handler handler) {
        super(handler);

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        context=c;

        //Log.e("### MobileDataStateChangedContentObserver", "xxx");

        previousState = ActivateProfileHelper.isMobileData(context);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        super.onChange(selfChange);

        PPApplication.logE("##### MobileDataStateChangedContentObserver", "onChange");

        boolean actualState = ActivateProfileHelper.isMobileData(context);
        if (previousState != actualState) {
            Intent broadcastIntent = new Intent(context, MobileDataStateChangedBroadcastReceiver.class);
            broadcastIntent.putExtra(MobileDataStateChangedBroadcastReceiver.EXTRA_STATE, actualState);
            context.sendBroadcast(broadcastIntent);
            previousState = actualState;
        }
    }

}
