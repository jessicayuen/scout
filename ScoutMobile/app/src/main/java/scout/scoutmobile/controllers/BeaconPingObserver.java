package scout.scoutmobile.controllers;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

public interface BeaconPingObserver {
    public void onBeaconPing(Collection<Beacon> beacons, long seconds);
}
