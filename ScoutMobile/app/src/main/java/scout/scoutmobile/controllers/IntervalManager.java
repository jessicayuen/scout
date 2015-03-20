package scout.scoutmobile.controllers;

import android.graphics.Point;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.BluetoothBeacon;
import scout.scoutmobile.model.BluetoothBeaconData;
import scout.scoutmobile.model.Interval;
import scout.scoutmobile.utils.Logger;

public class IntervalManager implements BeaconPingObserver {
    private static final int MIN_REQ_BEACON_NUM = 3;
    private static IntervalManager instance = null;
    private static Logger mLogger = new Logger("IntervalManager");
    private static IntervalHandler intervalHandler = null;

    private static class IntervalHandler {
        Interval interval = null;
        ParseObject intervalObj = null;
        ParseObject businessObj = null;
        ParseObject customerObj = null;

        private void resetInterval() {
            this.interval = null;
            this.intervalObj = null;
            this.businessObj = null;
            this.customerObj = null;
        }
    }

    public static IntervalManager getInstance() {
        if (instance == null) {
            instance = new IntervalManager();
            intervalHandler = new IntervalHandler();
        }

        return instance;
    }

    @Override
    public void onBeaconPing(Collection<Beacon> beacons, long seconds) {
        if (beacons.size() < MIN_REQ_BEACON_NUM) {
            intervalHandler.resetInterval();

            return;
        }

        if (intervalHandler.interval == null) {
            // new interval
            initIntervalHandler(beacons);
            insertInterval();
        } else {
            // update current interval
            incrementIntervalToDate(seconds);
            updateInterval();
        }

        insertIntervalRecord(beacons);
    }

    private void initIntervalHandler(Collection<Beacon> beacons) {
        Beacon beacon = beacons.iterator().next();

        intervalHandler.interval = new Interval();
        intervalHandler.customerObj = getCustomer();
        intervalHandler.businessObj = getBusiness(beacon);
    }

    private void insertInterval() {
        try {
            ParseObject intervalObj = new ParseObject(Consts.TABLE_INTERVAL);
            intervalObj.put("business", intervalHandler.businessObj);
            intervalObj.put("customer", intervalHandler.customerObj);
            intervalObj.put("from", intervalHandler.interval.getFrom());
            intervalObj.put("to", intervalHandler.interval.getTo());
            intervalObj.save();

            intervalHandler.intervalObj = intervalObj;

            if (intervalHandler.intervalObj == null) {
                throw new Exception("Interval Object is null");
            }
        } catch (ParseException e) {
            mLogger.logError(e);
        } catch (Exception e) {
            mLogger.logError(e);
        }
    }

    private void updateInterval() {
        try {
            intervalHandler.intervalObj.put("to", intervalHandler.interval.getTo());
            intervalHandler.intervalObj.saveInBackground();

            if (intervalHandler.intervalObj == null) {
                throw new Exception("Interval Object is null");
            }
        } catch (ParseException e) {
            mLogger.logError(e);
        } catch (Exception e) {
            mLogger.logError(e);
        }
    }

    private void insertIntervalRecord(Collection<Beacon> beacons) {
        ArrayList<BluetoothBeaconData> beaconDataList = getBeaconDataArrayList(beacons);

        try {
            if (beaconDataList.size() < MIN_REQ_BEACON_NUM) {
                throw new Exception("Insufficient beacons to create interval record");
            }

            Point location = getCoordinateWithBeacons(beaconDataList);

            ParseObject intervalRecordObj = new ParseObject(Consts.TABLE_INTERVAL_RECORD);
            intervalRecordObj.put("interval", intervalHandler.intervalObj);
            intervalRecordObj.put("timestamp", intervalHandler.interval.getTo());
            intervalRecordObj.put("distBeacon1", beaconDataList.get(0).getDistance());
            intervalRecordObj.put("distBeacon2", beaconDataList.get(1).getDistance());
            intervalRecordObj.put("distBeacon3", beaconDataList.get(2).getDistance());

            if (location != null) {
                intervalRecordObj.put("coordX", location.x);
                intervalRecordObj.put("coordY", location.y);
            }

            intervalRecordObj.saveInBackground();
        } catch (Exception e) {
            mLogger.logError(e);
        }
    }

    private ArrayList<BluetoothBeaconData> getBeaconDataArrayList(Collection<Beacon> beacons) {
        ArrayList<BluetoothBeaconData> bluetoothBeaconDataList = new ArrayList<BluetoothBeaconData>();

        if (beacons.size() == MIN_REQ_BEACON_NUM) {
            for (Iterator<Beacon> it = beacons.iterator(); it.hasNext(); ) {
                Beacon beacon = (Beacon) it.next();

                try {
                    ParseQuery<ParseObject> bluetoothBeaconQuery = ParseQuery.getQuery(Consts.TABLE_BEACON)
                        .whereEqualTo(Consts.COL_BEACONDATA_MACADDRESS, beacon.getBluetoothAddress());

                    ParseObject beaconObj = bluetoothBeaconQuery.getFirst();

                    if (beaconObj != null) {
                        BluetoothBeacon bluetoothBeacon = new BluetoothBeacon(beaconObj);

                        BluetoothBeaconData bluetoothBeaconData = new BluetoothBeaconData(
                                bluetoothBeacon, beacon.getTxPower(), beacon.getRssi(), beacon.getDistance());

                        bluetoothBeaconDataList.add(bluetoothBeaconData);
                    }
                } catch (ParseException e) {
                    mLogger.logError(e);
                }
            }
        } else if (beacons.size() > MIN_REQ_BEACON_NUM) {
            // determine the 3 best beacons to use
        }

        return bluetoothBeaconDataList;
    }

    private ParseObject getCustomer() {
        ParseObject customerObj = null;

        try {
            ParseUser currentUser = ParseUser.getCurrentUser();

            ParseQuery<ParseObject> customerQuery = ParseQuery.getQuery(Consts.TABLE_CUSTOMER)
                    .whereEqualTo("user", currentUser);

            customerObj = customerQuery.getFirst();
        } catch (ParseException e) {
            mLogger.logError(e);
        }

        return customerObj;
    }

    private ParseObject getBusiness(Beacon beacon) {
        ParseObject businessObj = null;

        try {
            ParseQuery<ParseObject> beaconQuery = ParseQuery.getQuery(Consts.TABLE_BEACON)
                    .whereEqualTo(Consts.COL_BEACON_MACADDRESS, beacon.getBluetoothAddress());

            businessObj = beaconQuery.getFirst().getParseObject(Consts.COL_BEACON_BUSINESS).fetch();
        } catch (ParseException e) {
            mLogger.logError(e);
        }
        return businessObj;
    }

    private void incrementIntervalToDate(long deltaTimeSec) {
        long toTime = intervalHandler.interval.getTo().getTime();
        long deltaTimeMilli = deltaTimeSec * 1000;

        intervalHandler.interval.setTo(new Date(toTime + deltaTimeMilli));
    }

    /**
     * Get coordinates with a list of beacons provided
     * @param bluetoothBeaconList
     * @return a Point if coordinate has been found. null other wise
     */
    private Point getCoordinateWithBeacons(ArrayList<BluetoothBeaconData> bluetoothBeaconList) {
        BluetoothBeaconData beaconA = bluetoothBeaconList.get(0);
        BluetoothBeaconData beaconB = bluetoothBeaconList.get(1);
        BluetoothBeaconData beaconC = bluetoothBeaconList.get(2);
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

}
