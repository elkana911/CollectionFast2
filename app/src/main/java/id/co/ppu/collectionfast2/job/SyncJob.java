package id.co.ppu.collectionfast2.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Utility;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncJob extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, Intent intent) {

            try {
                final double[] gps = Location.getGPS(context);
                NetUtil.syncLocation(context, gps, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (Utility.isWorkingHours(new Date(), 8, 17)) {
            try {
                NetUtil.refreshRVBFromServer(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
