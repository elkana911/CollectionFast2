package id.co.ppu.collectionfast2.pojo;

import java.util.List;

/**
 * Created by Eric on 29-Aug-16.
 */
public class LKPData{

    private TrnLDVHeader header;
    private List<TrnLDVDetails> details;
    private List<TrnCollectAddr> address;
    private List<TrnContractBuckets> buckets;
    private List<TrnRVB> rvb;
    private List<TrnBastbj> bastbj;
    private List<TrnVehicleInfo> vehicleInfo;
    private List<HistInstallments> historyInstallments;

    public TrnLDVHeader getHeader() {
        return header;
    }

    public void setHeader(TrnLDVHeader header) {
        this.header = header;
    }

    public List<TrnLDVDetails> getDetails() {
        return details;
    }

    public void setDetails(List<TrnLDVDetails> details) {
        this.details = details;
    }

    public List<TrnCollectAddr> getAddress() {
        return address;
    }

    public void setAddress(List<TrnCollectAddr> address) {
        this.address = address;
    }

    public List<TrnContractBuckets> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<TrnContractBuckets> buckets) {
        this.buckets = buckets;
    }

    public List<TrnRVB> getRvb() {
        return rvb;
    }

    public void setRvb(List<TrnRVB> rvb) {
        this.rvb = rvb;
    }

    public List<TrnBastbj> getBastbj() {
        return bastbj;
    }

    public void setBastbj(List<TrnBastbj> bastbj) {
        this.bastbj = bastbj;
    }

    public List<TrnVehicleInfo> getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(List<TrnVehicleInfo> vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public List<HistInstallments> getHistoryInstallments() {
        return historyInstallments;
    }

    public void setHistoryInstallments(List<HistInstallments> historyInstallments) {
        this.historyInstallments = historyInstallments;
    }
}
