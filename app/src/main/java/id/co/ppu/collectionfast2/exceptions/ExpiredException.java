package id.co.ppu.collectionfast2.exceptions;

/**
 * Created by Eric on 03-Nov-16.
 */

public class ExpiredException extends RuntimeException {

    public ExpiredException(Throwable ex) {
        super(ex);
    }
    public ExpiredException(String s) {
        super(s);
    }

}
