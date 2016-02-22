package sk.henrichg.phoneprofilesplus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeofencesScanner implements GoogleApiClient.ConnectionCallbacks,
                                         GoogleApiClient.OnConnectionFailedListener,
                                         LocationListener
{

    private GoogleApiClient mGoogleApiClient;
    private Context context;
    DataWrapper dataWrapper;

    protected LocationRequest mLocationRequest;
    public boolean mPowerSaveMode = false;
    public boolean mUpdatesStarted = false;

    protected ArrayList<com.google.android.gms.location.Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    // Bool to track whether the app is already resolving an error
    public boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";

    //private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    //private static final long GEOFENCE_EXPIRATION_IN_MILISECONDS =
    //        GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final String GEOFENCE_KEY_PREFIX = "PhoneProfilesPlusGeofence";

    public GeofencesScanner(Context context) {
        this.context = context;
        dataWrapper = new DataWrapper(context, false, false, 0);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        createLocationRequest();
    }

    public void connect() {
        if (!mResolvingError) {
            if (dataWrapper.getDatabaseHandler().getGeofenceCount() > 0)
                mGoogleApiClient.connect();
        }
    };

    public void connectForResolve() {
        if (!mGoogleApiClient.isConnecting() &&
                !mGoogleApiClient.isConnected()) {
            if (dataWrapper.getDatabaseHandler().getGeofenceCount() > 0)
                mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.d("GeofencesScanner.onConnected", "xxx");
        if (mGoogleApiClient.isConnected()) {
            //startLocationUpdates();
            mUpdatesStarted = false;
            GeofenceScannerAlarmBroadcastReceiver.setAlarm(context, false, true);
            clearAllEventGeofences();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        //Log.d("GeofencesScanner.onConnectionSuspended", "xxx");
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.d("GeofencesScanner.onConnectionFailed", "xxx");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            /*try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }*/
            showErrorNotification(connectionResult.getErrorCode());
            mResolvingError = true;
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorNotification(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        GlobalData.logE("GeofenceScanner.onLocationChanged", "location=" + location);

        List<Geofence> geofences = dataWrapper.getDatabaseHandler().getAllGeofences();

        boolean change = false;

        for (Geofence geofence : geofences) {

            Location geofenceLocation = new Location("GL");
            geofenceLocation.setLatitude(geofence._latitude);
            geofenceLocation.setLongitude(geofence._longitude);

            float distance = location.distanceTo(geofenceLocation);
            float radius = location.getAccuracy()+geofence._radius;

            int transitionType = 0;
            if (distance <= radius)
                transitionType = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
            else
                transitionType = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

            int savedTransition = dataWrapper.getDatabaseHandler().getGeofenceTransition(geofence._id);

            if (savedTransition != transitionType) {
                GlobalData.logE("GeofenceScanner.onLocationChanged", "geofence._name="+geofence._name);
                GlobalData.logE("GeofenceScanner.onLocationChanged", "transitionType="+transitionType);
                GlobalData.logE("GeofenceScanner.onLocationChanged", "savedTransition="+savedTransition);

                dataWrapper.getDatabaseHandler().updateGeofenceTransition(geofence._id, transitionType);
                change = true;
            }
        }

        if (change) {
            // send broadcast for calling EventsService
            Intent broadcastIntent = new Intent(context, GeofenceScannerBroadcastReceiver.class);
            context.sendBroadcast(broadcastIntent);
        }

    }

    public void clearAllEventGeofences() {
        // clear all geofence transitions
        dataWrapper.getDatabaseHandler().clearAllGeofenceTransitions();
    }

    //-------------------------------------------

    protected void createLocationRequest() {
        GlobalData.loadPreferences(context);

        Log.d("GeofenceScanner.createLocationRequest", "xxx");

        // check power save mode
        mPowerSaveMode = DataWrapper.isPowerSaveMode(context);
        if (mPowerSaveMode && GlobalData.applicationEventLocationUpdateInPowerSaveMode.equals("2")) {
            mLocationRequest = null;
            return;
        }

        mLocationRequest = new LocationRequest();

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        //int interval = GlobalData.applicationEventLocationUpdateInterval * 60;
        int interval = 10;
        if (mPowerSaveMode && GlobalData.applicationEventLocationUpdateInPowerSaveMode.equals("1"))
            interval = 2 * interval;
        final long UPDATE_INTERVAL_IN_MILLISECONDS = interval * 1000;

        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;


        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        if ((!GlobalData.applicationEventLocationUseGPS) || mPowerSaveMode) {
            Log.d("GeofenceScanner.createLocationRequest","PRIORITY_BALANCED_POWER_ACCURACY");
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
        else {
            Log.d("GeofenceScanner.createLocationRequest","PRIORITY_HIGH_ACCURACY");
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {

            if ((mLocationRequest != null) && Permissions.checkLocation(context)) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    mUpdatesStarted = true;
                } catch (SecurityException securityException) {
                    // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                    mUpdatesStarted = false;
                    return;
                }
            }

        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mUpdatesStarted = false;
        }
    }

    public void resetLocationUpdates(boolean powerSaveMode, boolean forceReset) {
        if ((forceReset) || (mPowerSaveMode != powerSaveMode)) {
            stopLocationUpdates();
            createLocationRequest();
            //startLocationUpdates();
            GeofenceScannerAlarmBroadcastReceiver.setAlarm(context, false, true);
        }
    }

    //-------------------------------------------

    private void showErrorNotification(int errorCode) {
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_pphelper_upgrade_notify) // notification icon
                .setContentTitle(context.getString(R.string.event_preferences_location_google_api_connection_error_title)) // title for notification
                .setContentText(context.getString(R.string.app_name) + ": " +
                        context.getString(R.string.event_preferences_location_google_api_connection_error_text)) // message for notification
                                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(context, GeofenceScannerErrorActivity.class);
        intent.putExtra(DIALOG_ERROR, errorCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        if (android.os.Build.VERSION.SDK_INT >= 16)
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            mBuilder.setCategory(Notification.CATEGORY_RECOMMENDATION);
            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(GlobalData.GEOFENCE_SCANNER_ERROR_NOTIFICATION_ID, mBuilder.build());
    }

}
