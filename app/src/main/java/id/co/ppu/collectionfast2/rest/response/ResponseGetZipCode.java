package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.master.MstZip;

/**
 * Created by Eric on 08-Sep-16.
 */
public class ResponseGetZipCode extends ResponseBasic implements Serializable{
    @SerializedName("data")
    private List<MstZip> data;

    public List<MstZip> getData() {
        return data;
    }

    public void setData(List<MstZip> data) {
        this.data = data;
    }
}
