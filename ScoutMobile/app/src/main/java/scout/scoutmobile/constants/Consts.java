package scout.scoutmobile.constants;

public final class Consts {

    //Parse table class names
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String CUSTOMER_USER_COL = "user";

    /**
     * for every table and tables col names add them in a readable format with a space separation
     */

    //Contants to access properties in parse user object
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String POINTS = "points";
    public static final String GENDER = "gender";

    //For registering and logging in
    public static final String EMAIL_VALIDATING_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";


    private Consts() {
        //preventing the class being created
        throw new AssertionError();
    }

}
