package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.didichuxing.datachannel.swan.agent.common.constants.Tags;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.security.authorize.AuthorizationException;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsCompression;
import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.bean.Event;
import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.swan.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.swan.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.metrics.HdfsMetricsFields;
import com.didichuxing.datachannel.swan.agent.sink.utils.EventUtils;
import com.didichuxing.datachannel.swan.agent.sink.utils.StringFilter;
import com.didichuxing.datachannel.swan.agent.sink.utils.TimeType;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/2 14:20
 */
public class HdfsSink extends AbstractSink<HdfsEvent> {

    private static final ILog                    LOGGER                = LogFactory.getLog(HdfsSink.class.getName());
    private static final ILog                    HDFS_LOGGER           = LogFactory.getLog("hdfs-auth");

    private static final long                    MAX_FAILED_SLEEP_TIME = 16000L;

    private static final long                    INITAL_TO_SLEEP_TIME  = 500L;

    // 记录已发送的批次数
    private AtomicInteger                        sendCounter           = new AtomicInteger(0);

    private volatile Boolean                     isStop                = false;

    private HdfsTargetConfig                     hdfsTargetConfig;
    private String                               dirPathRule;
    private String                               fileNameRule;

    /**
     * 考虑到存在一个文件对应写入多个hdfs文件的case
     * key: sourceKey
     * value：
     *  key: 当前时间区间
     *  value: datafile
     */
    private Map<String, Map<Long, HdfsDataFile>> hdfsDataFileMap       = new ConcurrentHashMap<>();

    private Map<String, Long>                    failedRateMap         = new ConcurrentHashMap<>();
    private Map<String, Long>                    failedRateMapBack     = new ConcurrentHashMap<>();

    private HdfsFileSystem                       hdfsFileSystem;
    private ByteArrayOutputStream                byteStream;

    private Long                                 interval              = -1L;
    private final static Long                    ONE_HOUR              = 60 * 60 * 1000L;
    private final static Long                    ONE_DAY               = 24 * 60 * 60 * 1000L;

    /**
     * 防止因为stream重建导致的数据丢失
     */
    private volatile boolean                     isStreamReBuild       = false;

    /**
     * 同一个inode是否对应hdfs多个文件，默认为false
     */
    private volatile boolean                     isFileSplit           = false;

    public HdfsSink(ModelConfig config, AbstractChannel channel, int orderNum, String dirPathRule, String fileNameRule,
                    HdfsFileSystem hdfsFileSystem){
        super(config, channel, orderNum);
        this.dirPathRule = dirPathRule;
        this.fileNameRule = fileNameRule;
        this.hdfsFileSystem = hdfsFileSystem;
    }

    @Override
    public int getSendNum() {
        return sendCounter.get();
    }

    @Override
    public HdfsEvent wrap(Event event) {
        if (event != null) {
            if (event instanceof LogEvent) {
                LogEvent logEvent = (LogEvent) event;
                HdfsEvent hdfsEvent = new HdfsEvent(event.getContent(), event.getBytes(), logEvent.getTimestamp(),
                                                    logEvent.getFileModifyTime() != -1L ? logEvent.getFileModifyTime() : logEvent.getLogTime(),
                                                    logEvent.getFileNodeKey(), logEvent.getPreOffset(),
                                                    logEvent.getLogId(), true, logEvent.getFileName(),
                                                    logEvent.getMasterFileName(), logEvent.getLogPathId(),
                                                    logEvent.getCollectTime(), logEvent.getUniqueKey(),
                                                    logEvent.getOffset());
                if (filter(hdfsEvent)) {
                    return hdfsEvent;
                }
            }
        }
        return null;
    }

    private boolean filter(HdfsEvent hdfsEvent) {
        long start = TimeUtils.getNanoTime();
        Boolean result = checkFilterRule(hdfsEvent);
        if (hdfsEvent != null && StringUtils.isNotBlank(hdfsTargetConfig.getFilterRule())) {
            if (taskPatternStatistics != null) {
                taskPatternStatistics.filterOneRecord(TimeUtils.getNanoTime() - start, result);
            }
        }
        return result;
    }

