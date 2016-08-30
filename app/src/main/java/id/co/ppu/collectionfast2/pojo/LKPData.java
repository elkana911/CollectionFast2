package id.co.ppu.collectionfast2.pojo;

import java.util.List;

/**
 * Created by Eric on 29-Aug-16.
 */
public class LKPData{

    private TrxLDVHeader header;
    private List<TrxLDVDetails> details;

    public TrxLDVHeader getHeader() {
        return header;
    }

    public void setHeader(TrxLDVHeader header) {
        this.header = header;
    }

    public List<TrxLDVDetails> getDetails() {
        return details;
    }

    public void setDetails(List<TrxLDVDetails> details) {
        this.details = details;
    }
}
