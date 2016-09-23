package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.ServerInfo;

/**
 * Created by Eric on 22-Sep-16.
 */

public class ResponseServerInfo extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private ServerInfo data;

    public ServerInfo getData() {
        return data;
    }

    public void setData(ServerInfo data) {
        this.data = data;
    }
}
