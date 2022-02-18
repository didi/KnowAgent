package com.didichuxing.datachannel.agent.common.configs.v2.component;

import java.util.Date;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;

/**
 * @description: 基本配置
 * @author: huangjw
 * @Date: 19/7/1 14:41
 */
public class CommonConfig implements Cloneable {

    /**
     * 采集任务ID
     */
    private Long    modelId;

    /**
     * 采集任务名称
     */
    private String  modelName;

    /**
     * 任务类型,1:正常任务 2:定时任务
     */
    private Integer modelType  = LogConfigConstants.COLLECT_TYPE_PERIODICITY;

    /**
     * 编码类型
     */
    private String  encodeType = "UTF-8";

    /**
     * 日志采集开始时刻
     */
    private Date    startTime;

    /**
     * 日志采集停止时刻
     */
    private Date    endTime;

    /**
     * 采集任务版本号
     */
    private Integer version    = 0;

    /**
     * 优先级
     */
    private Integer priority   = 0;

    /*
     * 是否停止
     */
    private boolean isStop     = false;

    /**
     * 服务名集
     */
    private String  serviceNames;

    public String getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    @Override
    public String toString() {
        return "CommonConfig{" + "modelId=" + modelId + ", modelName='" + modelName + '\''
               + ", modelType=" + modelType + ", encodeType='" + encodeType + '\'' + ", startTime="
               + startTime + ", endTime=" + endTime + ", version=" + version + ", priority="
               + priority + ", isStop=" + isStop + '}';
    }
}
