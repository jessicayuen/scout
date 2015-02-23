package scout.scoutmobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.BluetoothBeacon;
import scout.scoutmobile.model.BluetoothBeaconData;
import scout.scoutmobile.utils.Logger;

public class BluetoothBeaconService extends Service {

    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", null, null, null);
    private BeaconManager beaconManager = null;
    private Logger mLogger = new Logger("BluetoothBeaconService");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                StoreBeaconData(beacons);
                mLogger.log("Ranged beacons: " + beacons);
            }
        });

        mLogger.log("Beacon service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);

                    mLogger.log("Beacon service has started");
                } catch (RemoteException e) {
                    mLogger.log("Unable to start ranging: " + e.toString());
                }
            }
        });

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
            mLogger.log("Beacon service destroyed");

            super.onDestroy();
        } catch (RemoteException e) {
            mLogger.log("Unable to stop ranging beacons");
        }
    }

    private void StoreBeaconData (List<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            saveBeacon(beacon);
        }
    }

    private void saveBeacon (Beacon beacon) {
        final BluetoothBeacon bluetoothBeacon = new BluetoothBeacon(beacon);
        final BluetoothBeaconData bluetoothBeaconData = new BluetoothBeaconData(bluetoothBeacon, beacon);

        // query for unique beacon mac address
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_BEACON);
        query.whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, bluetoothBeacon.getMacAddress()).setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject beaconObject;
                ParseObject beaconDataObject;

                if (e == null) {
                    try {
                        // no parseObjects mean that the Beacon is new
                        if (parseObjects.isEmpty()) {
                            beaconObject = new ParseObject(Consts.TABLE_BEACON);

                            beaconObject.put(Consts.COL_BEACON_MACADDRESS, bluetoothBeacon.getMacAddress());
                            beaconObject.put(Consts.COL_BEACON_UUID, bluetoothBeacon.getUUID());
                            beaconObject.put(Consts.COL_BEACON_MAJOR, bluetoothBeacon.getMajor());
                            beaconObject.put(Consts.COL_BEACON_MINOR, bluetoothBeacon.getMinor());

                            beaconObject.save();
                        } else {
                            beaconObject = parseObjects.get(0);
                        }

                        beaconDataObject = new ParseObject(Consts.TABLE_BEACONDATA);

                        beaconDataObject.put(Consts.COL_BEACONDATA_BEACON, beaconObject);
                        beaconDataObject.put(Consts.COL_BEACONDATA_MEASUREDPOWER, bluetoothBeaconData.getMeasuredPower());
                        beaconDataObject.put(Consts.COL_BEACONDATA_RSSI, bluetoothBeaconData.getRSSI());
                        beaconDataObject.put(Consts.COL_BEACONDATA_DISTANCE, bluetoothBeaconData.getDistance());

                        beaconDataObject.saveInBackground();
                    } catch (ParseException parseSaveException) {
                        mLogger.logError(parseSaveException);
                    }
                } else {
                    mLogger.logError(e);
                }
            }
        });
    }
}
