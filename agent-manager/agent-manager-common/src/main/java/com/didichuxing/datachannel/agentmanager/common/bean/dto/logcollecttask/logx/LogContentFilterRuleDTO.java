package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "LogContentFilterRuleDTO", description = "日志过滤内容规则")
public class LogContentFilterRuleDTO {

    @ApiModelProperty(value = "是否需要对日志内容进行过滤 0：否 1：是")
    private Integer needLogContentFilter;//TODO：

    @ApiModelProperty(value = "日志内容过滤类型 0：包含 1：不包含，needLogContentFilter为1时必填")
    private Integer logContentFilterType;//TODO：

    @ApiModelProperty(value = "日志内容过滤表达式，needLogContentFilter为1时必填")
    private String logContentFilterExpression;//TODO：

    public void setNeedLogContentFilter(Integer needLogContentFilter) {
        this.needLogContentFilter = needLogContentFilter;
    }

    public void setLogContentFilterType(Integer logContentFilterType) {
        this.logContentFilterType = logContentFilterType;
    }

    public void setLogContentFilterExpression(String logContentFilterExpression) {
        this.logContentFilterExpression = logContentFilterExpression;
    }

    public Integer getNeedLogContentFilter() {
        return needLogContentFilter;
    }

    public Integer getLogContentFilterType() {
        return logContentFilterType;
    }

    public String getLogContentFilterExpression() {
        return logContentFilterExpression;
    }
}
