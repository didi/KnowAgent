package com.didichuxing.datachannel.agent.engine.limit.tune.thread;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitCpuThread implements Runnable {

    private static final Logger LOGGER     = LoggerFactory
                                               .getLogger(LimitCpuThread.class.getName());
    private static final float  FACTOR     = 0.1f;
    private static final float  TPS_FACTOR = 0.2f;
    // 最大10G/s
    private static final long   maxQPS     = 10 * 1024 * 1024 * 1024L;

    private boolean             isStop     = false;
    private LimitService        limiter;

    private long                allQps;                                                   // 当前整体的限制阀值
    private double              currentCpuUsage;                                          // 当前cpu利用率

    private String              period     = "cpu.period";
    private long                interval   = 30 * 1000;

    public LimitCpuThread(LimitService limiter, long qps) throws Exception {
        this.limiter = limiter;
        this.allQps = qps;
        this.interval = Long.parseLong(CommonUtils.readSettings().get(period));
    }

    @Override
    public void run() {
        while (!isStop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                LogGather.recordErrorLog("LimitCpuThread error", "sleep error", e);
            }

            try {
                // 1. 获得cpu阀值
                float threshold = limiter.getCpuThreshold();

                // 2. 获得cpu利用率
                double cpuUsage = GlobalProperties.getProcessMetricsService()
                    .getCurrentProcCpuUtil();

                // 3. 根据cpu利用率调整qps阀值
                if (cpuUsage > (threshold * (FACTOR + 1))) {
                    allQps = (long) (limiter.getAllQps() * 0.5);
                }

                if (cpuUsage < (threshold * (1 - FACTOR))) {
                    if (limiter.isLimited()) {
                        // 有线程触发限流，提示tps限制
                        allQps = (long) (limiter.getAllQps() * (1 + TPS_FACTOR));
                        limiter.clear();
                    }
                }

                if (allQps > maxQPS) {
                    allQps = maxQPS;
                }

                if (allQps < limiter.getMinThreshold()) {
                    allQps = limiter.getMinThreshold();
                }

                // 4. 记录cpu耗时
                this.currentCpuUsage = (double) (Math.round(cpuUsage * 100)) / 100;
            } catch (Throwable t) {
                LOGGER.error(JSON.toJSONString(t));
                LOGGER.error(JSON.toJSONString(t.getStackTrace()));
                LOGGER.error(
                    String.format("Limiter.cpuThread process cpuUsage error, message={%s}",
                        t.getMessage()), t);
            }
        }
    }

    public void stop() {
        this.isStop = true;
    }

    public long getAllQps() {
        return allQps;
    }

    public void reset(long tps) {
        allQps = tps;
    }

}
