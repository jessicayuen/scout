package scout.scoutmobile.controllers;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.CustomerSingleton;

/**
 * PointsManager manages the points the current logged on user has. It accumulates over time in
 * relation to when the user is near a beacon.
 */
public class PointsManager implements BeaconPingObserver {
    private static PointsManager instance = null;

    // Map of business id to seconds
    private static Map<String, Long> mSeconds = new HashMap<>();

    public static PointsManager getInstance() {
        if (instance == null) {
            instance = new PointsManager();
        }
        return instance;
    }

    @Override
    public void onBeaconPing(ParseObject beacon, final ParseObject business) {
        // Update the points every 300 seconds (1 min). Beacons ping every second right now.
        String id = business.getObjectId();

        if (mSeconds.containsKey(id)){
            mSeconds.put(id, mSeconds.get(id) + 1);
        } else {
            mSeconds.put(id, 1l);
        }

        if (mSeconds.get(id) > 300) {
            mSeconds.put(id, 0l);

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_POINTS)
                    .whereEqualTo(Consts.COL_POINTS_BUSINESS, business)
                    .whereEqualTo(Consts.COL_POINTS_CUSTOMER, CustomerSingleton.getInstance().getCurCustomer());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    // We're expecting one ParseObject or none at all.
                    if (parseObjects.size() == 0) {
                        ParseObject points = new ParseObject(Consts.TABLE_POINTS);
                        points.put(Consts.COL_POINTS_CUSTOMER, CustomerSingleton.getInstance().getCurCustomer());
                        points.put(Consts.COL_POINTS_BUSINESS, business);
                        points.put(Consts.COL_POINTS_POINTS, 1);
                        points.saveInBackground();
                    } else {
                        ParseObject points = parseObjects.get(0);
                        points.put(Consts.COL_POINTS_POINTS, points.getInt(Consts.COL_POINTS_POINTS) + 1);
                    }
                }
            });
        }
    }
}
