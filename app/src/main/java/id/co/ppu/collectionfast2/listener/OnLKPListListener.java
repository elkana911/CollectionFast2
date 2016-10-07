package id.co.ppu.collectionfast2.listener;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;

/**
 * Created by Eric on 29-Sep-16.
 */

public interface OnLKPListListener {
    void onLKPSelected(DisplayTrnLDVDetails detail);
    void onLKPCancelSync(DisplayTrnLDVDetails detail);

    void onLKPInquiry(String collectorCode, Date lkpDate);
}
