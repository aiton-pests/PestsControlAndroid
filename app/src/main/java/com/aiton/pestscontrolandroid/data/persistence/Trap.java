package com.aiton.pestscontrolandroid.data.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Trap implements Serializable {
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
    @ColumnInfo(name = "code_int")
    private String codeInt;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "update_server")
    private boolean updateServer;
    @ColumnInfo(name = "scount")
    private Integer scount;
    @ColumnInfo(name = "pic1")
    private String pic1;
    @ColumnInfo(name = "pic2")
    private String pic2;
    @ColumnInfo(name = "remark")
    private String remark;
    @ColumnInfo(name = "lureReplaced")
    private Integer lureReplaced;
    @ColumnInfo(name = "projectId")
    private String projectId;
    @ColumnInfo(name = "isChecked")
    private boolean isChecked;
    @Ignore
    public Trap() {
    }

    public Trap(int id, String deviceId, String stime, double longitude, double latitude, String positionError, String town, String village, String operator, String xb, String db, String qrcode, String codeInt, String userId, boolean updateServer, Integer scount, String pic1, String pic2, String remark, Integer lureReplaced, String projectId, boolean isChecked) {
        this.id = id;
        this.deviceId = deviceId;
        this.stime = stime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.positionError = positionError;
        this.town = town;
        this.village = village;
        this.operator = operator;
        this.xb = xb;
        this.db = db;
        this.qrcode = qrcode;
        this.codeInt = codeInt;
        this.userId = userId;
        this.updateServer = updateServer;
        this.scount = scount;
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.remark = remark;
        this.lureReplaced = lureReplaced;
        this.projectId = projectId;
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "Trap{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", stime='" + stime + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", positionError='" + positionError + '\'' +
                ", town='" + town + '\'' +
                ", village='" + village + '\'' +
                ", operator='" + operator + '\'' +
                ", xb='" + xb + '\'' +
                ", db='" + db + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", codeInt='" + codeInt + '\'' +
                ", userId='" + userId + '\'' +
                ", updateServer=" + updateServer +
                ", scount=" + scount +
                ", pic1='" + pic1 + '\'' +
                ", pic2='" + pic2 + '\'' +
                ", remark='" + remark + '\'' +
                ", lureReplaced=" + lureReplaced +
                ", projectId='" + projectId + '\'' +
                ", isChecked=" + isChecked +
                '}';
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

    public String getCodeInt() {
        return codeInt;
    }

    public void setCodeInt(String codeInt) {
        this.codeInt = codeInt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUpdateServer() {
        return updateServer;
    }

    public void setUpdateServer(boolean updateServer) {
        this.updateServer = updateServer;
    }

    public Integer getScount() {
        return scount;
    }

    public void setScount(Integer scount) {
        this.scount = scount;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLureReplaced() {
        return lureReplaced;
    }

    public void setLureReplaced(Integer lureReplaced) {
        this.lureReplaced = lureReplaced;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
