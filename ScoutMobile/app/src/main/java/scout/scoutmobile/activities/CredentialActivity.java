/**
 * Superclass for user credential related activities. These include but not limited to logging in
 * and registering.
 */

package scout.scoutmobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.CustomerSingleton;
import scout.scoutmobile.utils.GeneralUtils;
import scout.scoutmobile.utils.Logger;

public class CredentialActivity extends Activity {

    protected static final int MIN_USER_PASSWORD_LENGTH = 6;

    protected ProgressDialog mLoadingDialog;

    protected Logger mLogger;


    /**
     * Checks whether or not the email is valid syntax wise, it however does not validate the email
     * given with any external source
     * @param email the email provided by user
     * @return true if valid, false other wise
     */
    protected boolean isEmailValid(String email) {
        return email.matches(Consts.EMAIL_VALIDATING_REGEX);
    }

    /**
     * checks for the validity of the password, currently only checks if a password is of a certain
     * length
     * @param password password string to check
     * @return true if value false otherwise
     */
    protected boolean isPasswordValid(String password) {
        return password.length() >= MIN_USER_PASSWORD_LENGTH ;
    }

    /**
     * Checks for if a network is available to transfer data either wifi or mobile data
     * @return true if a source is available false other wise
     */
    protected boolean isNetworkingAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null) {
            return netInfo.isConnected();
        }

        return false;
    }

    /**
     * Toasts no network available when called
     */
    protected void toastNoNetwork() {
        showToast(getString(R.string.toast_network_unavailable));
    }

    /**
     * Displays a toast with the given message
     * @param message message to be displayed
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    protected void showProgress(boolean show) {
        if (show) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * Displays the error string with the given error code.
     * for more information visit https://parse.com/docs/android/api/com/parse/ParseException.html
     * @param errorCode the ParseException error code
     * @return a human readable string corresponding to the error code
     */
    protected String getErrorString(int errorCode) {
        String resStr = "";
        switch (errorCode) {
            case ParseException.EMAIL_TAKEN:
                resStr = getString(R.string.exception_email_exits);
                break;
            case ParseException.CONNECTION_FAILED:
                resStr = getString(R.string.exception_server_connection);
                break;
            case ParseException.INVALID_EMAIL_ADDRESS:
                resStr = getString(R.string.exception_invalid_email);
                break;
            default:
                return resStr = getString(R.string.exception_general);
        }
        return resStr;
    }

    protected void startMainActivity(Context context, Class<?> mainClass) {
        GeneralUtils.startMainActivity(context, mainClass);
        finish();
    }

    protected void setCurrentUser(ParseUser user, ParseObject customer) {
        CustomerSingleton customerSingleton = CustomerSingleton.getInstance();
        customerSingleton.setCurCustomer(customer);
        customerSingleton.setCurUser(user);
    }

}
