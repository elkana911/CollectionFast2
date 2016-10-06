package id.co.ppu.collectionfast2.pojo;

import android.support.annotation.NonNull;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.master.MstSecUser;
import id.co.ppu.collectionfast2.pojo.master.MstUser;

/**
 * Created by Eric on 01-Sep-16.
 */
public class UserData {

    @NonNull
    private List<MstUser> user;
    private List<MstSecUser> secUser;

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

    @Override
    public String toString() {
        return "UserData{" +
                "user=" + user +
                ", secUser=" + secUser +
                '}';
    }
}
