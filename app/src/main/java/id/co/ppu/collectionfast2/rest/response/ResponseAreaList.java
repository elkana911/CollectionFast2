package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.AreaList;

/**
 * Created by Eric on 20-Sep-16.
 */
public class ResponseAreaList extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private AreaList data;

    public AreaList getData() {
        return data;
    }

    public void setData(AreaList data) {
        this.data = data;
    }
}
