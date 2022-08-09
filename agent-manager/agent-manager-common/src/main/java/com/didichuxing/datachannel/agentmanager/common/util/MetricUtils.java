//package com.didichuxing.datachannel.agentmanager.common.util;
//
//import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class MetricUtils {
//    /**
//     * 把空缺的点用指定value补齐
//     *
//     * @param origin       原始图表
//     * @param startTime    开始时间
//     * @param endTime      结束时间
//     * @param step         粒度
//     * @param defaultValue 默认值
//     */
//    public static void buildEmptyMetric(List<MetricPoint> origin, Long startTime, Long endTime, int step, Object defaultValue) {
//        long startPoint = startTime / step;
//        long endPoint = endTime / step;
//        List<Long> timePoints = origin.stream().map(MetricPoint::getTimestamp).collect(Collectors.toList());
//        for (long i = startPoint; i < endPoint; ++i) {
//            if (timePoints.contains(i)) {
//                continue;
//            }
//            MetricPoint metricPoint = new MetricPoint();
//            metricPoint.setTimestamp(i * step);
//            metricPoint.setValue(defaultValue);
//            origin.add(metricPoint);
//        }
//    }
//
//    public static void buildEmptyMetric(List<MetricPoint> origin, Long startTime, Long endTime, int step) {
//        buildEmptyMetric(origin, startTime, endTime, step, 0);
//    }
//}