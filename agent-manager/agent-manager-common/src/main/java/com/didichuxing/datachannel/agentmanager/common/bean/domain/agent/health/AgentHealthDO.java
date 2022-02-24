package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.health;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;

public class AgentHealthDO extends BaseDO {

    /**
     * 主键 id
     */
    private Long id;
    /**
     * 关联Agent对象id值
     */
    private Long agentId;
    /**
     *  agent健康等级
     */
    private Integer agentHealthLevel;
    /**
     *  agent健康描述信息
     */
    private String agentHealthDescription;
    /**
     *  近一次“错误日志存在健康检查”为健康时的时间点
     */
    private Long lastestErrorLogsExistsCheckHealthyTime;
    /**
     *  agent启动时间
     */
    private Long agentStartupTime;
    /**
     *  agent上一次启动时间
     */
    private Long agentStartupTimeLastTime;
    /**
     * agent 巡检结果类型
     */
    private Integer agentHealthInspectionResultType;

    public Integer getAgentHealthInspectionResultType() {
        return agentHealthInspectionResultType;
    }

    public void setAgentHealthInspectionResultType(Integer agentHealthInspectionResultType) {
        this.agentHealthInspectionResultType = agentHealthInspectionResultType;
    }

    public Long getAgentStartupTime() {
        return agentStartupTime;
    }

    public void setAgentStartupTime(Long agentStartupTime) {
        this.agentStartupTime = agentStartupTime;
    }

    public Long getAgentStartupTimeLastTime() {
        return agentStartupTimeLastTime;
    }

    public void setAgentStartupTimeLastTime(Long agentStartupTimeLastTime) {
        this.agentStartupTimeLastTime = agentStartupTimeLastTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getAgentHealthLevel() {
        return agentHealthLevel;
    }

    public void setAgentHealthLevel(Integer agentHealthLevel) {
        this.agentHealthLevel = agentHealthLevel;
    }

    public String getAgentHealthDescription() {
        return agentHealthDescription;
    }

    public void setAgentHealthDescription(String agentHealthDescription) {
        this.agentHealthDescription = agentHealthDescription == null ? null : agentHealthDescription.trim();
    }

    public Long getLastestErrorLogsExistsCheckHealthyTime() {
        return lastestErrorLogsExistsCheckHealthyTime;
    }

    public void setLastestErrorLogsExistsCheckHealthyTime(Long lastestErrorLogsExistsCheckHealthyTime) {
        this.lastestErrorLogsExistsCheckHealthyTime = lastestErrorLogsExistsCheckHealthyTime;
    }

}