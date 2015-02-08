package scout.scoutmobile.constants;

public final class Consts {

    //Parse table class names
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String CUSTOMER_USER_COL = "user";
    public static final String TABLE_PLACE = "Business";
    public static final String TABLE_POINTS = "Points";
    public static final String TABLE_REWARDS = "Rewards";

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

    private Consts() {
        //preventing the class being created
        throw new AssertionError();
    }

}
