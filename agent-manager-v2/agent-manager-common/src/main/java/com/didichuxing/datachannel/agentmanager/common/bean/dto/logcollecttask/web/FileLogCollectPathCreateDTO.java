package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "文件类型路径采集配置", description = "")
public class FileLogCollectPathCreateDTO extends LogCollectPathCreateDTO {

    @ApiModelProperty(value = "文件名后缀匹配规则")
    private FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO;

    public void setFileNameSuffixMatchRuleDTO(FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO) {
        this.fileNameSuffixMatchRuleDTO = fileNameSuffixMatchRuleDTO;
    }

    public FileNameSuffixMatchRuleDTO getFileNameSuffixMatchRuleDTO() {
        return fileNameSuffixMatchRuleDTO;
    }

}
