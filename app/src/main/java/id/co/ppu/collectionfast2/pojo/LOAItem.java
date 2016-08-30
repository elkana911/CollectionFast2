package id.co.ppu.collectionfast2.pojo;

/**
 * Created by Eric on 11-Aug-16.
 */
public class LOAItem {
    private String id;
    private String custName;
    private String contractNo;
    private String kelKec;

    public LOAItem(String id, String custName, String contractNo, String kelKec) {
        this.id = id;
        this.custName = custName;
        this.contractNo = contractNo;
        this.kelKec = kelKec;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getKelKec() {
        return kelKec;
    }

    public void setKelKec(String kelKec) {
        this.kelKec = kelKec;
    }
}
