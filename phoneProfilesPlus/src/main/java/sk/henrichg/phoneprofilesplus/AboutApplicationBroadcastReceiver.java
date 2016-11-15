package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

public class AboutApplicationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        GlobalData.logE("##### AboutApplicationBroadcastReceiver.onReceive", "xxx");

        if (!GlobalData.getApplicationStarted(context))
            // application is not started
            return;

        int daysAfterFirstStart = GlobalData.getDaysAfterFirtsStart(context)+1;
        GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.onReceive", "daysAfterFirstStart="+daysAfterFirstStart);

        if (daysAfterFirstStart == 7) {
            GlobalData.setDaysAfterFirstStart(context, 8);
            // show notification about "Please donate me."
            NotificationCompat.Builder mBuilder;
            Intent _intent = new Intent(context, AboutApplicationActivity.class);

            String ntitle = context.getString(R.string.about_application_donate_button);
            String ntext = context.getString(R.string.donation_description);
            if (android.os.Build.VERSION.SDK_INT < 24) {
                ntitle = context.getString(R.string.app_name);
                ntext = context.getString(R.string.about_application_donate_button) + ": " +
                        context.getString(R.string.donation_description);
            }
            mBuilder =   new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_exclamation_notify) // notification icon
                            .setContentTitle(ntitle) // title for notification
                            .setContentText(ntext)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(ntext))
                            .setAutoCancel(true); // clear notification after click

            PendingIntent pi = PendingIntent.getActivity(context, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            if (android.os.Build.VERSION.SDK_INT >= 16)
                mBuilder.setPriority(Notification.PRIORITY_MAX);
            if (android.os.Build.VERSION.SDK_INT >= 21)
            {
                mBuilder.setCategory(Notification.CATEGORY_EVENT);
                mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }
            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(GlobalData.ABOUT_APPLICATION_DONATE_NOTIFICATION_ID, mBuilder.build());
        }
        else
        if (daysAfterFirstStart < 7)
            GlobalData.setDaysAfterFirstStart(context, daysAfterFirstStart);

        setAlarm(context);
    }

    @SuppressLint({"SimpleDateFormat", "NewApi"})
    public static void setAlarm(Context context) {
        GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.setAlarm","xxx");

        int daysAfterFirstStart = GlobalData.getDaysAfterFirtsStart(context);
        if (daysAfterFirstStart >= 7)
            return;

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AboutApplicationBroadcastReceiver.class);

        removeAlarm(context);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        //SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
        //GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));

        long alarmTime = calendar.getTimeInMillis();

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (GlobalData.exactAlarms && (android.os.Build.VERSION.SDK_INT >= 23))
            //alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
        else if (GlobalData.exactAlarms && (android.os.Build.VERSION.SDK_INT >= 19))
            //alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
        else
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);

        GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.setAlarm", "alarm is set");

    }

    public static void removeAlarm(Context context) {
        GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.removeAlarm","xxx");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        Intent intent = new Intent(context, AboutApplicationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
            GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.removeAlarm","alarm found");

            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        else
            GlobalData.logE("@@@ AboutApplicationBroadcastReceiver.removeAlarm","alarm not found");
    }

}
