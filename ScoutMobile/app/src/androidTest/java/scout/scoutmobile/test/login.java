package scout.scoutmobile.test;

import scout.scoutmobile.activities.SplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class login extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public login() {
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
        //Wait for activity: 'scout.scoutmobile.activities.LoginActivity'
		assertTrue("scout.scoutmobile.activities.LoginActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.LoginActivity.class));
        //Set default small timeout to 120284 milliseconds
		Timeout.setSmallTimeout(120284);
        //Enter the text: 'test@test.com'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email), "test@test.com");
        //Enter the text: 'qqqqqq'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.password));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.password), "qqqqqq");
        //Click on Sign in
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.email_sign_in_button));
        //Wait for activity: 'scout.scoutmobile.activities.PlacesActivity'
		assertTrue("scout.scoutmobile.activities.PlacesActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.PlacesActivity.class));
	}
}
