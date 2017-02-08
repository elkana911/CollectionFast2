package id.co.ppu.collectionfast2.pojo;

import android.text.TextUtils;

import java.util.Date;

/**
 * Created by Eric on 08-Feb-17.
 */

public class ArrivalDataBuffer {
    private String photoFile;
    private String latitude;
    private String longitude;
    private Date timestamp;

    public String getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(String photoFile) {
        this.photoFile = photoFile;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEmpty() {
        return timestamp != null
                || !TextUtils.isEmpty(latitude)
                || !TextUtils.isEmpty(longitude)
                || !TextUtils.isEmpty(photoFile);
    }

}
