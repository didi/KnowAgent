package com.didichuxing.datachannel.system.metrcis.service.linux;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class LinuxMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxMetricsService.class);

    /**
     * cpu 核数（逻辑核）
     */
    protected Integer CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * linux 根据shell命令获取系统或者进程资源
     * @param procFDShell shell命令
     * @param resourceMessage    资源描述信息
     * @param pid 进程 id
     * @return
     */
    protected List<String> getOutputByCmd(String procFDShell, String resourceMessage, Long pid) {
        Process process = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            if(null != pid) {
                procFDShell = String.format(procFDShell, pid);
            }
            String[] cmd = new String[] { "sh", "-c", procFDShell };
            process = Runtime.getRuntime().exec(cmd);
            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
            return lines;
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[{}]失败", resourceMessage, ex);
            return Collections.emptyList();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程对应输入流失败", resourceMessage, resourceMessage, ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程失败", resourceMessage, resourceMessage, ex);
            }
        }
    }

    protected  <T> T getMetricValueByKey(Map<String, T> key2MetricValueMap, String key, String metricName, T defaultValue) {
        T metricValue = key2MetricValueMap.get(key);
        if(null == metricValue) {
            metricValue = defaultValue;
            LOGGER.error(
                    String.format(
                            "class=%s|method=%s|errorMsg=get metric %s value of key:%s is null",
                            "LinuxMetricsService",
                            "getMetricValueByKey",
                            metricName,
                            key
                    )
            );
        }
        return metricValue;
    }

    protected Long getSystemMemTotal() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemTotal:' | awk '{print $2}'", "系统物理内存总量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemTotal()||msg=data is null");
            return 0L;
        }
    }

}
