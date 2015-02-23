package scout.scoutmobile.utils;

/**
 * Contains general utility functions used elsewhere
 * NOTE: should consider inheriting ParseUser class and override the logout instead
 *      of making own logout.
 */

//Current implementation check could cause problems when users uninstall app hmm.

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import scout.scoutmobile.constants.Consts;

public class GeneralUtils {


    public static void logUserOut() {
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
    }

    private GeneralUtils() {
        throw new AssertionError();
    }

}
