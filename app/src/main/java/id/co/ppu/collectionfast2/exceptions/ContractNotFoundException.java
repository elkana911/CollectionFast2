package id.co.ppu.collectionfast2.exceptions;

/**
 * Created by Eric on 03-Nov-16.
 */

public class ContractNotFoundException extends RuntimeException {

    public ContractNotFoundException(Throwable ex) {
        super(ex);
    }
    public ContractNotFoundException(String s) {
        super(s);
    }

    public ContractNotFoundException() {
        super("");
    }
}
