package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集路径
 */
@Data
public class LogCollectPathDO extends BaseDO {

    /**
     * 文件类型日志采集路径 id
     */
    private Long id;
    /**
     * 对应日志采集任务id
     */
    private Long logCollectTaskId;
    /**
     * 待采集路径
     */
    private String path;
    /**
     * 容器场景下，容器路径对应在宿主机路径
     */
    private String realPath;

    /**
     * 字符集
     */
    private String charset;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}

