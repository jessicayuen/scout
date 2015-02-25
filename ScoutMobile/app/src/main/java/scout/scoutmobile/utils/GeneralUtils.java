package scout.scoutmobile.utils;

import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import scout.scoutmobile.activities.LoginActivity;
import scout.scoutmobile.constants.Consts;

public class GeneralUtils {

    private static Logger m_Logger = new Logger("GeneralUtils");

    public static void logUserOut(Context context) {
        final ParseUser user = ParseUser.getCurrentUser();
        user.remove(Consts.COL_USER_LOGGEDIN);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    user.saveEventually();
                }
                ParseUser.logOut();
            }
        });

        startMainActivity(context, LoginActivity.class);
    }

    /**
     * NOTE: This should be called whenever an action occurs, should normally be called on application
     * resume.
     *
     * Checks whether or not current user logged in is the one registered in Parse database, this
     * handles unique user login by checking User tables loggedin field for bt mac address matches
     * the current users bt mac address. If the current parseUser does not have the same mac
     * address as the one registered in Parse database, we log the current user out
     */
    public static void verifyUserLoggedIn(Context context) {
        ParseUser curUser = ParseUser.getCurrentUser();
        boolean userLoggedIn = false;
        try {
            curUser.fetchIfNeeded();
            String parseLoggedInUser = curUser.getString(Consts.COL_USER_LOGGEDIN);
            userLoggedIn = ((parseLoggedInUser != null) &&
                    (parseLoggedInUser != Consts.USER_LOGGED) &&
                    !parseLoggedInUser.isEmpty());
            m_Logger.log("user logged in " + userLoggedIn);
        } catch (ParseException e) {
            m_Logger.logError(e);
        }

        if (userLoggedIn) {
            logUserOut(context);
        }
    }

    /**
     * NOTE: finish() should be called in the activity that calls this to prevent malformed behaviour
     * on back button presses
     * starts an unique activity with given context and mainclass, this pretty much starts the applcation
     * over again.
     * @param context
     * @param mainClass
     */
    public static void startMainActivity(Context context, Class<?> mainClass) {
        m_Logger.log("Starting main activity");
        Intent mainActivity = new Intent(context, mainClass);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(mainActivity);
    }

    private GeneralUtils() {
    }

}
