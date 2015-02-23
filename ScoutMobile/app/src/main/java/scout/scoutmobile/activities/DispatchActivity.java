/**
 * The sole purpose of this activity is to dispatch the application to the correct activity.
 * Since parse user once logged in has a local instance stored, we can bypass the need to go to
 * the login screen whenever the application is reopened.
 */
package scout.scoutmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends CredentialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent invokedActivity;
        ParseUser loggedInUser = ParseUser.getCurrentUser();
        if (loggedInUser != null) {
            invokedActivity = new Intent(this, PlacesActivity.class);
        } else {
            invokedActivity = new Intent(this, LoginActivity.class);
        }
        invokedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(invokedActivity);
        finish();
    }

}
