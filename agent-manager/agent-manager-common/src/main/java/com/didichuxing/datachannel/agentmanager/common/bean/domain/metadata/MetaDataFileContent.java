package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "元数据内容")
@Data
public class MetaDataFileContent {

    /**
     * 主机信息 表
     */
    private List<List<Object>> hostTable;

    /**
     * 应用信息 表
     */
    private List<List<Object>> applicationTable;

}
