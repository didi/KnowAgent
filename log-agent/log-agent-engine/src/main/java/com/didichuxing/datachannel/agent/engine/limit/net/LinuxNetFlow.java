package com.didichuxing.datachannel.agent.engine.limit.net;

import com.didichuxing.datachannel.agent.engine.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ronaldo
 */
public class LinuxNetFlow {

    /**
     * 系统接收的字节数
     */
    private long         systemReceiveBytes;

    /**
     * 系统发送的字节数
     */
    private long         systemTransmitBytes;

    /**
     * 进程接收的字节数
     */
    private long         processReceiveBytes;

    /**
     * 进程发送的字节数
     */
    private long         processTransmitBytes;

    /**
     * 当前时间
     */
    private long         currentTime;

    /**
     * 系统收发字节数的路径
     */
    private final String SYSTEM_PATH = "/proc/net/dev";

    /**
     * 进程收发字节数的路径
     */
    private final String PROCESS_PATH;

    public LinuxNetFlow(long pid) throws Exception {
        this.PROCESS_PATH = "/proc/" + pid + "/net/dev";
        this.currentTime = System.currentTimeMillis();

        List<String> systemReceiveAndTransmitBytes = FileUtils.readFileContent(SYSTEM_PATH, -1);
        List<String> processReceiveAndTransmitBytes = FileUtils.readFileContent(PROCESS_PATH, -1);
        this.systemReceiveBytes = getBytes(systemReceiveAndTransmitBytes, 2);
        this.systemTransmitBytes = getBytes(systemReceiveAndTransmitBytes, 10);
        this.processReceiveBytes = getBytes(processReceiveAndTransmitBytes, 2);
        this.processTransmitBytes = getBytes(processReceiveAndTransmitBytes, 10);
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
            if ("lo:".equals(array[1])) {
                continue;
            }
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
        return 1000.0 * bytesGap / timeGap;
    }

    /**
     * 计算系统每秒发送字节数
     */
    public double getSystemTransmitBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.systemTransmitBytes - before.systemTransmitBytes;
        return 1000.0 * bytesGap / timeGap;
    }

    /**
     * 计算进程每秒接收字节数
     */
    public double getProcessReceiveBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.processReceiveBytes - before.processReceiveBytes;
        return 1000.0 * bytesGap / timeGap;
    }

    /**
     * 计算进程每秒发送字节数
     */
    public double getProcessTransmitBytesPs(LinuxNetFlow before) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap = this.processTransmitBytes - before.processTransmitBytes;
        return 1000.0 * bytesGap / timeGap;
    }
}
