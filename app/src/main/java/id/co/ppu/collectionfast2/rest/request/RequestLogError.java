package id.co.ppu.collectionfast2.rest.request;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.trn.TrnErrorLog;

/**
 * Created by Eric on 13-Oct-16.
 */
public class RequestLogError extends RequestBasic{

    private List<TrnErrorLog> logs;

    public List<TrnErrorLog> getLogs() {
        return logs;
    }

    public void setLogs(List<TrnErrorLog> logs) {
        this.logs = logs;
    }
}
