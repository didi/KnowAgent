package com.didichuxing.datachannel.agent.channel.log;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.system.metrcis.Metrics;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ChannelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: logChannel
 * @author: huangjw
 * @Date: 19/7/10 12:49
 */
public class LogChannel extends AbstractChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogChannel.class);

    private Map<String, FileNodeOffset> lastestOffsetMap = new ConcurrentHashMap<>(3);

    private LinkedBlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>();
    // 存储量
    private volatile AtomicLong storage = new AtomicLong(0L);
    private volatile AtomicInteger size = new AtomicInteger(0);

    private LogSource logSource;
    private ChannelConfig channelConfig;

    private volatile boolean isStop = false;

    private final Object lock = new Object();

    private volatile CompletableFuture<?> availableFuture = new CompletableFuture<>();

    private String channelId;

    /**
     * metrics 自动计算
     */
    private PeriodStatistics channelBytesSize = new PeriodStatistics();
    private PeriodStatistics channelCountSize = new PeriodStatistics();
    private PeriodStatistics channelUsedPercent = new PeriodStatistics();

    public LogChannel(LogSource logSource, ChannelConfig channelConfig) {
        this.logSource = logSource;
        this.channelConfig = channelConfig;
        this.channelId = UUID.randomUUID().toString();
        Metrics.getPeriodMetricAutoComputeComponent().registerAutoComputeTask(
                this.channelId,
                this
        );
    }

    @Override
    public void configure(ComponentConfig config) {
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        LOGGER.info("begin to change channel config. newOne is " + newOne);
        this.channelConfig = ((ModelConfig) newOne).getChannelConfig();
        return true;
    }

    @Override
    public boolean start() {
        isStop = false;
        return true;
    }

    @Override
    public boolean init(ComponentConfig config) {
        bulidUniqueKey();
        isStop = false;
        return true;
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop logChannel. uniqueKey is " + uniqueKey + ", force is " + force);
        if(!Metrics.getPeriodMetricAutoComputeComponent().unRegisterAutoComputeTask(
                this.channelId,
                this
        )) {
            LOGGER.error(
                    String.format("stop the channel's auto compute metrics task failed, uniqueKey is: %s",
                            uniqueKey
                    )
            );
        }
        isStop = true;
        return false;
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public void setMetrics(TaskMetrics taskMetrics) {

        calcChannelMetrics();

        taskMetrics.setChannelbytesmax(this.channelConfig.getMaxBytes());
        taskMetrics.setChannelcountmax(this.channelConfig.getMaxNum().longValue());

        PeriodStatistics channelBytesSizePeriodStatistics = channelBytesSize.snapshot();
        taskMetrics.setChannelbytessize(channelBytesSizePeriodStatistics.getLast());
        taskMetrics.setChannelbytessizemin(channelBytesSizePeriodStatistics.getMin());
        taskMetrics.setChannelbytessizemax(channelBytesSizePeriodStatistics.getMax());
        taskMetrics.setChannelbytessizemean(channelBytesSizePeriodStatistics.getAvg());
        taskMetrics.setChannelbytessizestd(channelBytesSizePeriodStatistics.getStdDev());
        taskMetrics.setChannelbytessize55quantile(channelBytesSizePeriodStatistics.getQuantile55());
        taskMetrics.setChannelbytessize75quantile(channelBytesSizePeriodStatistics.getQuantile75());
        taskMetrics.setChannelbytessize95quantile(channelBytesSizePeriodStatistics.getQuantile95());
        taskMetrics.setChannelbytessize99quantile(channelBytesSizePeriodStatistics.getQuantile99());


        PeriodStatistics channelCountSizePeriodStatistics = channelCountSize.snapshot();
        taskMetrics.setChannelcountsize(channelCountSizePeriodStatistics.getLast());
        taskMetrics.setChannelcountsizemin(channelCountSizePeriodStatistics.getMin());
        taskMetrics.setChannelcountsizemax(channelCountSizePeriodStatistics.getMax());
        taskMetrics.setChannelcountsizemean(channelCountSizePeriodStatistics.getAvg());
        taskMetrics.setChannelcountsizestd(channelCountSizePeriodStatistics.getStdDev());
        taskMetrics.setChannelcountsize55quantile(channelCountSizePeriodStatistics.getQuantile55());
        taskMetrics.setChannelcountsize75quantile(channelCountSizePeriodStatistics.getQuantile75());
        taskMetrics.setChannelcountsize95quantile(channelCountSizePeriodStatistics.getQuantile95());
        taskMetrics.setChannelcountsize99quantile(channelCountSizePeriodStatistics.getQuantile99());

        PeriodStatistics channelUsedPercentPeriodStatistics = channelUsedPercent.snapshot();
        taskMetrics.setChannelusedpercent(channelUsedPercentPeriodStatistics.getLast());
        taskMetrics.setChannelusedpercentmin(channelUsedPercentPeriodStatistics.getMin());
        taskMetrics.setChannelusedpercentmax(channelUsedPercentPeriodStatistics.getMax());
        taskMetrics.setChannelusedpercentmean(channelUsedPercentPeriodStatistics.getAvg());
        taskMetrics.setChannelusedpercentstd(channelUsedPercentPeriodStatistics.getStdDev());
        taskMetrics.setChannelusedpercent55quantile(channelUsedPercentPeriodStatistics.getQuantile55());
        taskMetrics.setChannelusedpercent75quantile(channelUsedPercentPeriodStatistics.getQuantile75());
        taskMetrics.setChannelusedpercent95quantile(channelUsedPercentPeriodStatistics.getQuantile95());
        taskMetrics.setChannelusedpercent99quantile(channelUsedPercentPeriodStatistics.getQuantile99());

    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcChannelMetrics() {
        Double channelCountSizeCurrent = Double.valueOf(size.get());
        Double channelBytesSizeCurrent = Double.valueOf(storage.get());
        channelBytesSize.add(channelBytesSizeCurrent);
        channelCountSize.add(channelCountSizeCurrent);
        Double channelUsedPercent = Math.max(
                MathUtil.divideWith2Digit(channelCountSizeCurrent * 100, this.channelConfig.getMaxNum()),
                MathUtil.divideWith2Digit(channelBytesSizeCurrent * 100, this.channelConfig.getMaxBytes())
        );
        this.channelUsedPercent.add(channelUsedPercent);
    }

    @Override
    public boolean delete() {
        return stop(true);
    }

    @Override
    public void bulidUniqueKey() {
        setUniqueKey(logSource.getUniqueKey());
    }

    @Override
    public void tryAppend(Event event) {
        if (event == null) {
            return;
        }
        LogEvent logEvent = (LogEvent) event;
        while (!isStop) {
            try {
                if (queue.size() < this.channelConfig.getMaxNum()
                        && storage.get() + logEvent.length() <= this.channelConfig.getMaxBytes()) {
                    // size add 和 queue add 顺序不可乱，涉及到多线程问题
                    size.incrementAndGet();
                    queue.offer(logEvent);
                    storage.addAndGet(logEvent.length());
                    break;
                }
                if (logEvent.length() > this.channelConfig.getMaxBytes()) {
                    byte[] newBytes = new byte[(this.channelConfig.getMaxBytes().intValue() - (int) storage.get()) / 2];
                    System.arraycopy(logEvent.getBytes(), 0, newBytes, 0,
                            (this.channelConfig.getMaxBytes().intValue() - (int) storage.get()) / 2);
                    logEvent.setBytes(newBytes);
                }
                // 休眠等待
                resetUnavailable();
                try {
                    availableFuture.get(50, TimeUnit.MILLISECONDS);
                } catch (TimeoutException ignore) {
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("LogChannel error", "tryAppend error!", e);
            }
        }
    }

    @Override
    public Event tryGet(long timeout) {
        try {
            LogEvent event = queue.poll(timeout, TimeUnit.MICROSECONDS);
            if (event != null) {
                storage.addAndGet(0L - event.length());
                if(!availableFuture.isDone()) {
                    availableFuture.complete(null);
                }
            }
            return event;
        } catch (Exception e) {
            LogGather.recordErrorLog("LogChannel error", "tryGet error!", e);
        }
        return null;
    }

    @Override
    public Event tryGet() {
        try {
            LogEvent event = queue.poll(10, TimeUnit.MILLISECONDS);
            if (event != null) {
                storage.addAndGet(0L - event.length());
                if(!availableFuture.isDone()) {
                    availableFuture.complete(null);
                }
            }
            return event;
        } catch (Exception e) {
            LogGather.recordErrorLog("LogChannel error", "tryGet error!", e);
        }
        return null;
    }

    @Override
    public void commitEvent(Event event) {
        if (event != null) {
            try {
                synchronized (lock) {
                    if (event instanceof LogEvent) {
                        LogEvent logEvent = (LogEvent) event;
                        FileNodeOffset fileNodeOffset = new FileNodeOffset(logEvent);
                        // lastestOffsetMap不存在 或者 fileNodeOffset的offset比lastestOffsetMap的offset大时才更新
                        if (lastestOffsetMap.containsKey(fileNodeOffset.getFileNodeKey())) {
                            if (lastestOffsetMap.get(fileNodeOffset.getFileNodeKey()).getOffset() < fileNodeOffset.getOffset()) {
                                lastestOffsetMap.put(fileNodeOffset.getFileNodeKey(), fileNodeOffset);
                            } else {
                                // 此时表示新消费到的logEvent与lastestOffsetMap已有的fileNodeOffset的offset小或者相等，表示发生了文件的清空和offset的重置
                                WorkingFileNode wfn = logSource.getCollectingFileNodeMap().get(fileNodeOffset.getFileNodeKey());
                                // 修复当文件积累过多时，wfn被删除的问题，此时wfn会为null
                                if (wfn != null && wfn.isBackflow()) {
                                    LOGGER.warn("wfn's fileNode had been backflowed. reset lastestOffsetMap. wfn is "
                                            + wfn);
                                    lastestOffsetMap.put(fileNodeOffset.getFileNodeKey(), fileNodeOffset);
                                    wfn.setBackflow(false);
                                }
                            }
                        } else {
                            lastestOffsetMap.put(fileNodeOffset.getFileNodeKey(), fileNodeOffset);
                        }
                    }
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("LogChannel error", "commitEvent error! logPath is " + logSource.getLogPath(),
                        e);
            } finally {
                size.decrementAndGet();
            }
        }
    }

    public Map<String, FileNodeOffset> getLastestOffsetMap() {
        return lastestOffsetMap;
    }

    public void setLastestOffsetMap(Map<String, FileNodeOffset> lastestOffsetMap) {
        this.lastestOffsetMap = lastestOffsetMap;
    }

    /**
     * 提交进度
     */
    @Override
    public void commit() {
        Map<String, FileNodeOffset> fileNodeOffsetMap = getLastestOffsetMap();
        for (FileNodeOffset offset : fileNodeOffsetMap.values()) {
            WorkingFileNode wfn = logSource.getCollectingFileNodeMap().get(offset.getFileNodeKey());
            if (wfn != null) {
                wfn.getFileNode().setOffset(offset.getOffset());
                wfn.getFileNode().setOffsetTimeStamp(offset.getTimestamp());
            }
        }
        Map<String, WorkingFileNode> wfnMap = logSource.getCollectingFileNodeMap();
        sync(wfnMap.keySet());

        lastestOffsetMap.clear();
    }

    /**
     * 保持同步
     *
     * @param fileNodeKeys
     */
    public void sync(Set<String> fileNodeKeys) {
        if (fileNodeKeys == null) {
            lastestOffsetMap.clear();
            return;
        }

        if (CollectionUtils.isEqualCollection(fileNodeKeys, lastestOffsetMap.keySet())) {
            return;
        }
        Set<String> delKeys = new HashSet<>();
        for (String key : lastestOffsetMap.keySet()) {
            if (!fileNodeKeys.contains(key)) {
                delKeys.add(key);
            }
        }
        if (delKeys.size() != 0) {
            LOGGER.info("begin to sync logChannel. fileNodeKeys is " + StringUtils.join(fileNodeKeys, ",")
                    + ",delKeys is " + StringUtils.join(delKeys, ","));
            for (String key : delKeys) {
                lastestOffsetMap.remove(key);
            }
        }
    }

    @Override
    public Map<String, Object> metric() {

        return null;
    }

    private void resetUnavailable() {
        if (availableFuture.isDone()) {
            this.availableFuture = new CompletableFuture<>();
        }
    }

    public ChannelConfig getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(ChannelConfig channelConfig) {
        this.channelConfig = channelConfig;
    }

    public LinkedBlockingQueue<LogEvent> getQueue() {
        return queue;
    }

    public void setQueue(LinkedBlockingQueue<LogEvent> queue) {
        this.queue = queue;
    }

    public long getStorage() {
        return storage.get();
    }

    public void setStorage(long storage) {
        this.storage.set(storage);
    }
}
