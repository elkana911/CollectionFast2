package id.co.ppu.collectionfast2.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;

/**
 * Created by Eric on 26-Sep-16.
 */

public class ResponseRVB extends ResponseBasic implements Serializable{

    @SerializedName("data")
    private List<TrnRVB> data;

    public List<TrnRVB> getData() {
        return data;
    }

    public void setData(List<TrnRVB> data) {
        this.data = data;
    }
}
