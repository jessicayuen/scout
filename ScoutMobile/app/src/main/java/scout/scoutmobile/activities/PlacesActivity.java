package scout.scoutmobile.activities;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scout.scoutmobile.R;
import scout.scoutmobile.ScoutAndroidApplication;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.Place;
import scout.scoutmobile.utils.GeneralUtils;
import scout.scoutmobile.utils.Logger;


/**
 * Business Directory for listing the registered businesses within Scout App.
 */
public class PlacesActivity extends ActionBarActivity {

    private static final String TAG = PlacesActivity.class.getSimpleName();
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    Logger mLogger = new Logger("PlacesActivity");
    ScoutAndroidApplication scoutApp;
    private NotificationManager notificationManager;
    private BeaconManager beaconManager;

    /**
     * Used for setting custom list view items.
     */
    private class PlaceAdapter extends BaseAdapter {

        final List<Place> mPlaces;

        public PlaceAdapter(final List<Place> places) {
            super();
            this.mPlaces = places;
        }

        @Override
        public int getCount() { return mPlaces.size(); }
        @Override
        public Object getItem(int i) { return null; }
        @Override
        public long getItemId(int position) { return 0; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(
                    R.layout.place_list_item,
                    null
            );

            TextView titleView = (TextView) view.findViewById(R.id.placeTitle);
            TextView pointsView = (TextView) view.findViewById(R.id.placePoints);

            Place place = mPlaces.get(position);

            final ViewHolder listItem = new ViewHolder();
            listItem.position = position;
            listItem.thumbnail = (ImageView) view.findViewById(R.id.placeImage);

            ParseFile imageFile = place.getImageFile();
            if (imageFile != null) {
                imageFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            if (listItem.position == position) {
                                Place place = mPlaces.get(listItem.position);
                                listItem.thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            }
                        }
                    }
                });
            }

            // Set the Place list item values
            titleView.setText(place.getTitle());
            pointsView.setText(place.getPoints().toString() + " points");

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        this.getAllPlaces();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GeneralUtils.verifyUserLoggedIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_log_out:
                GeneralUtils.logUserOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Retrieves the list of all places.
     * @throws RuntimeException If an exception was thrown during the query
     */
    private void getAllPlaces() {
        final ProgressDialog progress = ProgressDialog.show(this,
                Consts.PROGRESS_WAIT,Consts.PROGRESS_BUSINESS_ALL_QUERY);

        // Query for all businesses
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_PLACE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    final Map<String, Place> placesMap = new HashMap<>();
                    // Get the points that the logged in user has for each business
                    queryPoints(objects, placesMap, new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                for (ParseObject businessPoints : parseObjects) {
                                    String businessId = businessPoints.
                                            getParseObject(Consts.COL_POINTS_BUSINESS).
                                            getObjectId();

                                    // Look in the map for the business id and update the points
                                    if (placesMap.containsKey(businessId)) {
                                        // We're expecting the entries to be unique
                                        Integer points = businessPoints.
                                                getInt(Consts.COL_POINTS_POINTS);

                                        Place place = placesMap.get(businessId);
                                        place.setPoints(points);
                                    }
                                }
                                // Finally, we can update the list view with this info
                                updateListView(new ArrayList<>(placesMap.values()));
                                progress.dismiss();
                            } else {
                                mLogger.logError(e);
                            }
                        }
                    });
                } else {
                    mLogger.logError(e);
                }
            }
        });
    }

    /**
     * @param businesses The list of business ParseObjects
     * @param callback The callback object to handle the results
     * @throws ParseException If an exception was thrown during the query
     */
    private void queryPoints(final List<ParseObject> businesses,
                             final Map<String, Place> placeMap,
                             final FindCallback<ParseObject> callback) {
        final List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        // Get the current customer.
        ParseQuery<ParseObject> customerQuery = ParseQuery.getQuery(Consts.TABLE_CUSTOMER)
                .whereEqualTo(Consts.COL_CUSTOMER_USER, ParseUser.getCurrentUser());
        customerQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                // We're only expecting one match
                if ((!parseObjects.isEmpty()) && (e == null)) {
                    ParseObject customer = parseObjects.get(0);

                    for (ParseObject business : businesses) {
                        // While we're here, lets also add in the places for optimization
                        placeMap.put(business.getObjectId(), new Place(
                                        business.getString(Consts.COL_PLACE_NAME),
                                        business.getParseFile(Consts.COL_PLACE_THUMBNAIL_URL),
                                        0,
                                        business.getObjectId())
                        );

                        // Let's create these queries with parse - would be easier with a join
                        // but I don't know a way in parse yet.
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_POINTS).
                                whereEqualTo(Consts.COL_POINTS_CUSTOMER, customer)
                                .whereEqualTo(Consts.COL_POINTS_BUSINESS, business);
                        queries.add(query);
                    }

                    ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

                    mainQuery.findInBackground(callback);

                } else {
                    mLogger.logError(e != null ? e :
                            new RuntimeException("Customer does not exists."));
                }
            }
        });
    }

    /**
     * Updates the list view to show all businesses / places.
     * @param places
     */
    private void updateListView(final List<Place> places) {
        ListView placesList = (ListView) findViewById(R.id.placesList);
        PlaceAdapter placeAdapter = new PlaceAdapter(places);

        placesList.setAdapter(placeAdapter);

        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Transition to rewards activity for this business.
                Intent rewardsActivity = new Intent(
                        PlacesActivity.this, RewardsActivity.class);
                Place selected = places.get(position);
                rewardsActivity.putExtra(Consts.PLACE_ID, selected.getId());
                rewardsActivity.putExtra(Consts.PLACE_NAME, selected.getTitle());
                rewardsActivity.putExtra(Consts.PLACE_POINTS, selected.getPoints());
                try {
                    rewardsActivity.putExtra(Consts.BUSINESS_IMAGE, selected.getImageFile().getUrl());
                } catch (NullPointerException e) {
                    rewardsActivity.putExtra(Consts.BUSINESS_IMAGE, "");
                }
                startActivity(rewardsActivity);
            }
        });
    }

    /**
     * Class for keeping track of listitems in the list view when loading images asychronously
     */
    private class ViewHolder {
        public ImageView thumbnail;
        public int position;
    }
}
