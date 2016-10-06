package id.co.ppu.collectionfast2.pojo.sync;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncTrnBastbj extends RealmObject implements Serializable {
    @PrimaryKey
    private String bastbjNo;

    private String createdBy;
    private String lastUpdateBy;

    private Date syncedDate;

    public String getBastbjNo() {
        return bastbjNo;
    }

    public void setBastbjNo(String bastbjNo) {
        this.bastbjNo = bastbjNo;
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
