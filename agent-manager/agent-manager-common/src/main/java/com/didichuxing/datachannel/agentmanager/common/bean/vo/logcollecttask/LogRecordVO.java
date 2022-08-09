package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "表示一条日志记录", description = "")
public class LogRecordVO {

    @ApiModelProperty(value = "日志内容")
    private String record;

    @ApiModelProperty(value = "是否为一条正确日志记录 正确表示具备合法日期-格式串 0:否 1：是")
    private Integer valid;

    public LogRecordVO(String record, Integer valid) {
        this.record = record;
        this.valid = valid;
    }

    public LogRecordVO() {
    }

}
