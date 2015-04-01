package scout.scoutmobile.model;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Represents a Business location, in the user's perspective. The
 * relevant information to the user is the Business name, the number
 * of points accumulated at that location, and an image of the Business.
 */
public class Place {

    private String mTitle;
    private ParseFile mImageFile;
    private byte[] mImageByte;
    private Integer mPoints;
    private String mId;

    public Place(String title, ParseFile imageFile, Integer points, String id) {
        this.mTitle = title;
        this.mImageFile = imageFile;
        this.mPoints = points;
        this.mId = id;
        this.mImageByte = null;
    }

    public String getTitle() {
        return mTitle;
    }

    public ParseFile getImageFile() {
        return mImageFile;
    }

    public byte[] getImageByte() {
        return mImageByte;
    }

    public Integer getPoints() {
        return mPoints;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setImageFile(ParseFile imageFile) {
        this.mImageFile = imageFile;
    }

    public void setImageByte(byte[] imageByte) {
        this.mImageByte = imageByte;
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