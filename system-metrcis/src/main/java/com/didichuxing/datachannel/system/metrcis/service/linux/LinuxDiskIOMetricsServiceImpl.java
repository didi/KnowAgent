package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.DiskIOMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinuxDiskIOMetricsServiceImpl extends LinuxMetricsService implements DiskIOMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxDiskIOMetricsServiceImpl.class);

    private Map<String, PeriodStatistics> iOUtil = new HashMap<>();

    private static LinuxDiskIOMetricsServiceImpl instance;

    private LinuxDiskIOMetricsServiceImpl() {}

    public static synchronized LinuxDiskIOMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxDiskIOMetricsServiceImpl();
        }
        return instance;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOUtil() {
        Map<String, Double> device2IOUtilMap = getIOUtilOnly();
        for (Map.Entry<String, Double> entry : device2IOUtilMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOUtil.containsKey(device)) {
                this.iOUtil.get(device).add(value);
            } else {
                PeriodStatistics iOUtilPeriodStatistics = new PeriodStatistics();
                iOUtilPeriodStatistics.add(value);
                this.iOUtil.put(device, iOUtilPeriodStatistics);
            }
        }
    }

    private Map<String, Double> getIOUtilOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$14}'", "各设备I/O请求的CPU时间百分比", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxDiskIOMetricsServiceImpl()||method=getIOUtil()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getAvgQuSz() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getAvgRqSz() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIORAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadRequest() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIORRQMS() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOSVCTM() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOUtil() {
        for (PeriodStatistics value : iOUtil.values()) {
            value.snapshot();
        }
        return iOUtil;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWAwait() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteRequest() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadWriteBytes() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWRQMS() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTime() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTimePercent() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTime() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTimePercent() {
        return null;
    }

}
