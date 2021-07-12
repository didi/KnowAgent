package com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;

public class AgentVersionPO extends BasePO {

    /**
     * 采集端版本 id
     */
    private Long id;
    /**
     * 采集端安装包/配置文件名
     */
    private String fileName;
    /**
     * 采集端安装包/配置文件对应md5值
     */
    private String fileMd5;
    /**
     * 版本文件类型
     * 0：agent 安装压缩包
     * 1：agent 配置文件
     */
    private Integer fileType;
    /**
     * 版本文件描述信息
     */
    private String description;
    /**
     * 版本号
     */
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
