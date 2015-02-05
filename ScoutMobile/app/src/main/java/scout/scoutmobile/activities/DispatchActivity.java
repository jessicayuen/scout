package scout.scoutmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

import scout.scoutmobile.PlacesActivity;
import scout.scoutmobile.models.User;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser loggedInUser = ParseUser.getCurrentUser();
        Intent invokedActivity;
        if (loggedInUser != null) {
            User.getInstance().setCurrentUser(loggedInUser);
            invokedActivity = new Intent(this, PlacesActivity.class);
        } else {
            invokedActivity = new Intent(this, LoginActivity.class);
        }
        invokedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(invokedActivity);
        finish();
    }
}
