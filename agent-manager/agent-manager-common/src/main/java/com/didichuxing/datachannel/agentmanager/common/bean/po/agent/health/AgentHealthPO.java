package com.didichuxing.datachannel.agentmanager.common.bean.po.agent.health;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;

public class AgentHealthPO extends BasePO {

    private Long id;

    private Long agentId;

    private Integer agentHealthLevel;

    private String agentHealthDescription;

    private Long lastestErrorLogsExistsCheckHealthyTime;

    private Long agentStartupTime;

    private Long agentStartupTimeLastTime;

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