    /**
     * 返回true表示需要发送，返回false表示该条日志被过滤掉，不需要发送
     *
     * @param event
     * @return
     */
    private boolean checkFilterRule(HdfsEvent event) {
        if (event == null) {
            return true;
        }

        boolean canSend = true;
        // 没有过滤规则时，全部需要发送
        if (StringUtils.isBlank(hdfsTargetConfig.getFilterRule())) {
            canSend = true;
        } else {
            if (hdfsTargetConfig.getFilterOprType() == LogConfigConstants.FILTER_TYPE_CONTAINS) {
                canSend = StringFilter.doFilter(hdfsTargetConfig.getFilterRule(), event.getContent());
            } else if (hdfsTargetConfig.getFilterOprType() == LogConfigConstants.FILTER_TYPE_UNCONTAINS) {
                canSend = !StringFilter.doFilter(hdfsTargetConfig.getFilterRule(), event.getContent());
            } else {
                canSend = true;
            }
        }

        if (!canSend) {
            return false;
        }

        if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            if (COLLECT_ALL_WHEN_TEMPORALITY) {
                return true;
            }
            if (modelConfig.getCommonConfig().getStartTime() != null
                && modelConfig.getCommonConfig().getEndTime() != null) {
                if (modelConfig.getCommonConfig().getStartTime().getTime() >= event.getLogTime()
                    && modelConfig.getCommonConfig().getEndTime().getTime() <= event.getLogTime()) {
                }
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void send(HdfsEvent hdfsEvent) {
        if (hdfsEvent == null) {
            return;
        }
        Long start = TimeUtils.getNanoTime();
        sendCounter.incrementAndGet();
        if (!isStop && !sendReally(hdfsEvent)) {
            long initalSleepTime = INITAL_TO_SLEEP_TIME;
            while (true) {
                try {
                    Thread.sleep(initalSleepTime);
                } catch (Exception e) {
                    LogGather.recordErrorLog("HdfsSink error",
                                             "sendMessage sleep was interrupted when hdfsEvent is sent", e);
                }

                if (isStop || sendReally(hdfsEvent)) {
                    break;
                }
                initalSleepTime *= 2;
                if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                    initalSleepTime = INITAL_TO_SLEEP_TIME;
                }
            }
        }
        if (taskPatternStatistics != null) {
            taskPatternStatistics.sinkOneRecord(hdfsEvent.length(), TimeUtils.getNanoTime() - start);
        }

        if (hdfsTargetConfig.getAsync()) {
            appendFaildRate(hdfsEvent.getSourceKey(), hdfsEvent.getRateOfProgress());
        } else {
            start = TimeUtils.getNanoTime();
            // 同步
            if (!isStop && !flush()) {
                long initalSleepTime = INITAL_TO_SLEEP_TIME;
                while (true) {
                    try {
                        Thread.sleep(initalSleepTime);
                    } catch (Exception e) {
                        LogGather.recordErrorLog("HdfsSink error",
                                                 "sendMessage sleep was interrupted when hdfsEvent is flushed", e);
                    }

                    if (isStop || flush()) {
                        break;
                    }
                    initalSleepTime *= 2;
                    if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                        initalSleepTime = INITAL_TO_SLEEP_TIME;
                    }
                }
            }
            if (taskPatternStatistics != null) {
                taskPatternStatistics.flushOneRecord(TimeUtils.getNanoTime() - start);
            }
        }
    }

    private void appendFaildRate(String souceKey, Long rate) {
        Long existRate = failedRateMap.get(souceKey);
        if (existRate == null || existRate > rate) {
            failedRateMap.put(souceKey, rate);
        }
    }

