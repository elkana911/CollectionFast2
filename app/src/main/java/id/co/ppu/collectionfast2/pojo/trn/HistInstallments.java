package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 08-Sep-16.
 */
public class HistInstallments extends RealmObject implements Serializable {

    @SerializedName("pk")
    private HistInstallmentsPK pk;

    @SerializedName("dueDate")
    private Date dueDate;

    @SerializedName("penaltyAmt")
    private Long penaltyAmt;

    @SerializedName("paidDate")
    private Date paidDate;

    @SerializedName("prncAmtDtlCust")
    private Long prncAmtDtlCust;

    @SerializedName("intrAmtDtlAr")
    private Long intrAmtDtlAr;

    @SerializedName("collFee")
    private Long collFee;

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

    public HistInstallmentsPK getPk() {
        return pk;
    }

    public void setPk(HistInstallmentsPK pk) {
        this.pk = pk;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getPenaltyAmt() {
        return penaltyAmt;
    }

    public void setPenaltyAmt(Long penaltyAmt) {
        this.penaltyAmt = penaltyAmt;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public Long getPrncAmtDtlCust() {
        return prncAmtDtlCust;
    }

    public void setPrncAmtDtlCust(Long prncAmtDtlCust) {
        this.prncAmtDtlCust = prncAmtDtlCust;
    }

    public Long getIntrAmtDtlAr() {
        return intrAmtDtlAr;
    }

    public void setIntrAmtDtlAr(Long intrAmtDtlAr) {
        this.intrAmtDtlAr = intrAmtDtlAr;
    }

    public Long getCollFee() {
        return collFee;
    }

    public void setCollFee(Long collFee) {
        this.collFee = collFee;
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

    @Override
    public String toString() {
        return "HistInstallments{" +
                "pk=" + pk +
                ", dueDate=" + dueDate +
                ", penaltyAmt=" + penaltyAmt +
                ", paidDate=" + paidDate +
                ", prncAmtDtlCust=" + prncAmtDtlCust +
                ", intrAmtDtlAr=" + intrAmtDtlAr +
                ", collFee=" + collFee +
                ", startedTimestamp=" + startedTimestamp +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
