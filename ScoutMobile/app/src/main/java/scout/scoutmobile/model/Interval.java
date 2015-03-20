package scout.scoutmobile.model;

import java.util.Date;

public class Interval {

    private Date mFrom;
    private Date mTo;

    public Interval() {
        this.mFrom = new Date();
        this.mTo = new Date();
    }

    public Date getFrom() {
        return mFrom;
    }

    public Date getTo() {
        return mTo;
    }

    public void setTo(Date to) {
        this.mTo = to;
    }
}
