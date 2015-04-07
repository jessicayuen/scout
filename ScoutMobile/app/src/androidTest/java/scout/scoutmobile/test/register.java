package scout.scoutmobile.test;

import scout.scoutmobile.activities.SplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class register extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public register() {
		super(SplashScreenActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}
  
	public void testRun() {
        //Wait for activity: 'scout.scoutmobile.activities.SplashScreenActivity'
		solo.waitForActivity(scout.scoutmobile.activities.SplashScreenActivity.class, 2000);
        //Wait for activity: 'scout.scoutmobile.activities.PlacesActivity'
		assertTrue("scout.scoutmobile.activities.PlacesActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.PlacesActivity.class));
        //Set default small timeout to 19248 milliseconds
		Timeout.setSmallTimeout(19248);
        //Click on Register
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.email_register_button));
        //Wait for activity: 'scout.scoutmobile.activities.RegisterActivity'
		assertTrue("scout.scoutmobile.activities.RegisterActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.RegisterActivity.class));
        //Enter the text: 'joe'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_name_first_edittext));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_name_first_edittext), "joe");
        //Enter the text: 'shmoe'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_name_last_edittext));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_name_last_edittext), "shmoe");
        //Enter the text: 'joe@shmoe.com'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_email_edittext));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_email_edittext), "joe@shmoe.com");
        //Enter the text: 'qqqqqq'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_password_edittext));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_password_edittext), "qqqqqq");
        //Enter the text: 'qqqqqq'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_password_again_edittext));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.register_password_again_edittext), "qqqqqq");
        //Click on Register
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.register_accept_btn));
        //Wait for activity: 'scout.scoutmobile.activities.PlacesActivity'
		assertTrue("scout.scoutmobile.activities.PlacesActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.PlacesActivity.class));
	}
}
