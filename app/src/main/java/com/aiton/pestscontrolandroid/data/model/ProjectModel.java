package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;

//@ApiModel(value="Project项目对象", description="项目对象")
public class ProjectModel  implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Integer getDeskCount() {
        return deskCount;
    }

    public void setDeskCount(Integer deskCount) {
        this.deskCount = deskCount;
    }

    public Integer getEntities() {
        return entities;
    }

    public void setEntities(Integer entities) {
        this.entities = entities;
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

    //    @ApiModelProperty(value = "编号")
    private String id;

//    @ApiModelProperty(value = "名称")
    private String name;

//    @ApiModelProperty(value = "记录数")
    private Integer recordCount;

//    @ApiModelProperty(value = "文件个数")
    private Integer fileCount;

//    @ApiModelProperty(value = "文件存储")
    private Integer deskCount;

//    @ApiModelProperty(value = "支持实体")
    private Integer entities;

//    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    private Integer isDeleted;

//    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtCreate;

//    @ApiModelProperty(value = "更新时间")
//    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String gmtModified;

//    @ApiModelProperty(value = "每个项目只能有一个项目管理员")
    private String userId;


}
