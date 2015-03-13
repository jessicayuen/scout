package scout.scoutmobile.model;


import com.parse.ParseObject;
import com.parse.ParseUser;

public class CustomerSingleton {

    private static CustomerSingleton instance = null;

    ParseUser curUser = null;
    ParseObject curCustomer = null;

    public static CustomerSingleton getInstance() {
        if (instance == null) {
            instance = new CustomerSingleton();
        }
        return instance;
    }

    public ParseObject getCurCustomer() {
        return curCustomer;
    }

    /**
     * returns the current customer parseObject for query purposes
     */
    public void setCurCustomer(ParseObject curCustomer) {
        this.curCustomer = curCustomer;
    }

    public ParseUser getCurUser() {
        return curUser;
    }

    public void setCurUser(ParseUser curUser) {
        this.curUser = curUser;
    }

    /**
     * returns the customerObjectId for query purposes
     * @return customer objectId String
     */
    public String getCustomerId() {
        return curCustomer.getObjectId();
    }


    private CustomerSingleton() {


    }

}
