package com.didichuxing.datachannel.swan.agent.channel.log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.didichuxing.datachannel.swan.agent.common.loggather.LogGather;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.swan.agent.channel.log.metrics.LogChannelMetricsFields;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ChannelConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.bean.Event;
import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.swan.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.swan.agent.source.log.LogSource;
import com.didichuxing.datachannel.swan.agent.source.log.beans.WorkingFileNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: logChannel
 * @author: huangjw
 * @Date: 19/7/10 12:49
 */
public class LogChannel extends AbstractChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogChannel.class);
    private static final Logger WARN_LOGGER = LoggerFactory.getLogger("channel");

    private Map<String, FileNodeOffset> lastestOffsetMap = new ConcurrentHashMap<>(3);

    private LinkedBlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>();
    // 存储量
    private volatile AtomicLong storage = new AtomicLong(0L);
    private volatile AtomicInteger size = new AtomicInteger(0);

    private LogSource logSource;
    private ChannelConfig channelConfig;

    private volatile boolean isStop = false;

    private final Object lock = new Object();

    public LogChannel(LogSource logSource, ChannelConfig channelConfig) {
        this.logSource = logSource;
        this.channelConfig = channelConfig;
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
        isStop = true;
        return false;
    }

    @Override
    public int size() {
        return size.get();
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
                    queue.add(logEvent);
                    storage.addAndGet(logEvent.length());
                    break;
                }
                WARN_LOGGER.warn("tryAppend error for queue is full. queue size is " + queue.size() + ", storage is "
                        + storage + ", event's length is " + event.length() + ",logModeId: "
                        + logSource.getModelConfig().getModelConfigKey());
                if (logEvent.length() > this.channelConfig.getMaxBytes()) {
                    byte[] newBytes = new byte[(this.channelConfig.getMaxBytes().intValue() - (int) storage.get()) / 2];
                    System.arraycopy(logEvent.getBytes(), 0, newBytes, 0,
                            (this.channelConfig.getMaxBytes().intValue() - (int) storage.get()) / 2);
                    logEvent.setBytes(newBytes);
                }
                // 休眠等待
                Thread.sleep(50);
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
            LogEvent event = queue.poll();
            if (event != null) {
                storage.addAndGet(0L - event.length());
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
        Map<String, Object> ret = new HashMap<>();

        ret.put(LogChannelMetricsFields.PREFIX_TYPE, "logChannel");
        ret.put(LogChannelMetricsFields.PREFIX_SIZE, size);
        ret.put(LogChannelMetricsFields.PREFIX_CAPACITY, storage);
        return ret;
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
