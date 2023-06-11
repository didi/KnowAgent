package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.NetCardMetricsService;

import java.util.HashMap;
import java.util.Map;

public class MacOSNetCardMetricsServiceImpl implements NetCardMetricsService {

    @Override
    public Map<String, String> getMacAddress() {
        Map<String, String> map = new HashMap<>();
        map.put("eth0", "00:50:56:24:fd:7e");
        return map;
    }

    @Override
    public Map<String, Long> getBandWidth() {
        Map<String, Long> map = new HashMap<>();
        map.put("eth0", 10 * 1024 * 1024L);
        return map;
    }

    @Override
    public Map<String, PeriodStatistics> getReceiveBytesPs() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getSendBytesPs() {
        Map<String, PeriodStatistics> map = new HashMap<>();
        map.put("eth0", PeriodStatistics.defaultValue());
        return map;
    }

}
