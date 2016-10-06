package id.co.ppu.collectionfast2.pojo.master;

import java.util.List;

/**
 * Created by Eric on 19-Sep-16.
 */
public class MasterArea {
    private List<MstKelurahan> kelurahan;
    private List<MstKecamatan> kecamatan;
    private List<MstCities> city;
    private List<MstProvinsi> provinsi;

    public List<MstKelurahan> getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(List<MstKelurahan> kelurahan) {
        this.kelurahan = kelurahan;
    }

    public List<MstKecamatan> getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(List<MstKecamatan> kecamatan) {
        this.kecamatan = kecamatan;
    }

    public List<MstCities> getCity() {
        return city;
    }

    public void setCity(List<MstCities> city) {
        this.city = city;
    }

    public List<MstProvinsi> getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(List<MstProvinsi> provinsi) {
        this.provinsi = provinsi;
    }
}
