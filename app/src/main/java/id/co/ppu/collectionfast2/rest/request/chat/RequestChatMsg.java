package id.co.ppu.collectionfast2.rest.request.chat;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatMsg {

    private List<TrnChatMsg> msg;

    public List<TrnChatMsg> getMsg() {
        return msg;
    }

    public void setMsg(List<TrnChatMsg> msg) {
        this.msg = msg;
    }
}
