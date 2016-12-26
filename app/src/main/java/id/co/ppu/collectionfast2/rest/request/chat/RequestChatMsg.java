package id.co.ppu.collectionfast2.rest.request.chat;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatMsg {

    private TrnChatMsg msg;

    public TrnChatMsg getMsg() {
        return msg;
    }

    public void setMsg(TrnChatMsg msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RequestChatMsg{" +
                "msg=" + msg +
                '}';
    }
}
