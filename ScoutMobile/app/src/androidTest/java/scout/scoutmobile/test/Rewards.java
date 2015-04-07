package scout.scoutmobile.test;

import scout.scoutmobile.activities.SplashScreenActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class Rewards extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
  	private Solo solo;
  	
  	public Rewards() {
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
        //Scroll to Ellantos 119 points
		android.widget.ListView listView0 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
		solo.scrollListToLine(listView0, 1);
        //Click on Ellantos 119 points
		solo.clickOnView(solo.getView(scout.scoutmobile.R.id.placeImage, 2));
        //Wait for activity: 'scout.scoutmobile.activities.RewardsActivity'
		assertTrue("scout.scoutmobile.activities.RewardsActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.RewardsActivity.class));
        //Set default small timeout to 11424 milliseconds
		Timeout.setSmallTimeout(11424);
        //Scroll to ImageView
		android.widget.ListView listView1 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
		solo.scrollListToLine(listView1, 0);
        //Click on ImageView
		solo.clickOnView(solo.getView(android.widget.ImageView.class, 1));
        //Click on Log Out
		solo.clickInList(1, 0);
        //Wait for activity: 'scout.scoutmobile.activities.LoginActivity'
		assertTrue("scout.scoutmobile.activities.LoginActivity is not found!", solo.waitForActivity(scout.scoutmobile.activities.LoginActivity.class));
	}
}
