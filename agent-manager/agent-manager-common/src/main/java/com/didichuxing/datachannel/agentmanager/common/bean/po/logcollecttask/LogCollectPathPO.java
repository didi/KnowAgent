package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * 目录类型日志采集路径
 */
@Data
public class LogCollectPathPO extends BasePO {

    /**
     * 目录类型日志采集路径 id
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public String getPath() {
        return path;
    }
}