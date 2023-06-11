package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.service.DiskMetricsService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
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
                LOGGER.error("class=LinuxDiskMetricsServiceImpl||method=getFsType()||msg=data is not enough");
                return result;
            }
            String key = array[1];
            String value = array[0];
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getBytesTotal() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$2,$6}'", "磁盘各分区总量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getBytesTotal()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            long value = 1024 * Long.parseLong(array[1]);
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
                LOGGER.error("class=LinuxDiskMetricsServiceImpl||method=getBytesFree()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getBytesUsed() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$3,$6}'", "磁盘各分区用量大小", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getBytesUsed()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Double> getBytesUsedPercent() {
        Map<String, Long> path2diskBytesUsedMap = getBytesUsed();
        Map<String, Long> path2diskBytesTotalMap = getBytesTotal();
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Long> entry : path2diskBytesUsedMap.entrySet()) {
            String key = entry.getKey();
            Long numerator = entry.getValue();
            Long denominator = path2diskBytesTotalMap.get(key);
            if(null == numerator || numerator == denominator) {
                //TODO：logger it
                result.put(key, 0d);
            }
            if(0 == denominator) {
                //TODO：logger it
                result.put(key, 0d);
            }
            result.put(key, MathUtil.divideWith2Digit(numerator * 100, denominator));
        }
        return result;
    }

    @Override
    public Map<String, Integer> getInodesTotal() {
        Map<String, Integer> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$2,$6}'", "系统各分区inode总数量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getInodesTotal()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            Integer value = Integer.parseInt(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Integer> getInodesFree() {
        Map<String, Integer> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$4,$6}'", "系统各分区空闲inode数量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getInodesFree()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            Integer value = Integer.parseInt(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Integer> getInodesUsed() {
        Map<String, Integer> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$3,$6}'", "系统各分区已用inode数量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getInodesUsed()||msg=data is not enough");
                return result;
            }
            String key = array[2];
            Integer value = Integer.parseInt(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Double> getInodesUsedPercent() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$2,$3,$6}'", "系统各分区已用inode占比", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 4) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getInodesUsedPercent()||msg=data is not enough");
                return result;
            }
            String key = array[3];
            long inodeTotal = Long.parseLong(array[1]);
            long inodeUsed = Long.parseLong(array[2]);
            result.put(key, MathUtil.divideWith2Digit(inodeUsed * 100, inodeTotal));
        }
        return result;
    }

}
