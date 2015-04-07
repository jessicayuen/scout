package scout.scoutmobile.test;

import scout.scoutmobile.activities.SplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class logout extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public logout() {
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
        //Take screenshot
        solo.takeScreenshot();
        //Wait for activity: 'scout.scoutmobile.activities.SplashScreenActivity'
		solo.waitForActivity(scout.scoutmobile.activities.SplashScreenActivity.class, 2000);
        //Wait for activity: 'scout.scoutmobile.activities.LoginActivity'
		assertTrue("scout.scoutmobile.activities.LoginActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.LoginActivity.class));
        //Enter the text: 'test2'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email), "test2");
        //Wait for dialog
		solo.waitForDialogToOpen(5000);
        //Enter the text: 'test@test.com'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.email), "test@test.com");
        //Click on Empty Text View
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.password));
        //Enter the text: 'qqqqqq'
		solo.clearEditText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.password));
		solo.enterText((android.widget.EditText) solo.getView(scout.scoutmobile.R.id.password), "qqqqqq");
        //Click on Sign in
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.email_sign_in_button));
        //Wait for activity: 'scout.scoutmobile.activities.PlacesActivity'
		assertTrue("scout.scoutmobile.activities.PlacesActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.PlacesActivity.class));
        //Set default small timeout to 55040 milliseconds
		Timeout.setSmallTimeout(55040);
        //Click on One Size Fits All 0 points
		solo.clickInList(1, 0);
        //Wait for activity: 'scout.scoutmobile.activities.RewardsActivity'
		assertTrue("scout.scoutmobile.activities.RewardsActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.RewardsActivity.class));
        //Click on ImageView
		solo.clickOnView(solo.getView(android.widget.ImageView.class, 1));
        //Click on Log Out
		solo.clickInList(1, 0);
        //Wait for activity: 'scout.scoutmobile.activities.LoginActivity'
		assertTrue("scout.scoutmobile.activities.LoginActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.LoginActivity.class));
	}
}
