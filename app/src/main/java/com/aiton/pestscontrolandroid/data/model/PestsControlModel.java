package com.aiton.pestscontrolandroid.data.model;


import java.io.Serializable;
import java.util.Date;

public class PestsControlModel  implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getPestsType() {
        return pestsType;
    }

    public void setPestsType(String pestsType) {
        this.pestsType = pestsType;
    }

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "采集编号")
    private String id;

//    @ApiModelProperty(value = "采集设备")
    private String deviceId;

//    @ApiModelProperty(value = "采集时间")
    private String stime;

//    @ApiModelProperty(value = "经度")
    private Double longitude;

//    @ApiModelProperty(value = "纬度")
    private Double latitude;

//    @ApiModelProperty(value = "定位误差（米）")
    private String positionError;

//    @ApiModelProperty(value = "树径")
    private String treeWalk;

//    @ApiModelProperty(value = "砍倒照片")
    private String fellPic;

//    @ApiModelProperty(value = "树桩照片")
    private String stumpPic;

//    @ApiModelProperty(value = "处理好照片")
    private String finishPic;

//    @ApiModelProperty(value = "镇")
    private String town;

//    @ApiModelProperty(value = "村")
    private String village;

//    @ApiModelProperty(value = "作业人")
    private String operator;

//    @ApiModelProperty(value = "小班")
    private String xb;

//    @ApiModelProperty(value = "大班")
    private String db;

//    @ApiModelProperty(value = "二维码")
    private String qrcode;

//    @ApiModelProperty(value = "项目编号")
    private String projectId;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Integer isDeleted;

//    @ApiModelProperty(value = "创建时间")
    private String gmtCreate;

//    @ApiModelProperty(value = "更新时间")
    private String gmtModified;

//    @ApiModelProperty(value = "用户ID")
    private String userId;

//    @ApiModelProperty(value = "袋数")
    private String bagNumber;

//    @ApiModelProperty(value = "APP存储的ID")
    private Integer appId;

//    @ApiModelProperty(value = "防治业务类型，包括  诱木   、砍倒的树  、诱捕器   ")
    private String pestsType;

}
