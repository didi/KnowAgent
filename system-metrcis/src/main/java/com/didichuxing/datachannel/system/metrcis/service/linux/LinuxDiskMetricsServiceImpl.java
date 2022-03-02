package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.service.DiskMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinuxDiskMetricsServiceImpl extends LinuxMetricsService implements DiskMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxDiskMetricsServiceImpl.class);

    private static LinuxDiskMetricsServiceImpl instance;

    public static synchronized LinuxDiskMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxDiskMetricsServiceImpl();
        }
        return instance;
    }

    private LinuxDiskMetricsServiceImpl() {}
    @Override
    public Map<String, String> getFsType() {
        Map<String, String> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -T | awk 'NR>1{print $2,$7}'", "磁盘各分区对应文件系统类型", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxDiskMetricsServiceImpl||method=getFsType||msg=data is not enough");
                return result;
            }
            String key = array[1];
            String value = array[0];
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getBytesFree() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$4,$6}'", "磁盘各分区余量大小", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxDiskMetricsServiceImpl||method=getBytesFree||msg=data is not enough");
                return result;
            }
            String key = array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

}
