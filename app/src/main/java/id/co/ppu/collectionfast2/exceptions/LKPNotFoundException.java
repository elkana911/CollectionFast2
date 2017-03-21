package id.co.ppu.collectionfast2.exceptions;

/**
 * Created by Eric on 03-Nov-16.
 */

public class LKPNotFoundException extends RuntimeException {

    public LKPNotFoundException(Throwable ex) {
        super(ex);
    }
    public LKPNotFoundException(String s) {
        super(s);
    }

    public LKPNotFoundException() {
        super("");
    }
}
