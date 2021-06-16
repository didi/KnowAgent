package com.didichuxing.datachannel.swan.agent.engine.limit.tune.thread;

import com.didichuxing.datachannel.swan.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.swan.agent.engine.limit.cpu.CpuTime;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

public class LimitCpuThread implements Runnable {

    private static final ILog  LOGGER     = LogFactory.getLog(LimitCpuThread.class.getName());
    private static final float FACTOR     = 0.1f;
    private static final float TPS_FACTOR = 0.2f;
    // 最大10G/s
    private static final long  maxQPS     = 10 * 1024 * 1024 * 1024L;

    private boolean            isStop     = false;
    private LimitService       limiter;

    private long               allQps;                                                        // 当前整体的限制阀值
    private float              currentCpuUsage;                                               // 当前cpu利用率
    private CpuTime            lastCpuTime;                                                   // 记录上次的cpu耗时

    private String             peroid     = "cpu.period";
    private long               interval   = 30 * 1000;

    public LimitCpuThread(LimitService limiter, long qps) throws Exception {
        this.limiter = limiter;
        this.allQps = qps;
        this.lastCpuTime = new CpuTime();
        this.interval = Long.parseLong(CommonUtils.readSettings().get(peroid));
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
                float cpuUsage = getCpuUsage();

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
                this.currentCpuUsage = (float) (Math.round(cpuUsage * 100)) / 100;
            } catch (Throwable t) {
                LogGather.recordErrorLog("Limiter.cpuThread", "process cpuUsage error", t);
            }
        }
    }

    public void stop() {
        this.isStop = true;
    }

    public float getCurrentCpuUsage() {
        return currentCpuUsage;
    }

    public long getAllQps() {
        return allQps;
    }

    public void reset(long tps) {
        allQps = tps;
    }

    // 获得上次周期的cpu耗时
    private float getCpuUsage() throws Exception {
        CpuTime curCpuTime = new CpuTime();
        float cpuUsage = curCpuTime.getUsage(lastCpuTime);
        lastCpuTime = curCpuTime;

        return cpuUsage;
    }
}
