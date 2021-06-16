package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "文件类型路径采集配置", description = "")
public class FileLogCollectPathUpdateDTO extends LogCollectPathUpdateDTO {

    @ApiModelProperty(value = "文件名后缀匹配规则")
    private FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO;

    @ApiModelProperty(value = "该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~ 当前时间间隔")
    private Long collectDelayThresholdMs;

    public void setFileNameSuffixMatchRuleDTO(FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO) {
        this.fileNameSuffixMatchRuleDTO = fileNameSuffixMatchRuleDTO;
    }

    public FileNameSuffixMatchRuleDTO getFileNameSuffixMatchRuleDTO() {
        return fileNameSuffixMatchRuleDTO;
    }

    public Long getCollectDelayThresholdMs() {
        return collectDelayThresholdMs;
    }

    public void setCollectDelayThresholdMs(Long collectDelayThresholdMs) {
        this.collectDelayThresholdMs = collectDelayThresholdMs;
    }

}
