package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Html;

public class EventPreferencesLocation extends EventPreferences {

    public long _geofenceId;
    public boolean _whenOutside;

    static final String PREF_EVENT_LOCATION_ENABLED = "eventLocationEnabled";
    static final String PREF_EVENT_LOCATION_GEOFENCE_ID = "eventLocationGeofenceId";
    static final String PREF_EVENT_LOCATION_WHEN_OUTSIDE = "eventLocationStartWhenOutside";

    static final String PREF_EVENT_LOCATION_CATEGORY = "eventLocationCategory";

    private DataWrapper dataWrapper = null;

    public EventPreferencesLocation(Event event,
                                    boolean enabled,
                                    long geofenceId,
                                    boolean _whenOutside)
    {
        super(event, enabled);

        this._geofenceId = geofenceId;
        this._whenOutside = _whenOutside;
    }

    @Override
    public void copyPreferences(Event fromEvent)
    {
        this._enabled = ((EventPreferencesLocation)fromEvent._eventPreferencesLocation)._enabled;
        this._geofenceId = ((EventPreferencesLocation)fromEvent._eventPreferencesLocation)._geofenceId;
        this._whenOutside = ((EventPreferencesLocation)fromEvent._eventPreferencesLocation)._whenOutside;
    }

    @Override
    public void loadSharedPreferences(SharedPreferences preferences)
    {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Editor editor = preferences.edit();
            editor.putBoolean(PREF_EVENT_LOCATION_ENABLED, _enabled);
            editor.putLong(PREF_EVENT_LOCATION_GEOFENCE_ID, this._geofenceId);
            editor.putBoolean(PREF_EVENT_LOCATION_WHEN_OUTSIDE, this._whenOutside);
            editor.commit();
        //}
    }

    @Override
    public void saveSharedPreferences(SharedPreferences preferences)
    {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this._enabled = preferences.getBoolean(PREF_EVENT_LOCATION_ENABLED, false);
            this._geofenceId = preferences.getLong(PREF_EVENT_LOCATION_GEOFENCE_ID, 0);
            this._whenOutside = preferences.getBoolean(PREF_EVENT_LOCATION_WHEN_OUTSIDE, false);
        //}
    }

    @Override
    public String getPreferencesDescription(boolean addBullet, Context context)
    {
        String descr = "";

        if (!this._enabled)
        {
            ;
        }
        else
        {
            if (addBullet) {
                descr = descr + "<b>\u2022 </b>";
                descr = descr + "<b>" + context.getString(R.string.event_type_locations) + ": " + "</b>";
            }

            String selectedLocation = context.getString(R.string.applications_multiselect_summary_text_not_selected);
            if (this._geofenceId != 0) {
                selectedLocation = getGeofenceName(this._geofenceId, context);
            }
            descr = descr + selectedLocation;
            if (this._whenOutside)
                descr = descr + "; " + context.getString(R.string.event_preferences_location_when_outside_description);
        }

        return descr;
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
    {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (key.equals(PREF_EVENT_LOCATION_GEOFENCE_ID)) {
                Preference preference = prefMng.findPreference(key);
                if (preference != null) {
                    long lValue;
                    if (!value.isEmpty())
                        lValue = Long.valueOf(value);
                    else
                        lValue = 0;
                    preference.setSummary(getGeofenceName(lValue, context));
                    GUIData.setPreferenceTitleStyle(preference, false, true, false);
                }
            }
        //}
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
    {
        if (key.equals(PREF_EVENT_LOCATION_GEOFENCE_ID))
        {
            setSummary(prefMng, key, String.valueOf(preferences.getLong(key, 0)), context);
        }
    }

    @Override
    public void setAllSummary(PreferenceManager prefMng, SharedPreferences preferences, Context context)
    {
        setSummary(prefMng, PREF_EVENT_LOCATION_GEOFENCE_ID, preferences, context);
    }

    @Override
    public void setCategorySummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context) {
        EventPreferencesLocation tmp = new EventPreferencesLocation(this._event,
                                                this._enabled, this._geofenceId, this._whenOutside);
        if (preferences != null)
            tmp.saveSharedPreferences(preferences);

        Preference preference = prefMng.findPreference(PREF_EVENT_LOCATION_CATEGORY);
        if (preference != null) {
            GUIData.setPreferenceTitleStyle(preference, tmp._enabled, false, !tmp.isRunnable(context));
            preference.setSummary(Html.fromHtml(tmp.getPreferencesDescription(false, context)));
        }
    }

    @Override
    public boolean isRunnable(Context context)
    {

        boolean runable = super.isRunnable(context);

        runable = runable && (_geofenceId != 0);

        return runable;
    }

    @Override
    public void checkPreferences(PreferenceManager prefMng, Context context) {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final boolean enabled = PhoneProfilesService.isLocationEnabled(context.getApplicationContext());
            Preference preference = prefMng.findPreference(PREF_EVENT_LOCATION_GEOFENCE_ID);
            if (preference != null) preference.setEnabled(enabled);
            preference = prefMng.findPreference(PREF_EVENT_LOCATION_WHEN_OUTSIDE);
        if (preference != null) preference.setEnabled(enabled);
        //}
    }

    @Override
    public boolean activateReturnProfile()
    {
        return true;
    }

    @Override
    public void setSystemEventForStart(Context context)
    {
        if (_enabled && (!GeofenceScannerAlarmBroadcastReceiver.isAlarmSet(context/*, false*/)))
            GeofenceScannerAlarmBroadcastReceiver.setAlarm(context, true, false);
    }

    @Override
    public void setSystemEventForPause(Context context)
    {
    }

    @Override
    public void removeSystemEvent(Context context)
    {
    }

    private String getGeofenceName(long geofenceId, Context context) {
        if (dataWrapper == null)
            dataWrapper = new DataWrapper(context.getApplicationContext(), false, false, 0);
        String name = dataWrapper.getDatabaseHandler().getGeofenceName(geofenceId);
        if (name.isEmpty())
            name = context.getString(R.string.event_preferences_locations_location_not_selected);
        return name;
    }
}
