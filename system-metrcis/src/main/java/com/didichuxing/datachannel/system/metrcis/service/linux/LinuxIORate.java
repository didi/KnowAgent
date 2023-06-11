package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.util.FileUtils;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 要求Linux内核版本大于2.6.20
 * @author Ronaldo
 */
public class LinuxIORate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxIORate.class);

    /**
     * 当前时间
     */
    private long          currentTime;

    /**
     * 进程io读写路径
     */
    private final String  PROCESS_PATH;

    /**
     * 进程读取总次数
     */
    private long          processIOReadTimes;

    /**
     * 进程写入总次数
     */
    private long          processIOWriteTimes;

    /**
     * 进程读取总字节数
     */
    private long          processIOReadBytes;

    /**
     * 进程写入总字节数
     */
    private long          processIOWriteBytes;

    public LinuxIORate(long pid) throws Exception {
        this.PROCESS_PATH = "/proc/" + pid + "/io";
        this.currentTime = System.currentTimeMillis();

        // 获取路径每行数据
        Map<String, Long> processIOInfoMap = getProcessIoInfo(FileUtils.readFileContent(PROCESS_PATH, -1));
        this.processIOReadTimes = processIOInfoMap.get("syscr");
        this.processIOWriteTimes = processIOInfoMap.get("syscw");
        this.processIOReadBytes = processIOInfoMap.get("read_bytes");
        this.processIOWriteBytes = processIOInfoMap.get("write_bytes");

    }

    private Map<String, Long> getProcessIoInfo(List<String> processIOInfoLines) {
        Map<String, Long> result = new HashMap<>();
        if(!processIOInfoLines.isEmpty()) {
            for (String line : processIOInfoLines) {
                String[] array = line.split(":\\s+");
                if (array.length < 2) {
                    LOGGER.error("{} line is too short", PROCESS_PATH);
                    return result;
                }
                long ioValue = Long.valueOf(array[1]);
                result.put(array[0], ioValue);
            }
        }
        return result;
    }

    /**
     * 获取进程读取次数频率
     * @param before
     * @return
     */
    public double getIOReadTimesRate(LinuxIORate before) {
        long timeGap = this.currentTime - before.currentTime;
        long readTimesGap = this.processIOReadTimes - before.processIOReadTimes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * readTimesGap, timeGap);
        } else {
            return 1000.0 * readTimesGap;
        }

    }

    /**
     * 获取进程写入次数频率
     * @param before
     * @return
     */
    public double getIOWriteTimesRate(LinuxIORate before) {
        long timeGap = this.currentTime - before.currentTime;
        long readTimesGap = this.processIOWriteTimes - before.processIOWriteTimes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * readTimesGap, timeGap);
        } else {
            return 1000.0 * readTimesGap;
        }
    }

    /**
     * 获取进程读取字节速率
     * @param before
     * @return
     */
    public double getIOReadBytesRate(LinuxIORate before) {
        long timeGap = this.currentTime - before.currentTime;
        long readTimesGap = this.processIOReadBytes - before.processIOReadBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * readTimesGap, timeGap);
        } else {
            return 1000.0 * readTimesGap;
        }
    }

    /**
     * 获取进程写入字节速率
     * @param before
     * @return
     */
    public double getIOWriteBytesRate(LinuxIORate before) {
        long timeGap = this.currentTime - before.currentTime;
        long readTimesGap = this.processIOWriteBytes - before.processIOWriteBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * readTimesGap, timeGap);
        } else {
            return 1000.0 * readTimesGap;
        }

    }
}
