package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.DiskIOMetricsService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinuxDiskIOMetricsServiceImpl extends LinuxMetricsService implements DiskIOMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxDiskIOMetricsServiceImpl.class);

    private Map<String, PeriodStatistics> iOUtil = new HashMap<>();
    private Map<String, PeriodStatistics> avgQuSz = new HashMap<>();
    private Map<String, PeriodStatistics> avgRqSz = new HashMap<>();
    private Map<String, PeriodStatistics> iOAwait = new HashMap();
    private Map<String, PeriodStatistics> iORAwait = new HashMap();
    private Map<String, PeriodStatistics> iOReadRequest = new HashMap();
    private Map<String, PeriodStatistics> iOReadBytes = new HashMap();
    private Map<String, PeriodStatistics> iORRQMS = new HashMap();
    private Map<String, PeriodStatistics> iOSVCTM = new HashMap();
    private Map<String, PeriodStatistics> iOWAwait = new HashMap();
    private Map<String, PeriodStatistics> iOWriteRequest = new HashMap();
    private Map<String, PeriodStatistics> iOWriteBytes = new HashMap();
    private Map<String, PeriodStatistics> iOReadWriteBytes = new HashMap();
    private Map<String, PeriodStatistics> iOWRQMS = new HashMap();
    private Map<String, PeriodStatistics> diskReadTime = new HashMap();
    private Map<String, PeriodStatistics> readTimePercent = new HashMap();
    private Map<String, PeriodStatistics> writeTime = new HashMap<>();
    private Map<String, PeriodStatistics> writeTimePercent = new HashMap<>();

    private static LinuxDiskIOMetricsServiceImpl instance;

    private LinuxDiskIOMetricsServiceImpl() {}

    public static synchronized LinuxDiskIOMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxDiskIOMetricsServiceImpl();
        }
        return instance;
    }

    @Override
    public Map<String, PeriodStatistics> getIOUtil() {
        if(iOUtil.isEmpty()) {
            calcIOUtil();
        }
        for (PeriodStatistics value : iOUtil.values()) {
            value.snapshot();
        }
        return iOUtil;
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
        if(avgQuSz.isEmpty()) {
            calcAvgQuSzOnly();
        }
        for (PeriodStatistics value : avgQuSz.values()) {
            value.snapshot();
        }
        return avgQuSz;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcAvgQuSzOnly() {
        Map<String, Double> device2AvgQuSzMap = getAvgQuSzOnly();
        for (Map.Entry<String, Double> entry : device2AvgQuSzMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.avgQuSz.containsKey(device)) {
                this.avgQuSz.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.avgQuSz.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getAvgQuSzOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 9);
        List<String> lines = getOutputByCmd(procFDShell, "各设备平均队列长度", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getAvgQuSz()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getAvgRqSz() {
        if(avgRqSz.isEmpty()) {
            calcAvgRqSz();
        }
        for (PeriodStatistics value : avgRqSz.values()) {
            value.snapshot();
        }
        return avgRqSz;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcAvgRqSz() {
        Map<String, Double> device2AvgRqSzMap = getAvgRqSzOnly();
        for (Map.Entry<String, Double> entry : device2AvgRqSzMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.avgRqSz.containsKey(device)) {
                this.avgRqSz.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.avgRqSz.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getAvgRqSzOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 8);
        List<String> lines = getOutputByCmd(procFDShell, "各设备平均请求大小", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getAvgRqSzOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOAwait() {
        if(iOAwait.isEmpty()) {
            calcIOAwait();
        }
        for (PeriodStatistics value : iOAwait.values()) {
            value.snapshot();
        }
        return iOAwait;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOAwait() {
        Map<String, Double> device2IOAwaitMap = getIOAwaitOnly();
        for (Map.Entry<String, Double> entry : device2IOAwaitMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOAwait.containsKey(device)) {
                this.iOAwait.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOAwait.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOAwaitOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 10);
        List<String> lines = getOutputByCmd(procFDShell, "各设备每次IO平均处理时间", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOAwaitOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIORAwait() {
        if(iORAwait.isEmpty()) {
            calcIORAwait();
        }
        for (PeriodStatistics value : iORAwait.values()) {
            value.snapshot();
        }
        return iORAwait;
    }

    private Map<String, Double> getIORAwaitOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 11);
        List<String> lines = getOutputByCmd(procFDShell, "各设备读请求平均耗时", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIORAwaitOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIORAwait() {
        Map<String, Double> device2IORAwaitMap = getIORAwaitOnly();
        for (Map.Entry<String, Double> entry : device2IORAwaitMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iORAwait.containsKey(device)) {
                this.iORAwait.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iORAwait.put(device, periodStatistics);
            }
        }
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadRequest() {
        if(iOReadRequest.isEmpty()) {
            calcIOReadRequest();
        }
        for (PeriodStatistics value : iOReadRequest.values()) {
            value.snapshot();
        }
        return iOReadRequest;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOReadRequest() {
        Map<String, Double> device2IOReadRequestMap = getIOReadRequestOnly();
        for (Map.Entry<String, Double> entry : device2IOReadRequestMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOReadRequest.containsKey(device)) {
                this.iOReadRequest.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOReadRequest.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOReadRequestOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 4);
        List<String> lines = getOutputByCmd(procFDShell, "各设备每秒读请求数量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOReadRequest()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadBytes() {
        if(iOReadBytes.isEmpty()) {
            calcIOReadBytes();
        }
        for (PeriodStatistics value : iOReadBytes.values()) {
            value.snapshot();
        }
        return iOReadBytes;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOReadBytes() {
        Map<String, Double> device2IOReadBytesMap = getIOReadBytesOnly();
        for (Map.Entry<String, Double> entry : device2IOReadBytesMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOReadBytes.containsKey(device)) {
                this.iOReadBytes.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOReadBytes.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOReadBytesOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$6}'", "各设备每秒读取字节数", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getIOReadBytesOnly()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = 1024 * Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIORRQMS() {
        if(iORRQMS.isEmpty()) {
            calcIORRQMS();
        }
        for (PeriodStatistics value : iORRQMS.values()) {
            value.snapshot();
        }
        return iORRQMS;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIORRQMS() {
        Map<String, Double> device2IORRQMSMap = getIORRQMSOnly();
        for (Map.Entry<String, Double> entry : device2IORRQMSMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iORRQMS.containsKey(device)) {
                this.iORRQMS.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iORRQMS.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIORRQMSOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 2);
        List<String> lines = getOutputByCmd(procFDShell, "各设备每秒合并到设备队列的读请求数", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIORRQMSOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOSVCTM() {
        if(iOSVCTM.isEmpty()) {
            calcIOSVCTM();
        }
        for (PeriodStatistics value : iOSVCTM.values()) {
            value.snapshot();
        }
        return iOSVCTM;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOSVCTM() {
        Map<String, Double> device2IOSVCTMMap = getIOSVCTMOnly();
        for (Map.Entry<String, Double> entry : device2IOSVCTMMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOSVCTM.containsKey(device)) {
                this.iOSVCTM.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOSVCTM.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOSVCTMOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 13);
        List<String> lines = getOutputByCmd(procFDShell, "每次各设备IO平均服务时间", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOSVCTM()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWAwait() {
        if(iOWAwait.isEmpty()) {
            calcIOWAwait();
        }
        for (PeriodStatistics value : iOWAwait.values()) {
            value.snapshot();
        }
        return iOWAwait;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOWAwait() {
        Map<String, Double> device2IOWAwaitMap = getIOWAwaitOnly();
        for (Map.Entry<String, Double> entry : device2IOWAwaitMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOWAwait.containsKey(device)) {
                this.iOWAwait.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOWAwait.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOWAwaitOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 12);
        List<String> lines = getOutputByCmd(procFDShell, "各设备写请求平均耗时", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOWAwaitOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteRequest() {
        if(iOWriteRequest.isEmpty()) {
            calcIOWriteRequest();
        }
        for (PeriodStatistics value : iOWriteRequest.values()) {
            value.snapshot();
        }
        return iOWriteRequest;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOWriteRequest() {
        Map<String, Double> device2IOWriteRequestMap = getIOWriteRequestOnly();
        for (Map.Entry<String, Double> entry : device2IOWriteRequestMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOWriteRequest.containsKey(device)) {
                this.iOWriteRequest.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOWriteRequest.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOWriteRequestOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 5);
        List<String> lines = getOutputByCmd(procFDShell, "各设备每秒写请求数量", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOWriteRequestOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWriteBytes() {
        if(iOWriteBytes.isEmpty()) {
            calcIOWriteBytes();
        }
        for (PeriodStatistics value : iOWriteBytes.values()) {
            value.snapshot();
        }
        return iOWriteBytes;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOWriteBytes() {
        Map<String, Double> device2IOWriteBytesMap = getIOWriteBytesOnly();
        for (Map.Entry<String, Double> entry : device2IOWriteBytesMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOWriteBytes.containsKey(device)) {
                this.iOWriteBytes.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOWriteBytes.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOWriteBytesOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$7}'", "各设备每秒写字节数", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemIOWriteBytes()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = 1024 * Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getIOReadWriteBytes() {
        if(iOReadWriteBytes.isEmpty()) {
            calcIOReadWriteBytes();
        }
        for (PeriodStatistics value : iOReadWriteBytes.values()) {
            value.snapshot();
        }
        return iOReadWriteBytes;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOReadWriteBytes() {
        Map<String, Double> device2IOReadWriteBytesMap = getIOReadWriteBytesOnly();
        for (Map.Entry<String, Double> entry : device2IOReadWriteBytesMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOReadWriteBytes.containsKey(device)) {
                this.iOReadWriteBytes.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOReadWriteBytes.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOReadWriteBytesOnly() {
        Map<String, Double> device2IOReadBytesMap = getIOReadBytesOnly();
        Map<String, Double> device2IOWriteBytesMap = getIOWriteBytesOnly();
        Map<String, Double> device2IOReadWriteBytesMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : device2IOReadBytesMap.entrySet()) {
            String device = entry.getKey();
            Double iOReadBytes = entry.getValue();
            Double iOWriteBytes = getMetricValueByKey(device2IOWriteBytesMap, device, "IOWriteBytes", 0d);
            Double iOReadWriteBytes = iOWriteBytes + iOReadBytes;
            device2IOReadWriteBytesMap.put(device, iOReadWriteBytes);
        }
        return device2IOReadWriteBytesMap;
    }

    @Override
    public Map<String, PeriodStatistics> getIOWRQMS() {
        if(iOWRQMS.isEmpty()) {
            calcIOWRQMS();
        }
        for (PeriodStatistics value : iOWRQMS.values()) {
            value.snapshot();
        }
        return iOWRQMS;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcIOWRQMS() {
        Map<String, Double> device2IOWRQMSMap = getIOWRQMSOnly();
        for (Map.Entry<String, Double> entry : device2IOWRQMSMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.iOWRQMS.containsKey(device)) {
                this.iOWRQMS.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.iOWRQMS.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getIOWRQMSOnly() {
        Map<String, Double> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", 3);
        List<String> lines = getOutputByCmd(procFDShell, "各设备每秒合并到设备队列的写请求数", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", "getIOWRQMSOnly()");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTime() {
        if(diskReadTime.isEmpty()) {
            calcDiskReadTime();
        }
        for (PeriodStatistics value : diskReadTime.values()) {
            value.snapshot();
        }
        return diskReadTime;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcDiskReadTime() {
        Map<String, Double> device2DiskReadTimeMap = getDiskReadTimeOnly();
        for (Map.Entry<String, Double> entry : device2DiskReadTimeMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.diskReadTime.containsKey(device)) {
                this.diskReadTime.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.diskReadTime.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getDiskReadTimeOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$5}'", "各设备读操作耗时", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskReadTime()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            Double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskReadTimePercent() {
        if(readTimePercent.isEmpty()) {
            calcDiskReadTimePercent();
        }
        for (PeriodStatistics value : readTimePercent.values()) {
            value.snapshot();
        }
        return readTimePercent;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcDiskReadTimePercent() {
        Map<String, Double> device2ReadTimePercentMap = getDiskReadTimePercentOnly();
        for (Map.Entry<String, Double> entry : device2ReadTimePercentMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.readTimePercent.containsKey(device)) {
                this.readTimePercent.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.readTimePercent.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getDiskReadTimePercentOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$5,$9}'", "读取磁盘时间百分比", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getDiskReadTimePercentOnly()||msg=data is not enough");
                return result;
            }
            String device = array[0];
            Double readTime = Double.parseDouble(array[1]);
            Double writeTime = Double.parseDouble(array[2]);
            Double readWriteTime = readTime + writeTime;
            Double readTimePercent = MathUtil.divideWith2Digit(readTime * 100, readWriteTime);
            result.put(device, readTimePercent);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTime() {
        if(writeTime.isEmpty()) {
            calcDiskWriteTime();
        }
        for (PeriodStatistics value : writeTime.values()) {
            value.snapshot();
        }
        return writeTime;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcDiskWriteTime() {
        Map<String, Double> device2WriteTimeMap = getDiskWriteTimeOnly();
        for (Map.Entry<String, Double> entry : device2WriteTimeMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.writeTime.containsKey(device)) {
                this.writeTime.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.writeTime.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getDiskWriteTimeOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$9}'", "各设备写操作耗时", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getDiskWriteTimeOnly()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            Double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getDiskWriteTimePercent() {
        if(writeTimePercent.isEmpty()) {
            calcDiskWriteTimePercent();
        }
        for (PeriodStatistics value : writeTimePercent.values()) {
            value.snapshot();
        }
        return writeTimePercent;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcDiskWriteTimePercent() {
        Map<String, Double> device2WriteTimePercentMap = getDiskWriteTimePercentOnly();
        for (Map.Entry<String, Double> entry : device2WriteTimePercentMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.writeTimePercent.containsKey(device)) {
                this.writeTimePercent.get(device).add(value);
            } else {
                PeriodStatistics periodStatistics = new PeriodStatistics();
                periodStatistics.add(value);
                this.writeTimePercent.put(device, periodStatistics);
            }
        }
    }

    private Map<String, Double> getDiskWriteTimePercentOnly() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$5,$9}'", "读取磁盘时间百分比", null);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getDiskReadTimePercentOnly()||msg=data is not enough");
                return result;
            }
            String device = array[0];
            Double readTime = Double.parseDouble(array[1]);
            Double writeTime = Double.parseDouble(array[2]);
            Double readWriteTime = readTime + writeTime;
            Double writeTimePercent = MathUtil.divideWith2Digit(writeTime * 100, readWriteTime);
            result.put(device, writeTimePercent);
        }
        return result;
    }

}
