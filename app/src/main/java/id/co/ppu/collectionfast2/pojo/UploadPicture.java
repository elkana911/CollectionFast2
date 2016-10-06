package id.co.ppu.collectionfast2.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 19-Sep-16.
 */
public class UploadPicture extends RealmObject implements Serializable {

    @PrimaryKey
    private String contractNo;
    private String collectorId;

    private String picture1;
    private String lat1;
    private String long1;

    private String picture2;
    private String lat2;
    private String long2;

    private String picture3;
    private String lat3;
    private String long3;

    private String picture4;
    private String lat4;
    private String long4;

    private String picture5;
    private String lat5;
    private String long5;

    private String picture6;
    private String lat6;
    private String long6;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getPicture1() {
        return picture1;
    }

    public void setPicture1(String picture1) {
        this.picture1 = picture1;
    }

    public String getPicture2() {
        return picture2;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public String getPicture3() {
        return picture3;
    }

    public void setPicture3(String picture3) {
        this.picture3 = picture3;
    }

    public String getPicture4() {
        return picture4;
    }

    public void setPicture4(String picture4) {
        this.picture4 = picture4;
    }

    public String getLat1() {
        return lat1;
    }

    public void setLat1(String lat1) {
        this.lat1 = lat1;
    }

    public String getLong1() {
        return long1;
    }

    public void setLong1(String long1) {
        this.long1 = long1;
    }

    public String getLat2() {
        return lat2;
    }

    public void setLat2(String lat2) {
        this.lat2 = lat2;
    }

    public String getLong2() {
        return long2;
    }

    public void setLong2(String long2) {
        this.long2 = long2;
    }

    public String getLat3() {
        return lat3;
    }

    public void setLat3(String lat3) {
        this.lat3 = lat3;
    }

    public String getLong3() {
        return long3;
    }

    public void setLong3(String long3) {
        this.long3 = long3;
    }

    public String getLat4() {
        return lat4;
    }

    public void setLat4(String lat4) {
        this.lat4 = lat4;
    }

    public String getLong4() {
        return long4;
    }

    public void setLong4(String long4) {
        this.long4 = long4;
    }

    public String getPicture5() {
        return picture5;
    }

    public void setPicture5(String picture5) {
        this.picture5 = picture5;
    }

    public String getLat5() {
        return lat5;
    }

    public void setLat5(String lat5) {
        this.lat5 = lat5;
    }

    public String getLong5() {
        return long5;
    }

    public void setLong5(String long5) {
        this.long5 = long5;
    }

    public String getPicture6() {
        return picture6;
    }

    public void setPicture6(String picture6) {
        this.picture6 = picture6;
    }

    public String getLat6() {
        return lat6;
    }

    public void setLat6(String lat6) {
        this.lat6 = lat6;
    }

    public String getLong6() {
        return long6;
    }

    public void setLong6(String long6) {
        this.long6 = long6;
    }
}
