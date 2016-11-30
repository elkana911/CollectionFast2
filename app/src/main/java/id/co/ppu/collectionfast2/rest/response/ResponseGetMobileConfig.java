package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.master.MstMobileSetup;

/**
 * Created by Eric on 02-Des-16.
 */
public class ResponseGetMobileConfig extends ResponseBasic implements Serializable{
    @SerializedName("data")
    private List<MstMobileSetup> data;

    public List<MstMobileSetup> getData() {
        return data;
    }

    public void setData(List<MstMobileSetup> data) {
        this.data = data;
    }
}
