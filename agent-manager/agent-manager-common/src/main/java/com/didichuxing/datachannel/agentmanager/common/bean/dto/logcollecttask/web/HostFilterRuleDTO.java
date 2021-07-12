package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "HostFilterRuleDTO", description = "主机过滤规则")
public class HostFilterRuleDTO {

    @ApiModelProperty(value = "是否需要主机过滤规则 0：否 1：是")
    private Integer needHostFilterRule;

    @ApiModelProperty(value = "主机筛选命中sql，白名单")
    private String filterSQL;

    @ApiModelProperty(value = "主机名命中列表，白名单")
    private List<String> hostNames;

    public void setNeedHostFilterRule(Integer needHostFilterRule) {
        this.needHostFilterRule = needHostFilterRule;
    }

    public void setFilterSQL(String filterSQL) {
        this.filterSQL = filterSQL;
    }

    public void setHostNames(List<String> hostNames) {
        this.hostNames = hostNames;
    }

    public Integer getNeedHostFilterRule() {
        return needHostFilterRule;
    }

    public String getFilterSQL() {
        return filterSQL;
    }

    public List<String> getHostNames() {
        return hostNames;
    }
}
