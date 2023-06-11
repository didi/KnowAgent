package com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.context.AgentHealthCheckContext;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author william.
 */
public abstract class BaseProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);

    @Override
    public void process(Context context, ProcessorChain chain) {
        AgentHealthCheckContext agentHealthCheckContext = (AgentHealthCheckContext) context;
        /*
         * 执行该处理器逻辑
         */
        try {
            process(agentHealthCheckContext);
        } catch (Exception ex) {
            LOGGER.error(
                    String.format("class=BaseProcessor|method=process|errMsg=%s", ex.getMessage()),
                    ex
            );
        }
        /*
         * 执行下一个处理器对应处理逻辑
         */
        chain.process(context, chain);
    }

    /**
     * 执行该处理器逻辑
     * @param context 执行器上下文对象
     */
    protected abstract void process(AgentHealthCheckContext context);

    /**
     * 设置agent健康度检查结果
     * @param agentHealthInspectionResultEnum 日志采集任务健康度巡检结果枚举
     * @param context agent 健康度巡检上下文对象
     */
    protected void setAgentHealthCheckResult(
            AgentHealthInspectionResultEnum agentHealthInspectionResultEnum,
            AgentHealthCheckContext context,
            String... agentHealthInspectionDescriptionParameters
    ) {
        String agentHealthDescription = String.format(
                agentHealthInspectionResultEnum.getDescription(),
                agentHealthInspectionDescriptionParameters
        );
        context.setAgentHealthLevelEnum(agentHealthInspectionResultEnum.getAgentHealthLevel());
        context.setAgentHealthDescription(agentHealthDescription);
        context.setAgentHealthInspectionResultEnum(agentHealthInspectionResultEnum);
    }

}
