package scout.scoutmobile.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
import android.os.IBinder;
import android.os.RemoteException;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.controllers.BeaconPingObserver;
import scout.scoutmobile.model.BluetoothBeacon;
import scout.scoutmobile.model.BluetoothBeaconData;
import scout.scoutmobile.model.CustomerSingleton;
import scout.scoutmobile.utils.Logger;

public class BluetoothBeaconService extends Service implements BeaconConsumer {

    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static List<BeaconPingObserver> mPingObservers = new ArrayList<>();
    private Logger mLogger = new Logger("BluetoothBeaconService");
    private BeaconManager beaconManager = null;
    private long scanDuration = 5000l;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setForegroundScanPeriod(scanDuration);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT));
        beaconManager.bind(this);

        mLogger.log("Beacon service has been created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        beaconManager.unbind(this);
        mLogger.log("Beacon service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                // Beacons aren't inserted in the business side yet so if the beacon is new, insert it here
                //insertNewBeacons(beacons);

                notifyBeaconPingObservers(beacons, scanDuration);
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            mLogger.logError(e);
        }
    }

    // If there are new beacons, use this to insert them
    // This should be handled by the webapp
    private void insertNewBeacons (Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            final BluetoothBeacon bluetoothBeacon =
                    new BluetoothBeacon(beacon.getBluetoothAddress(), beacon.getId1().toUuidString(),
                            beacon.getId2().toInt(), beacon.getId3().toInt(), -1, -1);

            ParseQuery<ParseObject> queryBeacon = ParseQuery.getQuery(Consts.TABLE_BEACON);
            queryBeacon.whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, bluetoothBeacon.getMacAddress()).setLimit(1);

            queryBeacon.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    ParseObject beaconObject;

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

                                mLogger.log("Inserted beacon: "+ bluetoothBeacon.getMacAddress()+ " " + bluetoothBeacon.getUUID());
                            } else {
                                mLogger.log("Detected beacon: "+ bluetoothBeacon.getMacAddress()+ " " + bluetoothBeacon.getUUID());
                            }
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

    public static void addBeaconPingObserver(BeaconPingObserver observer) {
        mPingObservers.add(observer);
    }

    private void notifyBeaconPingObservers(Collection<Beacon> beacons, long milliseconds) {
        for (BeaconPingObserver observer : mPingObservers) {
            observer.onBeaconPing(beacons, milliseconds / 1000);
        }
    }
}
