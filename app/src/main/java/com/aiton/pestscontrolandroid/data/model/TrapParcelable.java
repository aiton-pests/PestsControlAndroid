package com.aiton.pestscontrolandroid.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;

public class TrapParcelable  implements Parcelable {
    private int id;
//    @ColumnInfo(name = "device_id")
    private String deviceId;
//    @ColumnInfo(name = "stime")
    private String stime;
    //经度
//    @ColumnInfo(name = "longitude")
    private double longitude;
    //纬度
//    @ColumnInfo(name = "latitude")
    private double latitude;
//    @ColumnInfo(name = "position_error")
    private String positionError;
//    @ColumnInfo(name = "stown")
    private String town;
//    @ColumnInfo(name = "village")
    private String village;
//    @ColumnInfo(name = "soperator")
    private String operator;
//    @ColumnInfo(name = "xbh")
    private String xb;
//    @ColumnInfo(name = "dbh")
    private String db;
//    @ColumnInfo(name = "qrcode")
    private String qrcode;
//    @ColumnInfo(name = "code_int")
    private String codeInt;
//    @ColumnInfo(name = "user_id")
    private String userId;
//    @ColumnInfo(name = "update_server")
    private boolean updateServer;
//    @ColumnInfo(name = "scount")
    private Integer scount;
//    @ColumnInfo(name = "pic1")
    private String pic1;
//    @ColumnInfo(name = "pic2")
    private String pic2;
//    @ColumnInfo(name = "remark")
    private String remark;
//    @ColumnInfo(name = "lureReplaced")
    private Integer lureReplaced;
//    @ColumnInfo(name = "projectId")
    private String projectId;
//    @ColumnInfo(name = "isChecked")
    private boolean isChecked;

    public TrapParcelable() {
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

    protected TrapParcelable(Parcel in) {
        id = in.readInt();
        deviceId = in.readString();
        stime = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        positionError = in.readString();
        town = in.readString();
        village = in.readString();
        operator = in.readString();
        xb = in.readString();
        db = in.readString();
        qrcode = in.readString();
        codeInt = in.readString();
        userId = in.readString();
        updateServer = in.readByte() != 0;
        if (in.readByte() == 0) {
            scount = null;
        } else {
            scount = in.readInt();
        }
        pic1 = in.readString();
        pic2 = in.readString();
        remark = in.readString();
        if (in.readByte() == 0) {
            lureReplaced = null;
        } else {
            lureReplaced = in.readInt();
        }
        projectId = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(deviceId);
        dest.writeString(stime);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(positionError);
        dest.writeString(town);
        dest.writeString(village);
        dest.writeString(operator);
        dest.writeString(xb);
        dest.writeString(db);
        dest.writeString(qrcode);
        dest.writeString(codeInt);
        dest.writeString(userId);
        dest.writeByte((byte) (updateServer ? 1 : 0));
        if (scount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(scount);
        }
        dest.writeString(pic1);
        dest.writeString(pic2);
        dest.writeString(remark);
        if (lureReplaced == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(lureReplaced);
        }
        dest.writeString(projectId);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrapParcelable> CREATOR = new Creator<TrapParcelable>() {
        @Override
        public TrapParcelable createFromParcel(Parcel in) {
            return new TrapParcelable(in);
        }

        @Override
        public TrapParcelable[] newArray(int size) {
            return new TrapParcelable[size];
        }
    };
}
