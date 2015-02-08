package scout.scoutmobile.model;

/**
 * Represents a Business location, in the user's perspective. The
 * relevant information to the user is the Business name, the number
 * of points accumulated at that location, and an image of the Business.
 */
public class Place {

    private String mTitle;
    private String mThumbnailUrl;
    private Integer mPoints;
    private String mId;

    public Place(String title, String thumbnailUrl, Integer points, String id) {
        this.mTitle = title;
        this.mThumbnailUrl = thumbnailUrl;
        this.mPoints = points;
        this.mId = id;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }
}