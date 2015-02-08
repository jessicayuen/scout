package scout.scoutmobile.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import scout.scoutmobile.R;

/**
 * Business Directory for listing the registered businesses within Scout App.
 */
public class PlacesActivity extends ActionBarActivity {

    List<Place> mPlaces;

    /**
     * Represents a Business location, in the user's perspective. The
     * relevant information to the user is the Business name, the number
     * of points accumulated at that location, and an image of the Business.
     */
    private class Place {
        private String mImageURL;
        private String mTitle;
        private String mPoints;

        public Place(String imageURL, String title, String points) {
            this.mImageURL = imageURL;
            this.mTitle = title;
            this.mPoints = points;
        }

        public String getImageURL() { return mImageURL; }
        public String getTitle() { return mTitle; }
        public String getPoints() { return mPoints; }
    }

    private class PlaceAdapter extends BaseAdapter {
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

            TextView title = (TextView) view.findViewById(R.id.placeTitle);
            TextView points = (TextView) view.findViewById(R.id.placePoints);
            ImageView image = (ImageView) view.findViewById(R.id.placeImage);

            // Set the Place list item values
            //image.setImageResource(mPlaces.get(position).getImageURL());

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        ListView placesList = (ListView) findViewById(R.id.placesList);
        PlaceAdapter placeAdapter = new PlaceAdapter();

        placesList.setAdapter(placeAdapter);

        placesList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.placeTitle);
                // Transition to rewards activity for this business.
            }
        });
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
}
