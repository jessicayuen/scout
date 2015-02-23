package scout.scoutmobile.model;

import com.estimote.sdk.Beacon;

/**
 * Represents the recorded data from an active beacon at that moment in time.
 */
public class BluetoothBeaconData {

    // The parent beacon for the corresponding bluetooth beacon data
    private BluetoothBeacon mBluetoothBeacon;

    // A factory-calibrated, read-only constant which indicates what's the expected RSSI at a
    // distance of 1 meter to the beacon.
    private Integer mMeasuredPower;

    // The strength of the beacon's signal as seen on the receiving device
    private Integer mRSSI;

    public BluetoothBeaconData(BluetoothBeacon bluetoothBeacon, Integer measuredPower, Integer rssi) {
        this.mBluetoothBeacon = bluetoothBeacon;
        this.mMeasuredPower = measuredPower;
        this.mRSSI = rssi;
    }

    public BluetoothBeaconData(BluetoothBeacon bluetoothBeacon, Beacon beacon) {
        this(bluetoothBeacon, beacon.getMeasuredPower(), beacon.getRssi());
    }

    public BluetoothBeacon getBluetoothBeacon() {
        return mBluetoothBeacon;
    }

    public Integer getMeasuredPower() {
        return mMeasuredPower;
    }

    public Integer getRSSI() {
        return mRSSI;
    }
}
