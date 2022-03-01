package com.didichuxing.datachannel.system.metrcis.service.macos;

import com.didichuxing.datachannel.system.metrcis.service.DiskMetricsService;

import java.util.HashMap;
import java.util.Map;

public class MacOSDiskMetricsServiceImpl implements DiskMetricsService {

    @Override
    public Map<String, String> getFsType() {
        Map<String, String> map = new HashMap<>();
        map.put("/", "ext4");
        return map;
    }

    @Override
    public Map<String, Long> getBytesFree() {
        Map<String, Long> map = new HashMap<>();
        map.put("/", 100 * 1024 * 1024 * 1024L);
        return map;
    }

}
