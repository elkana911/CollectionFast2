package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 29-Aug-16.
 */
public class DisplayTrnLDVDetails extends RealmObject implements Serializable {

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("collId")
    private String collId;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("workStatus")
    private String workStatus;

    @SerializedName("createdBy")
    private String createdBy;

    // perlu direlasi krn buat tampilan di lkp list
    private TrnCollectAddr address;

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public TrnCollectAddr getAddress() {
        return address;
    }

    public void setAddress(TrnCollectAddr address) {
        this.address = address;
    }

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    @Override
    public String toString() {
        return "DisplayTrnLDVDetails{" +
                "ldvNo='" + ldvNo + '\'' +
                ", seqNo=" + seqNo +
                ", collId='" + collId + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", custNo='" + custNo + '\'' +
                ", custName='" + custName + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", address=" + address +
                '}';
    }
}
