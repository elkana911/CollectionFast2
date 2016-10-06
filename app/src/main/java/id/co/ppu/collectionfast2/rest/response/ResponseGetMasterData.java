package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.master.MasterData;

/**
 * Created by Eric on 08-Sep-16.
 */
public class ResponseGetMasterData extends ResponseBasic implements Serializable{
    @SerializedName("data")
    private MasterData data;

    public MasterData getData() {
        return data;
    }

    public void setData(MasterData data) {
        this.data = data;
    }
}
