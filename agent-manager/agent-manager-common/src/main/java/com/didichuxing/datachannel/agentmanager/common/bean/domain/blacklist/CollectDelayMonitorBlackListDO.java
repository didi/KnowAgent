package com.didichuxing.datachannel.agentmanager.common.bean.domain.blacklist;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

/**
 * @author huqidong
 * @date 2020-09-21
 * 采集延迟检查黑名单信息
 */
@Data
public class CollectDelayMonitorBlackListDO extends BaseDO {

    /**
     * 采集延迟检查黑名单信息唯一标识
     */
    private Long id;
    /**
     * 采集延迟检查黑名单类型：
     * 0：表示主机
     * 1：表示采集任务
     * 2：表示主机 + 采集任务
     */
    private Integer collectDelayMonitorBlackListType;
    /**
     * 主机名
     */
    private String hostName;
    /**
     * 对应日志采集任务id
     */
    private Long logCollectorTaskId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCollectDelayMonitorBlackListType() {
        return collectDelayMonitorBlackListType;
    }

    public void setCollectDelayMonitorBlackListType(Integer collectDelayMonitorBlackListType) {
        this.collectDelayMonitorBlackListType = collectDelayMonitorBlackListType;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getLogCollectorTaskId() {
        return logCollectorTaskId;
    }

    public void setLogCollectorTaskId(Long logCollectorTaskId) {
        this.logCollectorTaskId = logCollectorTaskId;
    }
}