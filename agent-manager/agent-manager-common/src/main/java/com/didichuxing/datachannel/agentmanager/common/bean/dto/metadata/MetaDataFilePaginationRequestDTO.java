package com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MetaDataFilePaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "文件名", notes="")
    private String fileName;

    @ApiModelProperty(value = "文件 md5 信息", notes="")
    private String fileMd5;

    @ApiModelProperty(value = "描述信息", notes="")
    private String description;

    @ApiModelProperty(value = "上传时间起始检索时间", notes="")
    private Long uploadTimeStart;

    @ApiModelProperty(value = "上传时间结束检索时间", notes="")
    private Long uploadTimeEnd;

    @ApiModelProperty(value = "排序字段 可选 create_time ", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序排序", notes="")
    private Boolean asc;

}
