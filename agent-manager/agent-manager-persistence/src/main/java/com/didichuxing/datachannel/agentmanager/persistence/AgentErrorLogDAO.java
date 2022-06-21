package com.didichuxing.datachannel.agentmanager.persistence;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AgentErrorLogDAO {

    /**
     * 插入给定指标数据
     * @param record 待插入指标数据
     * @return 非 0 表示插入成功
     */
    int insertSelective(ErrorLogPO record);

    /**
     * 删除给定时间 time 之前的 Agent 错误日志数据
     * @param time 删除时间右边界值（单位：毫秒）
     * @return 非 0 表示删除成功
     */
    int deleteBeforeTime(@Param("time") Long time);

    /**
     * 获取给定 hostName 的 Agent 在 给定时间范围内的错误日志集
     * @param hostName Agent 主机名
     * @param startTime 开始时间（单位：毫秒）
     * @param endTime 结束时间（单位：毫秒）
     * @return 返回获取到的给定 hostName 的 Agent 在 给定时间范围内的错误日志集
     */
    List<String> getErrorLogs(@Param("hostName") String hostName, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
