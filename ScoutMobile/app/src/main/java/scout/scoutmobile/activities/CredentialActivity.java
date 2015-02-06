package scout.scoutmobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
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

    protected boolean isPasswordValid(String password) {
        return password.length() >= MIN_USER_PASSWORD_LENGTH ;
    }

    protected boolean isNetworkingAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo != null) {
            return netInfo.isConnected();
        }

        return false;
    }

    protected void toastNoNetwork() {
        showToast(getString(R.string.toast_network_unavailable));
    }

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

    protected String getErrorString(int errorCode) {
        String resStr = "";
        switch (errorCode) {
            case ParseException.ACCOUNT_ALREADY_LINKED:
                resStr = "Account already logged in";
                break;
            case ParseException.EMAIL_TAKEN:
                resStr = "Account already exists with that email";
                break;
            case ParseException.CONNECTION_FAILED:
                resStr = "Error Connecting to the server";
                break;
            default:
                return resStr = "";
        }
        return resStr;
    }
}
