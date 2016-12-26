package id.co.ppu.collectionfast2.rest.response.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.rest.response.ResponseBasic;

/**
 * Created by Eric on 25-Oct-16.
 */

public class ResponseChatHistory extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private List<TrnChatMsg> data;

    public List<TrnChatMsg> getData() {
        return data;
    }

    public void setData(List<TrnChatMsg> data) {
        this.data = data;
    }
}
