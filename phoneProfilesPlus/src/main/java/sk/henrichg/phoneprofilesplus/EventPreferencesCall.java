package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Html;

public class EventPreferencesCall extends EventPreferences {

    public int _callEvent;
    public String _contacts;
    public String _contactGroups;
    public int _contactListType;

    static final String PREF_EVENT_CALL_ENABLED = "eventCallEnabled";
    static final String PREF_EVENT_CALL_EVENT = "eventCallEvent";
    static final String PREF_EVENT_CALL_CONTACTS = "eventCallContacts";
    static final String PREF_EVENT_CALL_CONTACT_GROUPS = "eventCallContactGroups";
    static final String PREF_EVENT_CALL_CONTACT_LIST_TYPE = "eventCallContactListType";

    static final String PREF_EVENT_CALL_CATEGORY = "eventCallCategory";

    static final int CALL_EVENT_RINGING = 0;
    static final int CALL_EVENT_INCOMING_CALL_ANSWERED = 1;
    static final int CALL_EVENT_OUTGOING_CALL_STARTED = 2;

    static final int CONTACT_LIST_TYPE_WHITE_LIST = 0;
    static final int CONTACT_LIST_TYPE_BLACK_LIST = 1;
    static final int CONTACT_LIST_TYPE_NOT_USE = 2;

    public EventPreferencesCall(Event event,
                                    boolean enabled,
                                    int callEvent,
                                    String contacts,
                                    String contactGroups,
                                    int contactListType)
    {
        super(event, enabled);

        this._callEvent = callEvent;
        this._contacts = contacts;
        this._contactGroups = contactGroups;
        this._contactListType = contactListType;
    }

    @Override
    public void copyPreferences(Event fromEvent)
    {
        this._enabled = ((EventPreferencesCall)fromEvent._eventPreferencesCall)._enabled;
        this._callEvent = ((EventPreferencesCall)fromEvent._eventPreferencesCall)._callEvent;
        this._contacts = ((EventPreferencesCall)fromEvent._eventPreferencesCall)._contacts;
        this._contactGroups = ((EventPreferencesCall)fromEvent._eventPreferencesCall)._contactGroups;
        this._contactListType = ((EventPreferencesCall)fromEvent._eventPreferencesCall)._contactListType;
    }

    @Override
    public void loadSharedPreferences(SharedPreferences preferences)
    {
        Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_CALL_ENABLED, _enabled);
        editor.putString(PREF_EVENT_CALL_EVENT, String.valueOf(this._callEvent));
        editor.putString(PREF_EVENT_CALL_CONTACTS, this._contacts);
        editor.putString(PREF_EVENT_CALL_CONTACT_GROUPS, this._contactGroups);
        editor.putString(PREF_EVENT_CALL_CONTACT_LIST_TYPE, String.valueOf(this._contactListType));
        editor.commit();
    }

    @Override
    public void saveSharedPreferences(SharedPreferences preferences)
    {
        this._enabled = preferences.getBoolean(PREF_EVENT_CALL_ENABLED, false);
        this._callEvent = Integer.parseInt(preferences.getString(PREF_EVENT_CALL_EVENT, "0"));
        this._contacts = preferences.getString(PREF_EVENT_CALL_CONTACTS, "");
        this._contactGroups = preferences.getString(PREF_EVENT_CALL_CONTACT_GROUPS, "");
        this._contactListType = Integer.parseInt(preferences.getString(PREF_EVENT_CALL_CONTACT_LIST_TYPE, "0"));
    }

    @Override
    public String getPreferencesDescription(boolean addBullet, Context context)
    {
        String descr = "";

        if (!this._enabled)
        {
            //descr = descr + context.getString(R.string.event_type_call) + ": ";
            //descr = descr + context.getString(R.string.event_preferences_not_enabled);
        }
        else
        {
            if (addBullet) {
                descr = descr + "<b>\u2022 </b>";
                descr = descr + "<b>" + context.getString(R.string.event_type_call) + ": " + "</b>";
            }

            descr = descr + context.getString(R.string.pref_event_call_event);
            String[] callEvents = context.getResources().getStringArray(R.array.eventCallEventsArray);
            descr = descr + ": " + callEvents[this._callEvent] + "; ";
            descr = descr + context.getString(R.string.pref_event_call_contactListType);
            String[] cntactListTypes = context.getResources().getStringArray(R.array.eventCallContactListTypeArray);
            descr = descr + ": " + cntactListTypes[this._contactListType];
        }

        return descr;
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
    {
        if (key.equals(PREF_EVENT_CALL_EVENT) || key.equals(PREF_EVENT_CALL_CONTACT_LIST_TYPE))
        {
            ListPreference listPreference = (ListPreference)prefMng.findPreference(key);
            int index = listPreference.findIndexOfValue(value);
            CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
            listPreference.setSummary(summary);
        }
        if (key.equals(PREF_EVENT_CALL_CONTACTS))
        {
            Preference preference = prefMng.findPreference(key);
            GUIData.setPreferenceTitleStyle(preference, false, true, false);
        }
        if (key.equals(PREF_EVENT_CALL_CONTACT_GROUPS))
        {
            Preference preference = prefMng.findPreference(key);
            GUIData.setPreferenceTitleStyle(preference, false, true, false);
        }
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
    {
        if (key.equals(PREF_EVENT_CALL_EVENT) ||
            key.equals(PREF_EVENT_CALL_CONTACT_LIST_TYPE) ||
            key.equals(PREF_EVENT_CALL_CONTACTS) ||
            key.equals(PREF_EVENT_CALL_CONTACT_GROUPS))
        {
            setSummary(prefMng, key, preferences.getString(key, ""), context);
        }
    }

    @Override
    public void setAllSummary(PreferenceManager prefMng, SharedPreferences preferences, Context context)
    {
        setSummary(prefMng, PREF_EVENT_CALL_EVENT, preferences, context);
        setSummary(prefMng, PREF_EVENT_CALL_CONTACT_LIST_TYPE, preferences, context);
        setSummary(prefMng, PREF_EVENT_CALL_CONTACTS, preferences, context);
        setSummary(prefMng, PREF_EVENT_CALL_CONTACT_GROUPS, preferences, context);
    }

    @Override
    public void setCategorySummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context) {
        EventPreferencesCall tmp = new EventPreferencesCall(this._event, this._enabled, this._callEvent, this._contacts, this._contactGroups, this._contactListType);
        if (preferences != null)
            tmp.saveSharedPreferences(preferences);

        Preference preference = prefMng.findPreference(PREF_EVENT_CALL_CATEGORY);
        if (preference != null) {
            GUIData.setPreferenceTitleStyle(preference, tmp._enabled, false, !tmp.isRunable());
            preference.setSummary(Html.fromHtml(tmp.getPreferencesDescription(false, context)));
        }
    }

    @Override
    public boolean isRunable()
    {

        boolean runable = super.isRunable();

        runable = runable && ((_contactListType == CONTACT_LIST_TYPE_NOT_USE) ||
                              (!(_contacts.isEmpty() && _contactGroups.isEmpty())));

        return runable;
    }

    @Override
    public boolean activateReturnProfile()
    {
        return true;
    }

    @Override
    public void setSystemRunningEvent(Context context)
    {
        // set alarm for state PAUSE
    }

    @Override
    public void setSystemPauseEvent(Context context)
    {
        // set alarm for state RUNNING
    }

    @Override
    public void removeSystemEvent(Context context)
    {
    }

}
