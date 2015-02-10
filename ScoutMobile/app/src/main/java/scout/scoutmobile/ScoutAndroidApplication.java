package scout.scoutmobile;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.estimote.sdk.BeaconManager;
import com.parse.Parse;


public class ScoutAndroidApplication extends Application {

    private static final String APP_ID = "DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3";
    private static final String CLIENT_KEY = "V7CmhQ7lDeGZQ4AWaZFnyOklBONwc2EaAVZAgyA6";

    private static ScoutAndroidApplication scoutApp;
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 123;
    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public ScoutAndroidApplication getApplicationContext(){
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
        scoutApp = this;
        Parse.initialize(scoutApp, APP_ID, CLIENT_KEY);
        beaconManager = null;
    }

    public void postNotification(String msg) {
        Intent notifyIntent = new Intent(ScoutAndroidApplication.this, ScoutAndroidApplication.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                ScoutAndroidApplication.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(ScoutAndroidApplication.this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentTitle("Notify Demo")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
