package id.co.ppu.collectionfast2.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Aug-16.
 */
public class TrxLDVDetails extends RealmObject implements Serializable{

    @SerializedName("ldvNo")
    private String ldvNo;

    @PrimaryKey
    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("period")
    private String period;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("ovdInstNo")
    private Long ovdInstNo;

    @SerializedName("ovdDueDate")
    private Date ovdDueDate;

    @SerializedName("dueDate")
    private Date dueDate;

    @SerializedName("instNo")
    private Long instNo;

    @SerializedName("principalAmount")
    private Long principalAmount;

    @SerializedName("interestAmount")
    private Long interestAmount;

    @SerializedName("principalAMBC")
    private Long principalAMBC;

    @SerializedName("interestAMBC")
    private Long interestAMBC;//AmountMustBeCollected

    @SerializedName("penaltyAMBC")
    private Long penaltyAMBC;

    @SerializedName("principalAmountCollected")
    private Long principalAmountCollected;

    @SerializedName("interestAmountCollected")
    private Long interestAmountCollected;

    @SerializedName("penaltyAmountCollected")
    private Long penaltyAmountCollected;

    @SerializedName("ldvFlag")
    private String ldvFlag;

    @SerializedName("promiseDate")
    private Date promiseDate;

    @SerializedName("workStatus")
    private String workStatus;

    @SerializedName("visitSeq")
    private Long visitSeq;

    @SerializedName("principalOutstanding")
    private Long principalOutstanding;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Long getOvdInstNo() {
        return ovdInstNo;
    }

    public void setOvdInstNo(Long ovdInstNo) {
        this.ovdInstNo = ovdInstNo;
    }

    public Date getOvdDueDate() {
        return ovdDueDate;
    }

    public void setOvdDueDate(Date ovdDueDate) {
        this.ovdDueDate = ovdDueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getInstNo() {
        return instNo;
    }

    public void setInstNo(Long instNo) {
        this.instNo = instNo;
    }

    public Long getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Long principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Long getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(Long interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Long getPrincipalAMBC() {
        return principalAMBC;
    }

    public void setPrincipalAMBC(Long principalAMBC) {
        this.principalAMBC = principalAMBC;
    }

    public Long getInterestAMBC() {
        return interestAMBC;
    }

    public void setInterestAMBC(Long interestAMBC) {
        this.interestAMBC = interestAMBC;
    }

    public Long getPenaltyAMBC() {
        return penaltyAMBC;
    }

    public void setPenaltyAMBC(Long penaltyAMBC) {
        this.penaltyAMBC = penaltyAMBC;
    }

    public Long getPrincipalAmountCollected() {
        return principalAmountCollected;
    }

    public void setPrincipalAmountCollected(Long principalAmountCollected) {
        this.principalAmountCollected = principalAmountCollected;
    }

    public Long getInterestAmountCollected() {
        return interestAmountCollected;
    }

    public void setInterestAmountCollected(Long interestAmountCollected) {
        this.interestAmountCollected = interestAmountCollected;
    }

    public Long getPenaltyAmountCollected() {
        return penaltyAmountCollected;
    }

    public void setPenaltyAmountCollected(Long penaltyAmountCollected) {
        this.penaltyAmountCollected = penaltyAmountCollected;
    }

    public String getLdvFlag() {
        return ldvFlag;
    }

    public void setLdvFlag(String ldvFlag) {
        this.ldvFlag = ldvFlag;
    }

    public Date getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(Date promiseDate) {
        this.promiseDate = promiseDate;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Long getVisitSeq() {
        return visitSeq;
    }

    public void setVisitSeq(Long visitSeq) {
        this.visitSeq = visitSeq;
    }

    public Long getPrincipalOutstanding() {
        return principalOutstanding;
    }

    public void setPrincipalOutstanding(Long principalOutstanding) {
        this.principalOutstanding = principalOutstanding;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @Override
    public String toString() {
        return "TrxLDVDetails{" +
                "ldvNo='" + ldvNo + '\'' +
                ", seqNo=" + seqNo +
                ", period='" + period + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", custNo='" + custNo + '\'' +
                ", custName='" + custName + '\'' +
                ", ovdInstNo=" + ovdInstNo +
                ", ovdDueDate=" + ovdDueDate +
                ", dueDate=" + dueDate +
                ", instNo=" + instNo +
                ", principalAmount=" + principalAmount +
                ", interestAmount=" + interestAmount +
                ", principalAMBC=" + principalAMBC +
                ", interestAMBC=" + interestAMBC +
                ", penaltyAMBC=" + penaltyAMBC +
                ", principalAmountCollected=" + principalAmountCollected +
                ", interestAmountCollected=" + interestAmountCollected +
                ", penaltyAmountCollected=" + penaltyAmountCollected +
                ", ldvFlag='" + ldvFlag + '\'' +
                ", promiseDate=" + promiseDate +
                ", workStatus='" + workStatus + '\'' +
                ", visitSeq=" + visitSeq +
                ", principalOutstanding=" + principalOutstanding +
                ", startedTimestamp=" + startedTimestamp +
                '}';
    }
}
