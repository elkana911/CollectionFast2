package id.co.ppu.collectionfast2.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 08-Sep-16.
 */
public class HistInstallmentsPK extends RealmObject implements Serializable {

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("instNo")
    private Long instNo;

    public Long getInstNo() {
        return instNo;
    }

    public void setInstNo(Long instNo) {
        this.instNo = instNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    @Override
    public String toString() {
        return "HistInstallmentsPK{" +
                "contractNo='" + contractNo + '\'' +
                ", instNo=" + instNo +
                '}';
    }
}
