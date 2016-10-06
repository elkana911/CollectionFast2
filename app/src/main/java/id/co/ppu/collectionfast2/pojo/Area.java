package id.co.ppu.collectionfast2.pojo;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.master.MstCities;
import id.co.ppu.collectionfast2.pojo.master.MstKecamatan;
import id.co.ppu.collectionfast2.pojo.master.MstKelurahan;
import id.co.ppu.collectionfast2.pojo.master.MstProvinsi;
import id.co.ppu.collectionfast2.pojo.master.MstZip;

/**
 * Created by Eric on 20-Sep-16.
 */
public class Area {
    private MstKelurahan kelurahan;
    private MstKecamatan kecamatan;
    private MstCities city;
    private MstProvinsi provinsi;
    private List<MstZip> zip;

    public MstKelurahan getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(MstKelurahan kelurahan) {
        this.kelurahan = kelurahan;
    }

    public MstKecamatan getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(MstKecamatan kecamatan) {
        this.kecamatan = kecamatan;
    }

    public MstCities getCity() {
        return city;
    }

    public void setCity(MstCities city) {
        this.city = city;
    }

    public MstProvinsi getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(MstProvinsi provinsi) {
        this.provinsi = provinsi;
    }

    public List<MstZip> getZip() {
        return zip;
    }

    public void setZip(List<MstZip> zip) {
        this.zip = zip;
    }
}
