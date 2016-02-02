package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofencesScannerService extends IntentService {

    public GeofencesScannerService() {
        super("GeofencesScannerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.e("GeofencesScannerService", "Error getting geofence event: " + geofencingEvent.getErrorCode());
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger multiple geofences.
                List<com.google.android.gms.location.Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(
                        this,
                        geofenceTransition,
                        triggeringGeofences
                );

                // Send notification and log the transition details.
                //sendNotification(geofenceTransitionDetails);
                Log.d("GeofencesScannerService", geofenceTransitionDetails);
            } else {
                // Log the error.
                Log.e("GeofencesScannerService", "Invalid geofence transition type: "+geofenceTransition);
            }
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<com.google.android.gms.location.Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (com.google.android.gms.location.Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
            default:
                return "Unknown";
        }
    }
}
