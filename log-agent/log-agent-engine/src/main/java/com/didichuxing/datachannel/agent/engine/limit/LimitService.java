package com.didichuxing.datachannel.agent.engine.limit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.common.configs.v2.LimitConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelLimitConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.limit.tune.TuneNode;
import com.didichuxing.datachannel.agent.engine.limit.tune.thread.AllocateQpsThread;
import com.didichuxing.datachannel.agent.engine.limit.tune.thread.LimitCpuThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 全局限流
 */
public enum LimitService {
    LIMITER;

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitService.class);
    private ConcurrentHashMap<String, LimitNode> limitNodeMap = new ConcurrentHashMap<>();
    private LimitConfig config;

    private AllocateQpsThread allocateQpsThread;
    private LimitCpuThread limitCpuThread;

    private Thread qpsThread;

    private Thread cpuThread;

    private boolean limited = false;

    private boolean inited = false;

    public void init(LimitConfig config) throws Exception {
        LOGGER.info("begin to init Limiter");
        this.config = config;
        this.allocateQpsThread = new AllocateQpsThread(this);
        this.limitCpuThread = new LimitCpuThread(this, config.getStartThreshold());

        this.qpsThread = new Thread(allocateQpsThread);
        this.qpsThread.setName("allocate-tps-thread");
        this.qpsThread.setDaemon(true);

        this.cpuThread = new Thread(limitCpuThread);
        this.cpuThread.setName("limit-cpu-thread");
        this.cpuThread.setDaemon(true);
        inited = true;
        LOGGER.info("success to init Limiter!");
    }

    public void start() throws Exception {
        LOGGER.info("begin to start Limiter!");
        if (!inited) {
            LOGGER.warn("failed to start Limiter for not inited!");
            return;
        }
        this.qpsThread.start();
        this.cpuThread.start();
        LOGGER.info("success to start Limiter!");
    }

    public void stop() {
        LOGGER.info("begin to stop Limiter");
        if (!inited) {
            LOGGER.warn("failed to stop Limiter for not inited!");
            return;
        }
        allocateQpsThread.stop();
        limitCpuThread.stop();
        LOGGER.info("success to stop Limiter!");
    }

    // TODO 配置变更
    public void onChange(LimitConfig config) {
        LOGGER.info("begin to change limitConfig.config is " + config);
        if (!inited) {
            LOGGER.warn("failed to change Limiter for not inited!");
            return;
        }
        this.config = config;
        this.limitCpuThread.reset(config.getStartThreshold());
        LOGGER.info("success to change limitConfig!");
    }

    /**
     * 单节点配置变更
     *
     * @param abstractTask
     * @param config
     */
    public void onChangeNode(AbstractTask abstractTask, ModelLimitConfig config) {
        LOGGER.info("begin to change limit config. abstractTask is " + abstractTask.getUniqueKey() + ", config is "
                + config);
        String name = abstractTask.getUniqueKey();

        if (!limitNodeMap.containsKey(name)) {
            register(abstractTask, config);
            return;
        }

        limitNodeMap.get(name).reset(config);
        LOGGER.info("success to change limit!");
    }

    public boolean register(AbstractTask abstractTask, ModelLimitConfig config) {
        LOGGER.info("begin to register limit. abstractTask is " + abstractTask.getUniqueKey());
        String name = abstractTask.getUniqueKey();

        if (limitNodeMap.containsKey(name)) {
            LOGGER.info("fileNode limit is already exists. ignore!");
            return false;
        }

        limitNodeMap.putIfAbsent(name, new LimitNode(name, this, config, abstractTask.getTaskLimter()));
        LOGGER.info("success to register limit!");
        return true;
    }

    public boolean register(AbstractTask abstractTask, LimitNode limitNode) {
        LOGGER.info("begin to register limit. abstractTask is " + abstractTask.getUniqueKey());
        String name = abstractTask.getUniqueKey();

        if (limitNodeMap.containsKey(name)) {
            LOGGER.info("fileNode limit is already exists. ignore!");
            return false;
        }

        limitNodeMap.putIfAbsent(name, limitNode);
        LOGGER.info("success to register limit!");
        return true;
    }

    public boolean unregister(AbstractTask abstractTask) {
        LOGGER.info("begin to unregister limit. abstractTask is " + abstractTask.getUniqueKey());
        String name = abstractTask.getUniqueKey();
        boolean result = false;
        if (limitNodeMap.containsKey(name)) {
            limitNodeMap.remove(name);
            result = true;
        }
        LOGGER.info("success to unregister limit");
        return result;
    }

    public double limit(AbstractTask abstractTask, int bytes) throws Exception {
        String name = abstractTask.getUniqueKey();

        LimitNode limitNode;
        limitNode = limitNodeMap.get(name);
        if (limitNode == null) {
            throw new Exception("limiter not exist, name:" + name);
        }

        double spent = limitNode.limit(bytes);
        if (spent > 0.0d) {
            limited = true;
        }
        return spent;
    }

    public boolean isLimited() {
        return limited;
    }

    public void clear() {
        limited = false;
    }

    public List<TuneNode> getTuneNodes() {
        List<TuneNode> tuneNodes = new ArrayList<>();
        for (String key : limitNodeMap.keySet()) {
            tuneNodes.add(limitNodeMap.get(key).getTuneNode());
        }
        return tuneNodes;
    }

    public LimitConfig getConfig() {
        return config;
    }

    public long getAllQps() {
        return limitCpuThread.getAllQps();
    }

    public float getCpuThreshold() {
        return config.getCpuThreshold();
    }

    public long getMinThreshold() {
        return config.getMinThreshold();
    }
}