    private boolean sendReally(HdfsEvent hdfsEvent) {
        int i = 0;
        while (i < hdfsTargetConfig.getRetryTimes()) {
            try {
                writeToHdfs(hdfsEvent);
                break;
            } catch (AuthorizationException e1) {
                HDFS_LOGGER.warn("this host[" + CommonUtils.getHOSTNAME() + "] is not in hdfs white list.", e1);
            } catch (Exception e) {
                LogGather.recordErrorLog("HdfsSink error", "send hdfsEvent error!", e);
                if (i >= hdfsTargetConfig.getRetryTimes() - 1) {
                    try {
                        Thread.sleep(60 * 1000L);
                    } catch (Exception e1) {

                    }
                    // 此时重建stream
                    rebuildStream(hdfsEvent);
                    isStreamReBuild = true;
                }
            } finally {
                i++;
            }
        }
        return i != hdfsTargetConfig.getRetryTimes();
    }

    private synchronized void writeToHdfs(HdfsEvent hdfsEvent) throws Exception {
        try {
            EventUtils.buildHdfsContent(byteStream, this, hdfsEvent);

            // 如果落盘是非Sequencefile类型，需要加回车标识符作为分隔
            if (hdfsTargetConfig.getCompression() <= HdfsCompression.LZ4.getStatus()
                || hdfsTargetConfig.getCompression() == HdfsCompression.GZIP.getStatus()) {
                if (!StringUtils.endsWith(hdfsEvent.getContent(), "\n")) {
                    byteStream.write(LogConfigConstants.CTRL_BYTES);
                }
            }
            HdfsDataFile dataFile = getHdfsDateFile(hdfsEvent);
            byte[] byteArray = byteStream.toByteArray();
            dataFile.write(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            throw e;
        } finally {
            byteStream.reset();
        }
    }

    private void rebuildStream(HdfsEvent hdfsEvent) {
        if (hdfsEvent == null) {
            return;
        }
        try {
            HdfsDataFile dataFile = getHdfsDateFile(hdfsEvent);
            dataFile.reBuildStream();
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsSink error", "rebuildStream error!modelId is " + hdfsEvent.getModelId(), e);
        }
    }

    public HdfsDataFile getHdfsDateFile(HdfsEvent event) throws Exception {
        Map<Long, HdfsDataFile> map = hdfsDataFileMap.get(event.getSourceKey());
        boolean needToBuild = false;
        Long intervalTime = 0L;
        if (isFileSplit) {
            if (map != null && map.size() != 0) {
                // 此时只有 map为单独的一个
                for (HdfsDataFile hdfsDataFile : map.values()) {
                    if (hdfsDataFile.isClosed()) {
                        needToBuild = true;
                        break;
                    } else {
                        return hdfsDataFile;
                    }
                }
            } else {
                TimeType timeType = getTimeType(event);
                if (timeType != null) {
                    String timeStr = event.getSourceName().substring(event.getAnotherSourceName().length() + 1);
                    intervalTime = TimeUtils.getLongTimeStamp(timeStr, timeType.getFormat());
                    event.setLogTime(intervalTime);
                } else {
                    // 解决新创建文件最新修改时间为是59s(而不是00s)，但是时区却是下一个时区的问题。
                    // 例如，新创建的didi.log数据是1点的数据，但是其创建时的最新修改时间是00:59:59，此时应该写入1点的hdfs路径
                    boolean isExist = false;
                    intervalTime = getIntervalTime(event.getLogTime());
                    for (Map<Long, HdfsDataFile> itemMap : hdfsDataFileMap.values()) {
                        if (itemMap.containsKey(intervalTime)) {
                            isExist = true;
                        }
                    }

                    if (isExist) {
                        if (((event.getLogTime() / 60000) % 60) == 59) {
                            if (interval == -1) {
                                if (dirPathRule.contains("${HH}")) {
                                    interval = ONE_HOUR;
                                } else {
                                    interval = ONE_DAY;
                                }
                            }
                            intervalTime = intervalTime + interval;
                            LOGGER.warn("event's logTime is already existed. change interverlTime to " + intervalTime
                                        + ", event is " + event);
                            event.setLogTime(intervalTime);
                        }
                    }
                }
                needToBuild = true;
            }
        } else {
            intervalTime = getIntervalTime(event.getLogTime());
            if (map == null) {
                needToBuild = true;
            } else {
                HdfsDataFile dataFile = map.get(intervalTime);
                if (dataFile == null || dataFile.isClosed()) {
                    if (dataFile != null) {
                        // 说明被close，此时需要移除
                        map.remove(intervalTime);
                    }
                    needToBuild = true;

                }
            }
        }

        if (needToBuild) {
            synchronized (lock) {
                map = hdfsDataFileMap.get(event.getSourceKey());
                if (map != null && map.get(intervalTime) != null && !map.get(intervalTime).isClosed()) {
                    // 解决多线程问题
                    // 在某个线程创建完成之后，再校验一遍，若有，则直接使用之前线程创建的datafile
                    return map.get(intervalTime);
                } else {
                    String rootPath = hdfsTargetConfig.getRootPath();
                    if (rootPath.equals("/")) {
                        rootPath = "";
                    }
                    String path = getHdfsPath(event);
                    String fileName = getHdfsFileName(event);
                    String compression = getCompression();
                    HdfsDataFile dataFile = null;
                    if (compression.equals(HdfsCompression.SEQ_SNAPPY.getComment())
                        || compression.equals(HdfsCompression.SEQ_LZ4.getComment())
                        || compression.equals(HdfsCompression.SEQ_GZIP.getComment())) {
                        dataFile = new SequenceHdfsDataFile(this, rootPath + path, fileName, event.getSourceKey(),
                                                            hdfsFileSystem, compression);
                    } else {
                        dataFile = new HdfsDataFile(this, rootPath + path, fileName, event.getSourceKey(),
                                                    hdfsFileSystem, compression);
                    }
                    dataFile.init();
                    if (map == null) {
                        hdfsDataFileMap.put(event.getSourceKey(), new HashMap<>());
                    }
                    hdfsDataFileMap.get(event.getSourceKey()).put(intervalTime, dataFile);
                    return dataFile;
                }
            }
        } else {
            return map.get(intervalTime);
        }
    }

    private Long getIntervalTime(Long time) {
        if (interval == -1) {
            if (dirPathRule.contains("${HH}")) {
                interval = ONE_HOUR;
            } else {
                interval = ONE_DAY;
            }
        }

        return (time / interval) * interval;
    }

    /**
     * 获取hdfs 路径名
     *
     * @param hdfsEvent
     * @return
     */
    private String getHdfsPath(HdfsEvent hdfsEvent) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(hdfsEvent.getLogTime());

        String dataDir = dirPathRule;
        dataDir = dataDir.replace("${yyyy}", String.valueOf(c.get(Calendar.YEAR)));
        dataDir = dataDir.replace("${MM}", paddingZero(c.get(Calendar.MONTH) + 1));
        dataDir = dataDir.replace("${dd}", paddingZero(c.get(Calendar.DAY_OF_MONTH)));
        dataDir = dataDir.replace("${HH}", paddingZero(c.get(Calendar.HOUR_OF_DAY)));

        return dataDir;
    }

