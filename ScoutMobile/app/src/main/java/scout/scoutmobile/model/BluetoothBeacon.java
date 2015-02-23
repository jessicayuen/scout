package scout.scoutmobile.model;

import com.estimote.sdk.Beacon;

/**
 * Represents a physical beacon of a business owner.
 */
public class BluetoothBeacon {

    private String mMacAddress;

    // a string that identifies beacons in the region (beacons can share a UUID)
    // UUIDs could always be changed by the user
    private String mUUID;

    // A number that can be used to identify specific beacons
    // (several beacons in a region can have the same major value)
    private Integer mMajor;

    // A number that can be used to identify a specific beacon
    // (the minor value should be unique to one beacon within a region)
    private Integer mMinor;

    public BluetoothBeacon(String macAddress, String uuid, Integer major, Integer minor) {
        this.mMacAddress = macAddress;
        this.mUUID = uuid;
        this.mMajor = major;
        this.mMinor = minor;
    }

    public BluetoothBeacon(Beacon beacon) {
        this(beacon.getMacAddress(), beacon.getProximityUUID(), beacon.getMajor(),
                beacon.getMinor());
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getUUID() {
        return mUUID;
    }

    public Integer getMajor() {
        return mMajor;
    }

    public Integer getMinor() {
        return mMinor;
    }
}
