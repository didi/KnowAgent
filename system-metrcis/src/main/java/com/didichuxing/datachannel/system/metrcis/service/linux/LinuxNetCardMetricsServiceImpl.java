package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.NetCardMetricsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinuxNetCardMetricsServiceImpl extends LinuxMetricsService implements NetCardMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxNetCardMetricsServiceImpl.class);

    private Map<String, PeriodStatistics> sendBytesPs = new HashMap<>();

    @Override
    public Map<String, String> getMacAddress() {
        List<String> lines = getOutputByScript("mac-addresses.sh", this.getClass().getResource("/").getPath(), "获取系统各网卡信息");
        if(CollectionUtils.isNotEmpty(lines)) {
            if(lines.size() % 2 == 0) {
                Map<String, String> result = new HashMap<>();
                for (int i = 0; i < lines.size() - 2; i+=2) {
                    String device = lines.get(i).substring(0, lines.indexOf(":"));
                    String macAddress = lines.get(i+1).split(":")[1].trim();
                    result.put(device, macAddress);
                }
                return result;
            } else {
                LOGGER.error(
                        String.format(
                                "class=LinuxNetCardMetricsServiceImpl||method=getMacAddress||errMsg=获取网卡信息[%s]错误，网卡信息行数须为偶数行",
                                JSON.toJSONString(lines)
                        )
                );
                return new HashMap<>();
            }
        } else {
            LOGGER.error("class=LinuxNetCardMetricsServiceImpl||method=getMacAddress||errMsg=获取网卡信息错误，网卡信息为空");
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Long> getBandWidth() {
        List<String> lines = getOutputByScript("port-speed.sh", this.getClass().getResource("/").getPath(), "获取系统各网卡信息");
        if(CollectionUtils.isNotEmpty(lines)) {
            if(lines.size() % 2 == 0) {
                Map<String, Long> result = new HashMap<>();
                for (int i = 0; i < lines.size() - 2; i+=2) {
                    String device = lines.get(i).substring(0, lines.indexOf(":"));
                    String bandWidthStr = lines.get(i+1).split(":")[1].trim();
                    Long bandWidth = Long.valueOf(bandWidthStr.substring(0, bandWidthStr.indexOf("Mb/s")));
                    result.put(device, bandWidth);
                }
                return result;
            } else {
                LOGGER.error(
                        String.format(
                                "class=LinuxNetCardMetricsServiceImpl||method=getMacAddress||errMsg=获取网卡信息[%s]错误，网卡信息行数须为偶数行",
                                JSON.toJSONString(lines)
                        )
                );
                return new HashMap<>();
            }
        } else {
            LOGGER.error("class=LinuxNetCardMetricsServiceImpl||method=getMacAddress||errMsg=获取网卡信息错误，网卡信息为空");
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, PeriodStatistics> getSendBytesPs() {
        for (PeriodStatistics value : sendBytesPs.values()) {
            value.snapshot();
        }
        return sendBytesPs;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSendBytesPs() {
        Map<String, Double> device2SendBytesMap = getSendBytesPsOnly();
        for (Map.Entry<String, Double> entry : device2SendBytesMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.sendBytesPs.containsKey(device)) {
                this.sendBytesPs.get(device).add(value);
            } else {
                PeriodStatistics sendBytesPsPeriodStatistics = new PeriodStatistics();
                sendBytesPsPeriodStatistics.add(value);
                this.sendBytesPs.put(device, sendBytesPsPeriodStatistics);
            }
        }
    }

    private Map<String, Double> getSendBytesPsOnly() {
        List<String> lines = getOutputByCmd("sar -n DEV 1 1", "获取各网卡上行流量", null);
        if(CollectionUtils.isNotEmpty(lines)) {
            int startIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                if(lines.get(i).contains("IFACE")) {
                    startIndex = i + 1;
                }
            }
            if(startIndex == -1 || startIndex == lines.size() - 1) {
                LOGGER.error(
                        String.format(
                                "class=LinuxNetCardMetricsServiceImpl||method=getSendBytesPsOnly||errMsg=获取网卡实时上行流量错误，网卡实时上行流量信息[%s]不符合预期格式",
                                JSON.toJSONString(lines)
                        )
                );
                return new HashMap<>();
            } else {
                Map<String, Double> result = new HashMap<>();
                for (int i = startIndex; i < lines.size(); i++) {
                    if(StringUtils.isNotBlank(lines.get(i))) {
                        String[] infos = lines.get(i).split("\\s+");
                        String device = infos[1];
                        Double sendBytes = Double.valueOf(infos[5]);
                        result.put(device, sendBytes);
                    }
                }
                return result;
            }
        } else {
            LOGGER.error("class=LinuxNetCardMetricsServiceImpl||method=getSendBytesPsOnly||errMsg=获取网卡实时上行流量错误，网卡实时上行流量为空");
            return new HashMap<>();
        }
    }

}
