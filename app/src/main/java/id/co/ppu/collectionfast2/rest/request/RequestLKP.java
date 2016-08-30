package id.co.ppu.collectionfast2.rest.request;

/**
 * Created by Eric on 19-Aug-16.
 */
public class RequestLKP {
    private String collectorCode;

    public String getCollectorCode() {
        return collectorCode;
    }

    public void setCollectorCode(String collectorCode) {
        this.collectorCode = collectorCode;
    }

    @Override
    public String toString() {
        return "RequestLKP{" +
                "collectorCode='" + collectorCode + '\'' +
                '}';
    }
}
