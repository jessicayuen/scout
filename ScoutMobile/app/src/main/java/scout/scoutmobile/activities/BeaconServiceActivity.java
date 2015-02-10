package scout.scoutmobile.activities;

        import android.app.Activity;
        import android.app.NotificationManager;
        import android.bluetooth.BluetoothAdapter;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.RemoteException;
        import android.util.Log;
        import android.widget.Toast;

        import com.estimote.sdk.Beacon;
        import com.estimote.sdk.BeaconManager;
        import com.estimote.sdk.Region;
        import com.estimote.sdk.utils.L;

        import java.util.List;
        import java.util.concurrent.TimeUnit;

        import scout.scoutmobile.ScoutAndroidApplication;
        import scout.scoutmobile.utils.Logger;

public class BeaconServiceActivity extends Activity {

    private static final String TAG = BeaconServiceActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    ScoutAndroidApplication scoutApp;
    private NotificationManager notificationManager;
    private BeaconManager beaconManager;
    private Region region;
    protected Logger mLogger;
    private String notification;
    //private LeDeviceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger = new Logger("BeaconServiceActivity");

        scoutApp = (ScoutAndroidApplication) getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Configure verbose debug logging.
        L.enableDebugLogging(true);

        // Configure BeaconManager.
        beaconManager = scoutApp.getBeaconManager();
        if(beaconManager == null) {
            beaconManager = new BeaconManager(scoutApp.getApplicationContext());
            scoutApp.setBeaconManager(beaconManager);
        }
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
                                //TODO: send data to server here
                            }
                            scoutApp.postNotification(notification);
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
        }
        else {
            // If Bluetooth is not enabled, let user enable it.
            if (!beaconManager.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                connectToService();
            }
        }
        startMainActivity(BeaconServiceActivity.this, PlacesActivity.class);
    }

/*    protected void onDestroy() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        notificationManager.cancel(NOTIFICATION_ID);
        beaconManager.disconnect();
        super.onDestroy();
    }*/

    private void connectToService() {
        getActionBar().setSubtitle("Scanning...");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(BeaconServiceActivity.this, "Cannot start ranging, an error occurred",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
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
}
