package id.co.ppu.collectionfast2.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class MstZip extends RealmObject implements Serializable {

    @SerializedName("pk")
    private MstZipPK pk;

    @SerializedName("zipDesc")
    private String zipDesc;

    @SerializedName("provCode")
    private String provCode;

    @SerializedName("cityCode")
    private String cityCode;

    @SerializedName("kecCode")
    private String kecCode;

    @SerializedName("kelCode")
    private String kelCode;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    public MstZipPK getPk() {
        return pk;
    }

    public void setPk(MstZipPK pk) {
        this.pk = pk;
    }

    public String getZipDesc() {
        return zipDesc;
    }

    public void setZipDesc(String zipDesc) {
        this.zipDesc = zipDesc;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getKecCode() {
        return kecCode;
    }

    public void setKecCode(String kecCode) {
        this.kecCode = kecCode;
    }

    public String getKelCode() {
        return kelCode;
    }

    public void setKelCode(String kelCode) {
        this.kelCode = kelCode;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @Override
    public String toString() {
        return "MstZip{" +
                "pk=" + pk +
                ", zipDesc='" + zipDesc + '\'' +
                ", provCode='" + provCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", kecCode='" + kecCode + '\'' +
                ", kelCode='" + kelCode + '\'' +
                ", startedTimestamp=" + startedTimestamp +
                '}';
    }
}
