package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PhoneStateScanner extends PhoneStateListener {

    private Context context;
    private TelephonyManager telephonyManager = null;
    //private TelephonyManager telephonyManager2 = null;

    public int registeredCell = Integer.MAX_VALUE;
    //public int registeredCell2 = Integer.MAX_VALUE;

    public boolean enabledAutoRegistration = false;
    public int durationForAutoRegistration = 0;
    public String cellsNameForAutoRegistration = "";

    public static MobileCellsRegistrationService autoRegistrationService = null;

    public static String ACTION_PHONE_STATE_CHANGED = "sk.henrichg.phoneprofilesplus.ACTION_PHONE_STATE_CHANGED";

    public PhoneStateScanner(Context context) {
        this.context = context;
        /*if (Build.VERSION.SDK_INT >= 24) {
            TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
                // Loop through the subscription list i.e. SIM list.
                List<SubscriptionInfo> subscriptionList = mSubscriptionManager.getActiveSubscriptionInfoList();
                if (subscriptionList != null) {
                    for (int i = 0; i < mSubscriptionManager.getActiveSubscriptionInfoCountMax(); i++) {
                        SubscriptionInfo subscriptionInfo = subscriptionList.get(i);
                        int subscriptionId = subscriptionInfo.getSubscriptionId();
                        if (telephonyManager1 == null)
                            telephonyManager1 = telephonyManager.createForSubscriptionId(subscriptionId);
                        if (telephonyManager2 == null)
                            telephonyManager2 = telephonyManager.createForSubscriptionId(subscriptionId);
                    }
                } else
                    telephonyManager1 = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
            }
        }
        else {*/
            telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        //}
        GlobalData.getMobileCellsAutoRegistration(context);
    }

    public void connect() {
        if (GlobalData.isPowerSaveMode && GlobalData.applicationEventMobileCellsScanInPowerSaveMode.equals("2"))
            // start scanning in power save mode is not allowed
            return;

        if ((telephonyManager != null) &&
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                Permissions.checkLocation(context.getApplicationContext()))
            telephonyManager.listen(this,
                //  PhoneStateListener.LISTEN_CALL_STATE
                    PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                  | PhoneStateListener.LISTEN_CELL_LOCATION
                //| PhoneStateListener.LISTEN_DATA_ACTIVITY
                //| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                  | PhoneStateListener.LISTEN_SERVICE_STATE
                //| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                //| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                //| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                );
        /*if ((telephonyManager2 != null) &&
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                Permissions.checkLocation(context.getApplicationContext()))
            telephonyManager2.listen(this,
                //  PhoneStateListener.LISTEN_CALL_STATE
                    PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                  | PhoneStateListener.LISTEN_CELL_LOCATION
                //| PhoneStateListener.LISTEN_DATA_ACTIVITY
                //| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                  | PhoneStateListener.LISTEN_SERVICE_STATE
                //| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                //| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                //| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
            );*/
        startAutoRegistration();
    };

    public void disconnect() {
        if ((telephonyManager != null) && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
        /*if ((telephonyManager2 != null) && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            telephonyManager2.listen(this, PhoneStateListener.LISTEN_NONE);*/
        stopAutoRegistration();
    }

    public void resetListening(boolean oldPowerSaveMode, boolean forceReset) {
        if ((forceReset) || (GlobalData.isPowerSaveMode != oldPowerSaveMode)) {
            disconnect();
            connect();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getAllCellInfo(List<CellInfo> cellInfo) {
        // only for registered cells is returned identify
        // SlimKat in Galaxy Nexus - returns null :-/
        // Honor 7 - returns emty list (not null), Dual SIM?

        if (cellInfo!=null) {

            if (Permissions.checkLocation(context.getApplicationContext())) {

                //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "---- start ----------------------------");

                for (CellInfo _cellInfo : cellInfo) {
                    //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "registered="+_cellInfo.isRegistered());

                    if (_cellInfo instanceof CellInfoGsm) {
                        //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "gsm info="+_cellInfo);
                        CellIdentityGsm identityGsm = ((CellInfoGsm) _cellInfo).getCellIdentity();
                        if (identityGsm.getCid() != Integer.MAX_VALUE) {
                            //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "gsm mCid="+identityGsm.getCid());
                            if (_cellInfo.isRegistered())
                                registeredCell = identityGsm.getCid();
                        }
                    } else if (_cellInfo instanceof CellInfoLte) {
                        //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "lte info="+_cellInfo);
                        CellIdentityLte identityLte = ((CellInfoLte) _cellInfo).getCellIdentity();
                        if (identityLte.getCi() != Integer.MAX_VALUE) {
                            //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "lte mCi="+identityLte.getCi());
                            if (_cellInfo.isRegistered())
                                registeredCell = identityLte.getCi();
                        }
                    } else if (_cellInfo instanceof CellInfoWcdma) {
                        //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "wcdma info="+_cellInfo);
                        if (android.os.Build.VERSION.SDK_INT >= 18) {
                            CellIdentityWcdma identityWcdma = null;
                            identityWcdma = ((CellInfoWcdma) _cellInfo).getCellIdentity();
                            if (identityWcdma.getCid() != Integer.MAX_VALUE) {
                                //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "wcdma mCid=" + identityWcdma.getCid());
                                if (_cellInfo.isRegistered())
                                    registeredCell = identityWcdma.getCid();
                            }
                        }
                        //else {
                        //    GlobalData.logE("PhoneStateScanner.getAllCellInfo", "wcdma mCid=not supported for API level < 18");
                        //}
                    } else if (_cellInfo instanceof CellInfoCdma) {
                        //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "cdma info="+_cellInfo);
                        CellIdentityCdma identityCdma = ((CellInfoCdma) _cellInfo).getCellIdentity();
                        if (identityCdma.getBasestationId() != Integer.MAX_VALUE) {
                            //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "wcdma mCid="+identityCdma.getBasestationId());
                            if (_cellInfo.isRegistered())
                                registeredCell = identityCdma.getBasestationId();
                        }
                    }
                    //else {
                    //    GlobalData.logE("PhoneStateScanner.getAllCellInfo", "unknown info="+_cellInfo);
                    //}
                }

                //GlobalData.logE("PhoneStateScanner.getAllCellInfo", "---- end ----------------------------");

                GlobalData.logE("PhoneStateScanner.getAllCellInfo", "registeredCell=" + registeredCell);
            }

        }
        else
            GlobalData.logE("PhoneStateScanner.getAllCellInfo", "cell info is null");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void getAllCellInfo() {
        if (telephonyManager != null) {
            List<CellInfo> cellInfo = telephonyManager.getAllCellInfo();
            getAllCellInfo(cellInfo);
        }
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo)
    {
        super.onCellInfoChanged(cellInfo);

        GlobalData.logE("PhoneStateScanner.onCellInfoChanged", "xxx");

        if (cellInfo == null)
            getAllCellInfo();
        else
            getAllCellInfo(cellInfo);

        doAutoRegistration();
        sendBroadcast();
    }

    @Override
    public void onServiceStateChanged (ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);

        GlobalData.logE("PhoneStateScanner.onServiceStateChanged", "serviceState="+serviceState);

        getRegisteredCell();

        doAutoRegistration();
        sendBroadcast();
    }

    private void getCellLocation(CellLocation location) {

        if (location!=null) {

            if (Permissions.checkLocation(context.getApplicationContext())) {

                if (location instanceof GsmCellLocation) {
                    GsmCellLocation gcLoc = (GsmCellLocation) location;
                    //GlobalData.logE("PhoneStateScanner.getCellLocation", "gsm location="+gcLoc);
                    if (gcLoc.getCid() != Integer.MAX_VALUE) {
                        //GlobalData.logE("PhoneStateScanner.getCellLocation", "gsm mCid="+gcLoc.getCid());
                        registeredCell = gcLoc.getCid();
                    }
                } else if (location instanceof CdmaCellLocation) {
                    CdmaCellLocation ccLoc = (CdmaCellLocation) location;
                    //GlobalData.logE("PhoneStateScanner.getCellLocation", "cdma location="+ccLoc);
                    if (ccLoc.getBaseStationId() != Integer.MAX_VALUE) {
                        //GlobalData.logE("PhoneStateScanner.getCellLocation", "cdma mCid="+ccLoc.getBaseStationId());
                        registeredCell = ccLoc.getBaseStationId();
                    }
                }
                //else {
                //    GlobalData.logE("PhoneStateScanner.getCellLocation", "unknown location="+location);
                //}

                GlobalData.logE("PhoneStateScanner.getCellLocation", "registeredCell=" + registeredCell);

            }

        }
        else
            GlobalData.logE("PhoneStateScanner.getCellLocation", "location is null");
    }

    public void getCellLocation() {
        if (telephonyManager != null) {
            CellLocation location = telephonyManager.getCellLocation();
            getCellLocation(location);
        }
    }

    @Override
    public void onCellLocationChanged (CellLocation location) {
        super.onCellLocationChanged(location);

        GlobalData.logE("PhoneStateScanner.onCellLocationChanged", "xxx");

        if (location == null)
            getCellLocation();
        else
            getCellLocation(location);

        doAutoRegistration();
        sendBroadcast();
    }

    /*
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
        super.onSignalStrengthsChanged(signalStrength);

        signal = signalStrength.getGsmSignalStrength()*2-113;
    }
    */

    public void getRegisteredCell() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getAllCellInfo();
        getCellLocation();
    }

    public void rescanMobileCells() {
        getRegisteredCell();
        doAutoRegistration();
        sendBroadcast();
    }

    private void sendBroadcast() {
        // broadcast for start EventsService
        Intent broadcastIntent = new Intent(context, PhoneStateChangeBroadcastReceiver.class);
        context.sendBroadcast(broadcastIntent);

        // broadcast for cells editor
        Intent intent = new Intent(ACTION_PHONE_STATE_CHANGED);
        //intent.putExtra("state", mode);
        context.sendBroadcast(intent);
    }

    private void doAutoRegistration() {
        if (!GlobalData.getApplicationStarted(context))
            // application is not started
            return;

        if (enabledAutoRegistration) {
            Log.d("PhoneStateScanner.doAutoRegistration", "xxx");
            List<MobileCellsData> localCellsList = new ArrayList<MobileCellsData>();
            localCellsList.add(new MobileCellsData(registeredCell, cellsNameForAutoRegistration, true, false));
            DatabaseHandler db = DatabaseHandler.getInstance(context);
            db.saveMobileCellsList(localCellsList, true);
        }
    }

    public void startAutoRegistration() {
        if (!GlobalData.getApplicationStarted(context))
            // application is not started
            return;

        GlobalData.getMobileCellsAutoRegistration(context);
        if (enabledAutoRegistration) {
            Log.d("PhoneStateScanner.startAutoRegistration","xxx");
            stopAutoRegistration();
            context.startService(new Intent(context.getApplicationContext(), MobileCellsRegistrationService.class));
        }
    }

    public void stopAutoRegistration() {
        if (autoRegistrationService != null) {
            context.stopService(new Intent(context.getApplicationContext(), MobileCellsRegistrationService.class));
            autoRegistrationService = null;
        }
    }
}
