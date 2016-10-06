package id.co.ppu.collectionfast2.pojo;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.master.MstCities;
import id.co.ppu.collectionfast2.pojo.master.MstKecamatan;
import id.co.ppu.collectionfast2.pojo.master.MstKelurahan;
import id.co.ppu.collectionfast2.pojo.master.MstProvinsi;
import id.co.ppu.collectionfast2.pojo.master.MstZip;

/**
 * Created by Eric on 25-Sep-16.
 */

public class AreaList {
    private List<MstKelurahan> kelurahan;
    private List<MstKecamatan> kecamatan;
    private List<MstCities> city;
    private List<MstProvinsi> provinsi;
    private List<MstZip> zip;

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

    public List<MstZip> getZip() {
        return zip;
    }

    public void setZip(List<MstZip> zip) {
        this.zip = zip;
    }
}
