package id.co.ppu.collectionfast2.rest.request;

import java.util.List;

/**
 * Created by Eric on 13-Sep-16.
 */
public class RequestZipCode extends RequestBasic{
    private List<String> zipCode;

    public List<String> getZipCode() {
        return zipCode;
    }

    public void setZipCode(List<String> zipCode) {
        this.zipCode = zipCode;
    }

}
