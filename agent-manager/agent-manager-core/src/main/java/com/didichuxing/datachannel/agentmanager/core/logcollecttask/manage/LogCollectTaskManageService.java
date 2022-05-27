package com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskPaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask.LogSliceRuleVO;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务管理服务接口
 */
public interface LogCollectTaskManageService {

    /**
     * 创建一个日志采集任务信息
     * @param logCollectTask 待添加日志采集任务对象
     * @param operator 操作人
     * @return 创建成功的日志采集任务对象id
     */
    Long createLogCollectTask(LogCollectTaskDO logCollectTask, String operator);

    /**
     * 删除一个日志采集任务对象
     * @param id 待删除日志采集任务id
     * @param operator 操作人
     */
    void deleteLogCollectTask(Long id, String operator);

    /**
     * 更新一个日志采集任务对象
     * @param logCollectTask 待更新日志采集任务对象
     * @param operator 操作人
     */
    void updateLogCollectTask(LogCollectTaskDO logCollectTask, String operator);

    /**
     * 根据主机id获取需要运行在该主机上所有的日志采集任务对象集
     * @param hostId 主机id
     * @return 需要运行在该主机上所有的日志采集任务对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByHostId(Long hostId);

    /**
     * 根据给定 id 获取对应 LogCollectTaskPO 对象
     * @param id 日志采集任务 id
     * @return 返回根据给定 id 获取对应 LogCollectTaskPO 对象
     */
    LogCollectTaskDO getById(Long id);

    Integer getRelatedAgentCount(Long id);

    /**
     * 启/停指定日志采集任务
     * @param logCollectTaskId 日志采集任务id
     * @param status 启/停状态
     * @param operator 操作人
     */
    void switchLogCollectTask(Long logCollectTaskId, Integer status, String operator);

    /**
     * 根据给定参数分页查询结果集
     * @param logCollectTaskPaginationQueryConditionDO 分页查询条件
     * @return 返回根据给定参数分页查询到的结果集
     */
    List<LogCollectTaskPaginationRecordDO> paginationQueryByConditon(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    /**
     * 根据给定参数查询满足条件的结果集总数量
     * @param logCollectTaskPaginationQueryConditionDO 查询条件
     * @return 返回根据给定参数查询到的满足条件的结果集总数量
     */
    Integer queryCountByCondition(LogCollectTaskPaginationQueryConditionDO logCollectTaskPaginationQueryConditionDO);

    /**
     * 获取系统中所有需要被检查健康度的日志采集任务集
     * @return 返回系统中所有需要被检查健康度的日志采集任务集
     */
    List<LogCollectTaskDO> getAllLogCollectTask2HealthCheck();

    /**
     * 获取需要运行给定主机上所有的日志采集任务对象集
     * @param hostDO 主机对象
     * @return 需要运行在该主机上所有的日志采集任务对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByHost(HostDO hostDO);

    /**
     * 获取serviceId对应Service对象关联的LogCollectTask对象集
     * @param serviceId Service对象id值
     * @return 返回获取serviceId对应Service对象关联的LogCollectTask对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByServiceId(Long serviceId);

    /**
     * 根据 agentId 获取对应 agent 所有待采集的日志采集任务集
     * @param agentId agent 对象 id 值
     * @return 根据 agentId 获取到的对应 agent 所有待采集的日志采集任务集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByAgentId(Long agentId);

    /**
     * @param agentHostName agent宿主机名
     * @return 根据 agentHostName 获取到的对应 agent 所有待采集的日志采集任务集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByAgentHostName(String agentHostName);

    /**
     * 获取给定kafkaClusterId对应KafkaCluster对象关联的LogCollectTaskDO对象集
     * @param kafkaClusterId KafkaCluster对象id值
     * @return 返回给定kafkaClusterId对应KafkaCluster对象关联的LogCollectTaskDO对象集
     */
    List<LogCollectTaskDO> getLogCollectTaskListByKafkaClusterId(Long kafkaClusterId);

    /**
     * @return 返回系统日志采集任务总数
     */
    Long countAll();

    /**
     * @return 返回系统全量日志采集任务 id 集
     */
    List<Long> getAllIds();

    /**
     * 校验 logcollecttask 是否未关联主机
     * @param logCollectTaskId logcollecttask 对象 id
     * @return true：logcollecttask 未关联任何主机 false：logcollecttask 存在关联主机
     */
    boolean checkNotRelateAnyHost(Long logCollectTaskId);

    /**
     * @param logCollectTaskHealthLevelCode 日志采集任务健康度对应 code（对应枚举类 LogCollectTaskHealthLevelEnum）
     * @return 返回系统中给定健康度的日志采集任务对象集
     */
    List<LogCollectTaskDO> getByHealthLevel(Integer logCollectTaskHealthLevelCode);

    /**
     * @return 返回系统全量日志采集任务
     */
    List<LogCollectTaskDO> getAll();

    /**
     * @return 返回系统配置的所有日期/时间格式
     */
    List<String> getDateTimeFormats();

    /**
     * @param content 日志样本
     * @param sliceDateTimeStringStartIndex 切片时间戳串开始位置索引
     * @param sliceDateTimeStringEndIndex 切片时间戳串结束位置索引
     * @return 返回根据给定日志样本与切片时间戳串开始、结束位置索引获取到的对应切片规则配置
     */
    LogSliceRuleVO getSliceRule(String content, Integer sliceDateTimeStringStartIndex, Integer sliceDateTimeStringEndIndex);

}
