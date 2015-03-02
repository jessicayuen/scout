package scout.scoutmobile.model;

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

    // The estimated distance in meters from the parent bluetooth beacon
    private Double mDistance;

    public BluetoothBeaconData(BluetoothBeacon bluetoothBeacon, Integer measuredPower, Integer rssi, Double distance) {
        this.mBluetoothBeacon = bluetoothBeacon;
        this.mMeasuredPower = measuredPower;
        this.mRSSI = rssi;
        this.mDistance = distance;
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

    public Double getDistance() {
        return mDistance;
    }
}
