package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 17-Feb-17.
 */

public class TrnFlagTimestampPK extends RealmObject implements Serializable{
    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("collCode")
    private String collCode;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    @Override
    public String toString() {
        return "TrnFlagTimestampPK{" +
                "contractNo='" + contractNo + '\'' +
                ", ldvNo='" + ldvNo + '\'' +
                ", collCode='" + collCode + '\'' +
                '}';
    }
}
