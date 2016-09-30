package id.co.ppu.collectionfast2.listener;

import id.co.ppu.collectionfast2.pojo.ServerInfo;

/**
 * Created by Eric on 29-Sep-16.
 */
public interface OnPostRetrieveServerInfo {
    void onSuccess(ServerInfo serverInfo);

    void onFailure(Throwable throwable);
}
