package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.metrics;

import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.bean.SystemMetrics;
import lombok.Data;

/**
 * @author william.
 * agent 上报指标定义
 */
@Data
public class AgentMetrics {

    /**
     * agent 业务级指标
     */
    private AgentBusinessMetrics agentBusinessMetrics;
    /**
     * 系统级指标
     */
    private SystemMetrics systemMetrics;
    /**
     * 当前进程级指标
     */
    private ProcMetrics procMetrics;

}
