package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Aug-16.
 */
public class TrxLDVHeader extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("ldvDate")
    private Date ldvDate;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("collCode")
    private String collCode;

    @SerializedName("spvCode")
    private String spvCode;

    @SerializedName("type")
    private String type;

    @SerializedName("unitTotal")
    private Long unitTotal;

    @SerializedName("prncAMBC")
    private Long prncAMBC;

    @SerializedName("prncAC")
    private Long prncAC;

    @SerializedName("intrAMBC")
    private Long intrAMBC;

    @SerializedName("intrAC")
    private Long intrAC;

    @SerializedName("ambcTotal")
    private Long ambcTotal;

    @SerializedName("acTotal")
    private Long acTotal;

    @SerializedName("maxEntryDays")
    private Long maxEntryDays;

    @SerializedName("coyCode")
    private String coyCode;

    @SerializedName("workFlag")
    private String workFlag;

    @SerializedName("approvedDate")
    private Date approvedDate;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Date getLdvDate() {
        return ldvDate;
    }

    public void setLdvDate(Date ldvDate) {
        this.ldvDate = ldvDate;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getSpvCode() {
        return spvCode;
    }

    public void setSpvCode(String spvCode) {
        this.spvCode = spvCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUnitTotal() {
        return unitTotal;
    }

    public void setUnitTotal(Long unitTotal) {
        this.unitTotal = unitTotal;
    }

    public Long getPrncAMBC() {
        return prncAMBC;
    }

    public void setPrncAMBC(Long prncAMBC) {
        this.prncAMBC = prncAMBC;
    }

    public Long getPrncAC() {
        return prncAC;
    }

    public void setPrncAC(Long prncAC) {
        this.prncAC = prncAC;
    }

    public Long getIntrAMBC() {
        return intrAMBC;
    }

    public void setIntrAMBC(Long intrAMBC) {
        this.intrAMBC = intrAMBC;
    }

    public Long getIntrAC() {
        return intrAC;
    }

    public void setIntrAC(Long intrAC) {
        this.intrAC = intrAC;
    }

    public Long getAmbcTotal() {
        return ambcTotal;
    }

    public void setAmbcTotal(Long ambcTotal) {
        this.ambcTotal = ambcTotal;
    }

    public Long getAcTotal() {
        return acTotal;
    }

    public void setAcTotal(Long acTotal) {
        this.acTotal = acTotal;
    }

    public Long getMaxEntryDays() {
        return maxEntryDays;
    }

    public void setMaxEntryDays(Long maxEntryDays) {
        this.maxEntryDays = maxEntryDays;
    }

    public String getCoyCode() {
        return coyCode;
    }

    public void setCoyCode(String coyCode) {
        this.coyCode = coyCode;
    }

    public String getWorkFlag() {
        return workFlag;
    }

    public void setWorkFlag(String workFlag) {
        this.workFlag = workFlag;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @Override
    public String toString() {
        return "TrxLDVHeader{" +
                "ldvNo='" + ldvNo + '\'' +
                ", ldvDate=" + ldvDate +
                ", officeCode='" + officeCode + '\'' +
                ", collCode='" + collCode + '\'' +
                ", spvCode='" + spvCode + '\'' +
                ", type='" + type + '\'' +
                ", unitTotal=" + unitTotal +
                ", prncAMBC=" + prncAMBC +
                ", prncAC=" + prncAC +
                ", intrAMBC=" + intrAMBC +
                ", intrAC=" + intrAC +
                ", ambcTotal=" + ambcTotal +
                ", acTotal=" + acTotal +
                ", maxEntryDays=" + maxEntryDays +
                ", coyCode='" + coyCode + '\'' +
                ", workFlag='" + workFlag + '\'' +
                ", approvedDate=" + approvedDate +
                ", startedTimestamp=" + startedTimestamp +
                '}';
    }
}
