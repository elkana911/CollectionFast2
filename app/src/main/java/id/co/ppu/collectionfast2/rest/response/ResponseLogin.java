package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collectionfast2.pojo.User;

public class ResponseLogin extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

}
