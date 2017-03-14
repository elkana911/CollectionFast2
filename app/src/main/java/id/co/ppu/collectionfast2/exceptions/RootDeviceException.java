package id.co.ppu.collectionfast2.exceptions;

/**
 * Created by Eric on 03-Nov-16.
 */

public class RootDeviceException extends RuntimeException {

    public RootDeviceException(Throwable ex) {
        super(ex);
    }
    public RootDeviceException(String s) {
        super(s);
    }

    public RootDeviceException() {
        super("");
    }
}
