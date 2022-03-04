package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LogCollectTaskPaginationRecordDO extends BaseDO {

    /**
     * 日志采集任务 id
     */
    private Long logCollectTaskId;
    /**
     * 日志采集任务名
     */
    private String logCollectTaskName;
    /**
     * 采集任务类型 0：常规流式采集 1：按指定时间范围采集
     */
    private Integer logCollectTaskType;
    /**
     * 日志采集任务关联服务集
     */
    private List<ServiceDO> relationServiceList;
    /**
     * 日志采集任务关联接收端集群
     */
    private ReceiverDO relationReceiverDO;
    /**
     * 采集任务健康等级
     * 0：绿色 表示：采集任务很健康，对业务没有任何影响，且运行该采集任务的 AgentPO 也健康
     * 1：黄色 表示：采集任务存在风险，该采集任务有对应错误日志输出
     * 2：红色 表示：采集任务不健康，对业务有影响，该采集任务需要做采集延迟监控但乱序输出，或该采集任务需要做采集延迟监控但延迟时间超过指定阈值、该采集任务对应 kafka 集群信息不存在 待维护
     */
    private Integer logCollectTaskHealthLevel;
    /**
     * 日志采集任务执行结束时间
     */
    private Date logCollectTaskFinishTime;
    /**
     * 日志采集任务对应数据流 topic
     */
    private String sendTopic;
    /**
     * 日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 "按指定时间范围采集" 类型）
     */
    private Integer logCollectTaskStatus;
    /**
     * kafka 集群 id
     */
    private Long kafkaClusterId;

    /**
     * 日志采集任务健康描述信息
     */
    private String logCollectTaskHealthDescription;

    /**
     * 日志采集任务巡检结果类型
     */
    private Integer logCollectTaskHealthInspectionResultType;

    public Long getKafkaClusterId() {
        return kafkaClusterId;
    }

    public void setKafkaClusterId(Long kafkaClusterId) {
        this.kafkaClusterId = kafkaClusterId;
    }

    public Integer getLogCollectTaskHealthInspectionResultType() {
        return logCollectTaskHealthInspectionResultType;
    }

    public void setLogCollectTaskHealthInspectionResultType(Integer logCollectTaskHealthInspectionResultType) {
        this.logCollectTaskHealthInspectionResultType = logCollectTaskHealthInspectionResultType;
    }

    public String getLogCollectTaskHealthDescription() {
        return logCollectTaskHealthDescription;
    }

    public void setLogCollectTaskHealthDescription(String logCollectTaskHealthDescription) {
        this.logCollectTaskHealthDescription = logCollectTaskHealthDescription;
    }

    public Integer getLogCollectTaskStatus() {
        return logCollectTaskStatus;
    }

    public void setLogCollectTaskStatus(Integer logCollectTaskStatus) {
        this.logCollectTaskStatus = logCollectTaskStatus;
    }

    public String getSendTopic() {
        return sendTopic;
    }

    public void setSendTopic(String sendTopic) {
        this.sendTopic = sendTopic;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public Integer getLogCollectTaskType() {
        return logCollectTaskType;
    }

    public void setLogCollectTaskType(Integer logCollectTaskType) {
        this.logCollectTaskType = logCollectTaskType;
    }

    public List<ServiceDO> getRelationServiceList() {
        return relationServiceList;
    }

    public void setRelationServiceList(List<ServiceDO> relationServiceList) {
        this.relationServiceList = relationServiceList;
    }

    public ReceiverDO getRelationReceiverDO() {
        return relationReceiverDO;
    }

    public void setRelationReceiverDO(ReceiverDO relationReceiverDO) {
        this.relationReceiverDO = relationReceiverDO;
    }

    public Integer getLogCollectTaskHealthLevel() {
        return logCollectTaskHealthLevel;
    }

    public void setLogCollectTaskHealthLevel(Integer logCollectTaskHealthLevel) {
        this.logCollectTaskHealthLevel = logCollectTaskHealthLevel;
    }

    public Date getLogCollectTaskFinishTime() {
        return logCollectTaskFinishTime;
    }

    public void setLogCollectTaskFinishTime(Date logCollectTaskFinishTime) {
        this.logCollectTaskFinishTime = logCollectTaskFinishTime;
    }

}
