package id.co.ppu.collectionfast2.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class MstZipPK extends RealmObject implements Serializable {
    @SerializedName("zipCode")
    private String zipCode;

    @SerializedName("subZipCode")
    private String subZipCode;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSubZipCode() {
        return subZipCode;
    }

    public void setSubZipCode(String subZipCode) {
        this.subZipCode = subZipCode;
    }

    @Override
    public String toString() {
        return "MstZipPK{" +
                "zipCode='" + zipCode + '\'' +
                ", subZipCode='" + subZipCode + '\'' +
                '}';
    }
}
