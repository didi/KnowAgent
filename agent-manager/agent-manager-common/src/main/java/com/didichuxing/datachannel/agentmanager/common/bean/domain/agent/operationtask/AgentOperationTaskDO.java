package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 采集端操作任务
 */
@Data
public class AgentOperationTaskDO extends BaseDO {

    /**
     * 采集端操作任务唯一标识
     */
    private Long id;
    /**
     * agent 操作任务名
     */
    private String taskName;
    /**
     * 任务类型 0：部署 1：升级
     */
    private Integer taskType;
    /**
     * 任务状态：30：运行中 100：完成
     */
    private Integer taskStatus;
    /**
     * 任务涉及主机数量
     */
    private Integer hostsNumber;
    /**
     * 待安装主机列表
     */
    private List<Long> hostIdList;
    /**
     * 安装任务时：无须传入
     * 卸载任务时：无须传入
     * 升级任务时：表示agent升级前agent对应agent_version id
     */
    private Long sourceAgentVersionId;
    /**
     * 安装任务时：表示待安装 agent 对应 agent version id
     * 卸载任务时：无须传入
     * 升级任务时：表示agent升级后agent对应agent_version id
     */
    private Long targetAgentVersionId;
    /**
     * 外部对应 agent 执行任务 id，如夜莺系统的 agent 任务 id
     */
    private Long externalAgentTaskId;
    /**
     * 任务开始执行时间
     */
    private Date taskStartTime;
    /**
     * 任务执行结束时间
     */
    private Date taskEndTime;
    /**
     * 待卸载/升级agent列表
     */
    private List<Long> agentIdList;

    public List<Long> getAgentIdList() {
        return agentIdList;
    }

    public void setAgentIdList(List<Long> agentIdList) {
        this.agentIdList = agentIdList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getHostsNumber() {
        return hostsNumber;
    }

    public void setHostsNumber(Integer hostsNumber) {
        this.hostsNumber = hostsNumber;
    }

    public List<Long> getHostIdList() {
        return hostIdList;
    }

    public void setHostIdList(List<Long> hostIdList) {
        this.hostIdList = hostIdList;
    }

    public Long getSourceAgentVersionId() {
        return sourceAgentVersionId;
    }

    public void setSourceAgentVersionId(Long sourceAgentVersionId) {
        this.sourceAgentVersionId = sourceAgentVersionId;
    }

    public Long getTargetAgentVersionId() {
        return targetAgentVersionId;
    }

    public void setTargetAgentVersionId(Long targetAgentVersionId) {
        this.targetAgentVersionId = targetAgentVersionId;
    }

    public Long getExternalAgentTaskId() {
        return externalAgentTaskId;
    }

    public void setExternalAgentTaskId(Long externalAgentTaskId) {
        this.externalAgentTaskId = externalAgentTaskId;
    }

    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

}
