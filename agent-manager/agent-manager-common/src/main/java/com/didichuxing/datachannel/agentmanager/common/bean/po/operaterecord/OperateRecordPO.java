package com.didichuxing.datachannel.agentmanager.common.bean.po.operaterecord;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.ModuleEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord.OperationEnum;
import java.util.Date;

/**
 * @author d06679
 * @date 2019/3/14
 */
@Data
public class OperateRecordPO extends BasePO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * @see ModuleEnum
     */
    private Integer moduleId;

    /**
     * @see OperationEnum
     */
    private Integer operateId;

    /**
     * 操作业务id String类型
     */
    private String  bizId;

    /**
     * 操作描述
     */
    private String  content;

    /**
     * 操作人  邮箱前缀
     */
    private String  operator;

    /**
     * 操作时间
     */
    private Date    operateTime;

    /**
     * 开始时间
     */
    private Date    beginTime;

    /**
     * 结束时间
     */
    private Date    endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
