package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.util.FileUtils;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Ronaldo
 */
public class LinuxNetFlow {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxNetFlow.class);

    /**
     * 系统接收的字节数
     */
    private long          systemReceiveBytes;

    /**
     * 系统发送的字节数
     */
    private long          systemTransmitBytes;

    /**
     * 进程接收的字节数
     */
    private long          processReceiveBytes;

    /**
     * 进程发送的字节数
     */
    private long          processTransmitBytes;

    /**
     * 当前时间
     */
    private long          currentTime;

    /**
     * 系统收发字节数的路径
     */
    private final String  SYSTEM_PATH = "/proc/net/dev";

    public LinuxNetFlow(long pid){
        String PROCESS_PATH = "/proc/" + pid + "/net/dev";
        this.currentTime = System.currentTimeMillis();
        List<String> processReceiveAndTransmitBytes = FileUtils.readFileContent(PROCESS_PATH, -1);
        this.processReceiveBytes = getBytes(processReceiveAndTransmitBytes, 2);
        this.processTransmitBytes = getBytes(processReceiveAndTransmitBytes, 10);
    }

    public LinuxNetFlow() {
        this.currentTime = System.currentTimeMillis();
        List<String> systemReceiveAndTransmitBytes = FileUtils.readFileContent(SYSTEM_PATH, -1);
        this.systemReceiveBytes = getBytes(systemReceiveAndTransmitBytes, 2);
        this.systemTransmitBytes = getBytes(systemReceiveAndTransmitBytes, 10);

    }

    /**
     * 根据文件内容和下标获取收发字节数
     * @param fileLines 文件内容
     * @param index 下标，对应是发送数据还是接收数据
     * @return
     * @throws Exception
     */
    private long getBytes(List<String> fileLines, int index) {
        long bytes = 0;
        for (int i = 2; i < fileLines.size(); i++) {
            String[] array = fileLines.get(i).split("\\s+");
//            if ("lo:".equals(array[1])) {
//                continue;
//            }
            bytes += Long.parseLong(array[index]);
        }
        return bytes;
    }

    /**
     * 计算系统每秒接收字节数
     */
    public double getSystemReceiveBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.systemReceiveBytes - before.systemReceiveBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * bytesGap, timeGap);
        } else {
            return 1000.0 * bytesGap;
        }
    }

    /**
     * 计算系统每秒发送字节数
     */
    public double getSystemTransmitBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.systemTransmitBytes - before.systemTransmitBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * bytesGap, timeGap);
        } else {
            return 1000.0 * bytesGap;
        }
    }

    /**
     * 计算进程每秒接收字节数
     */
    public double getProcessReceiveBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.processReceiveBytes - before.processReceiveBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * bytesGap, timeGap);
        } else {
            return 1000.0 * bytesGap;
        }
    }

    /**
     * 计算进程每秒发送字节数
     */
    public double getProcessTransmitBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.processTransmitBytes - before.processTransmitBytes;
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * bytesGap, timeGap);
        } else {
            return 1000.0 * bytesGap;
        }
    }

    public double getSystemSendReceiveBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = (this.systemTransmitBytes + this.systemReceiveBytes) - (before.systemTransmitBytes + before.systemReceiveBytes);
        if(0 != timeGap) {
            return MathUtil.divideWith2Digit(1000.0 * bytesGap, timeGap);
        } else {
            return 1000.0 * bytesGap;
        }
    }

}
