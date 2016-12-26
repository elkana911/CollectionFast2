package id.co.ppu.collectionfast2.rest.response.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatContact;
import id.co.ppu.collectionfast2.rest.response.ResponseBasic;


/**
 * Created by Eric on 20-Nov-16.
 */

public class ResponseGetOnlineContacts extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private List<TrnChatContact> data;

    public List<TrnChatContact> getData() {
        return data;
    }

    public void setData(List<TrnChatContact> data) {
        this.data = data;
    }
}
