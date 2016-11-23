package id.co.ppu.collectionfast2.rest.request;

/**
 * Created by Eric on 26-Sep-16.
 */

public class RequestRVB extends RequestBasic{
    private String collectorId;

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    @Override
    public String toString() {
        return "RequestRVB{" +
                "collectorId='" + collectorId + '\'' +
                '}';
    }
}
