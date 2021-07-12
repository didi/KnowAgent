package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "日志采集路径配置", description = "")
public class LogCollectPathCreateDTO {

    @ApiModelProperty(value = "待采集路径")
    private String path;

    @ApiModelProperty(value = "日志切片规则")
    private LogSliceRuleDTO logSliceRuleDTO;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LogSliceRuleDTO getLogSliceRuleDTO() {
        return logSliceRuleDTO;
    }

    public void setLogSliceRuleDTO(LogSliceRuleDTO logSliceRuleDTO) {
        this.logSliceRuleDTO = logSliceRuleDTO;
    }
}
