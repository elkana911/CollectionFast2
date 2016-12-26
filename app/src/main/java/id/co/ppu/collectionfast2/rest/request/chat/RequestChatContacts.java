package id.co.ppu.collectionfast2.rest.request.chat;

import java.util.List;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatContacts {
    private List<String> collsCode;

    public List<String> getCollsCode() {
        return collsCode;
    }

    public void setCollsCode(List<String> collsCode) {
        this.collsCode = collsCode;
    }

    @Override
    public String toString() {
        return "RequestChatContacts{" +
                "collsCode=" + collsCode +
                '}';
    }
}
