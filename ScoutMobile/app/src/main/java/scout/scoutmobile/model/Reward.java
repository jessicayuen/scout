package scout.scoutmobile.model;

/**
 * Represents a reward from the user's perspective, which includes
 * the reward description, points, and QR code
 */
public class Reward {

    private Integer mPoints;
    private String mDescription;
    private String mID;

    public Reward(Integer points, String description, String id) {
        this.mPoints = points;
        this.mDescription = description;
        this.mID = id;
    }

    public Integer getPoints() {
        return mPoints;
    }

    public void setPoints(Integer points) {
        this.mPoints = points;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getID() {
        return mID;
    }

    public void setID(String id) {
        this.mID = id;
    }
}

