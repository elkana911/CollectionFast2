package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnChangeAddrPK extends RealmObject implements Serializable {
    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("contractNo")
    private String contractNo;

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

    @Override
    public String toString() {
        return "TrnChangeAddrPK{" +
                "seqNo=" + seqNo +
                ", contractNo='" + contractNo + '\'' +
                '}';
    }
}
