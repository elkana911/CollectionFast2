package id.co.ppu.collectionfast2.rest.request;

/**
 * Created by Eric on 20-Sep-16.
 */
public class RequestArea extends RequestBasic{
    private String kelurahan;
    private String zipCode;

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "RequestArea{" +
                "kelurahan='" + kelurahan + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
