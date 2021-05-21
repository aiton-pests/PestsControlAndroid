package com.aiton.pestscontrolandroid.data.model;



import java.io.Serializable;
import java.util.Date;

//@ApiModel(value="Qrcode对象", description="二维码")
public class QrcodeModel  implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "ID")
    private String id;

//    @ApiModelProperty(value = "二维码编号")
    private String codeNumber;

//    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String stime;

//    @ApiModelProperty(value = "实体二维码 URL")
    private String codeUrl;

//    @ApiModelProperty(value = "用户ID")
    private String userId;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Integer isDeleted;

//    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtCreate;

//    @ApiModelProperty(value = "更新时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtModified;

//    @ApiModelProperty(value = "二维编码")
    private Integer codeInt;

//    @ApiModelProperty(value = "业务类型，目前只有诱捕器、其它业务类型")
    private String stype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Integer getCodeInt() {
        return codeInt;
    }

    public void setCodeInt(Integer codeInt) {
        this.codeInt = codeInt;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }
}
