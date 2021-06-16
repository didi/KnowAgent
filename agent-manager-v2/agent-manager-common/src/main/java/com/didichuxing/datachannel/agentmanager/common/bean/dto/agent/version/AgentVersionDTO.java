package com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentVersionDTO {

    @ApiModelProperty(value = "AgentVersion对象id 注：添加接口不可填，更新接口必填")
    private Long id;

    @ApiModelProperty(value = "Agent安装包版本号")
    private String agentVersion;

    @ApiModelProperty(value = "Agent安装包名")
    private String agentPackageName;

    @ApiModelProperty(value = "Agent安装包版本描述")
    private String agentVersionDescription;

    @ApiModelProperty(value = "上传Agent安装包文件")
    private MultipartFile uploadFile;

    @ApiModelProperty(value = "文件MD5值")
    private String fileMd5;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getAgentPackageName() {
        return agentPackageName;
    }

    public void setAgentPackageName(String agentPackageName) {
        this.agentPackageName = agentPackageName;
    }

    public String getAgentVersionDescription() {
        return agentVersionDescription;
    }

    public void setAgentVersionDescription(String agentVersionDescription) {
        this.agentVersionDescription = agentVersionDescription;
    }

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }
}
