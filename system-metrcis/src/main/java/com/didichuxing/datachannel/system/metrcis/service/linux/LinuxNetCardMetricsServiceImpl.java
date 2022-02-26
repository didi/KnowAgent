package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.NetCardMetricsService;

import java.util.Map;

public class LinuxNetCardMetricsServiceImpl implements NetCardMetricsService {
    @Override
    public Map<String, String> getMacAddress() {
        return null;
    }

    @Override
    public Map<String, Long> getBandWidth() {
        return null;
    }

    @Override
    public Map<String, PeriodStatistics> getSendBytesPs() {
        return null;
    }

}
