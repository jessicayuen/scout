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

    private static final int TRILATERATION_REQ_BEACON_NUM = 3;
    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static List<BeaconPingObserver> mPingObservers = new ArrayList<>();
    private Logger mLogger = new Logger("BluetoothBeaconService");
    private BeaconManager beaconManager = null;
    private long scanDuration = 5000l;
    private long startTime, endTime;
    private int minimalDuration = 30000;// timeout before sending user duration info
    private boolean withinProximity = false; //indicates whether or not the user entered a store

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setForegroundScanPeriod(scanDuration);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT));
        beaconManager.bind(this);

        startTime = endTime = 0;
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
                insertNewBeacons(beacons);

                notifyBeaconPingObservers(beacons, scanDuration);

                // if app stable should remove the old code below

               /*
                int numOfBeacons = beacons.size();
                if (numOfBeacons > 0) {
                    if (!withinProximity) {
                        withinProximity = true;
                        startTime = System.currentTimeMillis();
                    }
                    StoreBeaconData(beacons);

                    if (numOfBeacons >= TRILATERATION_REQ_BEACON_NUM) {
//                        getCoordinateWithBeacons(beacons);
                    } else {
                        endTime = System.currentTimeMillis();
                    }

                    notifyBeaconPingObservers(beacons, scanDuration);
                }


                if (withinProximity) {
                    long duration = endTime - startTime; //is subtraction expensive?
                    if (duration >= minimalDuration) {
                        sendUserStayedDuration(duration);
                        withinProximity = false;
                    }
                }
                */
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            mLogger.logError(e);
        }
    }

    private void insertNewBeacons (Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            final BluetoothBeacon bluetoothBeacon =
                    new BluetoothBeacon(beacon.getBluetoothAddress(), beacon.getId1().toUuidString(),
                            beacon.getId2().toInt(), beacon.getId3().toInt(), -1, -1);
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


    private void StoreBeaconData (Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            saveBeacon(beacon);
        }
    }

    private void saveBeacon (Beacon beacon) {
        final BluetoothBeacon bluetoothBeacon =
                new BluetoothBeacon(beacon.getBluetoothAddress(), beacon.getId1().toUuidString(),
                                    beacon.getId2().toInt(), beacon.getId3().toInt(), -1, -1);
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

    /**
     * Get coordinates with a list of beacons provided
     * @param beacons
     * @return a Point if coordinate has been found. null other wise
     */
    private Point getCoordinateWithBeacons(Collection<Beacon> beacons) {
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
                    bluetoothBeacon = new BluetoothBeacon(obj);
                    detailedBeaconList.add(new BluetoothBeaconData(bluetoothBeacon, curBeacon.getTxPower(), curBeacon.getRssi(), curBeacon.getDistance()));
                }
            } catch (ParseException e) {
                mLogger.logError(e);
            }
        }

        int numOfDetailedBeacons = detailedBeaconList.size();
        if (numOfDetailedBeacons == TRILATERATION_REQ_BEACON_NUM) {
            beaconA = detailedBeaconList.get(0);
            beaconB = detailedBeaconList.get(1);
            beaconC = detailedBeaconList.get(2);
        } else if (numOfDetailedBeacons > TRILATERATION_REQ_BEACON_NUM) {
            //TODO:add logic for more beacons
        } else {
            //not enough beacon exists for trilateration.
            return null;
        }

        double W, Z, positionX, positionY, x1, y1, x2, y2, x3, y3;

        double distanceA = beaconA.getDistance();
        double distanceB = beaconB.getDistance();
        double distanceC = beaconC.getDistance();

        x1 = beaconA.getBluetoothBeacon().getCoordX();
        y1 = beaconA.getBluetoothBeacon().getCoordY();
        x2 = beaconB.getBluetoothBeacon().getCoordX();
        y2 = beaconB.getBluetoothBeacon().getCoordY();
        x3 = beaconC.getBluetoothBeacon().getCoordX();
        y3 = beaconC.getBluetoothBeacon().getCoordY();

        // checking if the any of the beacons are aligned if so we cant do trilateration since
        // having beacons on the same axis doesn't provide any valuable information
        if ((y1 - y2)*(x1 - x3) == (y1 - y3)*(x1 - x2)) {
            return null;
        }

        // algorithm based on 'Three distance known' of http://everything2.com/title/Triangulate
        // use case https://www.youtube.com/watch?v=dMWEl6GBGqk
        // results obtained after using the distances for the beacons are extremely inaccurate
        W = (Math.pow(x3, 2.0) - Math.pow(x2, 2.0) + Math.pow(y3, 2.0) -
                Math.pow(y2, 2.0) + Math.pow(distanceB, 2.0) - Math.pow(distanceC, 2.0)) / 2.0;
        Z = (Math.pow(x1, 2.0) - Math.pow(x2, 2.0) + Math.pow(y1, 2.0) -
                Math.pow(y2, 2.0) + Math.pow(distanceB, 2.0) - Math.pow(distanceA, 2.0)) / 2.0;

        positionY = ((Z * (x2 - x3)) - (W * (x2 - x1))) / (((y1 - y2) * (x2 - x3)) - ((y3 - y2) * (x2 - x1)));
        positionX = ((positionY * (y1 - y2)) - Z) / (x2 - x1);

        Point userLoc = new Point((int)Math.round(positionX), (int)Math.round(positionY));

        return userLoc;
    }

    public static void addBeaconPingObserver(BeaconPingObserver observer) {
        mPingObservers.add(observer);
    }

    private void notifyBeaconPingObservers(Collection<Beacon> beacons, long milliseconds) {
        for (BeaconPingObserver observer : mPingObservers) {
            observer.onBeaconPing(beacons, milliseconds / 1000);
        }
    }

    /**
     * store the duration of the user at a specific business
     * NOTE: currently only when user stay at a business for more than 30 sec is the information stored
     * @param duration
     */
    private void sendUserStayedDuration(long duration) {
        ParseObject userDuration = new ParseObject(Consts.TABLE_INTERVAL);
        userDuration.put(Consts.COL_INTERVAL_USER, CustomerSingleton.getInstance().getCurUser());
        userDuration.put(Consts.COL_INTERVAL_BUSINESS, "business"); //TODO need to somehow get business
        userDuration.put(Consts.COL_INTERVAL_DURATION, duration);
        userDuration.saveInBackground();
    }
}
