package scout.scoutmobile.constants;

import android.bluetooth.BluetoothAdapter;

public final class Consts {

    //Parse table class names
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String TABLE_PLACE = "Business";
    public static final String TABLE_POINTS = "Points";
    public static final String TABLE_REWARDS = "Reward";
    public static final String TABLE_BEACON = "Beacon";
    public static final String TABLE_BEACONDATA = "BeaconData";

    //Parse Customer object properties
    public static final String COL_CUSTOMER_USER = "user";

    //Parse user object properties
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String POINTS = "points";
    public static final String GENDER = "gender";

    //Parse business object properties
    public static final String COL_PLACE_NAME = "name";
    public static final String COL_PLACE_THUMBNAIL_URL = "thumbnailUrl";
    public static final String COL_PLACE_OWNER = "owner";

    //Parse points object properties
    public static final String COL_POINTS_CUSTOMER = "customer";
    public static final String COL_POINTS_BUSINESS = "business";
    public static final String COL_POINTS_POINTS = "points";

    //Parse rewards object properties
    public static final String COL_REWARDS_POINTS = "points";
    public static final String COL_REWARDS_DESC = "description";
    public static final String COL_REWARDS_BUSINESS = "business";
    public static final String COL_REWARDS_QR = "QRCode";

    //Parse beacon object properties
    public static final String COL_BEACON_MACADDRESS = "macAddress";
    public static final String COL_BEACON_UUID = "uuid";
    public static final String COL_BEACON_MAJOR = "major";
    public static final String COL_BEACON_MINOR = "minor";
    public static final String COL_BEACON_BUSINESS = "business";
    public static final String COL_BEACON_NAME = "name";

    //Parse beacon data object properties
    public static final String COL_BEACONDATA_MACADDRESS = "macAddress";
    public static final String COL_BEACONDATA_BEACON = "beacon";
    public static final String COL_BEACONDATA_MEASUREDPOWER = "measuredPower";
    public static final String COL_BEACONDATA_RSSI = "rssi";
    public static final String COL_BEACONDATA_DISTANCE = "distance";
    public static final String COL_BEACONDATA_BUSINESS = "business";
    public static final String COL_BEACONDATA_CUSTOMER = "customer";

    //For registering and logging in
    public static final String EMAIL_VALIDATING_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

    //For progress dialogs/bars
    public static final String PROGRESS_WAIT = "Please Wait";
    public static final String PROGRESS_BUSINESS_ALL_QUERY = "Retrieving business directories ...";
    public static final String PROGRESS_REWARDS_ALL_QUERY = "Retrieving rewards ...";

    //For intent extra passing
    public static final String PLACE_NAME = "place name";
    public static final String PLACE_ID = "place id";
    public static final String PLACE_POINTS = "place points";
    public static final String CUSTOMER_ID = "customer id";
    public static final String REWARD_ID = "reward id";

    //User login related
    public static final String COL_USER_LOGGEDIN = "loggedin";
    //TODO: use this when we want to use bluetooth mac to identify users -> getBtUniqueId();
    public static final String USER_LOGGED = "NOT_REALLY_RANDOM";

    private Consts() {
        //preventing the class being created
        throw new AssertionError();
    }

    //TODO: test cannot be tested with one device
    synchronized private static String getBtUniqueId() {
        BluetoothAdapter btAdapter = null; // Local Bluetooth adapter
        try {

            btAdapter = BluetoothAdapter.getDefaultAdapter();

            //}
            // Bluetooth is not supported
            if (btAdapter != null) {
                return btAdapter.getAddress();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

}
