package com.didichuxing.datachannel.agent.engine.limit.tune.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.limit.tune.TuneNode;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocateQpsThread implements Runnable {
    private static final Logger LOGGER          = LoggerFactory.getLogger(AllocateQpsThread.class);

    private static final int    MAX_LEVEL_VALUE = 10;

    private boolean             isStop          = false;
    private LimitService        limiter;

    private String              period          = "tps.period";
    private long                interval;

    public AllocateQpsThread(LimitService limiter) throws Exception {
        this.limiter = limiter;
        this.interval = Long.parseLong(CommonUtils.readSettings().get(period));
    }

    @Override
    public void run() {
        while (!isStop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                List<TuneNode> tuneNodes = limiter.getTuneNodes();
                allocateQps(tuneNodes, limiter.getAllQps());
            } catch (Throwable t) {
                LogGather.recordErrorLog("Limister-Allocate-Tps-Thread", "error", t);
            }
        }
    }

    public void stop() {
        this.isStop = true;
    }

    public void allocateQps(List<TuneNode> nodes, long allTps) throws Exception {
        if (nodes == null || nodes.size() == 0) {
            return;
        }

        // 获得各个node的临时变量
        for (TuneNode tuneNode : nodes) {
            tuneNode.reset();
        }

        // 如果allTps不能满足最小的需求，则直接按照最小需求限流
        long minNeed = getMinNeedQps(nodes);
        if (minNeed >= allTps) {
            for (TuneNode tuneNode : nodes) {
                tuneNode.setThreshold(tuneNode.getMinThreshold());
            }
            return;
        }

        // 将node按照level分层
        Map<Integer, List<TuneNode>> nodeMap = new HashMap<>();
        for (TuneNode node : nodes) {
            if (!nodeMap.containsKey(node.getLevel())) {
                nodeMap.put(node.getLevel(), new ArrayList<TuneNode>());
            }

            nodeMap.get(node.getLevel()).add(node);
        }

        // 对各层level 计算限流值
        allocateQps(nodeMap, MAX_LEVEL_VALUE, allTps);
    }

    // 对各个level分配tps
    // level越低级别越高
    private long allocateQps(Map<Integer, List<TuneNode>> nodeMap, int level, long qps)
                                                                                       throws Exception {
        if (level < 0) {
            return qps;
        }

        if (!nodeMap.containsKey(level)) {
            return allocateQps(nodeMap, level - 1, qps);
        }

        long minNeed = getMinNeedQps(nodeMap.get(level));
        if (qps < minNeed) {
            // 外层算法保证
            throw new Exception("too large");
        }

        // 替当前level保留最低qps
        qps -= minNeed;

        // 先给高level分配qps
        qps = allocateQps(nodeMap, level - 1, qps);

        // 恢复保留的qps
        qps += minNeed;

        // 给当前level分配tps
        return allocateTpsForOneLevel(nodeMap.get(level), qps);
    }

    // 给同一个level的node分配tps，tps是当前能够分配的流量
    private long allocateTpsForOneLevel(List<TuneNode> nodes, long allQps) {
        long needQps = getNeedTps(nodes);
        long minQps = getMinNeedQps(nodes);

        // qps足够分配
        if (allQps >= needQps) {
            for (TuneNode tuneNode : nodes) {
                tuneNode.setThreshold(tuneNode.getNeedThreshold());
            }
            return allQps - needQps;
        }

        // allQps < needQps，则按照比例分配
        allQps -= minQps;
        long allocTps = 0;
        for (TuneNode node : nodes) {
            long t = (long) (allQps * ((float) (node.getNeedThreshold() - node.getMinThreshold()) / (float) (needQps - minQps)));
            node.setThreshold(node.getMinThreshold() + t);
            allocTps += t;
        }

        allQps -= allocTps;
        return allQps;
    }

    private long getMinNeedQps(List<TuneNode> tuneNodes) {
        long minNeed = 0;
        for (TuneNode tuneNode : tuneNodes) {
            minNeed += tuneNode.getMinThreshold();
        }
        return minNeed;
    }

    private long getNeedTps(List<TuneNode> tuneNodes) {
        long need = 0;
        for (TuneNode node : tuneNodes) {
            need += node.getNeedThreshold();
        }

        return need;
    }
}
