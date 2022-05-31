package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.LogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.LogRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.YesOrNoEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskLimitPriorityLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskOldDataFilterTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.TimeUtils;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.manage.extension.LogCollectTaskManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class DefaultLogCollectTaskManageServiceExtensionImpl implements LogCollectTaskManageServiceExtension {

    @Override
    public LogCollectTaskPO logCollectTask2LogCollectTaskPO(LogCollectTaskDO logCollectTask) throws ServiceException {
        try {
            LogCollectTaskPO logCollectTaskPO = ConvertUtil.obj2Obj(logCollectTask, LogCollectTaskPO.class, "directoryLogCollectPathList", "fileLogCollectPathList", "serviceIdList");
            return logCollectTaskPO;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultLogCollectTaskManageServiceExtensionImpl||method=logCollectTask2LogCollectTaskPO||msg={%s}",
                            String.format("LogCollectTask对象{%s}转化为LogCollectTaskPO对象失败", JSON.toJSONString(logCollectTask))
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public CheckResult checkCreateParameterLogCollectTask(LogCollectTaskDO logCollectTaskDO) {
        if(CollectionUtils.isEmpty(logCollectTaskDO.getFileLogCollectPathList()) && CollectionUtils.isEmpty(logCollectTaskDO.getDirectoryLogCollectPathList())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "日志采集路径为空，日志采集任务必须具备目录型或文件型采集路径");
        }
        if(null == logCollectTaskDO.getLogCollectTaskType()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskType不可为空");
        }
        if(!LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType()) && !LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskType值非法，合法取值范围为[0,1]");
        }
        if(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            if (null == logCollectTaskDO.getCollectStartTimeBusiness() || logCollectTaskDO.getCollectStartTimeBusiness().equals(0L)) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "时间范围采集类型的日志采集任务对应 collectStartTimeBusiness 属性值不可为空 & 等于0");
            }
            if (null == logCollectTaskDO.getCollectEndTimeBusiness() && logCollectTaskDO.getCollectEndTimeBusiness() <= logCollectTaskDO.getCollectStartTimeBusiness()) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "时间范围采集类型的日志采集任务对应 collectStartTimeBusiness 属性值不可为空 & 大于 collectStartTimeBusiness 属性值");
            }
            /*if (null == logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs() || logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs().equals(0L)) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskExecuteTimeoutMs属性值不可为空 & 等于0");
            }*/
        }
        if(null == logCollectTaskDO.getOldDataFilterType()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "oldDataFilterType不可为空");
        }
        if(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            if(!logCollectTaskDO.getOldDataFilterType().equals(LogCollectTaskOldDataFilterTypeEnum.NO.getCode()) && (null == logCollectTaskDO.getCollectStartTimeBusiness() || logCollectTaskDO.getCollectStartTimeBusiness().equals(0))) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "附带历史数据过滤的流式日志采集任务 collectStartTimeBusiness 属性值不为空 & 等于0");
            }
        }
        if(CollectionUtils.isEmpty(logCollectTaskDO.getServiceIdList())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "serviceIdList属性值不可为空，请至少关联一个Service");
        }
        if(null == logCollectTaskDO.getKafkaClusterId() || logCollectTaskDO.getKafkaClusterId().equals(0L)) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "kafkaClusterId属性值不可为空 & 等于0");
        }
        if(StringUtils.isBlank(logCollectTaskDO.getSendTopic())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "sendTopic属性值不可为空");
        }
        if(null == logCollectTaskDO.getLimitPriority()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "limitPriority不可为空");
        }
        if(!LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode().equals(logCollectTaskDO.getLimitPriority()) && !LogCollectTaskLimitPriorityLevelEnum.MIDDLE.getCode().equals(logCollectTaskDO.getLimitPriority()) && !LogCollectTaskLimitPriorityLevelEnum.LOW.getCode().equals(logCollectTaskDO.getLimitPriority())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "limitPriority值非法，合法取值范围为[0,1,2]");
        }
        if(StringUtils.isBlank(logCollectTaskDO.getLogCollectTaskName())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskName属性值不可为空");
        }
        if(null == logCollectTaskDO.getCollectDelayThresholdMs()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "collectDelayThresholdMs属性值不可为空");
        }
        if(0 < logCollectTaskDO.getCollectDelayThresholdMs() && (2 * LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD) > logCollectTaskDO.getCollectDelayThresholdMs()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), String.format("collectDelayThresholdMs属性值最小值为", (2 * LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD)));
        }
        if(StringUtils.isBlank(logCollectTaskDO.getFileNameSuffixMatchRuleLogicJsonString())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "FileLogCollectPathDO.fileNameSuffixMatchRuleLogicJsonString属性值不可为空");
        }
        //校验文件型日志采集路径对象
        if(CollectionUtils.isNotEmpty(logCollectTaskDO.getFileLogCollectPathList())) {
            for (FileLogCollectPathDO fileLogCollectPathDO : logCollectTaskDO.getFileLogCollectPathList()) {
                if(StringUtils.isBlank(fileLogCollectPathDO.getPath())) {
                    return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "FileLogCollectPathDO.path属性值不可为空");
                }
            }
        }
        //TODO：校验目录型日志采集路径对象
        return new CheckResult(true);
    }

    @Override
    public LogCollectTaskDO logCollectTaskPO2LogCollectTaskDO(LogCollectTaskPO logCollectTaskPO) throws ServiceException {
        LogCollectTaskDO logCollectTask = null;
        try {
            logCollectTask = ConvertUtil.obj2Obj(logCollectTaskPO, LogCollectTaskDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultLogCollectTaskManageServiceExtensionImpl||method=logCollectTaskPO2LogCollectTaskDO||msg={%s}",
                            String.format("LogCollectTaskPO对象{%s}转化为LogCollectTask对象失败，原因为：%s", JSON.toJSONString(logCollectTaskPO), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == logCollectTask) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultLogCollectTaskManageServiceExtensionImpl||method=logCollectTaskPO2LogCollectTaskDO||msg={%s}",
                            String.format("LogCollectTaskPO对象{%s}转化为LogCollectTask对象失败", JSON.toJSONString(logCollectTaskPO))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return logCollectTask;
    }

    @Override
    public CheckResult checkUpdateParameterLogCollectTask(LogCollectTaskDO logCollectTaskDO) {
        if(null == logCollectTaskDO.getId() || logCollectTaskDO.getId().equals(0)) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "id不可为空 & 等于0");
        }
        if(CollectionUtils.isEmpty(logCollectTaskDO.getFileLogCollectPathList()) && CollectionUtils.isEmpty(logCollectTaskDO.getDirectoryLogCollectPathList())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "日志采集路径为空，日志采集任务必须具备目录型或文件型采集路径");
        }
        if(null == logCollectTaskDO.getLogCollectTaskType()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskType不可为空");
        }
        if(!LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType()) && !LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskType值非法，合法取值范围为[0,1]");
        }
        if(LogCollectTaskTypeEnum.TIME_SCOPE_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            if(null == logCollectTaskDO.getCollectStartTimeBusiness() || logCollectTaskDO.getCollectStartTimeBusiness().equals(0)) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "时间范围采集类型的日志采集任务对应 collectStartTimeBusiness 属性值不可为空 & 等于0");
            }
            if(null == logCollectTaskDO.getCollectEndTimeBusiness() && logCollectTaskDO.getCollectEndTimeBusiness().longValue() <= logCollectTaskDO.getCollectStartTimeBusiness().longValue()) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "时间范围采集类型的日志采集任务对应 collectStartTimeBusiness 属性值不可为空 & 大于 collectStartTimeBusiness 属性值");
            }
            /*if(null == logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs() || logCollectTaskDO.getLogCollectTaskExecuteTimeoutMs().equals(0)) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskExecuteTimeoutMs属性值不可为空 & 等于0");
            }*/
        }
        if(null == logCollectTaskDO.getOldDataFilterType()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "oldDataFilterType不可为空");
        }
        if(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode().equals(logCollectTaskDO.getLogCollectTaskType())) {
            if(!logCollectTaskDO.getOldDataFilterType().equals(LogCollectTaskOldDataFilterTypeEnum.NO) && (null == logCollectTaskDO.getCollectStartTimeBusiness() || logCollectTaskDO.getCollectStartTimeBusiness().equals(0))) {
                return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "附带历史数据过滤的流式日志采集任务 collectStartTimeBusiness 属性值不为空 & 等于0");
            }
        }
        if(CollectionUtils.isEmpty(logCollectTaskDO.getServiceIdList())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "serviceIdList属性值不可为空，请至少关联一个Service");
        }
        if(null == logCollectTaskDO.getKafkaClusterId() || logCollectTaskDO.getKafkaClusterId().equals(0)) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "kafkaClusterId属性值不可为空 & 等于0");
        }
        if(StringUtils.isBlank(logCollectTaskDO.getSendTopic())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "sendTopic属性值不可为空");
        }
        if(null == logCollectTaskDO.getLimitPriority()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "limitPriority不可为空");
        }
        if(!LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode().equals(logCollectTaskDO.getLimitPriority()) && !LogCollectTaskLimitPriorityLevelEnum.MIDDLE.getCode().equals(logCollectTaskDO.getLimitPriority()) && !LogCollectTaskLimitPriorityLevelEnum.LOW.getCode().equals(logCollectTaskDO.getLimitPriority())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "limitPriority值非法，合法取值范围为[0,1,2]");
        }
        if(StringUtils.isBlank(logCollectTaskDO.getLogCollectTaskName())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "logCollectTaskName属性值不可为空");
        }
        if(null == logCollectTaskDO.getCollectDelayThresholdMs()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "collectDelayThresholdMs属性值不可为空");
        }
        if(0 < logCollectTaskDO.getCollectDelayThresholdMs() && (2 * LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD) > logCollectTaskDO.getCollectDelayThresholdMs()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), String.format("collectDelayThresholdMs属性值最小值为", (2 * LogCollectTaskHealthCheckConstant.ALIVE_CHECK_LASTEST_MS_THRESHOLD)));
        }
        if(StringUtils.isBlank(logCollectTaskDO.getFileNameSuffixMatchRuleLogicJsonString())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "FileLogCollectPathDO.fileNameSuffixMatchRuleLogicJsonString属性值不可为空");
        }
        //校验文件型日志采集路径对象
        if(CollectionUtils.isNotEmpty(logCollectTaskDO.getFileLogCollectPathList())) {
            for (FileLogCollectPathDO fileLogCollectPathDO : logCollectTaskDO.getFileLogCollectPathList()) {

                if(StringUtils.isBlank(fileLogCollectPathDO.getPath())) {
                    return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "FileLogCollectPathDO.path属性值不可为空");
                }
            }
        }
        //TODO：校验目录型日志采集路径对象
        return new CheckResult(true);
    }

    @Override
    public LogCollectTaskDO updateLogCollectTask(LogCollectTaskDO source, LogCollectTaskDO target) throws ServiceException {
        if(StringUtils.isNotBlank(target.getAdvancedConfigurationJsonString())) {
            source.setAdvancedConfigurationJsonString(target.getAdvancedConfigurationJsonString());
        }
        if(null != target.getCollectEndTimeBusiness()) {
            source.setCollectEndTimeBusiness(target.getCollectEndTimeBusiness());
        }
        if(null != target.getCollectStartTimeBusiness()) {
            source.setCollectStartTimeBusiness(target.getCollectStartTimeBusiness());
        }
        if(StringUtils.isNotBlank(target.getHostFilterRuleLogicJsonString())) {
            source.setHostFilterRuleLogicJsonString(target.getHostFilterRuleLogicJsonString());
        }
        if(null != target.getKafkaClusterId()) {
            source.setKafkaClusterId(target.getKafkaClusterId());
        }
        if(null != target.getLimitPriority()) {
            source.setLimitPriority(target.getLimitPriority());
        }
        if(null != target.getLogCollectTaskExecuteTimeoutMs()) {
            source.setLogCollectTaskExecuteTimeoutMs(target.getLogCollectTaskExecuteTimeoutMs());
        }
        if(StringUtils.isNotBlank(target.getLogCollectTaskName())) {
            source.setLogCollectTaskName(target.getLogCollectTaskName());
        }
        if(StringUtils.isNotBlank(target.getLogCollectTaskRemark())) {
            source.setLogCollectTaskRemark(target.getLogCollectTaskRemark());
        }
        if(null != target.getLogCollectTaskType()) {
            source.setLogCollectTaskType(target.getLogCollectTaskType());
        }
        if(null != target.getOldDataFilterType()) {
            source.setOldDataFilterType(target.getOldDataFilterType());
        }
        if(StringUtils.isNotBlank(target.getSendTopic())) {
            source.setSendTopic(target.getSendTopic());
        }
        if(StringUtils.isNotBlank(target.getLogContentFilterRuleLogicJsonString())) {
            source.setLogContentFilterRuleLogicJsonString(target.getLogContentFilterRuleLogicJsonString());
        }
        if(null != target.getLogCollectTaskFinishTime()) {
            source.setLogCollectTaskFinishTime(target.getLogCollectTaskFinishTime());
        }
        if (!StringUtils.isBlank(target.getLogContentSliceRuleLogicJsonString()) && !"null".equals(target.getLogContentSliceRuleLogicJsonString())) {
            source.setLogContentSliceRuleLogicJsonString(target.getLogContentSliceRuleLogicJsonString());
        }
        if (!StringUtils.isBlank(target.getFileNameSuffixMatchRuleLogicJsonString()) && !"null".equals(target.getFileNameSuffixMatchRuleLogicJsonString())) {
            source.setFileNameSuffixMatchRuleLogicJsonString(target.getFileNameSuffixMatchRuleLogicJsonString());
        }
        if (!StringUtils.isBlank(target.getKafkaProducerConfiguration())) {
            source.setKafkaProducerConfiguration(target.getKafkaProducerConfiguration());
        }
        return source;
    }

    @Override
    public List<LogCollectTaskDO> logCollectTaskPOList2LogCollectTaskDOList(List<LogCollectTaskPO> logCollectTaskPOList) {
        return ConvertUtil.list2List(logCollectTaskPOList, LogCollectTaskDO.class);
    }

    @Override
    public List<LogRecordVO> slice(String content, String sliceTimestampFormat, String sliceTimestampPrefixString, Integer sliceTimestampPrefixStringIndex) {
        List<LogRecordVO> logRecordVOList = new ArrayList<>();
        String[] lines = content.split(System.lineSeparator());
        String record = "";
        YesOrNoEnum valid = YesOrNoEnum.YES;
        for (String line : lines) {
            String timeString = getTimeStringFormLineByIndex(line, sliceTimestampFormat, sliceTimestampPrefixString, sliceTimestampPrefixStringIndex);
            Long longTimeStamp = TimeUtils.getLongTimeStamp(timeString, sliceTimestampFormat);
            if(null != longTimeStamp && longTimeStamp > 0L) {//时间戳解析
                if(StringUtils.isNotBlank(record)) {//上一行存在
                    logRecordVOList.add(new LogRecordVO(record, valid.getCode()));
                    record = line;
                    valid = YesOrNoEnum.YES;
                } else {//上一行不存在
                    record = line;
                }
            } else {//时间戳 不 存 在
                if(StringUtils.isNotBlank(record)) {//上一行存在
                    record += System.lineSeparator() + line;
                } else {//上一行不存在
                    record += System.lineSeparator() + line;
                    valid = YesOrNoEnum.NO;
                }
            }
        }
        if(StringUtils.isNotBlank(record)) {
            logRecordVOList.add(new LogRecordVO(record, valid.getCode()));
        }
        return logRecordVOList;
    }

    /**
     * 根据index进行切割，从而得到对应的timeStamp
     */
    private String getTimeStringFormLineByIndex(String line, String timeFormat, String startFlag, Integer startFlagIndex) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        int timeFormatLength = timeFormat.length();
        String timeString;
        if (StringUtils.isEmpty(startFlag) && startFlagIndex == 0) {
            if (line.length() < timeFormatLength) {
                return null;
            } else {
                timeString = line.substring(0, timeFormatLength);
            }
        } else {
            try {
                // startFlag不为空
                // 对字符创进行切割，得到最终的字符创
                if (!StringUtils.isEmpty(startFlag)) {
                    boolean isVaild = true;
                    int currentIndex = 0;
                    for (int i = 0; i < startFlagIndex + 1; i++) {
                        int startSubIndex = line.indexOf(startFlag, currentIndex);
                        if (startSubIndex >= 0) {
                            currentIndex = startSubIndex + startFlag.length();
                        } else {
                            // 此时说明line中不存在startFlag
                            isVaild = false;
                            break;
                        }
                    }
                    if (isVaild) {
                        if (line.length() < timeFormatLength) {
                            return null;
                        } else {
                            timeString = line.substring(currentIndex, currentIndex + timeFormatLength);
                        }
                    } else {
                        // 此时说明line中不存在startFlag
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return timeString;
    }

}
