package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.master.MasterArea;

/**
 * Created by Eric on 08-Sep-16.
 */
public class ResponseGetMasterArea extends ResponseBasic implements Serializable{
    @SerializedName("data")
    private MasterArea data;

    public MasterArea getData() {
        return data;
    }

    public void setData(MasterArea data) {
        this.data = data;
    }
}
