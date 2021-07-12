package com.didichuxing.datachannel.agentmanager.common.bean.common;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 诊断结果
 */
public class DiagnosisResult {

    /**
     * 出故障的主机对象 id
     */
    private Long hostId;

    /**
     * 健康度
     */
    private Integer healthLevel;
    /**
     * 诊断时间
     */
    private Long diagnosisTime;

    /**
     * 诊断指标集
     */
    private List<DiagnosisMetric> diagnosisMetricList;

}
