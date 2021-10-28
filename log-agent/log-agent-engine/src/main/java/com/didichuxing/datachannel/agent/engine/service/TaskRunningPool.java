package com.didichuxing.datachannel.agent.engine.service;

import java.util.concurrent.*;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description: 任务运行线程池
 * @author: huangjw
 * @Date: 19/7/3 14:23
 */
public class TaskRunningPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunningPool.class.getName());

    /**
     * 文件采集池
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(
            24,
            1000,
            1000L,
            TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>(),
            new DefaultThreadFactory("task-Executor"));

    private static ExecutorService tempExecutorService = new ThreadPoolExecutor(2, 2, 1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new DefaultThreadFactory(
                    "temp-task-Executor"));

    /**
     * @param task
     */
    public static void submit(AbstractTask task) {
        if (task == null) {
            LOGGER.warn("task is null.ignore!");
            return;
        }
        LOGGER.info("submit task to pool.task's id is " + task.getUniqueKey());
        if (task.getModelConfig().getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            tempExecutorService.submit(task);
        } else {
            executorService.submit(task);
        }
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static ExecutorService getTempExecutorService() {
        return tempExecutorService;
    }
}
