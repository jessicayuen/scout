package scout.scoutmobile;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
<<<<<<< HEAD
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
=======
import android.content.Intent;
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9

import com.estimote.sdk.BeaconManager;
import com.parse.Parse;


public class ScoutAndroidApplication extends Application {

    private static final String APP_ID = "DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3";
    private static final String CLIENT_KEY = "V7CmhQ7lDeGZQ4AWaZFnyOklBONwc2EaAVZAgyA6";

<<<<<<< HEAD
    private static Context scoutApp;
=======
    private static ScoutAndroidApplication scoutApp;
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 123;
    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }

<<<<<<< HEAD
    public Context getContext(){
=======
    public ScoutAndroidApplication getApplicationContext(){
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
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
<<<<<<< HEAD
        scoutApp = getApplicationContext();
=======
        scoutApp = this;
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
        Parse.initialize(scoutApp, APP_ID, CLIENT_KEY);
        beaconManager = null;
    }

    public void postNotification(String msg) {
<<<<<<< HEAD
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
/*        Intent notifyIntent = new Intent(this.getContext(), ScoutAndroidApplication.class);
=======
        Intent notifyIntent = new Intent(ScoutAndroidApplication.this, ScoutAndroidApplication.class);
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                ScoutAndroidApplication.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
<<<<<<< HEAD
        Notification notification = new Notification.Builder(this.getContext())
=======
        Notification notification = new Notification.Builder(ScoutAndroidApplication.this)
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentTitle("Notify Demo")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
<<<<<<< HEAD
        notificationManager.notify(NOTIFICATION_ID, notification);*/
=======
        notificationManager.notify(NOTIFICATION_ID, notification);
>>>>>>> 01d7dc27e6673c962b2ec7a4aaec8966c2af4dd9
    }
}
