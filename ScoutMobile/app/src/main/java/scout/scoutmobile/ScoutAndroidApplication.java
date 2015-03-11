package scout.scoutmobile;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.parse.Parse;

import scout.scoutmobile.controllers.PointsManager;
import scout.scoutmobile.services.BluetoothBeaconService;


public class ScoutAndroidApplication extends Application {

    private static final String APP_ID = "DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3";
    private static final String CLIENT_KEY = "V7CmhQ7lDeGZQ4AWaZFnyOklBONwc2EaAVZAgyA6";

    private static Context scoutApp;
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 123;
    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public Context getContext(){
        return scoutApp;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public void setBeaconManager(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scoutApp = getApplicationContext();
        Parse.initialize(scoutApp, APP_ID, CLIENT_KEY);
        beaconManager = null;

        BluetoothBeaconService.addBeaconPingObserver(PointsManager.getInstance());
    }

    public void postNotification(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
/*        Intent notifyIntent = new Intent(this.getContext(), ScoutAndroidApplication.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                ScoutAndroidApplication.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this.getContext())
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentTitle("Notify Demo")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);*/
    }
}
