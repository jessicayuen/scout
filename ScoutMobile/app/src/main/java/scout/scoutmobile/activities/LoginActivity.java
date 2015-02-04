package scout.scoutmobile.activities;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import scout.scoutmobile.PlacesActivity;
import scout.scoutmobile.R;
import scout.scoutmobile.models.User;
import scout.scoutmobile.utils.Logger;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final String USER_SESSION_STATE_FILE_NAME = "emrebmemer";
    private static final String INTENT_EXTRA_LOGGED_OUT = "logout";
    private static final String EMAIL_VALIDATING_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    private static final int USER_SAVED_DATE_DATA_INDEX = 0;
    private static final int USER_EMAIL_DATA_INDEX = 1;
    private static final int USER_PASSWORD_DATA_INDEX = 2;

    private static final int MIN_USER_PASSWORD_LENGTH = 6;

    private Logger mLogger;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private CheckBox mRememberCheckbox;
    private ProgressDialog loadingDialog;

    private boolean mUserSessionRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new ProgressDialog(LoginActivity.this);

        File file = new File(USER_SESSION_STATE_FILE_NAME);
        mUserSessionRemembered = file.exists();

        Bundle extraIntentData = getIntent().getExtras();
        if (extraIntentData != null &&
            !extraIntentData.getString(INTENT_EXTRA_LOGGED_OUT).isEmpty()) {
            setKeepUserSessionAlive(false);
        }

        mLogger = new Logger("LoginActivity");

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class); //todo call register activity
                startActivity(registerIntent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mRememberCheckbox = (CheckBox) findViewById(R.id.login_remember_checkbox);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUserSessionRemembered) {
            attemptLogin();
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (mUserSessionRemembered) {
            //TODO should the user name and password be stored in file to relogin.
            Vector<String> userData = getUserSessionLoginInfo();
            email = userData.get(USER_EMAIL_DATA_INDEX);
            password = userData.get(USER_PASSWORD_DATA_INDEX);
        } else {
            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    showProgress(false);
                    if (e != null) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else {
                        setKeepUserSessionAlive(mRememberCheckbox.isChecked());
                        setCurrentUser(parseUser);
                        Intent mainActivity = new Intent(LoginActivity.this, PlacesActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        finish();
                    }
                }

            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches(EMAIL_VALIDATING_REGEX);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= MIN_USER_PASSWORD_LENGTH ;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        if (show) {
            loadingDialog.setMessage(getString(R.string.spinner_login));
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void setKeepUserSessionAlive(boolean sessionAlive) {
        if (sessionAlive) {
            try {
                Calendar cal = Calendar.getInstance();
                FileOutputStream fileOut = openFileOutput(USER_SESSION_STATE_FILE_NAME,
                        MODE_PRIVATE);
                OutputStreamWriter outStreamWriter = new OutputStreamWriter(fileOut);

                //TODO does write write new line?
                outStreamWriter.write(cal.getTime().toString());
                outStreamWriter.write(User.getInstance().getEmail());
                outStreamWriter.write(User.getInstance().getPassWord());
                outStreamWriter.flush();
                outStreamWriter.close();
                mUserSessionRemembered = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mUserSessionRemembered) {
                File file = new File(USER_SESSION_STATE_FILE_NAME);
                file.delete();
                mUserSessionRemembered = false;
                User.getInstance().resetUser();
                mLogger.log("Session file deleted.");
            }
        }
    }

    private Vector<String> getUserSessionLoginInfo() {
        Vector<String> stringVec = new Vector<>();

        try {
            FileInputStream fileInput = openFileInput(USER_SESSION_STATE_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInput);
            BufferedReader buffReader = new BufferedReader(inputStreamReader);

            String lineRead = null;

            while ((lineRead = buffReader.readLine()) != null) {
                stringVec.add(lineRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringVec;
    }

    private void setCurrentUser(ParseUser userInfo) {
        User curUser = User.getInstance();
        curUser.setEmail(userInfo.getString("email"));
        curUser.setPassWord(userInfo.getString("password"));
        curUser.setFirstName(userInfo.getString("firstname"));
        curUser.setLastName(userInfo.getString("lastname"));
        curUser.setPoints(userInfo.getInt("points"));
    }
}



