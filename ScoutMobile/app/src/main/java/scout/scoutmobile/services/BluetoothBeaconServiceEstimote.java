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
import com.parse.ParseUser;

import java.util.List;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.BluetoothBeacon;
import scout.scoutmobile.model.BluetoothBeaconData;
import scout.scoutmobile.model.CustomerSingleton;
import scout.scoutmobile.utils.Logger;

public class BluetoothBeaconServiceEstimote extends Service {

    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", null, null, null);
    private static final int TRILATERATION_REQ_BEACON_NUM = 3;
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
                if (beacons.size() > TRILATERATION_REQ_BEACON_NUM) {
                    saveCoordinateWithBeacons(beacons);
                }
                mLogger.log("Ranged beacons: " + beacons);
            }
        });

        mLogger.log("Beacon service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(beaconManager.isBluetoothEnabled() && beaconManager.hasBluetooth()) {
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
        } else {
            return Service.START_NOT_STICKY;
        }
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
        final BluetoothBeaconData bluetoothBeaconData = new BluetoothBeaconData(bluetoothBeacon, beacon.getMeasuredPower(), beacon.getRssi(), 0.0);

        // query for unique beacon mac address
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

    private void saveCoordinateWithBeacons(List<Beacon> beacons) {

        List<BluetoothBeaconData> detailedBeaconList = null;
        BluetoothBeaconData beaconA, beaconB, beaconC;
        beaconA = beaconB = beaconC = null;

        // iterate through list of beacons detected and get their coordinates from the server
        // since beacons cant really store any information on them.
        for (int i = 0; i < beacons.size(); i++) {
            BluetoothBeacon bluetoothBeacon;
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_BEACON);
            Beacon curBeacon = beacons.get(i);
            query.whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, curBeacon.getMacAddress());

            try {
                ParseObject obj = query.getFirst();
                if (obj != null) {
                    bluetoothBeacon = new BluetoothBeacon(obj.getString(Consts.COL_BEACON_MACADDRESS), obj.getString(Consts.COL_BEACON_UUID),
                            obj.getInt(Consts.COL_BEACON_MAJOR), obj.getInt(Consts.COL_BEACON_MINOR));
                    detailedBeaconList.add(new BluetoothBeaconData(bluetoothBeacon, curBeacon.getMeasuredPower(), curBeacon.getRssi(), 0.0));
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
}
