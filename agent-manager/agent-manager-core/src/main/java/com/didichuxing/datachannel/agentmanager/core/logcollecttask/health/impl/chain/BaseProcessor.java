package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.chain.ProcessorChain;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
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
        LogCollectTaskHealthCheckContext logCollectTaskHealthCheckContext = (LogCollectTaskHealthCheckContext) context;
        /*
         * 执行该处理器逻辑
         */
        try {
            process(logCollectTaskHealthCheckContext);
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
    protected abstract void process(LogCollectTaskHealthCheckContext context);

    protected void setLogCollectTaskHealthInfo(
            LogCollectTaskHealthCheckContext context,
            LogCollectTaskHealthInspectionResultEnum logCollectTaskHealthInspectionResultEnum,
            String... logCollectTaskHealthInspectionDescriptionParameters
    ) {
        context.setLogCollectTaskHealthLevelEnum(logCollectTaskHealthInspectionResultEnum.getLogCollectTaskHealthLevelEnum());
        String logCollectTaskHealthDescription = String.format(
                logCollectTaskHealthInspectionResultEnum.getDescription(),
                logCollectTaskHealthInspectionDescriptionParameters
        );
        context.setLogCollectTaskHealthDescription(logCollectTaskHealthDescription);
        context.setLogCollectTaskHealthInspectionResultEnum(logCollectTaskHealthInspectionResultEnum);
    }

}
