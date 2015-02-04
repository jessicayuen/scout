package scout.scoutmobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import scout.scoutmobile.PlacesActivity;
import scout.scoutmobile.R;


public class RegisterActivity extends Activity {

    private static final String EMAIL_VALIDATING_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    private static final int MIN_USER_PASSWORD_LENGTH = 6;

    private EditText mEmail;
    private EditText mPassword;
    private EditText mRetypedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button submitBtn = (Button) findViewById(R.id.register_accept_btn);
        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button cancelBtn = (Button) findViewById(R.id.register_cancel_btn);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEmail = (EditText) findViewById(R.id.register_email_edittext);
        mPassword = (EditText) findViewById(R.id.register_password_edittext);
        mRetypedPassword = (EditText) findViewById(R.id.register_password_again_edittext);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEmailValid(String email) {
        return email.matches(EMAIL_VALIDATING_REGEX);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= MIN_USER_PASSWORD_LENGTH; //TODO change this
    }

    private boolean createAccount() {

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();
        String verifyPass = mRetypedPassword.getText().toString();

        View focusView = null;
        boolean hasError = false;

        if (email.isEmpty()) {
            focusView = mEmail;
            hasError = true;
            mEmail.setError(getString(R.string.invalid_email));
        } else if (password.isEmpty() || isPasswordValid(password)) {
            focusView = mPassword;
            hasError = true;
            mPassword.setError(getString(R.string.invalid_password));
        } else if (verifyPass.isEmpty()) {
            focusView = mRetypedPassword;
            hasError = true;
            mRetypedPassword.setError(getString(R.string.missing_field));
        } else if (verifyPass.compareTo(password) != 0) {
            focusView = mRetypedPassword;
            hasError = true;
            mRetypedPassword.setError(getString(R.string.password_not_match));
        }

        boolean creation = true;
        if (creation == true) {
            Intent mainActivity = new Intent(this, PlacesActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            finish();
        }

        return false;
    }

    public void showCreationFailed() {

    }

    private class UserRegisterTask extends AsyncTask<Void, Void, Void> {

        public UserRegisterTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
