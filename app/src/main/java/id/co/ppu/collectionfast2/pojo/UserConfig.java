package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 06-Sep-16.
 */
public class UserConfig extends RealmObject implements Serializable{

    @SerializedName("syncMinute")
    private Long syncMinute;

    @SerializedName("serverDate")
    private Date serverDate;

    @SerializedName("kodeTarikRunningNumber")
    private Long kodeTarikRunningNumber;

    @SerializedName("kodeRVCollRunningNumber")
    private Long kodeRVCollRunningNumber;

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

    @Override
    public String toString() {
        return "UserConfig{" +
                "syncMinute=" + syncMinute +
                ", serverDate=" + serverDate +
                ", kodeTarikRunningNumber=" + kodeTarikRunningNumber +
                ", kodeRVCollRunningNumber=" + kodeRVCollRunningNumber +
                '}';
    }
}
