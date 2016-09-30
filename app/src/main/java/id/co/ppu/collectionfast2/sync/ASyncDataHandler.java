package id.co.ppu.collectionfast2.sync;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Eric on 29-Sep-16.
 */

public abstract class ASyncDataHandler<E> {

    protected List<E> list = new ArrayList<>();

    protected Realm realm;

    public ASyncDataHandler(Realm realm) {
        this.realm = realm;
        initData();
    }

    protected abstract void initData();

    public List<E> getDataToSync() {
        return list;
    }

    public abstract void syncData();

    public boolean anyDataToSync() {
        return list.size() > 0;
    }
}
