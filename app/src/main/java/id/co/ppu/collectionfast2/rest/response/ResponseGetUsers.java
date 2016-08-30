package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.User;

public class ResponseGetUsers extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private List<User> data;

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }

}
