/**
 * The sole purpose of this activity is to dispatch the application to the correct activity.
 * Since parse user once logged in has a local instance stored, we can bypass the need to go to
 * the login screen whenever the application is reopened.
 */
package scout.scoutmobile.activities;

import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import scout.scoutmobile.constants.Consts;

public class DispatchActivity extends CredentialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent invokedActivity;
        final ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_CUSTOMER);
            query.whereEqualTo(Consts.COL_CUSTOMER_USER, parseUser);
            ParseObject object = null;
            try {
                object = query.getFirst();
                if (object != null) {
                    setCurrentUser(parseUser, object);
                    invokedActivity = new Intent(this, PlacesActivity.class);
                } else {
                    invokedActivity = new Intent(this, LoginActivity.class);
                }
            } catch (ParseException e) {
                invokedActivity = new Intent(this, LoginActivity.class);
                e.printStackTrace();
            }
        } else {
            invokedActivity = new Intent(this, LoginActivity.class);
        }

        invokedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(invokedActivity);
        finish();
    }
}
