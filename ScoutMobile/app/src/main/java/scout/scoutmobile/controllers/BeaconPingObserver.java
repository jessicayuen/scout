package scout.scoutmobile.controllers;

import com.parse.ParseObject;

public interface BeaconPingObserver {
    public void onBeaconPing(ParseObject beacon, ParseObject business);
}
