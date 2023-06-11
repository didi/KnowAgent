package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.operationtask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentOperationTaskExecuteDetailListVO {

    @ApiModelProperty(value = "总数", notes="")
    private Integer total;

    @ApiModelProperty(value = "成功数", notes="")
    private Integer successful;

    @ApiModelProperty(value = "失败数", notes="")
    private Integer failed;

    @ApiModelProperty(value = "执行中", notes="")
    private Integer execution;

    @ApiModelProperty(value = "待执行", notes="")
    private Integer pending;

    @ApiModelProperty(value = "Agent操作任务执行明细集", notes="")
    private List<AgentOperationTaskExecuteDetailVO> agentOperationTaskExecuteDetailVOList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSuccessful() {
        return successful;
    }

    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getExecution() {
        return execution;
    }

    public void setExecution(Integer execution) {
        this.execution = execution;
    }

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public List<AgentOperationTaskExecuteDetailVO> getAgentOperationTaskExecuteDetailVOList() {
        return agentOperationTaskExecuteDetailVOList;
    }

    public void setAgentOperationTaskExecuteDetailVOList(List<AgentOperationTaskExecuteDetailVO> agentOperationTaskExecuteDetailVOList) {
        this.agentOperationTaskExecuteDetailVOList = agentOperationTaskExecuteDetailVOList;
    }
}
