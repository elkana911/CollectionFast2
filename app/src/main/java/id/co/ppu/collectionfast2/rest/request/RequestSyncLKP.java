package id.co.ppu.collectionfast2.rest.request;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnChangeAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;

/**
 * Created by Eric on 25-Sep-16.
 */

public class RequestSyncLKP extends RequestBasic{

    private List<TrnLDVHeader> ldvHeader;
    private List<TrnLDVDetails> ldvDetails;
    private List<TrnLDVComments> ldvComments;

    private List<TrnRVColl> rvColl;
    private List<TrnRVB> rvb;
    private List<TrnBastbj> bastbj;
    private List<TrnRepo> repo;
    private List<TrnChangeAddr> changeAddr;

    public List<TrnLDVHeader> getLdvHeader() {
        return ldvHeader;
    }

    public void setLdvHeader(List<TrnLDVHeader> ldvHeader) {
        this.ldvHeader = ldvHeader;
    }

    public List<TrnLDVDetails> getLdvDetails() {
        return ldvDetails;
    }

    public void setLdvDetails(List<TrnLDVDetails> ldvDetails) {
        this.ldvDetails = ldvDetails;
    }

    public List<TrnLDVComments> getLdvComments() {
        return ldvComments;
    }

    public void setLdvComments(List<TrnLDVComments> ldvComments) {
        this.ldvComments = ldvComments;
    }

    public List<TrnRVColl> getRvColl() {
        return rvColl;
    }

    public void setRvColl(List<TrnRVColl> rvColl) {
        this.rvColl = rvColl;
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

    public List<TrnRepo> getRepo() {
        return repo;
    }

    public void setRepo(List<TrnRepo> repo) {
        this.repo = repo;
    }

    public List<TrnChangeAddr> getChangeAddr() {
        return changeAddr;
    }

    public void setChangeAddr(List<TrnChangeAddr> changeAddr) {
        this.changeAddr = changeAddr;
    }
}
