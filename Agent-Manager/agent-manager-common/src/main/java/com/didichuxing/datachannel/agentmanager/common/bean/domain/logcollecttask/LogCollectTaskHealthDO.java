package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度信息
 */
@Data
public class LogCollectTaskHealthDO extends BaseDO {

    /**
     * 日志采集任务健康度信息唯一标识
     */
    private Long id;
    /**
     * 对应日志采集任务id
     */
    private Long logCollectTaskId;
    /**
     * 采集任务健康等级
     * 0：绿色 表示：采集任务很健康，对业务没有任何影响，且运行该采集任务的 AgentPO 也健康
     * 1：黄色 表示：采集任务存在风险，该采集任务有对应错误日志输出
     * 2：红色 表示：采集任务不健康，对业务有影响，该采集任务需要做采集延迟监控但乱序输出，或该采集任务需要做采集延迟监控但延迟时间超过指定阈值、该采集任务对应 kafka 集群信息不存在 待维护
     */
    private Integer logCollectTaskHealthLevel;
    /**
     * 日志采集任务健康描述信息
     */
    private String logCollectTaskHealthDescription;
    /**
     * 日志采集任务中各日志主文件对应的最近采集完整性时间，json 字符串形式，[{“logfilePath1”: 1602323589023 }, …]
     */
    private String lastestCollectDqualityTimePerLogFilePathJsonString;
    /**
     *  各 logpath 对应近一次“日志异常截断健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private String lastestAbnormalTruncationCheckHealthyTimePerLogFilePath;
    /**
     *  各 logpath 对应近一次“日志切片健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private String lastestLogSliceCheckHealthyTimePerLogFilePath;
    /**
     *  各 logpath 对应近一次“文件乱序健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private String lastestFileDisorderCheckHealthyTimePerLogFilePath;
    /**
     *  各 logpath 对应近一次“文件路径是否存在健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private String lastestFilePathExistsCheckHealthyTimePerLogFilePath;

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

    public Integer getLogCollectTaskHealthLevel() {
        return logCollectTaskHealthLevel;
    }

    public void setLogCollectTaskHealthLevel(Integer logCollectTaskHealthLevel) {
        this.logCollectTaskHealthLevel = logCollectTaskHealthLevel;
    }

    public String getLogCollectTaskHealthDescription() {
        return logCollectTaskHealthDescription;
    }

    public void setLogCollectTaskHealthDescription(String logCollectTaskHealthDescription) {
        this.logCollectTaskHealthDescription = logCollectTaskHealthDescription;
    }

    public String getLastestCollectDqualityTimePerLogFilePathJsonString() {
        return lastestCollectDqualityTimePerLogFilePathJsonString;
    }

    public void setLastestCollectDqualityTimePerLogFilePathJsonString(String lastestCollectDqualityTimePerLogFilePathJsonString) {
        this.lastestCollectDqualityTimePerLogFilePathJsonString = lastestCollectDqualityTimePerLogFilePathJsonString;
    }

    public String getLastestAbnormalTruncationCheckHealthyTimePerLogFilePath() {
        return lastestAbnormalTruncationCheckHealthyTimePerLogFilePath;
    }

    public void setLastestAbnormalTruncationCheckHealthyTimePerLogFilePath(String lastestAbnormalTruncationCheckHealthyTimePerLogFilePath) {
        this.lastestAbnormalTruncationCheckHealthyTimePerLogFilePath = lastestAbnormalTruncationCheckHealthyTimePerLogFilePath;
    }

    public String getLastestLogSliceCheckHealthyTimePerLogFilePath() {
        return lastestLogSliceCheckHealthyTimePerLogFilePath;
    }

    public void setLastestLogSliceCheckHealthyTimePerLogFilePath(String lastestLogSliceCheckHealthyTimePerLogFilePath) {
        this.lastestLogSliceCheckHealthyTimePerLogFilePath = lastestLogSliceCheckHealthyTimePerLogFilePath;
    }

    public String getLastestFileDisorderCheckHealthyTimePerLogFilePath() {
        return lastestFileDisorderCheckHealthyTimePerLogFilePath;
    }

    public void setLastestFileDisorderCheckHealthyTimePerLogFilePath(String lastestFileDisorderCheckHealthyTimePerLogFilePath) {
        this.lastestFileDisorderCheckHealthyTimePerLogFilePath = lastestFileDisorderCheckHealthyTimePerLogFilePath;
    }

    public String getLastestFilePathExistsCheckHealthyTimePerLogFilePath() {
        return lastestFilePathExistsCheckHealthyTimePerLogFilePath;
    }

    public void setLastestFilePathExistsCheckHealthyTimePerLogFilePath(String lastestFilePathExistsCheckHealthyTimePerLogFilePath) {
        this.lastestFilePathExistsCheckHealthyTimePerLogFilePath = lastestFilePathExistsCheckHealthyTimePerLogFilePath;
    }

}