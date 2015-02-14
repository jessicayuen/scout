package scout.scoutmobile.utils;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import scout.scoutmobile.constants.Consts;

public class GeneralUtils {


    public static void logUserOut(final ParseUser user) {
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
