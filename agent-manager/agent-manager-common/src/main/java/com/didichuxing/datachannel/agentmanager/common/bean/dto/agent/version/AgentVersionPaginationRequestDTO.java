package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentVersionPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "Agent安装包版本号", notes="")
    private List<String> agentVersionList;

    @ApiModelProperty(value = "Agent安装包名", notes="")
    private String agentPackageName;

    @ApiModelProperty(value = "Agent版本创建时间开始检索时间", notes="")
    private Long agentVersionCreateTimeStart;

    @ApiModelProperty(value = "Agent版本创建时间结束检索时间", notes="")
    private Long agentVersionCreateTimeEnd;

    @ApiModelProperty(value = "排序字段 可选version create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序排序", notes="")
    private Boolean asc;

    @ApiModelProperty(value = "版本描述信息", notes="")
    private String agentVersionDescription;

    public List<String> getAgentVersionList() {
        return agentVersionList;
    }

    public void setAgentVersionList(List<String> agentVersionList) {
        this.agentVersionList = agentVersionList;
    }

    public String getAgentPackageName() {
        return agentPackageName;
    }

    public void setAgentPackageName(String agentPackageName) {
        this.agentPackageName = agentPackageName;
    }

    public Long getAgentVersionCreateTimeStart() {
        return agentVersionCreateTimeStart;
    }

    public void setAgentVersionCreateTimeStart(Long agentVersionCreateTimeStart) {
        this.agentVersionCreateTimeStart = agentVersionCreateTimeStart;
    }

    public Long getAgentVersionCreateTimeEnd() {
        return agentVersionCreateTimeEnd;
    }

    public void setAgentVersionCreateTimeEnd(Long agentVersionCreateTimeEnd) {
        this.agentVersionCreateTimeEnd = agentVersionCreateTimeEnd;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public String getAgentVersionDescription() {
        return agentVersionDescription;
    }

    public void setAgentVersionDescription(String agentVersionDescription) {
        this.agentVersionDescription = agentVersionDescription;
    }
}
