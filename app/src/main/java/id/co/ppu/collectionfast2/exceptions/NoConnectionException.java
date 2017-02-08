package id.co.ppu.collectionfast2.exceptions;

/**
 * Created by Eric on 03-Nov-16.
 */

public class NoConnectionException extends RuntimeException {

    public NoConnectionException(Throwable ex) {
        super(ex);
    }
    public NoConnectionException(String s) {
        super(s);
    }

    public NoConnectionException() {
        super("");
    }
}
