package scout.scoutmobile.model;

import android.graphics.Point;

import java.util.Date;

public class IntervalRecord {

    private Interval mInterval;
    private Date mTimeStamp;
    private BluetoothBeacon mBluetoothBeacon1;
    private BluetoothBeacon mBluetoothBeacon2;
    private BluetoothBeacon mBluetoothBeacon3;
    private Integer mDistance1;
    private Integer mDistance2;
    private Integer mDistance3;
    private Point mCoordinate;

    public IntervalRecord(Interval mInterval, BluetoothBeacon mBluetoothBeacon1, BluetoothBeacon mBluetoothBeacon2, BluetoothBeacon mBluetoothBeacon3, Integer mDistance1, Integer mDistance2, Integer mDistance3) {
        this.mInterval = mInterval;
        this.mTimeStamp = new Date();
        this.mBluetoothBeacon1 = mBluetoothBeacon1;
        this.mBluetoothBeacon2 = mBluetoothBeacon2;
        this.mBluetoothBeacon3 = mBluetoothBeacon3;
        this.mDistance1 = mDistance1;
        this.mDistance2 = mDistance2;
        this.mDistance3 = mDistance3;

        // apply algorithm
        this.mCoordinate = new Point(0,0);
    }

    public Interval getInterval() {
        return mInterval;
    }

    public Date getTimeStamp() {
        return mTimeStamp;
    }

    public BluetoothBeacon getBluetoothBeacon1() {
        return mBluetoothBeacon1;
    }

    public BluetoothBeacon getBluetoothBeacon2() {
        return mBluetoothBeacon2;
    }

    public BluetoothBeacon getBluetoothBeacon3() {
        return mBluetoothBeacon3;
    }

    public Integer getDistance1() {
        return mDistance1;
    }

    public Integer getDistance2() {
        return mDistance2;
    }

    public Integer getDistance3() {
        return mDistance3;
    }

    public Point getCoordinate() {
        return mCoordinate;
    }
}
