package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;
import java.util.Date;

//@ApiModel(value="TrafficControl对象", description="交通工程业务表")
public class TrafficControlModel implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "采集编号")
    private String id;

//    @ApiModelProperty(value = "采集设备")
    private String deviceId;

//    @ApiModelProperty(value = "采集时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String stime;

//    @ApiModelProperty(value = "经度")
    private Double longitude;

//    @ApiModelProperty(value = "纬度")
    private Double latitude;

//    @ApiModelProperty(value = "定位误差（米）")
    private String positionError;

//    @ApiModelProperty(value = "事前照片")
    private String pic1;

//    @ApiModelProperty(value = "事中照片")
    private String pic2;

//    @ApiModelProperty(value = "事后照片")
    private String pic3;

//    @ApiModelProperty(value = "地址")
    private String address;

//    @ApiModelProperty(value = "二维码")
    private String qrcode;

//    @ApiModelProperty(value = "项目编号")
    private String projectId;

//    @ApiModelProperty(value = "设备类型，1、信号机；2、信号灯；3、杆件；4、摄像机；5、车检器")
    private String stype;

//    @ApiModelProperty(value = "用户ID")
    private String userId;

//    @ApiModelProperty(value = "APP存储的ID")
    private Integer appId;

//    @ApiModelProperty(value = "备注")
    private String remark;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Integer isDeleted;

//    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

//    @ApiModelProperty(value = "更新时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtModified;

//    @ApiModelProperty(value = "记录状态，1、事前；2、事中；3、事后")
    private String status;


//    @ApiModelProperty(value = "是否同步到App进行抽查标志  1（true）已删除， 0（false）未删除")
    private Integer isChecked;

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

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }
}
