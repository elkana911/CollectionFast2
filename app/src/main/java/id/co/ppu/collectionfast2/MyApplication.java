package id.co.ppu.collectionfast2;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Eric on 29-Aug-16.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }

    @Override
    public void onTerminate() {
        Log.e("CollectionFast2", "onTerminate");
        super.onTerminate();
    }
}
