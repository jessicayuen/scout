package scout.scoutmobile.model;

/**
 * Represents a Business location, in the user's perspective. The
 * relevant information to the user is the Business name, the number
 * of points accumulated at that location, and an image of the Business.
 */
public class Place {

    String mTitle;
    String mThumbnailUrl;
    Integer mPoints;

    public Place(String title, String thumbnailUrl, Integer points) {
        this.mTitle = title;
        this.mThumbnailUrl = thumbnailUrl;
        this.mPoints = points;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public Integer getPoints() {
        return mPoints;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    public void setPoints(Integer points) {
        this.mPoints = points;
    }
}