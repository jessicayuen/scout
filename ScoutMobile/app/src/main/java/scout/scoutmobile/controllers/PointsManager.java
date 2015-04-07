package scout.scoutmobile.controllers;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.CustomerSingleton;
import scout.scoutmobile.utils.Logger;

/**
 * PointsManager manages the points the current logged on user has. It accumulates over time in
 * relation to when the user is near a beacon.
 */
public class PointsManager implements BeaconPingObserver {
    private static PointsManager instance = null;
    private static Logger mLogger = new Logger("PointsManager");

    // Map of business id to seconds
    private static Map<String, Long> mSeconds = new HashMap<>();

    public static PointsManager getInstance() {
        if (instance == null) {
            instance = new PointsManager();
        }
        return instance;
    }

    @Override
    public void onBeaconPing(Collection<Beacon> beacons, final long seconds) {
        if (beacons.isEmpty()) {
            return;
        }

        // We only care about the first one, since it's the business that's important,
        // not the beacon.
        Beacon beacon = beacons.iterator().next();

        ParseQuery<ParseObject> beaconQuery = ParseQuery.getQuery(Consts.TABLE_BEACON)
                .whereEqualTo(Consts.COL_BEACON_MACADDRESS, beacon.getBluetoothAddress());

        beaconQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> beaconResults, ParseException e) {
                if (!beaconResults.isEmpty() && e == null) {
                    ParseObject business = beaconResults.get(0).getParseObject(Consts.COL_BEACON_BUSINESS);
                    // Update the points every 300 seconds (1 min).
                    long updateTime = 1;

                    updatePoints(business, updateTime);
                } else {
                    mLogger.logError(e != null ? e :
                            new RuntimeException("No beacon exists"));
                }
            }
        });
    }

    private void updatePoints(final ParseObject business, long updateTime) {
        String id = business.getObjectId();

        if (mSeconds.containsKey(id)){
            mSeconds.put(id, mSeconds.get(id) + 1);
        } else {
            mSeconds.put(id, 1l);
        }

        if (mSeconds.get(id) >= updateTime) {
            mSeconds.put(id, 0l);

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_POINTS)
                    .whereEqualTo(Consts.COL_POINTS_BUSINESS, business)
                    .whereEqualTo(Consts.COL_POINTS_CUSTOMER, CustomerSingleton.getInstance().getCurCustomer());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    // We're expecting one ParseObject or none at all.
                    ParseObject points;

                    if (parseObjects.size() == 0) {
                        points = new ParseObject(Consts.TABLE_POINTS);
                        points.put(Consts.COL_POINTS_CUSTOMER, CustomerSingleton.getInstance().getCurCustomer());
                        points.put(Consts.COL_POINTS_BUSINESS, business);
                        points.put(Consts.COL_POINTS_POINTS, 1);
                    } else {
                        points = parseObjects.get(0);
                        points.put(Consts.COL_POINTS_POINTS, points.getInt(Consts.COL_POINTS_POINTS) + 1);
                    }

                    points.saveInBackground();
                }
            });
        }
    }
}
