package com.didichuxing.datachannel.agent.engine.limit.cpu;

import com.didichuxing.datachannel.agent.engine.utils.FileUtils;
import com.didichuxing.datachannel.agent.engine.utils.ProcessUtils;
import org.apache.commons.lang3.StringUtils;

public class LinuxCpuTime {

    public long          all;                    // 整个系统从启动到现在的cpu耗时
    public long          process;                // 当前进程从启动到现在的cpu耗时

    // 获得当前进程从启动到现在的cpu耗时
    private final String PROCESS_PATH;

    // 获得整个系统从启动到现在的cpu耗时
    private final String ALL_PATH = "/proc/stat";

    // 获得当前的耗时
    public LinuxCpuTime(long pid, int cpuNum) throws Exception {
        this.all = getAllTime();
        this.process = getProcessTime();
        PROCESS_PATH = "/proc/" + pid + "/stat";
    }

    // 根据计算cpu耗时的差值，计算这段时间中的cpu耗时
    public float getUsage(LinuxCpuTime before) {
        float cpuUsage = ((float) (all - before.all)) / ProcessUtils.getInstance().getCpuNum();
        float proUsage = process - before.process;
        return proUsage * 100 / cpuUsage;
    }

    private long getAllTime() throws Exception {
        String[] array = FileUtils.readFirstLine(ALL_PATH);
        if (array[0] == null || !array[0].trim().equals("cpu")) {
            throw new Exception(ALL_PATH + " first line is not init with 'cpu'");
        }

        if (array.length < 8) {
            throw new Exception(ALL_PATH + " first line is too short");
        }

        long count = 0;
        for (int i = 1; i < array.length; i++) {
            if (StringUtils.isNotBlank(array[i])) {
                count += Long.valueOf(array[i]);
            }
        }

        return count;
    }

    private long getProcessTime() throws Exception {
        String[] array = FileUtils.readFirstLine(PROCESS_PATH);
        if (array.length < 17) {
            throw new Exception(PROCESS_PATH + " first line is too short");
        }

        long count = 0;
        count += Long.valueOf(array[13]);
        count += Long.valueOf(array[14]);
        count += Long.valueOf(array[15]);
        count += Long.valueOf(array[16]);
        return count;
    }

}