    /**
     * 获取hdfs 文件名
     * @param hdfsEvent
     * @return
     */
    private String getHdfsFileName(HdfsEvent hdfsEvent) {
        String fileName = fileNameRule;
        fileName = fileName.replace(LogConfigConstants.CREATETIME_FLAG,
                                    TimeUtils.getTimeString(System.currentTimeMillis(), TimeUtils.TIME_YYYYMMDDHHMMSS));
        fileName = fileName.replace(LogConfigConstants.SOURCEID_FLAG, hdfsEvent.getSourceKey());
        fileName = fileName.replace(LogConfigConstants.SINKID_FLAG, orderNum + "");
        fileName = fileName.replace(LogConfigConstants.COMPRESSION_FLAG, getCompression());

        return fileName;
    }

    public TimeType getTimeType(HdfsEvent hdfsEvent) {
        if (hdfsEvent.getSourceName().equals(hdfsEvent.getAnotherSourceName())) {
            return null;
        }

        String suffix = hdfsEvent.getSourceName().substring(hdfsEvent.getAnotherSourceName().length() + 1);
        for (TimeType type : TimeType.values()) {
            boolean isMatch = Pattern.matches(type.getMatchSample(), suffix);
            if (isMatch) {
                return type;
            }
        }
        return null;
    }

