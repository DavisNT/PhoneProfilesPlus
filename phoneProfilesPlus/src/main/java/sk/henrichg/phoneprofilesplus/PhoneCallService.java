package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class PhoneCallService extends IntentService {

    private Context context;
    private static AudioManager audioManager = null;

    private static boolean savedSpeakerphone = false;
    private static boolean speakerphoneSelected = false;

    public static final int CALL_EVENT_UNDEFINED = 0;
    public static final int CALL_EVENT_INCOMING_CALL_RINGING = 1;
    public static final int CALL_EVENT_OUTGOING_CALL_STARTED = 2;
    public static final int CALL_EVENT_INCOMING_CALL_ANSWERED = 3;
    public static final int CALL_EVENT_OUTGOING_CALL_ANSWERED = 4;
    public static final int CALL_EVENT_INCOMING_CALL_ENDED = 5;
    public static final int CALL_EVENT_OUTGOING_CALL_ENDED = 6;

    public static final int LINKMODE_NONE = 0;
    public static final int LINKMODE_LINK = 1;
    public static final int LINKMODE_UNLINK = 2;

    public PhoneCallService() {
        super("PhoneCallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            context = getApplicationContext();

            int phoneEvent = intent.getIntExtra(PhoneCallBroadcastReceiver.EXTRA_SERVICE_PHONE_EVENT, 0);
            boolean incoming = intent.getBooleanExtra(PhoneCallBroadcastReceiver.EXTRA_SERVICE_PHONE_INCOMING, true);
            String number = intent.getStringExtra(PhoneCallBroadcastReceiver.EXTRA_SERVICE_PHONE_NUMBER);

            switch (phoneEvent) {
                case PhoneCallBroadcastReceiver.SERVICE_PHONE_EVENT_START:
                    callStarted(incoming, number);
                    break;
                case PhoneCallBroadcastReceiver.SERVICE_PHONE_EVENT_ANSWER:
                    callAnswered(incoming, number);
                    break;
                case PhoneCallBroadcastReceiver.SERVICE_PHONE_EVENT_END:
                    callEnded(incoming, number);
                    break;
            }
        }

        try {
            Thread.sleep(1000); // // 1 second for EventsService
        } catch (InterruptedException e) {
        }

        PhoneCallBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void doCallEvent(int eventType, String phoneNumber, DataWrapper dataWrapper)
    {
        SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(GlobalData.PREF_EVENT_CALL_EVENT_TYPE, eventType);
        editor.putString(GlobalData.PREF_EVENT_CALL_PHONE_NUMBER, phoneNumber);
        editor.commit();

        if (GlobalData.applicationUnlinkRingerNotificationVolumes) {
            if ((eventType == CALL_EVENT_INCOMING_CALL_RINGING) || (eventType == CALL_EVENT_INCOMING_CALL_ENDED)) {
                /// for linked ringer and notification volume:
                //    notification volume in profile activation is set after ringer volume
                //    therefore reset ringer volume
                Profile profile = dataWrapper.getActivatedProfile();
                if (profile != null) {
                    Log.e("PhoneCallService", "doCallEvent - unlink");
                    Intent volumeServiceIntent = new Intent(context, ExecuteVolumeProfilePrefsService.class);
                    volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
                    int linkUnlink = LINKMODE_UNLINK;
                    if (eventType == CALL_EVENT_INCOMING_CALL_ENDED)
                        linkUnlink = LINKMODE_LINK;
                    volumeServiceIntent.putExtra(GlobalData.EXTRA_LINKUNLINK_VOLUMES, linkUnlink);
                    context.startService(volumeServiceIntent);
                    try {
                        Thread.sleep(1000); // // Delay 1 second for ExecuteVolumeProfilePrefsService
                    } catch (InterruptedException e) {
                    }
                }
                ///
            }
        }

        boolean callEventsExists = false;
        if (GlobalData.getGlobalEventsRuning(context))
            callEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_CALL) > 0;

        if (callEventsExists)
        {
            // start service
            Intent eventsServiceIntent = new Intent(context, EventsService.class);
            eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, EventsService.BROADCAST_RECEIVER_TYPE_NO_BROADCAST_RECEIVER);
            context.startService(eventsServiceIntent);
        }

    }

    private void callStarted(boolean incoming, String phoneNumber)
    {
        if (audioManager == null )
            audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        speakerphoneSelected = false;

        if (incoming) {
            DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);

            doCallEvent(CALL_EVENT_INCOMING_CALL_RINGING, phoneNumber, dataWrapper);

            dataWrapper.invalidateDataWrapper();
        }
    }

    private void callAnswered(boolean incoming, String phoneNumber)
    {

        if (audioManager == null )
            audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        try {
            // Delay 2 seconds mode changed to MODE_IN_CALL
            for (int i = 0; i < 20; i++) {
                if (audioManager.getMode() != AudioManager.MODE_IN_CALL)
                    Thread.sleep(100);
                else
                    break;
            }
        } catch (InterruptedException e) {
        }

        // audiomode is set to MODE_IN_CALL by system
        Log.e("PhoneCallService", "callAnswered audioMode=" + audioManager.getMode());

        DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);

        Profile profile = dataWrapper.getActivatedProfile();
        profile = GlobalData.getMappedProfile(profile, context);

        if (profile != null) {

            if (profile._volumeSpeakerPhone != 0) {

                if (audioManager == null)
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                savedSpeakerphone = audioManager.isSpeakerphoneOn();
                boolean changeSpeakerphone = false;
                if (savedSpeakerphone && (profile._volumeSpeakerPhone == 2)) // 2=speakerphone off
                    changeSpeakerphone = true;
                if ((!savedSpeakerphone) && (profile._volumeSpeakerPhone == 1)) // 1=speakerphone on
                    changeSpeakerphone = true;
                if (changeSpeakerphone) {
                    /// activate SpeakerPhone
                    audioManager.setSpeakerphoneOn(profile._volumeSpeakerPhone == 1);
                    speakerphoneSelected = true;
                }

            }
        }

        if (incoming)
            doCallEvent(CALL_EVENT_INCOMING_CALL_ANSWERED, phoneNumber, dataWrapper);
        else
            doCallEvent(CALL_EVENT_OUTGOING_CALL_ANSWERED, phoneNumber, dataWrapper);

        dataWrapper.invalidateDataWrapper();
    }

    private void callEnded(boolean incoming, String phoneNumber)
    {
        if (audioManager == null )
            audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        // audiomode is set to MODE_IN_CALL by system
        Log.e("PhoneCallService", "callEnded (before back speaker phone) audioMode="+audioManager.getMode());

        if (speakerphoneSelected)
        {
            audioManager.setSpeakerphoneOn(savedSpeakerphone);
        }

        speakerphoneSelected = false;

        // Delay 2 seconds mode changed to MODE_NORMAL
        try {
            for (int i = 0; i < 20; i++) {
                if (audioManager.getMode() != AudioManager.MODE_NORMAL)
                    Thread.sleep(100);
                else
                    break;
            }
        } catch (InterruptedException e) {
        }

        // audiomode is set to MODE_NORMAL by system
        Log.e("PhoneCallService", "callEnded (before unlink/EventsService) audioMode="+audioManager.getMode());

        DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);

        if (incoming)
            doCallEvent(CALL_EVENT_INCOMING_CALL_ENDED, phoneNumber, dataWrapper);
        else
            doCallEvent(CALL_EVENT_OUTGOING_CALL_ENDED, phoneNumber, dataWrapper);

        dataWrapper.invalidateDataWrapper();

    }

}
