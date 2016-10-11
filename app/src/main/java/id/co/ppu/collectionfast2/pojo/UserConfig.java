package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 06-Sep-16.
 */
public class UserConfig extends RealmObject implements Serializable{

    @PrimaryKey
    @SerializedName("uid")
    private String uid;

    @SerializedName("imei")
    private String imeiDevice;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("syncMinute")
    private Long syncMinute;

    @SerializedName("serverDate")
    private Date serverDate;

    @SerializedName("lastLogin")
    private Date lastLogin;

    @SerializedName("kodeTarikRunningNumber")
    private Long kodeTarikRunningNumber;

    @Deprecated
    @SerializedName("kodeRVCollRunningNumber")
    private Long kodeRVCollRunningNumber;

    @SerializedName("kodeRVCollLastGenerated")
    private Date kodeRVCollLastGenerated;

    @SerializedName("photoProfileUri")
    private String photoProfileUri;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImeiDevice() {
        return imeiDevice;
    }

    public void setImeiDevice(String imeiDevice) {
        this.imeiDevice = imeiDevice;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getSyncMinute() {
        return syncMinute;
    }

    public void setSyncMinute(Long syncMinute) {
        this.syncMinute = syncMinute;
    }

    public Long getKodeTarikRunningNumber() {
        return kodeTarikRunningNumber;
    }

    public void setKodeTarikRunningNumber(Long kodeTarikRunningNumber) {
        this.kodeTarikRunningNumber = kodeTarikRunningNumber;
    }

    public Long getKodeRVCollRunningNumber() {
        return kodeRVCollRunningNumber;
    }

    public void setKodeRVCollRunningNumber(Long kodeRVCollRunningNumber) {
        this.kodeRVCollRunningNumber = kodeRVCollRunningNumber;
    }

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPhotoProfileUri() {
        return photoProfileUri;
    }

    public void setPhotoProfileUri(String photoProfileUri) {
        this.photoProfileUri = photoProfileUri;
    }

    public Date getKodeRVCollLastGenerated() {
        return kodeRVCollLastGenerated;
    }

    public void setKodeRVCollLastGenerated(Date kodeRVCollLastGenerated) {
        this.kodeRVCollLastGenerated = kodeRVCollLastGenerated;
    }

    @Override
    public String toString() {
        return "UserConfig{" +
                "uid='" + uid + '\'' +
                ", imeiDevice='" + imeiDevice + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", syncMinute=" + syncMinute +
                ", serverDate=" + serverDate +
                ", lastLogin=" + lastLogin +
                ", kodeTarikRunningNumber=" + kodeTarikRunningNumber +
                ", kodeRVCollRunningNumber=" + kodeRVCollRunningNumber +
                ", kodeRVCollLastGenerated=" + kodeRVCollLastGenerated +
                ", photoProfileUri='" + photoProfileUri + '\'' +
                '}';
    }
}
