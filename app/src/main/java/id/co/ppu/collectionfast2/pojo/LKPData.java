package id.co.ppu.collectionfast2.pojo;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestamp;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;

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
    private List<TrnPhoto> photo;
    private List<HistInstallments> historyInstallments;

    private List<TrnRVColl> rvColl;
    private List<TrnRepo> repo;
    private List<TrnLDVComments> ldvComments;

    private List<TrnFlagTimestamp> flagTimestamps;

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

    public List<TrnRVColl> getRvColl() {
        return rvColl;
    }

    public void setRvColl(List<TrnRVColl> rvColl) {
        this.rvColl = rvColl;
    }

    public List<TrnRepo> getRepo() {
        return repo;
    }

    public void setRepo(List<TrnRepo> repo) {
        this.repo = repo;
    }

    public List<TrnLDVComments> getLdvComments() {
        return ldvComments;
    }

    public void setLdvComments(List<TrnLDVComments> ldvComments) {
        this.ldvComments = ldvComments;
    }

    public List<TrnPhoto> getPhoto() {
        return photo;
    }

    public void setPhoto(List<TrnPhoto> photo) {
        this.photo = photo;
    }

    public List<TrnFlagTimestamp> getFlagTimestamps() {
        return flagTimestamps;
    }

    public void setFlagTimestamps(List<TrnFlagTimestamp> flagTimestamps) {
        this.flagTimestamps = flagTimestamps;
    }
}
