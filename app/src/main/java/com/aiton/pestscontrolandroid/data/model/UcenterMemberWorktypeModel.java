package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;

public class UcenterMemberWorktypeModel implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "ID")
    private String id;

//    @ApiModelProperty(value = "业务类型")
    private String workType;

//    @ApiModelProperty(value = "用户ID")
    private String memberId;


//    @ApiModelProperty(value = "乐观锁")
    private Integer gmtVersion;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Boolean isDeleted;

//    @ApiModelProperty(value = "创建时间")
    private String gmtCreate;

//    @ApiModelProperty(value = "更新时间")
    private String gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Integer getGmtVersion() {
        return gmtVersion;
    }

    public void setGmtVersion(Integer gmtVersion) {
        this.gmtVersion = gmtVersion;
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
}
