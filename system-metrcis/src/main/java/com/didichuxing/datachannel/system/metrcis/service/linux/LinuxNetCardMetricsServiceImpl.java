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
    private Map<String, PeriodStatistics> receiveBytesPs = new HashMap<>();

    private static LinuxNetCardMetricsServiceImpl instance;

    private LinuxNetCardMetricsServiceImpl() {

    }

    public static synchronized LinuxNetCardMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxNetCardMetricsServiceImpl();
        }
        return instance;
    }


    @Override
    public Map<String, String> getMacAddress() {
        Map<String, Double> device2SendBytesPsMap = getSendBytesPsOnly();
        Map<String, String> result = new HashMap<>();
        for (String device : device2SendBytesPsMap.keySet()) {
            List<String> lines = getOutputByCmd(
                    String.format("ip link show dev %s |awk '/link/{print $2}'", device),
                    String.format("获取设备%s对应mac地址", device),
                    null
            );
            assert CollectionUtils.isNotEmpty(lines) && lines.size() == 1;
            String macAddress = lines.get(0);
            result.put(device, macAddress);
        }
        return result;
    }

    @Override
    public Map<String, Long> getBandWidth() {
        Map<String, Double> device2SendBytesPsMap = getSendBytesPsOnly();
        Map<String, Long> result = new HashMap<>();
        for (String device : device2SendBytesPsMap.keySet()) {
            List<String> lines = getOutputByCmd(
                    String.format("ethtool %s |grep \"Speed:\"", device),
                    String.format("获取设备%s对应带宽信息", device),
                    null
            );
            Long bandWidth = 0L;
            for (String line : lines) {
                if(line.contains("Mb/s")) {
                    String bandWidthStr = line.split(":")[1];
                    bandWidth = Long.valueOf(bandWidthStr.substring(0, bandWidthStr.indexOf("Mb/s")).trim());
                    break;
                }
            }
            result.put(device, bandWidth * 1024 * 1024);
        }
        return result;
    }

    @Override
    public Map<String, PeriodStatistics> getReceiveBytesPs() {
        if(receiveBytesPs.isEmpty()) {
            calcReceiveBytesPs();
        }
        for (PeriodStatistics value : receiveBytesPs.values()) {
            value.snapshot();
        }
        return receiveBytesPs;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcReceiveBytesPs() {
        Map<String, Double> device2ReceiveBytesPsMap = getReceiveBytesPsOnly();
        for (Map.Entry<String, Double> entry : device2ReceiveBytesPsMap.entrySet()) {
            String device = entry.getKey();
            Double value = entry.getValue();
            if(this.receiveBytesPs.containsKey(device)) {
                this.receiveBytesPs.get(device).add(value);
            } else {
                PeriodStatistics sendBytesPsPeriodStatistics = new PeriodStatistics();
                sendBytesPsPeriodStatistics.add(value);
                this.receiveBytesPs.put(device, sendBytesPsPeriodStatistics);
            }
        }
    }

    private Map<String, Double> getReceiveBytesPsOnly() {
        List<String> lines = getOutputByCmd("sar -n DEV 1 1", "获取各网卡下行流量", null);
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
                                "class=LinuxNetCardMetricsServiceImpl||method=getReceiveBytesPsOnly||errMsg=获取网卡实时下行流量错误，网卡实时下行流量信息[%s]不符合预期格式",
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
                        Double sendBytes = Double.valueOf(infos[4]) * 1024;
                        result.put(device, sendBytes);
                    }
                }
                return result;
            }
        } else {
            LOGGER.error("class=LinuxNetCardMetricsServiceImpl||method=getReceiveBytesPsOnly||errMsg=获取网卡实时下行流量错误，网卡实时下行流量为空");
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, PeriodStatistics> getSendBytesPs() {
        if(sendBytesPs.isEmpty()) {
            calcSendBytesPs();
        }
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
                        Double sendBytes = Double.valueOf(infos[5]) * 1024;
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