    @Override
    public boolean flush() {
        boolean result = true;
        if (isStop) {
            LOGGER.warn("hdfs sink is stoped. ignore. uniqueKey is " + uniqueKey);
            return true;
        }

        copyFailedOffset();

        if (isStreamReBuild) {
            isStreamReBuild = false;
            return false;
        }

        synchronized (lock) {
            sendCounter.set(0);
            for (Map<Long, HdfsDataFile> map : hdfsDataFileMap.values()) {
                for (HdfsDataFile dataFile : map.values()) {
                    if (!dataFile.isClosed()) {
                        if (!dataFile.sync()) {
                            result = false;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 拷贝失败的rate信息
     */
    private void copyFailedOffset() {
        failedRateMapBack.clear();
        for (Map.Entry<String, Long> entry : failedRateMap.entrySet()) {
            failedRateMapBack.put(entry.getKey(), entry.getValue());
        }
        failedRateMap.clear();
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop hdfs sink. uniqueKey is " + uniqueKey + ", force is " + force);
        isRunning = false;
        if (isStop) {
            return true;
        }
        try {
            boolean result = true;
            for (Map<Long, HdfsDataFile> map : hdfsDataFileMap.values()) {
                for (HdfsDataFile dataFile : map.values()) {
                    if (!force && !dataFile.isClosed()) {
                        dataFile.sync();
                    }
                    if (!dataFile.close()) {
                        result = false;
                    }
                }
            }
            if (force || result) {
                isStop = true;
                LOGGER.info("stop hdfs sink success! uniqueKey is " + uniqueKey + ", force is " + force);
            }
            if (byteStream != null) {
                byteStream.close();
            }
            return result;
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsSink error",
                                     "stop hdfsEvent error! uniqueKey is " + uniqueKey + ", force is " + force, e);
        }
        return false;
    }

    @Override
    public boolean delete() {
        return stop(true);
    }

    @Override
    public void configure(ComponentConfig config) {
        this.hdfsTargetConfig = (HdfsTargetConfig) this.modelConfig.getTargetConfig();
        this.byteStream = new ByteArrayOutputStream();
    }

    /**
     *
     * @param newOne
     * @return
     */
    @Override
    public boolean onChange(ComponentConfig newOne) {
        LOGGER.info("begin to change hdfs sink config. unique key is " + getUniqueKey() + ", new config is " + newOne);
        this.modelConfig = (ModelConfig) newOne;
        HdfsTargetConfig oldOne = this.hdfsTargetConfig;
        this.hdfsTargetConfig = (HdfsTargetConfig) this.modelConfig.getTargetConfig();

        // 需要全部重新构建hdfs文件格式
        if (!oldOne.getHdfsPath().equals(this.hdfsTargetConfig.getHdfsPath())
            || !oldOne.getHdfsFileName().equals(this.hdfsTargetConfig.getHdfsFileName())) {
            synchronized (lock) {
                for (Map<Long, HdfsDataFile> map : hdfsDataFileMap.values()) {
                    for (HdfsDataFile hdf : map.values()) {
                        try {
                            hdf.close();
                        } catch (Exception e) {
                            LogGather.recordErrorLog("HdfsSink error",
                                                     "onChange HdfsDataFile error! hdf is " + hdf.getUniqueKey(), e);
                        }
                    }
                }
                hdfsDataFileMap.clear();
            }
        }

        LOGGER.info("change config success! unique key is " + getUniqueKey());
        return true;
    }

    private String getCompression() {
        int cpInConf = this.hdfsTargetConfig.getCompression();
        HdfsCompression[] hdfsCompressions = HdfsCompression.values();
        for (HdfsCompression cp : hdfsCompressions) {
            if (cp.getStatus() == cpInConf) {
                return cp.getComment();
            }
        }
        return HdfsCompression.TEXT.getComment();
    }

    public HdfsFileSystem getHdfsFileSystem() {
        return hdfsFileSystem;
    }

    public void setHdfsFileSystem(HdfsFileSystem hdfsFileSystem) {
        this.hdfsFileSystem = hdfsFileSystem;
    }

    public Map<String, Long> getFailedRateMap() {
        return failedRateMap;
    }

    public void setFailedRateMap(Map<String, Long> failedRateMap) {
        this.failedRateMap = failedRateMap;
    }

    public AtomicInteger getSendCounter() {
        return sendCounter;
    }

    public void setSendCounter(AtomicInteger sendCounter) {
        this.sendCounter = sendCounter;
    }

    private String paddingZero(int number) {
        return (number < 10) ? "0" + number : "" + number;
    }

    public void sync(Set<String> sourceKeys) {
        syncHdfsDataFileMap(sourceKeys);
    }

    private void syncHdfsDataFileMap(Set<String> sourceKeys) {
        if (CollectionUtils.isEqualCollection(sourceKeys, hdfsDataFileMap.keySet())) {
            return;
        }
        if (sourceKeys.size() != 0) {
            Set<String> delKeys = new HashSet<>();
            for (String key : hdfsDataFileMap.keySet()) {
                if (!sourceKeys.contains(key)) {
                    delKeys.add(key);
                }
            }
            for (String key : delKeys) {
                closeBySourceKey(key);
                hdfsDataFileMap.remove(key);
            }
        } else {
            for (String key : hdfsDataFileMap.keySet()) {
                closeBySourceKey(key);
            }
            hdfsDataFileMap.clear();
        }
    }

    private void closeBySourceKey(String key) {
        if (hdfsDataFileMap.containsKey(key)) {
            for (HdfsDataFile hdf : hdfsDataFileMap.get(key).values()) {
                hdf.close();
            }
        }
    }

    @Override
    public Map<String, Object> metric() {
        Map<String, Object> ret = new HashMap<>();

        ret.put(HdfsMetricsFields.PREFIX_TYPE, Tags.TARGET_HDFS);
        ret.put(HdfsMetricsFields.PREFIX_HDFS_PATH, hdfsTargetConfig.getRootPath() + this.dirPathRule);

        return ret;
    }

    public Map<String, Map<Long, HdfsDataFile>> getHdfsDataFileMap() {
        return hdfsDataFileMap;
    }

    public void setHdfsDataFileMap(Map<String, Map<Long, HdfsDataFile>> hdfsDataFileMap) {
        this.hdfsDataFileMap = hdfsDataFileMap;
    }

    public HdfsTargetConfig getHdfsTargetConfig() {
        return hdfsTargetConfig;
    }

    public void setHdfsTargetConfig(HdfsTargetConfig hdfsTargetConfig) {
        this.hdfsTargetConfig = hdfsTargetConfig;
    }

    public Map<String, Long> getFailedRateMapBack() {
        return failedRateMapBack;
    }

    public void setFailedRateMapBack(Map<String, Long> failedRateMapBack) {
        this.failedRateMapBack = failedRateMapBack;
    }

    @Override
    public String getThreadName() {
        return Tags.TARGET_HDFS + "_" + uniqueKey;
    }

    public String getDirPathRule() {
        return dirPathRule;
    }

    public void setDirPathRule(String dirPathRule) {
        this.dirPathRule = dirPathRule;
        interval = -1L;
    }

    public String getFileNameRule() {
        return fileNameRule;
    }

    public void setFileNameRule(String fileNameRule) {
        this.fileNameRule = fileNameRule;
    }

    public boolean isFileSplit() {
        return isFileSplit;
    }

    public void setFileSplit(boolean fileSplit) {
        isFileSplit = fileSplit;
    }

    public ByteArrayOutputStream getByteStream() {
        return byteStream;
    }

    public void setByteStream(ByteArrayOutputStream byteStream) {
        this.byteStream = byteStream;
    }
}
