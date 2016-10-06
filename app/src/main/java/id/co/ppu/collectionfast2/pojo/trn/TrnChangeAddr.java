package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnChangeAddr extends RealmObject implements Serializable {
    @SerializedName("pk")
    private TrnChangeAddrPK pk;

    @SerializedName("collId")
    private String collId;

    @SerializedName("collAddr")
    private String collAddr;

    @SerializedName("collRt")
    private String collRt;

    @SerializedName("collRw")
    private String collRw;

    @SerializedName("collKelCode")
    private String collKelCode;

    @SerializedName("collKel")
    private String collKel;

    @SerializedName("collKecCode")
    private String collKecCode;

    @SerializedName("collKec")
    private String collKec;

    @SerializedName("collCityCode")
    private String collCityCode;

    @SerializedName("collCity")
    private String collCity;

    @SerializedName("collZip")
    private String collZip;

    @SerializedName("collSubZip")
    private String collSubZip;

    @SerializedName("collProvCode")
    private String collProvCode;

    @SerializedName("collProv")
    private String collProv;

    @SerializedName("collFixPhArea")
    private String collFixPhArea;

    @SerializedName("collFixPhone")
    private String collFixPhone;

    @SerializedName("collFaxArea")
    private String collFaxArea;

    @SerializedName("collFaximile")
    private String collFaximile;

    @SerializedName("collMobPhone")
    private String collMobPhone;

    @SerializedName("collEmail")
    private String collEmail;

    @SerializedName("collName")
    private String collName;

    @SerializedName("collMobPhone2")
    private String collMobPhone2;

    @SerializedName("collNickname")
    private String collNickname;

    @SerializedName("status")
    private String status;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("dateDone")
    private Date dateDone;

    @SerializedName("flagChanged")
    private String flagChanged;

    @SerializedName("dateChanged")
    private Date dateChanged;

    public TrnChangeAddrPK getPk() {
        return pk;
    }

    public void setPk(TrnChangeAddrPK pk) {
        this.pk = pk;
    }

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public String getCollAddr() {
        return collAddr;
    }

    public void setCollAddr(String collAddr) {
        this.collAddr = collAddr;
    }

    public String getCollRt() {
        return collRt;
    }

    public void setCollRt(String collRt) {
        this.collRt = collRt;
    }

    public String getCollRw() {
        return collRw;
    }

    public void setCollRw(String collRw) {
        this.collRw = collRw;
    }

    public String getCollKelCode() {
        return collKelCode;
    }

    public void setCollKelCode(String collKelCode) {
        this.collKelCode = collKelCode;
    }

    public String getCollKel() {
        return collKel;
    }

    public void setCollKel(String collKel) {
        this.collKel = collKel;
    }

    public String getCollKecCode() {
        return collKecCode;
    }

    public void setCollKecCode(String collKecCode) {
        this.collKecCode = collKecCode;
    }

    public String getCollKec() {
        return collKec;
    }

    public void setCollKec(String collKec) {
        this.collKec = collKec;
    }

    public String getCollCityCode() {
        return collCityCode;
    }

    public void setCollCityCode(String collCityCode) {
        this.collCityCode = collCityCode;
    }

    public String getCollCity() {
        return collCity;
    }

    public void setCollCity(String collCity) {
        this.collCity = collCity;
    }

    public String getCollZip() {
        return collZip;
    }

    public void setCollZip(String collZip) {
        this.collZip = collZip;
    }

    public String getCollSubZip() {
        return collSubZip;
    }

    public void setCollSubZip(String collSubZip) {
        this.collSubZip = collSubZip;
    }

    public String getCollProvCode() {
        return collProvCode;
    }

    public void setCollProvCode(String collProvCode) {
        this.collProvCode = collProvCode;
    }

    public String getCollProv() {
        return collProv;
    }

    public void setCollProv(String collProv) {
        this.collProv = collProv;
    }

    public String getCollFixPhArea() {
        return collFixPhArea;
    }

    public void setCollFixPhArea(String collFixPhArea) {
        this.collFixPhArea = collFixPhArea;
    }

    public String getCollFixPhone() {
        return collFixPhone;
    }

    public void setCollFixPhone(String collFixPhone) {
        this.collFixPhone = collFixPhone;
    }

    public String getCollFaxArea() {
        return collFaxArea;
    }

    public void setCollFaxArea(String collFaxArea) {
        this.collFaxArea = collFaxArea;
    }

    public String getCollFaximile() {
        return collFaximile;
    }

    public void setCollFaximile(String collFaximile) {
        this.collFaximile = collFaximile;
    }

    public String getCollMobPhone() {
        return collMobPhone;
    }

    public void setCollMobPhone(String collMobPhone) {
        this.collMobPhone = collMobPhone;
    }

    public String getCollEmail() {
        return collEmail;
    }

    public void setCollEmail(String collEmail) {
        this.collEmail = collEmail;
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public String getCollMobPhone2() {
        return collMobPhone2;
    }

    public void setCollMobPhone2(String collMobPhone2) {
        this.collMobPhone2 = collMobPhone2;
    }

    public String getCollNickname() {
        return collNickname;
    }

    public void setCollNickname(String collNickname) {
        this.collNickname = collNickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
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

    public String getFlagChanged() {
        return flagChanged;
    }

    public void setFlagChanged(String flagChanged) {
        this.flagChanged = flagChanged;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    @Override
    public String toString() {
        return "TrnChangeAddr{" +
                "pk=" + pk +
                ", collId='" + collId + '\'' +
                ", collAddr='" + collAddr + '\'' +
                ", collRt='" + collRt + '\'' +
                ", collRw='" + collRw + '\'' +
                ", collKelCode='" + collKelCode + '\'' +
                ", collKel='" + collKel + '\'' +
                ", collKecCode='" + collKecCode + '\'' +
                ", collKec='" + collKec + '\'' +
                ", collCityCode='" + collCityCode + '\'' +
                ", collCity='" + collCity + '\'' +
                ", collZip='" + collZip + '\'' +
                ", collSubZip='" + collSubZip + '\'' +
                ", collProvCode='" + collProvCode + '\'' +
                ", collProv='" + collProv + '\'' +
                ", collFixPhArea='" + collFixPhArea + '\'' +
                ", collFixPhone='" + collFixPhone + '\'' +
                ", collFaxArea='" + collFaxArea + '\'' +
                ", collFaximile='" + collFaximile + '\'' +
                ", collMobPhone='" + collMobPhone + '\'' +
                ", collEmail='" + collEmail + '\'' +
                ", collName='" + collName + '\'' +
                ", collMobPhone2='" + collMobPhone2 + '\'' +
                ", collNickname='" + collNickname + '\'' +
                ", status='" + status + '\'' +
                ", officeCode='" + officeCode + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", flagDone='" + flagDone + '\'' +
                ", dateDone=" + dateDone +
                ", flagChanged='" + flagChanged + '\'' +
                ", dateChanged=" + dateChanged +
                '}';
    }
}
