package scout.scoutmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.services.BluetoothBeaconService;
import scout.scoutmobile.utils.Logger;


public class RegisterActivity extends CredentialActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mRetypedPasswordEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mLogger = new Logger("RegisterActivity");

        mLoadingDialog = new ProgressDialog(RegisterActivity.this);
        mLoadingDialog.setMessage(getString(R.string.spinner_register));

        //setup submit button
        Button submitBtn = (Button) findViewById(R.id.register_accept_btn);
        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkingAvailable()) {
                    createAccount();
                } else {
                    toastNoNetwork();
                }
            }
        });

        //setup cancel button
        Button cancelBtn = (Button) findViewById(R.id.register_cancel_btn);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEmailEditText = (EditText) findViewById(R.id.register_email_edittext);
        mPasswordEditText = (EditText) findViewById(R.id.register_password_edittext);
        mRetypedPasswordEditText = (EditText) findViewById(R.id.register_password_again_edittext);
        mFirstNameEditText = (EditText) findViewById(R.id.register_name_first_edittext);
        mLastNameEditText = (EditText) findViewById(R.id.register_name_last_edittext);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    /**
     * Attempt to create an account in parse database. Before the server query, valid string checks
     * are made on email, password and verify password field.
     */
    private void createAccount() {

        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);
        mRetypedPasswordEditText.setError(null);

        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString();
        String verifyPass = mRetypedPasswordEditText.getText().toString();

        View focusView = null;
        boolean hasError = false;

        if (!isEmailValid(email)) {
            focusView = mEmailEditText;
            hasError = true;
            mEmailEditText.setError(getString(R.string.invalid_email));
        } else if (!isPasswordValid(password)) {
            focusView = mPasswordEditText;
            hasError = true;
            mPasswordEditText.setError(getString(R.string.invalid_password));
        } else if (verifyPass.isEmpty()) {
            focusView = mRetypedPasswordEditText;
            hasError = true;
            mRetypedPasswordEditText.setError(getString(R.string.missing_field));
        } else if (verifyPass.compareTo(password) != 0) {
            focusView = mRetypedPasswordEditText;
            hasError = true;
            mRetypedPasswordEditText.setError(getString(R.string.password_not_match));
        }

        if (hasError) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //populate the user to register
            final ParseUser parseUser = new ParseUser();
            parseUser.setEmail(email);
            parseUser.setPassword(password);
            parseUser.setUsername(email);
            parseUser.put(Consts.FIRST_NAME, mFirstNameEditText.getText().toString());
            parseUser.put(Consts.LAST_NAME, mLastNameEditText.getText().toString());

            mLogger.log("Registering user...");
            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        showProgress(false);
                        int code = e.getCode();
                        if (code == ParseException.EMAIL_TAKEN) {
                            mEmailEditText.setError(getString(R.string.error_email_exists));
                            mEmailEditText.requestFocus();
                        } else if (code == ParseException.INVALID_EMAIL_ADDRESS) {
                            mEmailEditText.setError(getString(R.string.error_invalid_email));
                            mEmailEditText.requestFocus();
                        } else {
                            mLogger.log("ERROR CODE:" + code);
                            showToast(getErrorString(code));
                        }
                    } else {
                        //create the relation that this user is a customer in parse
                        ParseObject customer = new ParseObject(Consts.TABLE_CUSTOMER);
                        customer.put(Consts.COL_CUSTOMER_USER, parseUser);
                        customer.saveInBackground();

                        parseUser.put(Consts.COL_USER_LOGGEDIN, Consts.USER_LOGGED);
                        parseUser.saveInBackground();

                        showProgress(false);

                        mLogger.log("Successfully registered user");
                        setCurrentUser(parseUser, customer);
                        startService(new Intent(RegisterActivity.this, BluetoothBeaconService.class));
                        startMainActivity(RegisterActivity.this, PlacesActivity.class);
                    }
                }
            });
        }
    }
}
