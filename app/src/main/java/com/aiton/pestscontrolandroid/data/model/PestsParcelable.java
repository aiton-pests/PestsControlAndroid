package com.aiton.pestscontrolandroid.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.aiton.pestscontrolandroid.data.persistence.Pests;

import java.util.List;

public class PestsParcelable implements Parcelable {
//    @PrimaryKey(autoGenerate = true)
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
//    @ColumnInfo(name = "tree_walk")
    private String treeWalk;
//    @ColumnInfo(name = "fell_pic")
    private String fellPic;
//    @ColumnInfo(name = "stump_pic")
    private String stumpPic;
//    @ColumnInfo(name = "finish_pic")
    private String finishPic;
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
//    @ColumnInfo(name = "bag_number")
    private String bagNumber;
    // 诱木；砍倒的树；诱捕器
    private String pestsType;

    private boolean updateServer;

    private boolean isChecked;

    public PestsParcelable() {
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

    public boolean isUpdateServer() {
        return updateServer;
    }

    public void setUpdateServer(boolean updateServer) {
        this.updateServer = updateServer;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    protected PestsParcelable(Parcel in) {
        id = in.readInt();
        deviceId = in.readString();
        stime = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        positionError = in.readString();
        treeWalk = in.readString();
        fellPic = in.readString();
        stumpPic = in.readString();
        finishPic = in.readString();
        town = in.readString();
        village = in.readString();
        operator = in.readString();
        xb = in.readString();
        db = in.readString();
        qrcode = in.readString();
        codeInt = in.readString();
        userId = in.readString();
        bagNumber = in.readString();
        pestsType = in.readString();
        updateServer = in.readByte() != 0;
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
        dest.writeString(treeWalk);
        dest.writeString(fellPic);
        dest.writeString(stumpPic);
        dest.writeString(finishPic);
        dest.writeString(town);
        dest.writeString(village);
        dest.writeString(operator);
        dest.writeString(xb);
        dest.writeString(db);
        dest.writeString(qrcode);
        dest.writeString(codeInt);
        dest.writeString(userId);
        dest.writeString(bagNumber);
        dest.writeString(pestsType);
        dest.writeByte((byte) (updateServer ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PestsParcelable> CREATOR = new Creator<PestsParcelable>() {
        @Override
        public PestsParcelable createFromParcel(Parcel in) {
            return new PestsParcelable(in);
        }

        @Override
        public PestsParcelable[] newArray(int size) {
            return new PestsParcelable[size];
        }
    };
}
