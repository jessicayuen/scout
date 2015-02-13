package scout.scoutmobile.dao;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import scout.scoutmobile.constants.Consts;

public class CustomerDao {

    public void getCustomerByParseUser(ParseUser parseUser, FindCallback<ParseObject> callback) {

        ParseQuery<ParseObject> customerQuery = ParseQuery.getQuery(Consts.TABLE_CUSTOMER)
                .whereEqualTo(Consts.COL_CUSTOMER_USER, parseUser);

        customerQuery.findInBackground(callback);
    }
}
