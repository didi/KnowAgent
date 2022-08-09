package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MetaDataFilePaginationQueryConditionDO extends PaginationRequestDTO {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件 md5 信息
     */
    private String fileMd5;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 上传时间起始检索时间
     */
    private Date createTimeStart;

    /**
     * 上传时间结束检索时间
     */
    private Date createTimeEnd;

    /**
     * 从第几行开始
     */
    private Integer limitFrom;

    /**
     * 获取满足条件的 top limitSize 结果集行数
     */
    private Integer limitSize;

    /**
     * 排序字段 可选 create_time
     */
    private String sortColumn;

    /**
     * 是否升序排序
     */
    private Boolean asc;

}
