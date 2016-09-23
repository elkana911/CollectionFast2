package id.co.ppu.collectionfast2.pojo;

import java.util.List;

/**
 * Created by Eric on 01-Sep-16.
 */
public class UserData {
    private List<MstUser> user;
    private List<MstSecUser> secUser;
    private List<TrnContractBuckets> activeContracts;
    
    private UserConfig config;

    public List<MstUser> getUser() {
        return user;
    }

    public void setUser(List<MstUser> user) {
        this.user = user;
    }

    public List<MstSecUser> getSecUser() {
        return secUser;
    }

    public void setSecUser(List<MstSecUser> secUser) {
        this.secUser = secUser;
    }

    public List<TrnContractBuckets> getActiveContracts() {
        return activeContracts;
    }

    public void setActiveContracts(List<TrnContractBuckets> activeContracts) {
        this.activeContracts = activeContracts;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "user=" + user +
                ", secUser=" + secUser +
                ", activeContracts=" + activeContracts +
                '}';
    }
}
