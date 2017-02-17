package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnFlagTimestamp extends RealmObject implements Serializable {

    @SerializedName("pk")
    private TrnFlagTimestampPK pk;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

//    @SerializedName("flagDone")
//    private String flagDone;

//    @SerializedName("dateDone")
//    private Date dateDone;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    public TrnFlagTimestampPK getPk() {
        return pk;
    }

    public void setPk(TrnFlagTimestampPK pk) {
        this.pk = pk;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getFlagToEmrafin() {
        return flagToEmrafin;
    }

    public void setFlagToEmrafin(String flagToEmrafin) {
        this.flagToEmrafin = flagToEmrafin;
    }

    public Date getDateToEmrafin() {
        return dateToEmrafin;
    }

    public void setDateToEmrafin(Date dateToEmrafin) {
        this.dateToEmrafin = dateToEmrafin;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastupdateBy() {
        return lastupdateBy;
    }

    public void setLastupdateBy(String lastupdateBy) {
        this.lastupdateBy = lastupdateBy;
    }

    public Date getLastupdateTimestamp() {
        return lastupdateTimestamp;
    }

    public void setLastupdateTimestamp(Date lastupdateTimestamp) {
        this.lastupdateTimestamp = lastupdateTimestamp;
    }

    @Override
    public String toString() {
        return "TrnFlagTimestamp{" +
                "pk=" + pk +
                ", fileName='" + fileName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
