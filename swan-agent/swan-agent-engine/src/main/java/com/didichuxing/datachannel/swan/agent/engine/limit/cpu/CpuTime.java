package com.didichuxing.datachannel.swan.agent.engine.limit.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.swan.agent.common.loggather.LogGather;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.swan.agent.engine.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuTime {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpuTime.class);

    public long all;    // 整个系统从启动到现在的cpu耗时
    public long process; // 当前进程从启动到现在的cpu耗时

    // 获得当前的耗时
    public CpuTime() throws Exception {
        this.all = getAllTime();
        this.process = getProcessTime();
    }

    public long getAll() {
        return all;
    }

    public long getProcess() {
        return process;
    }

    // 根据计算cpu耗时的差值，计算这段时间中的cpu耗时
    public float getUsage(CpuTime before) {
        float cpuUsage = ((float) (all - before.all)) / SystemUtils.getCpuNum();
        float proUsage = process - before.process;

        return proUsage * 100 / cpuUsage;
    }

    // 获得整个系统从启动到现在的cpu耗时
    private final static String ALL_PATH = "/proc/stat";

    public long getAllTime() throws Exception {
        String[] array = readFirstLine(ALL_PATH);
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

    // 获得当前进程从启动到现在的cpu耗时
    private final static String PROCESS_PATH;

    static {
        PROCESS_PATH = "/proc/" + SystemUtils.getPid().trim() + "/stat";
    }

    public long getProcessTime() throws Exception {
        String[] array = readFirstLine(PROCESS_PATH);
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

    // 读取文件的第一行数据
    public String[] readFirstLine(String path) throws Exception {
        String line = null;
        List<String> contents = readFileContent(new File(path), 1);
        if (contents != null && contents.size() != 0) {
            line = contents.get(0);
        }

        if (line == null || line.trim().length() == 0) {
            throw new Exception("line is empty");
        }

        line = line.trim();
        return line.split(" ");
    }

    /**
     * 读取文件
     *
     * @param file   file
     * @param maxNum num of content to read
     * @return result of read
     */
    private List<String> readFileContent(File file, int maxNum) {
        List<String> contents = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                contents.add(line);
                if (maxNum != -1) {
                    i++;
                    if (i >= maxNum) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error!", "readFileContent error file is " + file, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                LogGather.recordErrorLog("FileUtil error!", "BufferedReader close failed, file is " + file, e);
            }
        }
        return contents;
    }
}
