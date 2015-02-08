package scout.scoutmobile.activities;

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

import java.util.List;

import scout.scoutmobile.R;
import scout.scoutmobile.model.Reward;


public class RewardsActivity extends ActionBarActivity {

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
                    R.layout.place_list_item,
                    null
            );

            TextView descView = (TextView) view.findViewById(R.id.rewardsDescription);
            TextView pointsView = (TextView) view.findViewById(R.id.rewardsPoint);

            Reward reward = mRewards.get(position);

            // Set the Reward list item values
            descView.setText(reward.getDescription());
            pointsView.setText(reward.getPoints());

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);


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
                // Open up QR code view - future TODO
            }
        });
    }
}
