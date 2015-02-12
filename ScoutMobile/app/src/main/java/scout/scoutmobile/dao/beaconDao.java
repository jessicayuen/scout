package scout.scoutmobile.dao;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import scout.scoutmobile.constants.Consts;

public class BeaconDao {

    public void getBeaconByMacAddress(String macAddress, FindCallback<ParseObject> callback) {

        ParseQuery<ParseObject> beaconQuery = ParseQuery.getQuery(Consts.TABLE_BEACON).
                whereEqualTo(Consts.COL_BEACONS_MACADDRESS, macAddress);

        beaconQuery.findInBackground(callback);
    }

}