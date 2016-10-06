package id.co.ppu.collectionfast2.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 19-Sep-16.
 */
public class MstCities extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("cityCode")
    private String cityCode;

    @SerializedName("city")
    private String city;

    @SerializedName("provCode")
    private String provCode;

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

    @SerializedName("sandiBI")
    private String sandiBI;

    @SerializedName("niagaCodeDatiII")
    private String niagaCodeDatiII;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
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

    public String getSandiBI() {
        return sandiBI;
    }

    public void setSandiBI(String sandiBI) {
        this.sandiBI = sandiBI;
    }

    public String getNiagaCodeDatiII() {
        return niagaCodeDatiII;
    }

    public void setNiagaCodeDatiII(String niagaCodeDatiII) {
        this.niagaCodeDatiII = niagaCodeDatiII;
    }

    @Override
    public String toString() {
        return "MstCities{" +
                "cityCode='" + cityCode + '\'' +
                ", city='" + city + '\'' +
                ", provCode='" + provCode + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", startedTimestamp=" + startedTimestamp +
                ", updateBy='" + updateBy + '\'' +
                ", sandiBI='" + sandiBI + '\'' +
                ", niagaCodeDatiII='" + niagaCodeDatiII + '\'' +
                '}';
    }
}
