package scout.scoutmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.Place;
import scout.scoutmobile.utils.Logger;


/**
 * Business Directory for listing the registered businesses within Scout App.
 */
public class PlacesActivity extends ActionBarActivity {

    Logger mLogger = new Logger("PlacesActivity");

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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(
                            R.layout.place_list_item,
                            null
                        );

            TextView titleView = (TextView) view.findViewById(R.id.placeTitle);
            TextView pointsView = (TextView) view.findViewById(R.id.placePoints);
            ImageView imageView = (ImageView) view.findViewById(R.id.placeImage);

            Place place = mPlaces.get(position);

            // Set the Place list item values
            titleView.setText(place.getTitle());
            pointsView.setText(place.getPoints());
            //image.setImageResource(mPlaces.get(position).getImageURL());

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        String customerId = getIntent().getStringExtra(Consts.CUSTOMER_ID);
        if (!customerId.isEmpty() && customerId != null) {
            //do your things here
        }

        // Populate the list of places
        this.getAllPlaces(new ArrayList<Place>());
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates a list of a businesses. The reason we can't just return a list is because
     * the list may be returned before the callback is finished.
     * @throws RuntimeException If an exception was thrown during the query
     */
    private void getAllPlaces(final List<Place> places) {
        final ProgressDialog progress = ProgressDialog.show(this,
                Consts.PROGRESS_WAIT,Consts.PROGRESS_BUSINESS_ALL_QUERY);

        // Query for all businesses
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_PLACE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // Get the points that the logged in user has for each business
                    queryPoints(objects, new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            List<ParseObject> obj = parseObjects;

                            if (e == null) {
                                for (ParseObject businessPoints : parseObjects) {
                                    // We're expecting the entries to be unique
                                    Integer points = businessPoints.getInt(Consts.COL_POINTS_POINTS);
                                    ParseObject business = businessPoints.getParseObject(Consts.COL_POINTS_BUSINESS);
                                    String title = business.getString(Consts.COL_PLACE_NAME);
                                    String thumbnailUrl = business.getString(Consts.COL_PLACE_THUMBNAIL_URL);
                                    String id = business.getObjectId();

                                    places.add(new Place(title, thumbnailUrl, points, id));
                                }
                                // Finally, we can update the list view with this info
                                updateListView(places);
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
    private void queryPoints(List<ParseObject> businesses,
                             FindCallback<ParseObject> callback) {
        List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        for (ParseObject business : businesses) {
            // Let's create these queries with parse - would be easier with a join
            // but I don't know a way in parse yet.
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_POINTS).
                    whereEqualTo(Consts.COL_POINTS_CUSTOMER, ParseUser.getCurrentUser())
                    .whereEqualTo(Consts.COL_POINTS_BUSINESS, business);
            queries.add(query);
        }

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.findInBackground(callback);
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
                startActivity(rewardsActivity);
            }
        });
    }
}
