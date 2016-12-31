package id.co.ppu.collectionfast2.listener;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatContact;

/**
 * Created by Eric on 05-Oct-16.
 */

public interface OnGetChatContactListener {
    void onSuccess(List<TrnChatContact> list);
    void onFailure(Throwable throwable);
    void onSkip();
}
