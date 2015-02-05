package scout.scoutmobile.constants;

public final class Consts {

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
