package com.didichuxing.datachannel.agent.engine.limit.net;

import com.didichuxing.datachannel.agent.engine.utils.FileUtils;

import java.util.List;

/**
 * @author Ronaldo
 */
public class LinuxNetFlow {

    // 系统接收的字节数
    public long          systemReceiveBytes;
    // 系统发送的字节数
    public long          systemTransmitBytes;
    // 进程接收的字节数
    public long          processReceiveBytes;
    // 进程发送的字节数
    public long          processTransmitBytes;
    // 当前时间
    public long          currentTime;

    // 获取系统收发字节数
    private final String SYSTEM_PATH = "/proc/net/dev";

    // 获取进程收发字节数
    private final String PROCESS_PATH;

    public LinuxNetFlow(long pid) throws Exception {
        this.PROCESS_PATH = "/proc/" + pid + "/net/dev";
        this.currentTime = System.currentTimeMillis();
        this.systemReceiveBytes = getBytes(SYSTEM_PATH, true);
        this.systemTransmitBytes = getBytes(SYSTEM_PATH, false);
        this.processReceiveBytes = getBytes(PROCESS_PATH, true);
        this.processTransmitBytes = getBytes(PROCESS_PATH, false);
    }

    // 获取文件中收发字节数
    private long getBytes(String path, boolean isReceive) throws Exception {
        long bytes = 0;
        List<String> lines = FileUtils.readFileContent(path, -1);
        for (int i = 2; i < lines.size(); i++) {
            String[] array = lines.get(i).split("\\s+");
            if ("lo:".equals(array[1])) {
                continue;
            }
            if (isReceive) {
                bytes += Long.parseLong(array[2]);
            } else {
                bytes += Long.parseLong(array[10]);
            }
        }
        return bytes;
    }

    // 计算每秒收发字节数
    public long getBytesPS(LinuxNetFlow before, boolean isSystem, boolean isReceive) {
        long timeGap = this.currentTime - before.currentTime;
        long bytesGap;
        if (isSystem && isReceive) {
            bytesGap = this.systemReceiveBytes - before.systemReceiveBytes;
        } else if (isSystem && !isReceive) {
            bytesGap = this.systemTransmitBytes - before.systemTransmitBytes;
        } else if (!isSystem && isReceive) {
            bytesGap = this.processReceiveBytes - before.processReceiveBytes;
        } else {
            bytesGap = this.processTransmitBytes - before.processTransmitBytes;
        }
        return 1000 * bytesGap / timeGap;
    }
}
