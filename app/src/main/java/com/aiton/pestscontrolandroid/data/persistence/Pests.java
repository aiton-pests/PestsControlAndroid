package com.aiton.pestscontrolandroid.data.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Pests {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "device_id")
    private String deviceId;
    @ColumnInfo(name = "stime")
    private String stime;
    //经度
    @ColumnInfo(name = "longitude")
    private double longitude;
    //纬度
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "position_error")
    private String positionError;
    @ColumnInfo(name = "tree_walk")
    private String treeWalk;
    @ColumnInfo(name = "fell_pic")
    private String fellPic;
    @ColumnInfo(name = "stump_pic")
    private String stumpPic;
    @ColumnInfo(name = "finish_pic")
    private String finishPic;
    @ColumnInfo(name = "stown")
    private String town;
    @ColumnInfo(name = "village")
    private String village;
    @ColumnInfo(name = "soperator")
    private String operator;
    @ColumnInfo(name = "xbh")
    private String xb;
    @ColumnInfo(name = "dbh")
    private String db;
    @ColumnInfo(name = "qrcode")
    private String qrcode;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "bag_number")
    private String bagNumber;
    // 诱木；砍倒的树；诱捕器
    @ColumnInfo(name = "pests_type")
    private String pestsType;

    @ColumnInfo(name = "update_server")
    private boolean updateServer;

    @Override
    public String toString() {
        return "Pests{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", stime='" + stime + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", positionError='" + positionError + '\'' +
                ", treeWalk='" + treeWalk + '\'' +
                ", fellPic='" + fellPic + '\'' +
                ", stumpPic='" + stumpPic + '\'' +
                ", finishPic='" + finishPic + '\'' +
                ", town='" + town + '\'' +
                ", village='" + village + '\'' +
                ", operator='" + operator + '\'' +
                ", xb='" + xb + '\'' +
                ", db='" + db + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", userId='" + userId + '\'' +
                ", bagNumber='" + bagNumber + '\'' +
                ", pestsType='" + pestsType + '\'' +
                ", updateServer=" + updateServer +
                '}';
    }

    @Ignore
    public Pests() {
    }

    public Pests(int id, String deviceId, String stime, double longitude, double latitude, String positionError, String treeWalk, String fellPic, String stumpPic, String finishPic, String town, String village, String operator, String xb, String db, String qrcode, String userId, String bagNumber, String pestsType, boolean updateServer) {
        this.id = id;
        this.deviceId = deviceId;
        this.stime = stime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.positionError = positionError;
        this.treeWalk = treeWalk;
        this.fellPic = fellPic;
        this.stumpPic = stumpPic;
        this.finishPic = finishPic;
        this.town = town;
        this.village = village;
        this.operator = operator;
        this.xb = xb;
        this.db = db;
        this.qrcode = qrcode;
        this.userId = userId;
        this.bagNumber = bagNumber;
        this.pestsType = pestsType;
        this.updateServer = updateServer;
    }


    public boolean isUpdateServer() {
        return updateServer;
    }

    public void setUpdateServer(boolean updateServer) {
        this.updateServer = updateServer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPositionError() {
        return positionError;
    }

    public void setPositionError(String positionError) {
        this.positionError = positionError;
    }

    public String getTreeWalk() {
        return treeWalk;
    }

    public void setTreeWalk(String treeWalk) {
        this.treeWalk = treeWalk;
    }

    public String getFellPic() {
        return fellPic;
    }

    public void setFellPic(String fellPic) {
        this.fellPic = fellPic;
    }

    public String getStumpPic() {
        return stumpPic;
    }

    public void setStumpPic(String stumpPic) {
        this.stumpPic = stumpPic;
    }

    public String getFinishPic() {
        return finishPic;
    }

    public void setFinishPic(String finishPic) {
        this.finishPic = finishPic;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBagNumber() {
        return bagNumber;
    }

    public void setBagNumber(String bagNumber) {
        this.bagNumber = bagNumber;
    }

    public String getPestsType() {
        return pestsType;
    }

    public void setPestsType(String pestsType) {
        this.pestsType = pestsType;
    }
}
