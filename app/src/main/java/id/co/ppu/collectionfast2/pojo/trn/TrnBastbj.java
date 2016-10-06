package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnBastbj extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("bastbjNo")
    private String bastbjNo;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("bastbjDate")
    private Date bastbjDate;

    @SerializedName("bastbjUserId")
    private String bastbjUserId;

    @SerializedName("bastbjStatus")
    private String bastbjStatus;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    @SerializedName("bastbjOnHand")
    private String bastbjOnHand;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("dateDone")
    private Date dateDone;

    public String getBastbjNo() {
        return bastbjNo;
    }

    public void setBastbjNo(String bastbjNo) {
        this.bastbjNo = bastbjNo;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public Date getBastbjDate() {
        return bastbjDate;
    }

    public void setBastbjDate(Date bastbjDate) {
        this.bastbjDate = bastbjDate;
    }

    public String getBastbjUserId() {
        return bastbjUserId;
    }

    public void setBastbjUserId(String bastbjUserId) {
        this.bastbjUserId = bastbjUserId;
    }

    public String getBastbjStatus() {
        return bastbjStatus;
    }

    public void setBastbjStatus(String bastbjStatus) {
        this.bastbjStatus = bastbjStatus;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
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

    public String getBastbjOnHand() {
        return bastbjOnHand;
    }

    public void setBastbjOnHand(String bastbjOnHand) {
        this.bastbjOnHand = bastbjOnHand;
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

    public String getFlagDone() {
        return flagDone;
    }

    public void setFlagDone(String flagDone) {
        this.flagDone = flagDone;
    }

    public Date getDateDone() {
        return dateDone;
    }

    public void setDateDone(Date dateDone) {
        this.dateDone = dateDone;
    }

    @Override
    public String toString() {
        return bastbjNo;
    }
}
