package scout.scoutmobile.models;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import scout.scoutmobile.constants.Consts;

public class User {

    private static User currentUser = null;

    private ParseUser loggedInUser; // Current user logged in

    private int updateCounter = 0; // counter for when the user should be updated

    public static User getInstance() {
        if (currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    public int getPoints() {
        return loggedInUser.getInt(Consts.POINTS);
    }

    public void setPoints(int points) {
        loggedInUser.put(Consts.POINTS, points);
        checkUpdateStatus();
    }

    public String getEmail() {
        return loggedInUser.getEmail();
    }

    public String getFirstName() {
        return loggedInUser.getString(Consts.FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        loggedInUser.put(Consts.FIRST_NAME, firstName);
        checkUpdateStatus();
    }

    public String getLastName() {
        return loggedInUser.getString(Consts.LAST_NAME);
    }

    public void setLastName(String lastName) {
        loggedInUser.put(Consts.LAST_NAME, lastName);
        checkUpdateStatus();
    }

    public void logOut() {
        loggedInUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    loggedInUser.saveEventually();
                }
                loggedInUser.logOut();
                resetUser();
            }
        });
    }

    public void resetUser() {
        loggedInUser = null;
    }

    public void setCurrentUser(ParseUser user) {
        loggedInUser = user;
    }

    /**
     * checks whether or not the user data should be updated to the server
     */
    private void checkUpdateStatus() {
        if (updateCounter == 5) {
            loggedInUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        loggedInUser.saveEventually(); // fall back for when data fails to save immediately
                    }
                }
            });
            updateCounter = 0;
        } else {
            updateCounter++;
        }
    }

    public void checkForUpdates() {
        loggedInUser.fetchIfNeededInBackground();
    }

    private User() {
        resetUser();
    }
}
