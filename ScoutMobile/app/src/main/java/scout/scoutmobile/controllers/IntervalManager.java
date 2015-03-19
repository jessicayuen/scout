package scout.scoutmobile.controllers;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

import scout.scoutmobile.utils.Logger;

public class IntervalManager implements BeaconPingObserver {
    private static IntervalManager instance = null;
    private static Logger mLogger = new Logger("IntervalManager");


    @Override
    public void onBeaconPing(Collection<Beacon> beacons, long seconds) {
        if (beacons.isEmpty()) {
            return;
        }


    }

}
