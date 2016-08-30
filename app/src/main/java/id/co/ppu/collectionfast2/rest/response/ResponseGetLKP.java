package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.LKPData;

public class ResponseGetLKP extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private LKPData data;

    public LKPData getData() {
        return data;
    }

    public void setData(LKPData data) {
        this.data = data;
    }
}
