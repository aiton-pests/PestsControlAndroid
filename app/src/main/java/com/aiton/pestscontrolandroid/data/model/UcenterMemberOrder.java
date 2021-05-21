package com.aiton.pestscontrolandroid.data.model;


import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author testjava
 * @since 2020-03-09
 */
//@ApiModel(value="UcenterMember对象", description="会员表")
public class UcenterMemberOrder implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "会员id")
//    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

//    @ApiModelProperty(value = "微信openid")
    private String openid;

//    @ApiModelProperty(value = "手机号")
    private String mobile;

//    @ApiModelProperty(value = "密码")
    private String password;

//    @ApiModelProperty(value = "昵称")
    private String nickname;

//    @ApiModelProperty(value = "性别 1 女，2 男")
    private Integer sex;

//    @ApiModelProperty(value = "年龄")
    private Integer age;

//    @ApiModelProperty(value = "用户头像")
    private String avatar;

//    @ApiModelProperty(value = "用户签名")
    private String sign;

//    @ApiModelProperty(value = "是否禁用 1（true）已禁用，  0（false）未禁用")
    private Boolean isDisabled;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Boolean isDeleted;

//    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtCreate;

//    @ApiModelProperty(value = "更新时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtModified;

//    @ApiModelProperty(value = "默认项目，每个用户都会预设一个默认项目")
    private String projectId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
