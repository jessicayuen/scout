/**
 * The sole purpose of this activity is to dispatch the application to the correct activity.
 * Since parse user once logged in has a local instance stored, we can bypass the need to go to
 * the login screen whenever the application is reopened.
 */
package scout.scoutmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import scout.scoutmobile.constants.Consts;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser loggedInUser = ParseUser.getCurrentUser();
        if (loggedInUser != null) {
            ParseQuery customer = new ParseQuery(Consts.TABLE_CUSTOMER);
            customer.whereEqualTo(Consts.COL_CUSTOMER_USER, loggedInUser);
            customer.getFirstInBackground(new GetCallback() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject != null) {
                        Intent invokedActivity = new Intent(DispatchActivity.this, PlacesActivity.class);
                        invokedActivity.putExtra(Consts.CUSTOMER_ID, parseObject.getObjectId());
                        invokeActivity(invokedActivity);
                    } else {
                        invokeLoginActivity();
                    }
                }
            });
        } else {
            invokeLoginActivity();
        }
    }

    private void invokeActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void invokeLoginActivity() {
        Intent login = new Intent(this, LoginActivity.class);
        invokeActivity(login);
    }
}
