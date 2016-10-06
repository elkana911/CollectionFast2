package id.co.ppu.collectionfast2.listener;

/**
 * Created by Eric on 05-Oct-16.
 */

public interface OnSuccessError {
    void onSuccess(String msg);
    void onFailure(Throwable throwable);
    void onSkip();
}
