package scout.scoutmobile.activities;

        import android.app.Activity;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.bluetooth.BluetoothAdapter;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.RemoteException;
        import android.util.Log;
        import scout.scoutmobile.R;
        import android.widget.Toast;

        import com.estimote.sdk.Beacon;
        import com.estimote.sdk.BeaconManager;
        import com.estimote.sdk.Region;
        import com.estimote.sdk.utils.L;

        import java.util.List;
        import java.util.concurrent.TimeUnit;

        import scout.scoutmobile.utils.Logger;

public class BeaconServiceActivity extends Activity {

    private static final String TAG = BeaconServiceActivity.class.getSimpleName();
    private static final int NOTIFICATION_ID = 123;
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private Region region;
    protected Logger mLogger;
    String notification;
    //private LeDeviceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger = new Logger("BeaconServiceActivity");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Configure verbose debug logging.
        L.enableDebugLogging(true);
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
        // In order for this demo to be more responsive and immediate we lower down those values.
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> foundBeacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(foundBeacons.size() > 0) {
                            notification = "Entered beacon:";
                            for (Beacon beacon : foundBeacons) {
                                notification = notification + " " + beacon.getMacAddress();
                                //TODO: send data here
                            }
                            postNotification(notification);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();

        notificationManager.cancel(NOTIFICATION_ID);
        beaconManager.disconnect();
        super.onDestroy();
    }

    private void connectToService() {
        getActionBar().setSubtitle("Scanning...");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                    startMainActivity(BeaconServiceActivity.this, PlacesActivity.class);
                } catch (RemoteException e) {
                    Toast.makeText(BeaconServiceActivity.this, "Cannot start ranging, an error occurred",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                    Intent loginActivity = new Intent(BeaconServiceActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                }
            }
        });
    }

    protected void startMainActivity(Context context, Class<?> mainClass) {
        mLogger.log("Starting main activity");
        Intent mainActivity = new Intent(context, mainClass);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
        finish();
    }

    private void postNotification(String msg) {
        Intent notifyIntent = new Intent(BeaconServiceActivity.this, BeaconServiceActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                BeaconServiceActivity.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(BeaconServiceActivity.this)
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
