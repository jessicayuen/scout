package scout.scoutmobile.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.robotium.solo.Timeout;

import scout.scoutmobile.activities.SplashScreenActivity;


public class RedeemReward extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public RedeemReward() {
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
        //Set default small timeout to 25447 milliseconds
		Timeout.setSmallTimeout(25447);
        //Scroll to Ellantos 119 points
		android.widget.ListView listView0 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
		solo.scrollListToLine(listView0, 1);
        //Click on Ellantos 119 points
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.placeImage, 2));
        //Wait for activity: 'scout.scoutmobile.activities.RewardsActivity'
		assertTrue("scout.scoutmobile.activities.RewardsActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.RewardsActivity.class));
        //Click on 30 Appetizer
		solo.clickInList(3, 0);
        //Wait for activity: 'scout.scoutmobile.activities.RedeemActivity'
		assertTrue("scout.scoutmobile.activities.RedeemActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.RedeemActivity.class));
        //Press menu back key
		solo.goBack();
	}
}
