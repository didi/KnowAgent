package com.didichuxing.datachannel.agentmanager.common.bean.vo.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MetaDataFilePaginationRecordVO {

    @ApiModelProperty(value = "id", notes="")
    private Long id;

    @ApiModelProperty(value = "文件名", notes="")
    private String fileName;

    @ApiModelProperty(value = "文件 md5 信息", notes="")
    private String fileMd5;

    @ApiModelProperty(value = "描述信息", notes="")
    private String description;

    @ApiModelProperty(value = "上传时间", notes="")
    private Long uploadTime;

}
