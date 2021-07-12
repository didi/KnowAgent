package com.didichuxing.datachannel.agentmanager.common.bean.vo.operaterecord;

import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author d06679
 * @date 2019/3/14
 */
@Data
public class OperateRecordVO{

    /**
     * 主键
     */
    @ApiModelProperty("记录ID")
    private Integer id;

    /**
     * @see ModuleEnum
     */
    @ApiModelProperty("模块ID")
    private Integer moduleId;

    @ApiModelProperty("模块")
    private String  module;

    /**
     * @see OperationEnum
     */
    @ApiModelProperty("操作ID")
    private Integer operateId;

    @ApiModelProperty("操作")
    private String  operate;

    /**
     * 操作业务id String类型
     */
    @ApiModelProperty("业务ID")
    private String  bizId;

    /**
     * 操作描述
     */
    @ApiModelProperty("操作内容")
    private String  content;

    /**
     * 操作人  邮箱前缀
     */
    @ApiModelProperty("操作人")
    private String  operator;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private Date    operateTime;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    protected Date createTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    protected Date updateTime;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public String getModule() {
        return module;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public String getOperate() {
        return operate;
    }

    public String getBizId() {
        return bizId;
    }

    public String getContent() {
        return content;
    }

    public String getOperator() {
        return operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
