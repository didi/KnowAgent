package com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MetadataFileDTO {

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "metadata元数据文件")
    private MultipartFile uploadFile;

    @ApiModelProperty(value = "文件MD5值")
    private String fileMd5;

    public CheckResult checkParameter() {
        if(StringUtils.isBlank(description)) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                            "描述信息不可为空"
            );
        }
        if(StringUtils.isBlank(fileMd5)) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "文件md5信息不可为空"
            );
        }
        if(null == uploadFile || uploadFile.isEmpty()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "待上传文件不可为空"
            );
        }
        return new CheckResult(true);
    }

}
