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
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.model.Reward;
import scout.scoutmobile.utils.GeneralUtils;
import scout.scoutmobile.utils.Logger;


public class RewardsActivity extends ActionBarActivity {

    Logger mLogger = new Logger("RewardsActivity");

    /**
     * Used for setting custom list view reward items.
     */
    private class RewardsAdapter extends BaseAdapter {

        final List<Reward> mRewards;

        public RewardsAdapter(final List<Reward> rewards) {
            super();
            this.mRewards = rewards;
        }

        @Override
        public int getCount() { return mRewards.size(); }
        @Override
        public Object getItem(int i) { return null; }
        @Override
        public long getItemId(int position) { return 0; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(
                    R.layout.rewards_list_item,
                    null
            );

            TextView descView = (TextView) view.findViewById(R.id.rewardsDescription);
            TextView pointsView = (TextView) view.findViewById(R.id.rewardsPoint);

            Reward reward = mRewards.get(position);

            // Set the Reward list item values
            descView.setText(reward.getDescription());
            pointsView.setText(reward.getPoints().toString());

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        String placeId = getIntent().getStringExtra(Consts.PLACE_ID);
        String placeName = getIntent().getStringExtra(Consts.PLACE_NAME);
        Integer placePoints = getIntent().getIntExtra(Consts.PLACE_POINTS, 0);

        TextView pointsView = (TextView) findViewById(R.id.points);
        TextView placeView = (TextView) findViewById(R.id.business);

        pointsView.setText(placePoints.toString());
        placeView.setText(placeName);

        // Set the rewards list items
        this.setRewardsListItems(placeId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GeneralUtils.verifyUserLoggedIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rewards, menu);
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
     * Set the rewards list items by querying the rewards in parse.
     * @param placeId
     */
    private void setRewardsListItems(final String placeId) {
        final ProgressDialog progress = ProgressDialog.show(this,
                Consts.PROGRESS_WAIT, Consts.PROGRESS_REWARDS_ALL_QUERY);

        // Query for the business parse object equivalent to the placeId
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.TABLE_PLACE);
        query.getInBackground(placeId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject business, ParseException e) {
                if (e == null) {
                    // Query for all rewards with this business as a foreign key
                    ParseQuery<ParseObject> rewardsQuery =
                            ParseQuery.getQuery(Consts.TABLE_REWARDS)
                            .whereEqualTo(Consts.COL_REWARDS_BUSINESS, business);

                    rewardsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> rewardObjects, ParseException e) {
                            if (e == null) {
                                List<Reward> rewards = new ArrayList<>();
                                for (ParseObject r : rewardObjects) {
                                    Integer points = r.getInt(Consts.COL_REWARDS_POINTS);
                                    String desc = r.getString(Consts.COL_REWARDS_DESC);

                                    rewards.add(new Reward(points, desc, r.getObjectId()));
                                }
                                // Sort the rewards by points
                                Collections.sort(rewards, new Comparator<Reward>() {
                                    @Override
                                    public int compare(Reward lhs, Reward rhs) {
                                        return lhs.getPoints() - rhs.getPoints();
                                    }
                                });
                                // Finally, update the list view
                                updateListView(rewards);
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
     * Updates the list view to show all rewards.
     * @param rewards
     */
    private void updateListView(final List<Reward> rewards) {
        ListView rewardsList = (ListView) findViewById(R.id.rewardsList);
        RewardsAdapter rewardsAdapter = new RewardsAdapter(rewards);

        rewardsList.setAdapter(rewardsAdapter);

        rewardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Reward selected = rewards.get(position);
                // Open up QR code view
                Intent redeemActivity = new Intent(
                        RewardsActivity.this, RedeemActivity.class);
                redeemActivity.putExtra(Consts.REWARD_ID, selected.getID());
                redeemActivity.putExtra(Consts.CUSTOMER_ID,
                        ParseUser.getCurrentUser().getObjectId());
                startActivity(redeemActivity);
            }
        });
    }
}
