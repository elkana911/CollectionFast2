package id.co.ppu.collectionfast2.sync.pojo;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncTrnRepo extends RealmObject implements Serializable {
    @PrimaryKey
    private String repoNo;
    private String createdBy;
    private String lastUpdateBy;

    private Date syncedDate;

    public String getRepoNo() {
        return repoNo;
    }

    public void setRepoNo(String repoNo) {
        this.repoNo = repoNo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getSyncedDate() {
        return syncedDate;
    }

    public void setSyncedDate(Date syncedDate) {
        this.syncedDate = syncedDate;
    }
}
