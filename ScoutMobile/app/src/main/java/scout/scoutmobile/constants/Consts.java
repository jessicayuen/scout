package scout.scoutmobile.constants;

public final class Consts {

    //Contants to access properties in parse user object
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String POINTS = "points";
    public static final String GENDER = "gender";

    //For registering and logging in
    public static final String EMAIL_VALIDATING_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";


    private Consts() {
        //preventing the class being created
        throw new AssertionError();
    }

}
