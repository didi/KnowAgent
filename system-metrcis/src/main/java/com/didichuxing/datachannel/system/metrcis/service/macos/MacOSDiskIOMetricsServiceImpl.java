package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.DiskIOMetricsService;

import java.util.HashMap;
import java.util.Map;

public class MacOSDiskIOMetricsServiceImpl implements DiskIOMetricsService {

    @Override
    public Map<String, PeriodStatistics> getIOUtil() {
        Map<String, PeriodStatistics> map = new HashMap<>();
        map.put("vda", PeriodStatistics.defaultValue());
        return map;
    }

}
