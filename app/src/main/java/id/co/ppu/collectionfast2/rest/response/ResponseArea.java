package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.Area;

/**
 * Created by Eric on 20-Sep-16.
 */
public class ResponseArea extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private Area data;

    public Area getData() {
        return data;
    }

    public void setData(Area data) {
        this.data = data;
    }
}
