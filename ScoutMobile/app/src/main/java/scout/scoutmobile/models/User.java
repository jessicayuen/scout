package scout.scoutmobile.models;


import com.parse.ParseUser;

import scout.scoutmobile.constants.Consts;

public class User {

    private static User currentUser = null;

    private ParseUser loggedInUser; // Current user logged in

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
    }

    public String getEmail() {

        return loggedInUser.getEmail();
    }

    public String getFirstName() {
        return loggedInUser.getString(Consts.FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        loggedInUser.put(Consts.FIRST_NAME, firstName);
    }

    public String getLastName() {
        return loggedInUser.getString(Consts.LAST_NAME);
    }

    public void setLastName(String lastName) {
        loggedInUser.put(Consts.LAST_NAME, lastName);
    }

    public void logOut() {
        loggedInUser.logOut();
        resetUser();
    }

    public void resetUser() {
        loggedInUser = null;
    }

    public void setCurrentUser(ParseUser user) {
        loggedInUser = user;
    }

    private User() {
        resetUser();
    }
}
