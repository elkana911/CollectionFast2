package id.co.ppu.collectionfast2.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 19-Sep-16.
 */
public class MstKecamatan extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("kecCode")
    private String kecCode;

    @SerializedName("kecamatan")
    private String kecamatan;

    @SerializedName("cityCode")
    private String cityCode;

    @SerializedName("createBy")
    private String createBy;

    @SerializedName("createDate")
    private Date createDate;

    @SerializedName("updateDate")
    private Date updateDate;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("updateBy")
    private String updateBy;

    public String getKecCode() {
        return kecCode;
    }

    public void setKecCode(String kecCode) {
        this.kecCode = kecCode;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "MstKecamatan{" +
                "kecCode='" + kecCode + '\'' +
                ", kecamatan='" + kecamatan + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", startedTimestamp=" + startedTimestamp +
                ", updateBy='" + updateBy + '\'' +
                '}';
    }
}
