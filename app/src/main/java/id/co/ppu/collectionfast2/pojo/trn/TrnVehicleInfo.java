package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 16-Sep-16.
 */
public class TrnVehicleInfo extends RealmObject implements Serializable{

    @PrimaryKey
    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("objCategory")
    private String objCategory;

    @SerializedName("objBrand")
    private String objBrand;

    @SerializedName("objType")
    private String objType;

    @SerializedName("warna")
    private String warna;

    @SerializedName("noPolisi")
    private String noPolisi;

    @SerializedName("nosin")
    private String nosin;

    @SerializedName("noka")
    private String noka;

    @SerializedName("objTahun")
    private Long objTahun;

    @SerializedName("bpkbName")
    private String bpkbName;

    @SerializedName("bpkbIdNo")
    private String bpkbIdNo;

    @SerializedName("bpkbAddress")
    private String bpkbAddress;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getObjCategory() {
        return objCategory;
    }

    public void setObjCategory(String objCategory) {
        this.objCategory = objCategory;
    }

    public String getObjBrand() {
        return objBrand;
    }

    public void setObjBrand(String objBrand) {
        this.objBrand = objBrand;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getNoPolisi() {
        return noPolisi;
    }

    public void setNoPolisi(String noPolisi) {
        this.noPolisi = noPolisi;
    }

    public String getNosin() {
        return nosin;
    }

    public void setNosin(String nosin) {
        this.nosin = nosin;
    }

    public String getNoka() {
        return noka;
    }

    public void setNoka(String noka) {
        this.noka = noka;
    }

    public Long getObjTahun() {
        return objTahun;
    }

    public void setObjTahun(Long objTahun) {
        this.objTahun = objTahun;
    }

    public String getBpkbName() {
        return bpkbName;
    }

    public void setBpkbName(String bpkbName) {
        this.bpkbName = bpkbName;
    }

    public String getBpkbIdNo() {
        return bpkbIdNo;
    }

    public void setBpkbIdNo(String bpkbIdNo) {
        this.bpkbIdNo = bpkbIdNo;
    }

    public String getBpkbAddress() {
        return bpkbAddress;
    }

    public void setBpkbAddress(String bpkbAddress) {
        this.bpkbAddress = bpkbAddress;
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
        return "TrnVehicleInfo{" +
                "contractNo='" + contractNo + '\'' +
                ", objCategory='" + objCategory + '\'' +
                ", objBrand='" + objBrand + '\'' +
                ", objType='" + objType + '\'' +
                ", warna='" + warna + '\'' +
                ", noPolisi='" + noPolisi + '\'' +
                ", nosin='" + nosin + '\'' +
                ", noka='" + noka + '\'' +
                ", objTahun=" + objTahun +
                ", bpkbName='" + bpkbName + '\'' +
                ", bpkbIdNo='" + bpkbIdNo + '\'' +
                ", bpkbAddress='" + bpkbAddress + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
