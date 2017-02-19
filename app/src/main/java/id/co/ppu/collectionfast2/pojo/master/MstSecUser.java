package id.co.ppu.collectionfast2.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 23-Aug-16.
 */
public class MstSecUser extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("emailAddr")
    private String emailAddr;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("modulId")
    private String modulId;

    @SerializedName("birthPlace")
    private String birthPlace;

    @SerializedName("birthDate")
    private Date birthDate;

    @SerializedName("mobilePhone")
    private String mobilePhone;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("lastLogonTimestamp")
    private Date lastLogonTimestamp;

    @SerializedName("confirmed")
    private String confirmed;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("branchId")
    private String branchId;

    @SerializedName("bussUnit")
    private String bussUnit;

    // 17feb17
    @SerializedName("SN")
    private String sn;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;


//    @SerializedName("phoneSN")
//    private String phoneSN;

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getModulId() {
        return modulId;
    }

    public void setModulId(String modulId) {
        this.modulId = modulId;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Date getLastLogonTimestamp() {
        return lastLogonTimestamp;
    }

    public void setLastLogonTimestamp(Date lastLogonTimestamp) {
        this.lastLogonTimestamp = lastLogonTimestamp;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBussUnit() {
        return bussUnit;
    }

    public void setBussUnit(String bussUnit) {
        this.bussUnit = bussUnit;
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "MstSecUser{" +
                "emailAddr='" + emailAddr + '\'' +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", modulId='" + modulId + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", birthDate=" + birthDate +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", startedTimestamp=" + startedTimestamp +
                ", lastLogonTimestamp=" + lastLogonTimestamp +
                ", confirmed='" + confirmed + '\'' +
                ", fullName='" + fullName + '\'' +
                ", branchId='" + branchId + '\'' +
                ", bussUnit='" + bussUnit + '\'' +
                ", sn='" + sn + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
