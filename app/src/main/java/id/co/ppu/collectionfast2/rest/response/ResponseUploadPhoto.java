package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eric on 30-Sep-16.
 */

public class ResponseUploadPhoto extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
