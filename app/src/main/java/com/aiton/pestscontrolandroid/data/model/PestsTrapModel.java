package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;

//@ApiModel(value="PestsTrap对象", description="诱捕器业务数据表")
public class PestsTrapModel implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }

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
    private Integer scount;

//    @ApiModelProperty(value = "第一张照片")
    private String pic1;

//    @ApiModelProperty(value = "第二张照片")
    private String pic2;

//    @ApiModelProperty(value = "备注")
    private String remark;

//    @ApiModelProperty(value = "是否更换诱芯  1（true）已删除， 0（false）未删除")
    private Integer lureReplaced;

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

//    @ApiModelProperty(value = "APP存储的ID")
    private Integer appId;

//    @ApiModelProperty(value = "是否同步到App进行抽查标志  1（true）已删除， 0（false）未删除")
    private Integer isChecked;

}
