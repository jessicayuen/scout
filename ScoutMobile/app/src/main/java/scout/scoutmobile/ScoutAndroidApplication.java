package scout.scoutmobile;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

import scout.scoutmobile.controllers.IntervalManager;
import scout.scoutmobile.controllers.PointsManager;
import scout.scoutmobile.services.BluetoothBeaconService;


public class ScoutAndroidApplication extends Application {

    private static final String APP_ID = "DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3";
    private static final String CLIENT_KEY = "V7CmhQ7lDeGZQ4AWaZFnyOklBONwc2EaAVZAgyA6";

    private static Context scoutApp;

    public Context getContext(){
        return scoutApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scoutApp = getApplicationContext();
        Parse.initialize(scoutApp, APP_ID, CLIENT_KEY);

        BluetoothBeaconService.addBeaconPingObserver(PointsManager.getInstance());
        BluetoothBeaconService.addBeaconPingObserver(IntervalManager.getInstance());
    }
}
