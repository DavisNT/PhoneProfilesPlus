package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;

import java.util.Date;

public class PhoneCallBroadcastReceiver extends PhoneCallReceiver {

	private static AudioManager audioManager = null;
	
	private static int savedMode = AudioManager.MODE_NORMAL;
	private static boolean savedSpeakerphone = false;
	private static boolean speakerphoneSelected = false;
    public static boolean separateVolumes = false;
    public static int notificationVolume = -999;

	public static final String BROADCAST_RECEIVER_TYPE = "phoneCall";
	
	public static final int CALL_EVENT_UNDEFINED = 0; 
	public static final int CALL_EVENT_INCOMING_CALL_RINGING = 1; 
	public static final int CALL_EVENT_OUTGOING_CALL_STARTED = 2;
	public static final int CALL_EVENT_INCOMING_CALL_ANSWERED = 3; 
	public static final int CALL_EVENT_OUTGOING_CALL_ANSWERED = 4;
	public static final int CALL_EVENT_INCOMING_CALL_ENDED = 5; 
	public static final int CALL_EVENT_OUTGOING_CALL_ENDED = 6;
	
	protected boolean onStartReceive()
	{
		if (!GlobalData.getApplicationStarted(savedContext))
			return false;
		
		GlobalData.loadPreferences(savedContext);
		
		return true;
	}

	protected void onEndReceive()
	{
	}
	
	private void doCallEvent(int eventType, String phoneNumber, DataWrapper dataWrapper)
	{
		SharedPreferences preferences = savedContext.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(GlobalData.PREF_EVENT_CALL_EVENT_TYPE, eventType);
		editor.putString(GlobalData.PREF_EVENT_CALL_PHONE_NUMBER, phoneNumber);
		editor.commit();

		if (GlobalData.getGlobalEventsRuning(savedContext))
		{
			boolean callEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_CALL) > 0;
			
			if (callEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(savedContext, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(savedContext, eventsServiceIntent);
			}
			
		}
	}
	
	private void callStarted(boolean incoming, String phoneNumber)
	{
		if (audioManager == null )
			audioManager = (AudioManager)savedContext.getSystemService(Context.AUDIO_SERVICE);

		savedMode = audioManager.getMode();

        DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);

        if (incoming) {
            /// for linked ringer and notification volume:
            //    notification volume in profile activation is set after ringer volume
            //    therefore reset ringer volume

            separateVolumes = true;

            Profile profile = dataWrapper.getActivatedProfile();
            if (profile != null) {
                Intent volumeServiceIntent = new Intent(savedContext, ExecuteVolumeProfilePrefsService.class);
                volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
                volumeServiceIntent.putExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, true);
                savedContext.startService(volumeServiceIntent);
            }
        }

		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_RINGING, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_STARTED, phoneNumber, dataWrapper);
		dataWrapper.invalidateDataWrapper();
	}
	
	private void callAnswered(boolean incoming, String phoneNumber)
	{
        separateVolumes = false;

		DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);

		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_ANSWERED, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_ANSWERED, phoneNumber, dataWrapper);

        Profile profile = dataWrapper.getActivatedProfile();
        profile = GlobalData.getMappedProfile(profile, savedContext);

        if (profile != null) {

            if (profile._volumeSpeakerPhone != 0) {

                if (audioManager == null)
                    audioManager = (AudioManager) savedContext.getSystemService(Context.AUDIO_SERVICE);

                try {
                    Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                } catch (InterruptedException e) {
                }

                //Activate loudspeaker
                audioManager.setMode(AudioManager.MODE_IN_CALL);

                savedSpeakerphone = audioManager.isSpeakerphoneOn();
                audioManager.setSpeakerphoneOn(profile._volumeSpeakerPhone == 1);

                speakerphoneSelected = true;

            }
        }
		
		dataWrapper.invalidateDataWrapper();
	}

    private void setBackNotificationVolume(DataWrapper dataWrapper) {
        if (notificationVolume != -999) {
            Profile profile = dataWrapper.getActivatedProfile();
            if (profile != null) {
                Intent volumeServiceIntent = new Intent(savedContext, ExecuteVolumeProfilePrefsService.class);
                volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
                volumeServiceIntent.putExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, true);
                savedContext.startService(volumeServiceIntent);
            }

            notificationVolume = -999;
        }
    }

	private void callEnded(boolean incoming, String phoneNumber)
	{
        separateVolumes = false;

    	//Deactivate loudspeaker
		if (audioManager == null )
			audioManager = (AudioManager)savedContext.getSystemService(Context.AUDIO_SERVICE);

        //if (audioManager.isSpeakerphoneOn())
       	if (speakerphoneSelected)
    	{
    	    audioManager.setSpeakerphoneOn(savedSpeakerphone);
    		speakerphoneSelected = false;
        }

        audioManager.setMode(savedMode);

        DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);

        if (incoming)
            setBackNotificationVolume(dataWrapper);

		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_ENDED, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_ENDED, phoneNumber, dataWrapper);
		dataWrapper.invalidateDataWrapper();
	}
	
    protected void onIncomingCallStarted(String number, Date start) 
    {
    	callStarted(true, number);
    }

    protected void onOutgoingCallStarted(String number, Date start)
    {
    	//callStarted(false, number);
    }
    
    protected void onIncomingCallAnswered(String number, Date start)
    {
    	callAnswered(true, number);
    }

	protected void onOutgoingCallAnswered(String number, Date start)
	{
    	callAnswered(false, number);
	}
    
    protected void onIncomingCallEnded(String number, Date start, Date end)
    {
    	callEnded(true, number);
    }

    protected void onOutgoingCallEnded(String number, Date start, Date end)
    {
    	callEnded(false, number);
    }

    protected void onMissedCall(String number, Date start)
    {
    	callEnded(true, number);
    }

}
