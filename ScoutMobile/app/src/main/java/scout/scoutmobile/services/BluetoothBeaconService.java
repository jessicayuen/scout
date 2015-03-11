package scout.scoutmobile.services;

import android.app.Service;
import android.content.Intent;
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

    private static final int TRILATERATION_REQ_BEACON_NUM = 3;
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
        beaconManager.getBeaconParsers().add(
                new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
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
                if (beacons.size() > 0) {
                    StoreBeaconData(beacons);

                    if (beacons.size() >= TRILATERATION_REQ_BEACON_NUM) {
                        //saveCoordinateWithBeacons(beacons);
                    }

                    notifyBeaconPingObservers(beacons, scanDuration);
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            mLogger.logError(e);
        }
    }

    private void StoreBeaconData (Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            saveBeacon(beacon);
        }
    }

    private void saveBeacon (Beacon beacon) {
        final BluetoothBeacon bluetoothBeacon =
                new BluetoothBeacon(beacon.getBluetoothAddress(), beacon.getId1().toUuidString(),
                                    beacon.getId2().toInt(), beacon.getId3().toInt());
        final BluetoothBeaconData bluetoothBeaconData =
                new BluetoothBeaconData(bluetoothBeacon, beacon.getTxPower(), beacon.getRssi(), beacon.getDistance());

        ParseQuery<ParseObject> queryBeacon = ParseQuery.getQuery(Consts.TABLE_BEACON);
        queryBeacon.whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, bluetoothBeacon.getMacAddress()).setLimit(1);

        queryBeacon.findInBackground(new FindCallback<ParseObject>() {
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

                        beaconDataObject.put(Consts.COL_BEACONDATA_CUSTOMER, CustomerSingleton.getInstance().getCurCustomer());
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

    private void saveCoordinateWithBeacons(Collection<Beacon> beacons) {

        List<BluetoothBeaconData> detailedBeaconList = new ArrayList<BluetoothBeaconData>();
        BluetoothBeaconData beaconA, beaconB, beaconC;
        beaconA = beaconB = beaconC = null;

        // iterate through list of beacons detected and get their coordinates from the server
        // since beacons cant really store any information on them.
        for (Iterator it = beacons.iterator(); it.hasNext();) {
            Beacon curBeacon = (Beacon) it.next();
            BluetoothBeacon bluetoothBeacon;
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_BEACON);
            query.whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, curBeacon.getBluetoothAddress());

            try {
                ParseObject obj = query.getFirst();
                if (obj != null) {
                    bluetoothBeacon = new BluetoothBeacon(obj.getString(Consts.COL_BEACON_MACADDRESS), obj.getString(Consts.COL_BEACON_UUID),
                            obj.getInt(Consts.COL_BEACON_MAJOR), obj.getInt(Consts.COL_BEACON_MINOR));
                    detailedBeaconList.add(new BluetoothBeaconData(bluetoothBeacon, curBeacon.getTxPower(), curBeacon.getRssi(), curBeacon.getDistance()));
                }
            } catch (ParseException e) {
                mLogger.logError(e);
            }
        }

        int numOfDetailedBeacons = detailedBeaconList.size();
        if (numOfDetailedBeacons == TRILATERATION_REQ_BEACON_NUM) {
            beaconA = detailedBeaconList.get(1);
            beaconB = detailedBeaconList.get(2);
            beaconC = detailedBeaconList.get(3);
        } else if (numOfDetailedBeacons > TRILATERATION_REQ_BEACON_NUM) {
            //TODO:add logic for more beacons
        } else {
            //not enough beacon exists for trilateration.
            return;
        }

        double W, Z, positionX, positionY, positionYError, x1, y1, x2, y2, x3, y3;

        double distanceA = beaconA.getDistance();
        double distanceB = beaconB.getDistance();
        double distanceC = beaconC.getDistance();

        x1 = beaconA.getBluetoothBeacon().getCoordX();
        y1 = beaconA.getBluetoothBeacon().getCoordY();
        x2 = beaconB.getBluetoothBeacon().getCoordX();
        y2 = beaconB.getBluetoothBeacon().getCoordY();
        x3 = beaconC.getBluetoothBeacon().getCoordX();
        y3 = beaconC.getBluetoothBeacon().getCoordY();

        // algorithm based on 'Three distance known' of http://everything2.com/title/Triangulate
        W = distanceA*distanceA - distanceB*distanceB - x1*x1 - y1*y1 + x2*x2 + y2*y2;
        Z = distanceB*distanceB - distanceC*distanceC - x2*x2 - y2*y2 + x3*x3 + y3*y3;

        // NOTE: this algorithm can break when the x and y values of beacons are the same
        // However, placing the beacons on either the same x or y will not provide valueable information
        positionX = (W*(y3-y2) - Z*(y2-y1)) / (2 * ((x2-x1)*(y3-y2) - (x3-x2)*(y2-y1)));
        positionY = (W - 2*positionX*(x2-x1)) / (2*(y2-y1));

        //positionYError is a second measure of y to mitigate errors
        //this estimate will be extremely skewed as bluetooth waves can be distorted by many different
        //sources
        positionYError = (Z - 2*positionX*(x3-x2)) / (2*(y3-y2));

        positionY = (positionY + positionYError) / 2;

        //Store the coordinate object
        ParseObject newCoord = new ParseObject(Consts.TABLE_ESTIMATED_COORDINATE);
        newCoord.put(Consts.COL_COORDINATE_USER, CustomerSingleton.getInstance().getCurUser());
        newCoord.put(Consts.COL_COORDINATE_BUSINESS, "business"); //TODO: put the actual business object
        newCoord.put(Consts.COL_COORDINATE_COORDX, positionX);
        newCoord.put(Consts.COL_COORDINATE_COORDY, positionY);

        newCoord.saveInBackground();
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
