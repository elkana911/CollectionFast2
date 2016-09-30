package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 27-Sep-16.
 */

public class DisplayTrnContractBuckets extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("createdBy")
    private String createdBy;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
